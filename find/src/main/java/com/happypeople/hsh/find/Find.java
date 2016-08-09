/**
 */
package com.happypeople.hsh.find;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of find command TODO use interface HshCommand instead of main
 * method.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class Find implements HshCmd {
    @Override
    public int execute(final HshContext hsh, final List<String> args)
        throws Exception {
        final List<String> pathlist = new ArrayList<>(16);
        final List<Expr> exprlist = new ArrayList<>(8);
        for (int idx = 1; idx < args.size(); idx += 1) {
            if ("-name".equals(args.get(idx))) {
                exprlist.add(createNameExpr(args.get(idx + 1)));
                break;
            } else {
                pathlist.add(args.get(idx));
            }
        }
        if (pathlist.isEmpty()) {
            pathlist.add(".");
        }
        for (final String path : pathlist) {
            try {
                runRecursive(createFile(path), exprlist, hsh);
            } catch (final IOException excep) {
                excep.printStackTrace(hsh.getStdErr());
            }
        }
        return 0;
    }

    /**
     * Creates a File from a String.
     * @param path The usual path
     * @return A File created from path
     */
    private static File createFile(final String path) {
        return new File(path);
    }

    /**
     * Factory method for NameExpr().
     * @param arg Pattern to search for
     * @return A new NameExpr object
     */
    private static NameExpr createNameExpr(final String arg) {
        return new NameExpr(arg);
    }

    /**
     * Find greatly works recursive.
     * @param file The current path level
     * @param exprlist List of searched expression
     * @param hsh Context of the command execution
     * @throws IOException If one of the file operations throws it
     */
    private static void runRecursive(final File file, final List<Expr> exprlist,
        final HshContext hsh) throws IOException {
        if (!file.exists()) {
            return;
        }
        final Path fpath = file.toPath();
        Files.walkFileTree(
            fpath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
            new MyFileVisitor(exprlist, hsh)
        );
    }

    /**
     * Checks file for a match to any Expr contained in exprList.
     * @param file The File name to check
     * @param exprlist The List of Expr
     * @return True if match, false otherwise
     */
    private static boolean checkExpressions(final File file,
        final List<Expr> exprlist) {
        boolean ret = false;
        for (final Expr expr : exprlist) {
            if (expr.isMatch(file)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    /**
     * FileVisitor implementation to use Files.walkTree.
     */
    private static class MyFileVisitor implements FileVisitor<Path> {

        /**
         * Context of this cmd execution.
         */
        private final HshContext context;

        /**
         * List of Expr to search for.
         */
        private final List<Expr> exprlist;

        /**
         * Only one constructor.
         * @param exprlist List of Expr to search for.
         * @param hsh Context of command execution.
         */
        MyFileVisitor(final List<Expr> exprlist, final HshContext hsh) {
            this.exprlist = exprlist;
            this.context = hsh;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path path,
            final BasicFileAttributes attrs) throws IOException {
            if (checkExpressions(path.toFile(), this.exprlist)) {
                this.context.getStdOut()
                    .println(new StringBuilder().append(path));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path path,
            final BasicFileAttributes attrs) throws IOException {
            if (checkExpressions(path.toFile(), this.exprlist)) {
                this.context.getStdOut()
                    .println(new StringBuilder().append(path));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path path,
            final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path path,
            final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * Functional interface for pattern matching.
     */
    private interface Expr {
        /**
         * Checks for a match like a FileFilter.
         * @param file The File to check
         * @return True if match, else otherwise
         */
        boolean isMatch(File file);
    }

    /**
     * Expr implementation to match File names.
     */
    private static class NameExpr implements Expr {
        /**
         * The Pattern to match.
         */
        private final Pattern pattern;

        /**
         * Only one constructor.
         * @param patternstr Must not be null, see Pattern.
         */
        NameExpr(final String patternstr) {
            this.pattern = Pattern.compile(patternstr);
        }

        @Override
        public boolean isMatch(final File file) {
            return this.pattern.matcher(file.getName()).matches();
        }
    }

}
