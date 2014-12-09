package com.happypeople.hsh.find;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** find implementation */
public class Main {
	public static void main(final String[] args) {

		final List<String> pathList=new ArrayList<String>();
		final List<Expr> exprList=new ArrayList<Expr>();

		for(int i=1; i<args.length; i++) {
			if("-name".equals(args[i])) {
				exprList.add(new NameExpr(args[i+1]));
				break;
			}
			else
				pathList.add(args[i]);
		}
		if(pathList.size()==0)
			pathList.add(".");

		for(final String path : pathList)
			try {
				runRecursive(new File(path), exprList);
			} catch (final IOException e) {
				e.printStackTrace();
			}
	}

	private static void runRecursive(final File file, final List<Expr> exprList) throws IOException {
		if(!file.exists())
			return;

		final Path fpath=file.toPath();

		/*
		if(checkExpressions(file, exprList))
			System.out.println(fpath);
			*/

		//System.out.println("call walkFileTree, file="+file+" path: "+file.toPath());
		Files.walkFileTree(fpath, new FileVisitor<Path>() {

			public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) throws IOException {
				final File file=path.toFile();
				if(checkExpressions(file, exprList))
					System.out.println(fpath.resolve(path));
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFileFailed(final Path file, final IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}

		});


		if(file.isDirectory()) {
			for(final File f : file.listFiles())
				runRecursive(f, exprList);
		}
	}

	private static boolean checkExpressions(final File file, final List<Expr> exprList) {
		for(final Expr expr : exprList)
			if(!expr.isMatch(file))
				return false;
		return true;
	}


	public static void usage() {
		System.err.println("usage: "+Main.class.getName()+" <pathlist> [expression]");
		System.err.println("implemented expressions: -name <pattern>");
	}

	private static interface Expr {
		public boolean isMatch(File file);
	}

	private static class NameExpr implements Expr {
		private final Pattern pattern;
		public NameExpr(final String patternStr) {
			this.pattern=Pattern.compile(patternStr);
		}
		public boolean isMatch(final File file) {
			return pattern.matcher(file.getName()).matches();
		}

	}
}
