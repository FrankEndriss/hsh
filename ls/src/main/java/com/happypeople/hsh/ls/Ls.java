package com.happypeople.hsh.ls;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hshutil.util.AsyncIterator;
import com.happypeople.hshutil.util.CombinedComparator;

/** More or less Posix implementation of ls
 */
public class Ls implements HshCmd {
	final static boolean DEBUG=false;

	public static void main(final String[] margs) throws IOException {
		final ArrayList<String> args=new ArrayList<String>();
		args.add("ls");
		args.addAll(Arrays.asList(margs));
		try {
			new Ls().execute(null, args);
		}catch(final Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public int execute(final HshContext hsh, final ArrayList<String> args) throws Exception {
		final String lcmd=args.remove(0);

		final Options options=new Options();
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
		} catch (final ParseException e) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printUsage(new PrintWriter(hsh.getStdErr()), hsh.getCols(), lcmd, options);
			return 1;
		}

		final List<AtAcFormatter> formatterList = createFormatter(cmd);
		final OutputStyle outputStyle = createOutputStyle(cmd, formatterList);
		final DirectoryStream.Filter<Path> fileFilter = createFileFilter(cmd);
		final List<Comparator<? super FileEntry>> sorting = creatingSorting(cmd);
		Comparator<FileEntry> comp=null;
		if(sorting.size()>0) {
			comp=new CombinedComparator<FileEntry>(sorting);
		}

		final List<FileEntry> fileEntryList=new ArrayList<FileEntry>();
		// all other args are filenames
		for(final String arg : cmd.getArgs())
			fileEntryList.add(new FileEntry(Paths.get(arg)));

		// default argument
		if(fileEntryList.size()==0)
			fileEntryList.add(new FileEntry(Paths.get(".")));

		// arguments which are directly printed, ie files
		final List<FileEntry> printedArgs=new ArrayList<FileEntry>();
		// arguments which are listed, ie directories.
		final List<FileEntry> listedArgs=new ArrayList<FileEntry>();
		if(cmd.hasOption("d"))
			printedArgs.addAll(fileEntryList);
		else
			for(final FileEntry fe : fileEntryList)
				(Files.isDirectory(fe.getPath())?listedArgs:printedArgs).add(fe);

		final boolean recursive=cmd.hasOption("R");
		final boolean withNamePrefix=fileEntryList.size()>1 || recursive;

		boolean firstBlock=true;
		if(printedArgs.size()>0) {
			firstBlock=false;
			for(final FileEntry fileEntry : printedArgs)
				if(Files.exists(fileEntry.getPath()))
					outputStyle.printFile(fileEntry, hsh.getStdOut());
				else
					hsh.getStdOut().println("does not exists: "+fileEntry);
			outputStyle.doOutput(hsh.getStdOut(), hsh.getCols());
		}

		if(listedArgs.size()>0) {
			final Iterator<FileEntryWithIterator> dirsToList=
					getDirListing(listedArgs.iterator(), fileFilter, recursive?Integer.MAX_VALUE:0, comp);

			while(dirsToList.hasNext()) {
				final FileEntryWithIterator fileEntry=dirsToList.next();
				if(!firstBlock)
					hsh.getStdOut().println();
				firstBlock=false;

				if(Files.exists(fileEntry.getPath())) {
					listDir(fileEntry, hsh, withNamePrefix, outputStyle.createInstance());
				} else
					hsh.getStdOut().println("does not exists: "+fileEntry);
			}
		}

		return 0;
	}

