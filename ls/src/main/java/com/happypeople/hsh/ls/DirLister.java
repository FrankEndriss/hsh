package com.happypeople.hsh.ls;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.BlockingQueue;

public class DirLister implements FileVisitor<Path> {

	public static void list(Path dir, BlockingQueue<Path> resultQ) throws IOException, InterruptedException {
		list(dir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, resultQ);
	}

	/** Walks a file tree and reports results to the given resultQ
	 * @param dir see Files.walkFileTree
	 * @param options see Files.walkFileTree
	 * @param recurseDepth see Files.walkFileTree
	 * @param resultQ queue to which all found paths are written
	 * @throws IOException if the tree walk does not succeed
	 * @throws InterruptedException if the writing to the queue fails
	 */
	public static void list(Path dir, EnumSet<FileVisitOption> options, int recurseDepth, BlockingQueue<Path> resultQ) throws IOException, InterruptedException {
		DirLister dirLister=new DirLister(resultQ);
		Files.walkFileTree(dir, options, recurseDepth, dirLister);
		if(dirLister.iEx!=null)
			throw dirLister.iEx;
	}

	private BlockingQueue<Path> resultQ;
	private InterruptedException iEx;

	public DirLister(BlockingQueue<Path> resultQ) {
		this.resultQ=resultQ;
	}

	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1)
	throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
	throws IOException {
		try {
			resultQ.put(arg0);
		} catch (InterruptedException e) {
			iEx=e;
			return FileVisitResult.TERMINATE;
		}
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1)
	throws IOException {
		try {
			resultQ.put(arg0);
		} catch (InterruptedException e) {
			iEx=e;
			return FileVisitResult.TERMINATE;
		}
		return FileVisitResult.CONTINUE;
	}

	public FileVisitResult visitFileFailed(Path arg0, IOException arg1)
	throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
