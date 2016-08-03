/**
 */
package com.happypeople.hsh.find;

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
public final class Find {
    /**
     * Utility classes should not have constructor.
     */
    private Find() {
    }

    /**
     * Standard main TODO substitude to execute() and implement HshCmd.
     * @param args Standard argument vector
     */
    public static void main(final String[] args) {

        final List<String> pathlist = new ArrayList<>();
        final List<Expr> exprlist = new ArrayList<>();

        for (int idx = 1; idx < args.length; idx += 1) {
            if ("-name".equals(args[idx])) {
                exprlist.add(new NameExpr(args[idx + 1]));
                break;
            } else {
                pathlist.add(args[idx]);
            }
        }
        if (pathlist.isEmpty()) {
            pathlist.add(".");
        }

        for (final String path : pathlist) {
            try {
                runRecursive(new File(path), exprlist);
            } catch (final IOException excep) {
                excep.printStackTrace();
            }
        }
    }

    /**
     * Find greatly works recursive.
     * @param file The current path level
     * @param exprlist List of searched expression
     * @throws IOException If one of the file operations throws it
     */
    private static void runRecursive(final File file, final List<Expr> exprlist)
        throws IOException {
        if (!file.exists()) {
            return;
        }

        final Path fpath = file.toPath();

        Files.walkFileTree(
            fpath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE,
            new MyFileVisitor(exprlist)
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
     * Standard usage() implementation.
     */
    public static void usage() {
        System.err.println(
            "usage: " + Find.class.getName() + " <pathlist> [expression]"
        );
        System.err.println("implemented expressions: -name <pattern>");
    }

    /**
     * FileVisitor implementation to use Files.walkTree.
     */
    private static class MyFileVisitor implements FileVisitor<Path> {

        /**
         * List of Expr to search for.
         */
        private List<Expr> exprlist;

        /**
         * Only one constructor.
         * @param exprlist List of Expr to search for.
         */
        MyFileVisitor(final List<Expr> exprlist) {
            this.exprlist = exprlist;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path path,
            final BasicFileAttributes attrs) throws IOException {
            if (checkExpressions(path.toFile(), exprlist)) {
                System.out.println("" + path);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path path,
            final BasicFileAttributes attrs) throws IOException {
            if (checkExpressions(path.toFile(), exprlist)) {
                System.out.println("" + path);
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
