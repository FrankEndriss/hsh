package com.happypeople.hsh.find;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** find implementation */
public class Main {
	public static void main(String[] args) {
		if(args.length<1)
			usage();
		
		final List<String> pathList=new ArrayList<String>();
		final List<Expr> exprList=new ArrayList<Expr>();
		
		for(int i=0; i<args.length; i++) {
			if("-name".equals(args[i])) {
				exprList.add(new NameExpr(args[i+1]));
				break;
			}
			else
				pathList.add(args[i]);
		}
		if(pathList.size()==0)
			pathList.add(".");
		
		for(String path : pathList)
			try {
				runRecursive(new File(path), exprList);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private static void runRecursive(File file, final List<Expr> exprList) throws IOException {
		if(checkExpressions(file, exprList))
			System.out.println(file.getName());
		
		Files.walkFileTree(Paths.get(file.getPath()), new FileVisitor<Path>() {

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return null;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				checkExpressions(file.toFile(), exprList);
				return null;
			}

			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return null;
			}

			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				return null;
			}
			
		});


		if(file.isDirectory()) {
			for(final File f : file.listFiles())
				runRecursive(f, exprList);
		}
	}
	
	private static boolean checkExpressions(File file, List<Expr> exprList) {
		for(Expr expr : exprList)
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
		private Pattern pattern;
		public NameExpr(String patternStr) {
			this.pattern=Pattern.compile(patternStr);
		}
		public boolean isMatch(File file) {
			return pattern.matcher(file.getName()).matches();
		}
		
	}
}