	/** Lists all dirs given in the argument and all sub-dirs of that dirs, in depth-first order.
	 * This is implemented as running in an newly created Thread.
	 * @param dirs the dirs to walk the file trees
	 * @return an Iterator<FileEntry> over the result of the search.
	 */
	private Iterator<FileEntryWithIterator> getDirListing(
			final Iterator<FileEntry> dirs,
			final DirectoryStream.Filter<Path> ff,
			final int recursionDepth,
			final Comparator<FileEntry> comp) {
		final AsyncIterator<FileEntryWithIterator> ret=new AsyncIterator<FileEntryWithIterator>();
		new Thread() {
			private FileEntryWithIterator currentDir;
			@Override
			public void run() {
				try {
					while(dirs.hasNext()) {
						final FileEntry fe=dirs.next();
						try {
							walkFileTree(fe, ff, recursionDepth, comp, new LsFileVisitor() {

								@Override
								public void preVisitDirectory(final FileEntry dir) {
									ret.offer(currentDir=new FileEntryWithIterator(dir.getPath(), new AsyncIterator<FileEntry>()));
								}

								@Override
								public void visitFile(final FileEntry file) {
									currentDir.getFilesIterator().offer(file);
								}

								@Override
								public void postVisitDirectory(final FileEntry dir) {
									currentDir.getFilesIterator().close();
									currentDir=null;
								}
							});
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				}finally{
					ret.close();
				}
			}

			/** Does a breath-first (listing-first) traversal of the file tree rooted at path.
			 * This is, for all directories in the tree, first preVisitDirecotry is called, then for all
			 * entries in that director visitFile(), end then postVisitDirectory().
			 * If root is not a directory, nothing happens.
			 * @param path root of the tree to visit
			 * @param ff the Filter to use, see Files.newDirectoryStream(...)
			 * @param recursionDepth if>0 this method will call itself with an decremented recursionDepth
			 * for all subdirs
			 * @param comp if !=null files are listed (and dirs are recursed) in order of the comparator
			 * @param fileVisitor the callback
			 */
			private void walkFileTree(final FileEntry root,
					final DirectoryStream.Filter<Path> ff,
					final int recursionDepth,
					final Comparator<FileEntry> comp,
					final LsFileVisitor fileVisitor)
							throws IOException {
				if(!Files.isDirectory(root.getPath()))
					throw new NotDirectoryException("not directory: "+root.getPath());

				fileVisitor.preVisitDirectory(root);
				// list the entries of the directory collecting subdirs, then recurse in all subdirs
				final List<FileEntry> subdirs=new ArrayList<FileEntry>();
				final boolean recurse=recursionDepth>0;
				try(DirectoryStream<FileEntry> stream=root.listFiles(ff)) {
					List<FileEntry> sortedStream=null;
					if(comp!=null) {
						sortedStream=new ArrayList<FileEntry>();
						for(final FileEntry fe : stream)
							sortedStream.add(fe);
						Collections.sort(sortedStream, comp);
					}
					for(final FileEntry fe : sortedStream!=null?sortedStream:stream) {
						fileVisitor.visitFile(fe);
						if(recurse && Files.isDirectory(fe.getPath()))
							subdirs.add(fe);
					}
				}
				fileVisitor.postVisitDirectory(root);

				if(recurse)
					for(final FileEntry fe : subdirs)
						walkFileTree(fe, ff, recursionDepth-1, comp, fileVisitor);
			}
		}.start();
		return ret;
	}

	private interface LsFileVisitor {
		public void preVisitDirectory(final FileEntry dir);
		public void visitFile(final FileEntry file);
		public void postVisitDirectory(final FileEntry dir);
	}

	private List<Comparator<? super FileEntry>> creatingSorting(final CommandLine options) {
		final List<Comparator<? super FileEntry>> sorting=new ArrayList<Comparator<? super FileEntry>>();
		// TODO implement
		sorting.add(FileEntry.NAME_SORT);
		return sorting;
	}

	/** Creates the overall filter to controll what to list in directory listings
	 * @param cmd the command options
	 * @return a usable DirectoryStream.Filter<Path>
	 */
	private DirectoryStream.Filter<Path> createFileFilter(final CommandLine cmd) {
		if(cmd.hasOption("a"))
			return new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(final Path entry) throws IOException {
					return true;
				}
			};
		else if(cmd.hasOption("A"))
			return new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(final Path path) {
					final String lName=path.getFileName().toString();
					return ! (".".equals(lName) || "..".equals(lName));
				}
			};
		else
			return new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(final Path path) throws IOException {
					return !Files.isHidden(path);
				}
			};
	}

	/** Creates the OutputStyle.
	 * @param cmd the command options
	 * @param formatterList the formatter to do the output per file
	 * @return a new OutputStyle according to the options
	 */
	private OutputStyle createOutputStyle(final CommandLine cmd, final List<AtAcFormatter> formatterList) {
		OutputStyle outputStyle=null;
		if(cmd.hasOption("l") || cmd.hasOption("1"))
			outputStyle=new FloatOutputStyle(formatterList);
		else
			outputStyle=new VerticalOutputStyle(new AtAcFormatter(FileEntry.NAME_ATAC, Adjustment.LEFT));
		return outputStyle;
	}

	/** Creates the list of AtAcFormatters. This list is used to display the attributes
	 * of a File.
	 * @param cmd the commandline options
	 * @return a new List<AtAcFormatter> useable to format the output per File
	 */
	private List<AtAcFormatter> createFormatter(final CommandLine cmd) {
		// TODO implement other options
		final List<AtAcFormatter> formatterList=new ArrayList<AtAcFormatter>();
		if(cmd.hasOption("l")) {
			formatterList.add(new AtAcFormatter(FileEntry.PERM_ATAC, Adjustment.LEFT));
			formatterList.add(new AtAcFormatter(FileEntry.OWNER_ATAC, Adjustment.RIGHT));
			formatterList.add(new AtAcFormatter(FileEntry.GROUP_ATAC, Adjustment.RIGHT));
			formatterList.add(new AtAcFormatter(FileEntry.SIZE_ATAC, Adjustment.RIGHT));
			formatterList.add(new AtAcFormatter(FileEntry.MODIFIED_TIME_ATAC, Adjustment.LEFT));
		}
		formatterList.add(new AtAcFormatter(FileEntry.NAME_ATAC, Adjustment.NONE));
		return formatterList;
	}

	private void listDir(final FileEntryWithIterator dir,
			final HshContext hsh,
			final boolean withNamePrefix,
			final OutputStyle style)
	throws Exception {
		if(withNamePrefix)
			hsh.getStdOut().println(""+dir.getPath()+":");

		final Iterator<FileEntry> it=dir.getFilesIterator();
		while(it.hasNext())
			style.printFile(it.next(), hsh.getStdOut());

		style.doOutput(hsh.getStdOut(), hsh.getCols());
	}

	/** There are three output styles */
	interface OutputStyle {

		/** Creates a new instance of this OutputStyle, the same constructor is called like was called to
		 * create this object.
		 * @return a new OutputStyle-object of the same class as this object and same configuration (constructor call)
		 */
		public OutputStyle createInstance();

		/** Prints the output or puts it into a buffer
		 * @param f the file to print
		 * @param pw target PrintStream
		 */
		public void printFile(FileEntry f, PrintStream pw);

		/** The COLS_*-Styles require to process a list of filenames. The names
		 * are collected while the calls to printFile. And finally printed to
		 * the printWriter using this method.
		 * @param pw target PrintStream
		 * @throws Exception
		 */
		public void doOutput(PrintStream pw, int screenWidth) throws Exception;
	}

	/** Print one entry after the other, separated by a separator, ie newline */
	private static class FloatOutputStyle implements OutputStyle {
		private final List<FileEntry> fileEntryList=new ArrayList<FileEntry>();
		private final List<Integer> fieldWidths=new ArrayList<Integer>();
		private final List<AtAcFormatter> formatterList;

		/** Creates an floating output style.
		 * @param formatterList List of formatted fields to display per FileEntry.
		 * If formatterList.size()>1 the minWith of the fields is adjusted to
		 * display all Columns at the same position.
		 * Else the output is done while printFile() without caching the data.
		 */
		public FloatOutputStyle(final List<AtAcFormatter> formatterList) {
			this.formatterList=formatterList;
		}

		@Override
		public OutputStyle createInstance() {
			return new FloatOutputStyle(formatterList);
		}

		@Override
		public void printFile(final FileEntry fe, final PrintStream pw) {
			if(formatterList.size()>1) {
				while(fieldWidths.size()<formatterList.size())
					fieldWidths.add(0);
				for(int i=0; i<formatterList.size(); i++) {
					final int fieldLen=formatterList.get(i).get(fe, 0).length();
					if(fieldWidths.get(i)<fieldLen)
						fieldWidths.set(i, fieldLen);
				}
				fileEntryList.add(fe);
			} else // simply print the field
				pw.println(formatterList.get(0).get(fe, 0));
		}

		@Override
		public void doOutput(final PrintStream pw, final int screenWidth) {
			if(formatterList.size()>1) {
				for(final FileEntry fe : fileEntryList) {
					for(int i=0; i<formatterList.size(); i++) {
						if(i>0)
							pw.print(' ');	// separator
						pw.print(formatterList.get(i).get(fe, fieldWidths.get(i)));
					}
					pw.println();
				}
			} else
				pw.println();
		}
	};

	/** Print in columns sorted vertically. Default. Limited to one formatter, see constructor. */
	public final static class VerticalOutputStyle implements OutputStyle {
		final List<FileEntry> files=new ArrayList<FileEntry>();

		/** separator between file names, two blanks */
		final static String SEPARATOR="  ";
		/** length of separator between file names */
		final int SEPARATOR_LEN=SEPARATOR.length();

		private final AtAcFormatter formatter;

		public VerticalOutputStyle(final AtAcFormatter nameFormatter) {
			this.formatter=nameFormatter;
		}

		@Override
		public OutputStyle createInstance() {
			return new VerticalOutputStyle(formatter);
		}

		@Override
		public void printFile(final FileEntry fe, final PrintStream pw) {
			files.add(fe);
		}

		@Override
		public void doOutput(final PrintStream pw, final int screenWidth) throws Exception {
			if(files.size()==0)
				return;

			try {
				// find the number of cols by brute force.
				// start at a trivial maximum.
				int cols=Math.min(screenWidth/(SEPARATOR_LEN+1), files.size());

				ColsFormat colsFormat=null;
				for(; cols>0; cols--) {
					if(DEBUG)
						System.err.println("check cols: "+cols);
					// compute the number of rows according to cols
					int numColsLastRow=files.size()%cols;
					if(numColsLastRow==0)
						numColsLastRow=cols;
					final int rows=(files.size()/cols)+(numColsLastRow==cols?0:1);

					boolean fitsFlag=true;
					colsFormat=new ColsFormat(cols, SEPARATOR);
					for(int row=0; row<rows; row++) {
						for(int col=0; col<cols-1; col++) {
							final int dataidx=computeDataIdx(rows, row, col, numColsLastRow);
							if(dataidx<files.size()) { // check dataidx caused by last row
								colsFormat.updateColWidth(col, formatter.get(files.get(dataidx), 0).length());
								if(colsFormat.getRowWidth()>screenWidth-2) { // left blank 2 spaces on right side
									fitsFlag=false;
									break;
								}
							}
						}
						if(!fitsFlag)
							break;
					}

					if(fitsFlag)
						break;
				}
				// cols is now the number of cols to use for output

				int numColsLastRow=files.size()%cols;
				if(numColsLastRow==0)
					numColsLastRow=cols;
				final int rows=(files.size()/cols)+(numColsLastRow==cols?0:1);

				// finally do the output
				for(int row=0; row<rows; row++) {
					for(int col=0; col<cols; col++) {
						final int dataidx=computeDataIdx(rows, row, col, numColsLastRow);
						if(dataidx<files.size()) {
							if(col>0)
								pw.print(SEPARATOR);
							int minColWidth=colsFormat.getColWidth(col);
							// on last column do not fill up with blanks if LEFT or NONE
							if(col==cols-1 && formatter.getAdjustment()!=Adjustment.RIGHT)
								minColWidth=0;
							pw.print(formatter.get(files.get(dataidx), col<cols-1?colsFormat.getColWidth(col):0));
						}
					}
					pw.println();
				}
			}catch(final Exception e) {
				throw e;
			}finally {
				files.clear();
			}
		}

		/** Computes the index into the data list for the entry at row row and col col, given that
		 * rows rows are used.
		 * @param rows
		 * @param row
		 * @param col
		 * @param numColsOneMoreRow
		 * @return index into data list
		 */
		private int computeDataIdx(final int rows, final int row, final int col, final int numColsLastRow) {
			if(row==rows-1 && col>=numColsLastRow)
				return Integer.MAX_VALUE;

			final int fullCols=Math.min(numColsLastRow, col);
			final int ret=row+ fullCols*rows + (col>fullCols? (col-fullCols)*(rows-1) : 0);
			//final int ret=(rows-1)*col + Math.min(numColsLastRow, col) + row;
			if(DEBUG)
				System.err.println("rows:"+rows+" row:"+row+" col:"+col+" numOnMore:"+numColsLastRow+" datalen:"+files.size()+" ret="+ret);
			return ret;
		}


		/** This class encapsulates the format of the columns.
		 */
		class ColsFormat {
			final int cols;
			final int sepLen;
			final int[] widths;
			int sumColWidth=0;

			ColsFormat(final int cols, final String separator) {
				this.cols=cols;
				this.widths=new int[cols];
				this.sepLen=separator.length();
			}

			/** @return the width of the row according to widths of the cols and the len of the
			 * separator.
			 */
			int getRowWidth() {
				return sumColWidth + (cols-1)*sepLen;
			}

			void updateColWidth(final int col, final int width) {
				if(width>widths[col]) {
					sumColWidth=sumColWidth-widths[col]+width;
					widths[col]=width;
				}
			}

			int getColWidth(final int idx) {
				return widths[idx];
			}
		}

	};

	/** Print in columns sorted horizontally. */
	private static class HorizontalOutputStyle implements OutputStyle {

		@Override
		public OutputStyle createInstance() {
			return new HorizontalOutputStyle();
		}
		@Override
		public void printFile(final FileEntry fe, final PrintStream pw) {
			throw new RuntimeException("COLS_HORIZONTAL not implemented");
		}

		@Override
		public void doOutput(final PrintStream pw, final int screenWidth) {
			throw new RuntimeException("COLS_HORIZONTAL not implemented");
		}
	};
}
