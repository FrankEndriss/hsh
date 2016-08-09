package com.happypeople.hsh.ls;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hshutil.util.AsyncIterator;
import com.happypeople.hshutil.util.CombinedComparator;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * More or less Posix implementation of ls.
 */
public class Ls implements HshCmd {
    //private final static Logger LOG=LoggerFactory.getLogger(Ls.class);

    @Override
    public int execute(final HshContext hsh, final List<String> args)
        throws Exception {
        final String lcmd = args.remove(0);

        final Options options = new Options();
        options.addOption("l", false, "el: list long format");
        options.addOption("1", false, "one: list one file per line");
        options.addOption("d", false, "dont list directories entries");
        options.addOption("a", false, "list all files (do not hide .foo)");
        options.addOption("A", false, "list all files but not . and ..");
        options.addOption("R", false, "recurse subdirectories");

        final CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args.toArray(new String[0]));
        } catch (final ParseException exce) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printUsage(
                new PrintWriter(hsh.getStdErr()), hsh.getTerminal().getCols(),
                lcmd, options
            );
            return 1;
        }

        final List<AtAcFormatter> formatters = createFormatter(cmd);
        final OutputStyle outputstyle = createOutputStyle(cmd, formatters);
        final DirectoryStream.Filter<Path> filefilter = createFileFilter(cmd);
        final List<Comparator<? super FileEntry>> sorting = createSorting(
            cmd
        );
        Comparator<FileEntry> comp = null;
        if (sorting.size() > 0) {
            comp = new CombinedComparator<>(sorting);
        }

        final List<FileEntry> fileentlist = new ArrayList<>();
        // all other args are filenames
        for (final String arg : cmd.getArgs()) {
            fileentlist.add(new FileEntry(Paths.get(arg)));
        }

        // default argument
        if (fileentlist.size() == 0) {
            fileentlist.add(new FileEntry(Paths.get(".")));
        }

        // arguments which are directly printed, ie files
        final List<FileEntry> printedargs = new ArrayList<>();
        // arguments which are listed, ie directories.
        final List<FileEntry> listedargs = new ArrayList<>();
        if (cmd.hasOption("d")) {
            printedargs.addAll(fileentlist);
        } else {
            for (final FileEntry fent : fileentlist) {
                (Files.isDirectory(fent.getPath()) ? listedargs : printedargs)
                    .add(fent);
            }
        }

        final boolean recursive = cmd.hasOption("R");
        final boolean withnprefix = fileentlist.size() > 1 || recursive;

        boolean firstblock = true;
        if (printedargs.size() > 0) {
            firstblock = false;
            for (final FileEntry fileentry : printedargs)
                if (Files.exists(fileentry.getPath()))
                    outputstyle.printFile(fileentry, hsh.getStdOut());
                else
                    hsh.getStdOut().println("does not exists: " + fileentry);
            outputstyle.doOutput(hsh.getStdOut(), hsh.getTerminal().getCols());
        }

        if (listedargs.size() > 0) {
            final Iterator<FileEntryWithIterator> diriter = getDirListing(
                listedargs.iterator(), filefilter,
                recursive ? Integer.MAX_VALUE : 0, comp
            );

            while (diriter.hasNext()) {
                final FileEntryWithIterator fileentry = diriter.next();
                if (!firstblock)
                    hsh.getStdOut().println();
                firstblock = false;

                if (Files.exists(fileentry.getPath())) {
                    listDir(
                        fileentry, hsh, withnprefix,
                        outputstyle.createInstance()
                    );
                } else {
                    hsh.getStdOut().println("does not exists: " + fileentry);
                }
            }
        }

        return 0;
    }

    /**
     * Lists all dirs given in the argument and all sub-dirs of that dirs, in
     * depth-first order. This is implemented as running in an newly created
     * Thread.
     * @param dirs the dirs to walk the file trees
     * @return an Iterator of FileEntry over the result of the search.
     */
    private Iterator<FileEntryWithIterator> getDirListing(
        final Iterator<FileEntry> dirs,
        final DirectoryStream.Filter<Path> filter, final int recurdepth,
        final Comparator<FileEntry> comp) {
        final AsyncIterator<FileEntryWithIterator> ret = new AsyncIterator<>();
        new Thread() {
            private FileEntryWithIterator currentdir;

            @Override
            public void run() {
                try {
                    while (dirs.hasNext()) {
                        final FileEntry fent = dirs.next();
                        try {
                            walkFileTree(
                                fent, filter, recurdepth, comp,
                                new LsFileVisitor() {

                                    @Override
                                    public void preVisitDirectory(
                                        final FileEntry dir) {
                                        ret.offer(
                                            currentdir = new FileEntryWithIterator(
                                                dir.getPath(),
                                                new AsyncIterator<FileEntry>()
                                            )
                                        );
                                    }

                                    @Override
                                    public void visitFile(
                                        final FileEntry file) {
                                        currentdir.getFilesIterator()
                                            .offer(file);
                                    }

                                    @Override
                                    public void postVisitDirectory(
                                        final FileEntry dir) {
                                        currentdir.getFilesIterator().close();
                                        currentdir = null;
                                    }
                                }
                            );
                        } catch (final IOException exce) {
                            exce.printStackTrace();
                        }
                    }
                } finally {
                    ret.close();
                }
            }

            /**
             * Does a breath-first (listing-first) traversal of the file tree
             * rooted at path. This is, for all directories in the tree, first
             * preVisitDirecotry is called, then for all entries in that
             * director visitFile(), end then postVisitDirectory(). If root is
             * not a directory, nothing happens.
             * @param path root of the tree to visit
             * @param filter the Filter to use, see
             *  Files.newDirectoryStream(...)
             * @param recurdepth if>0 this method will call itself with an
             *  decremented recursionDepth for all subdirs
             * @param comp if !=null files are listed (and dirs are recursed) in
             *  order of the comparator
             * @param filevisitor the callback
             */
            private void walkFileTree(final FileEntry root,
                final DirectoryStream.Filter<Path> filter, final int recurdepth,
                final Comparator<FileEntry> comp,
                final LsFileVisitor filevisitor) throws IOException {
                if (!Files.isDirectory(root.getPath()))
                    throw new NotDirectoryException(
                        "not directory: " + root.getPath()
                    );

                filevisitor.preVisitDirectory(root);
                // list the entries of the directory collecting subdirs, then
                // recurse in all subdirs
                final List<FileEntry> subdirs = new ArrayList<>();
                final boolean recurse = recurdepth > 0;
                try (DirectoryStream<FileEntry> stream = root
                    .listFiles(filter)) {
                    List<FileEntry> sortedstream = null;
                    if (comp != null) {
                        sortedstream = new ArrayList<>();
                        for (final FileEntry fent : stream)
                            sortedstream.add(fent);
                        Collections.sort(sortedstream, comp);
                    }
                    for (final FileEntry fent : sortedstream != null
                        ? sortedstream : stream) {
                        filevisitor.visitFile(fent);
                        if (recurse && Files.isDirectory(fent.getPath()))
                            subdirs.add(fent);
                    }
                }
                filevisitor.postVisitDirectory(root);

                if (recurse)
                    for (final FileEntry fent : subdirs)
                        walkFileTree(
                            fent, filter, recurdepth - 1, comp, filevisitor
                        );
            }
        }.start();
        return ret;
    }

    /**
     * Listener for file tree traversal.
     * @author Frank Endriss (fj.endriss@gmail.com)
     * @version $Id$
     * @since 0.1
     */
    private interface LsFileVisitor {
        /** Called before any file of the directory.
         * @param dir The directory
         */
        void preVisitDirectory(FileEntry dir);

        /** Called once for any file.
         * @param file The current file
         */
        void visitFile(FileEntry file);

        /** Called after visiting all files of a directory.
         * @param dir The directory
         */
        void postVisitDirectory(FileEntry dir);
    }

    /**
     * Creates the list of comparators used for sorting of the output.
     * @param options The cmdline options
     * @return A List of Comparators according to the cmdline options (not implemented)
     */
    private List<Comparator<? super FileEntry>> createSorting(
        final CommandLine options) {
        final List<Comparator<? super FileEntry>> sorting = new ArrayList<>();
        // TODO implement
        sorting.add(FileEntry.NAME_SORT);
        return sorting;
    }

    /**
     * Creates the overall filter to control what to list in directory listings.
     * @param cmd the command options
     * @return a usable DirectoryStream.Filter<Path>
     */
    private DirectoryStream.Filter<Path> createFileFilter(
        final CommandLine cmd) {
        if (cmd.hasOption("a")) {
            return new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(final Path entry) throws IOException {
                    return true;
                }
            };
        } else if (cmd.hasOption("A")) {
            return new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(final Path path) {
                    final String lname = path.getFileName().toString();
                    return !(".".equals(lname) || "..".equals(lname));
                }
            };
        } else {
            return new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(final Path path) throws IOException {
                    return !Files.isHidden(path);
                }
            };
        }
    }

    /**
     * Creates the OutputStyle.
     * @param cmd the command options
     * @param formatters the formatter to do the output per file
     * @return a new OutputStyle according to the options
     */
    private OutputStyle createOutputStyle(final CommandLine cmd,
        final List<AtAcFormatter> formatters) {
        OutputStyle outputstyle = null;
        if (cmd.hasOption("l") || cmd.hasOption("1")) {
            outputstyle = new FloatOutputStyle(formatters);
        } else {
            outputstyle = new VerticalOutputStyle(
                new AtAcFormatter(FileEntry.NAME_ATAC, Adjustment.LEFT)
            );
        }
        return outputstyle;
    }

    /**
     * Creates the list of AtAcFormatters. This list is used to display the
     * attributes of a File.
     * @param cmd the commandline options
     * @return a new List<AtAcFormatter> useable to format the output per File
     */
    private List<AtAcFormatter> createFormatter(final CommandLine cmd) {
        // TODO implement other options
        final List<AtAcFormatter> formatters = new ArrayList<>();
        if (cmd.hasOption("l")) {
            formatters
                .add(new AtAcFormatter(FileEntry.PERM_ATAC, Adjustment.LEFT));
            formatters
                .add(new AtAcFormatter(FileEntry.OWNER_ATAC, Adjustment.RIGHT));
            formatters
                .add(new AtAcFormatter(FileEntry.GROUP_ATAC, Adjustment.RIGHT));
            formatters
                .add(new AtAcFormatter(FileEntry.SIZE_ATAC, Adjustment.RIGHT));
            formatters.add(
                new AtAcFormatter(FileEntry.MODIFIED_TIME_ATAC, Adjustment.LEFT)
            );
        }
        formatters.add(new AtAcFormatter(FileEntry.NAME_ATAC, Adjustment.NONE));
        return formatters;
    }

    private void listDir(final FileEntryWithIterator dir, final HshContext hsh,
        final boolean withnprefix, final OutputStyle style) throws Exception {
        if (withnprefix)
            hsh.getStdOut().println("" + dir.getPath() + ":");

        final Iterator<FileEntry> iter = dir.getFilesIterator();
        while (iter.hasNext())
            style.printFile(iter.next(), hsh.getStdOut());

        style.doOutput(hsh.getStdOut(), hsh.getTerminal().getCols());
    }

    /**
     * There are three output styles.
     */
    interface OutputStyle {
        /**
         * Creates a new instance of this OutputStyle, the same constructor is
         * called like was called to create this object.
         * @return a new OutputStyle-object of the same class as this object and
         *  same configuration (constructor call)
         */
        public OutputStyle createInstance();

        /**
         * Prints the output or puts it into a buffer
         * @param fent the file to print
         * @param out target PrintStream
         */
        void printFile(FileEntry fent, PrintStream out);

        /**
         * The COLS_*-Styles require to process a list of filenames. The names
         * are collected while the calls to printFile. And finally printed to
         * the printWriter using this method.
         * @param out target PrintStream
         * @throws Exception
         */
        void doOutput(PrintStream out, int screenwidth) throws Exception;
    }

    /** Print one entry after the other, separated by a separator, ie newline */
    private static class FloatOutputStyle implements OutputStyle {
        private final List<FileEntry> fileentlist = new ArrayList<>();
        private final List<Integer> fieldwidths = new ArrayList<>();
        private final List<AtAcFormatter> formatters;

        /**
         * Creates an floating output style.
         * @param formatters List of formatted fields to display per FileEntry.
         *        If formatterList.size()>1 the minWith of the fields is
         *        adjusted to display all Columns at the same position. Else the
         *        output is done while printFile() without caching the data.
         */
        FloatOutputStyle(final List<AtAcFormatter> formatters) {
            this.formatters = formatters;
        }

        @Override
        public OutputStyle createInstance() {
            return new FloatOutputStyle(formatters);
        }

        @Override
        public void printFile(final FileEntry fent, final PrintStream out) {
            if (formatters.size() > 1) {
                while (fieldwidths.size() < formatters.size())
                    fieldwidths.add(0);
                for (int idx = 0; idx < formatters.size(); idx += 1) {
                    final int fieldlen = formatters.get(idx).get(fent, 0)
                        .length();
                    if (fieldwidths.get(idx) < fieldlen)
                        fieldwidths.set(idx, fieldlen);
                }
                fileentlist.add(fent);
            } else { // simply print the field
                out.println(formatters.get(0).get(fent, 0));
            }
        }

        @Override
        public void doOutput(final PrintStream out, final int screenwidth) {
            if (formatters.size() > 1) {
                for (final FileEntry fent : fileentlist) {
                    for (int idx = 0; idx < formatters.size(); idx++) {
                        if (idx > 0)
                            out.print(' '); // separator
                        out.print(
                            formatters.get(idx).get(fent, fieldwidths.get(idx))
                        );
                    }
                    out.println();
                }
            } else {
                out.println();
            }
        }
    };

    /**
     * Print in columns sorted vertically. Default. Limited to one formatter,
     * see constructor.
     */
    public final static class VerticalOutputStyle implements OutputStyle {
        /**
         * Separator between file names, two blanks
         */
        final static String SEPARATOR = "\b\b";
        /**
         * 
         */
        private final List<FileEntry> files = new ArrayList<>(8);

        /**
         * Length of separator between file names.
         */
        final int separatorlen = SEPARATOR.length();

        private final AtAcFormatter formatter;

        public VerticalOutputStyle(final AtAcFormatter nameformatter) {
            this.formatter = nameformatter;
        }

        @Override
        public OutputStyle createInstance() {
            return new VerticalOutputStyle(formatter);
        }

        @Override
        public void printFile(final FileEntry fent, final PrintStream out) {
            files.add(fent);
        }

        @Override
        public void doOutput(final PrintStream out, final int screenwidth)
            throws Exception {
            if (files.size() == 0)
                return;

            try {
                // find the number of cols by brute force.
                // start at a trivial maximum.
                int cols = Math.min(
                    screenwidth / (separatorlen + 1), files.size()
                );

                ColsFormat colsformat = null;
                for (; cols > 0; cols--) {
                    // compute the number of rows according to cols
                    int numclastrow = files.size() % cols;
                    if (numclastrow == 0)
                        numclastrow = cols;
                    final int rows = (files.size() / cols)
                        + (numclastrow == cols ? 0 : 1);

                    boolean fitsflag = true;
                    colsformat = new ColsFormat(cols, SEPARATOR);
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < cols - 1; col++) {
                            final int dataidx = computeDataIdx(
                                rows, row, col, numclastrow
                            );
                            if (dataidx < files.size()) {
                                colsformat.updateColWidth(
                                    col, formatter.get(files.get(dataidx), 0)
                                        .length()
                                );
                                if (colsformat.getRowWidth() > screenwidth
                                    - 2) { // left blank 2 spaces on right side
                                    fitsflag = false;
                                    break;
                                }
                            }
                        }
                        if (!fitsflag)
                            break;
                    }

                    if (fitsflag)
                        break;
                }
                // cols is now the number of cols to use for output

                int numclastrow = files.size() % cols;
                if (numclastrow == 0)
                    numclastrow = cols;
                final int rows = (files.size() / cols)
                    + (numclastrow == cols ? 0 : 1);

                // finally do the output
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        final int dataidx = computeDataIdx(
                            rows, row, col, numclastrow
                        );
                        if (dataidx < files.size()) {
                            if (col > 0)
                                out.print(SEPARATOR);
                            int mincolwidth = colsformat.getColWidth(col);
                            // on last column do not fill up with blanks if LEFT
                            // or NONE
                            if (col == cols - 1 && formatter
                                .getAdjustment() != Adjustment.RIGHT)
                                mincolwidth = 0;
                            out.print(
                                formatter.get(
                                    files.get(dataidx), col < cols - 1
                                        ? colsformat.getColWidth(col) : 0
                                )
                            );
                        }
                    }
                    out.println();
                }
            } finally {
                files.clear();
            }
        }

        /**
         * Computes the index into the data list for the entry at row row and
         * col col, given that rows rows are used.
         * @param rows
         * @param row
         * @param col
         * @param numColsOneMoreRow
         * @return index into data list
         */
        private int computeDataIdx(final int rows, final int row, final int col,
            final int numclastrow) {
            if (row == rows - 1 && col >= numclastrow)
                return Integer.MAX_VALUE;

            final int fullcols = Math.min(numclastrow, col);
            final int ret = row + fullcols * rows
                + (col > fullcols ? (col - fullcols) * (rows - 1) : 0);
            return ret;
        }

        /**
         * This class encapsulates the format of the columns.
         */
        class ColsFormat {
            /**
             * The length of the separator String.
             */
            private final int seplen;
            /**
             * The colunm widths.
             */
            private final int[] widths;
            /**
             * Sum of column widths.
             */
            private int sumcolwidth;

            /**
             * Only one constructor.
             * @param cols Number of columns
             * @param separator The separator String, usually a single space
             */
            ColsFormat(final int cols, final String separator) {
                this.widths = new int[cols];
                this.seplen = separator.length();
                this.sumcolwidth = 0;
            }

            /**
             * Query the width of the whole row, this is all columns and the
             * separation spaces.
             * @return The width of the row according to widths of the cols and
             *         the len of the separator.
             */
            int getRowWidth() {
                return this.sumcolwidth
                    + (this.widths.length - 1) * this.seplen;
            }

            /**
             * Update the width of a column.
             * @param col The idx of the column to update
             * @param width The new width of the column
             */
            void updateColWidth(final int col, final int width) {
                if (width > this.widths[col]) {
                    this.sumcolwidth = this.sumcolwidth - this.widths[col]
                        + width;
                    this.widths[col] = width;
                }
            }

            /**
             * Query the width of a column.
             * @param idx The idx of that column
             * @return The width of that column
             */
            int getColWidth(final int idx) {
                return this.widths[idx];
            }
        }

    };

    /**
     * Print in columns sorted horizontally.
     */
    private static class HorizontalOutputStyle implements OutputStyle {

        @Override
        public OutputStyle createInstance() {
            return new HorizontalOutputStyle();
        }

        @Override
        public void printFile(final FileEntry fileent, final PrintStream out) {
            throw new RuntimeException("COLS_HORIZONTAL not implemented");
        }

        @Override
        public void doOutput(final PrintStream out, final int screenwidth) {
            throw new RuntimeException("COLS_HORIZONTAL not implemented");
        }
    };
}
