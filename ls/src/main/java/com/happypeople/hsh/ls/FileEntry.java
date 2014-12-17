package com.happypeople.hsh.ls;

import java.io.FileFilter;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** Encapsulates a File and its Attributes
 */
class FileEntry implements Comparable<FileEntry> {
	private final static List<Class<? extends BasicFileAttributes>> clsList=
			new ArrayList<Class<? extends BasicFileAttributes>>();
	static {
		clsList.add(PosixFileAttributes.class);
		clsList.add(DosFileAttributes.class);
		clsList.add(BasicFileAttributes.class);
	};

	private final Path path;
	private BasicFileAttributes attrs;

	FileEntry(final Path path) {
		if(path==null)
			throw new IllegalArgumentException("must not be null");
		this.path=path;
	}

	public Path getPath() {
		return path;
	}

	public BasicFileAttributes getAttrs() {
		if(attrs==null) {
			for(final Class<? extends BasicFileAttributes> cls : clsList) {
				try {
					attrs=Files.readAttributes(path, cls);
					break;
				}catch(final Exception e) {
					// ignore
				}
			}
		}
		if(attrs==null)
			throw new IllegalStateException("cannot read file attributes");
		return attrs;
	}

	public void listFiles(final FileFilter ff, BlockingQueue<FileEntry> resultQ) throws InterruptedException {
		final BlockingQueue<Path> lQ=new LinkedBlockingQueue<Path>();
		final Path finiMarker=Paths.get("blah");

		// async execution is needed to wrap the Path-objects into FileEntry-objects.
		// This could be better made with a wrapper-Class for BlockingQueue.
		// Or a queue-implementation with different Types for put() and take(), and
		// an job, which is ran on every transferred object. (see Streams, filters)
		// See ExecutorQueue / WrappingQueue
		new Thread() {
			public void run() {
				try {
					DirLister.list(path, EnumSet.noneOf(FileVisitOption.class), 1, lQ);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					try {
						lQ.put(finiMarker);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		Path lPath=null;
		while((lPath=lQ.take())!=finiMarker)
			if(ff==null || ff.accept(lPath.toFile()))
				resultQ.put(new FileEntry(lPath));
	}


	/** File attribute accessors */
	public static interface AttAccessor<C extends Comparable<C>> {
		public final static String UNKNOWN="<unknown>";
		public C get(FileEntry file);
	}

	public static class AttComparator<C extends Comparable<C>> implements Comparator<FileEntry> {
		private final AttAccessor<C> atac;
		AttComparator(final AttAccessor<C> atac) {
			this.atac=atac;
		}

		public int compare(final FileEntry o1, final FileEntry o2) {
			return atac.get(o1).compareTo(atac.get(o2));
		}
	}

	public final static AttAccessor<String> NAME_ATAC=new AttAccessor<String>() {
		public String get(final FileEntry file) {
			return file.getPath().toFile().getName();
		}
	};
	public final static Comparator<FileEntry> NAME_SORT=new AttComparator<String>(NAME_ATAC);


	public final static AttAccessor<FileTimeWrapper> MODIFIED_TIME_ATAC=new AttAccessor<FileTimeWrapper>() {
		public FileTimeWrapper get(final FileEntry file) {
			return new FileTimeWrapper(file.getAttrs().lastModifiedTime());
		}
	};

	public final static Comparator<FileEntry> MODIFIED_TIME_SORT=new AttComparator<FileTimeWrapper>(MODIFIED_TIME_ATAC);

	public final static AttAccessor<Long> SIZE_ATAC=new AttAccessor<Long>() {
		public Long get(final FileEntry file) {
			return file.getAttrs().size();
		}
	};
	public final static Comparator<FileEntry> SIZE_SORT=new AttComparator<Long>(SIZE_ATAC);

	public final static AttAccessor<String> GROUP_ATAC=new AttAccessor<String>() {
		public String get(final FileEntry file) {
			final BasicFileAttributes attrs=file.getAttrs();
			if(attrs instanceof PosixFileAttributes)
				return ((PosixFileAttributes)attrs).group().getName();
			return UNKNOWN;
		}
	};
	public final static Comparator<FileEntry> GROUP_SORT=new AttComparator<String>(GROUP_ATAC);

	public final static AttAccessor<String> OWNER_ATAC=new AttAccessor<String>() {
		public String get(final FileEntry file) {
			try {
				final FileOwnerAttributeView ownerAttributeView =
	        		Files.getFileAttributeView(file.getPath(), FileOwnerAttributeView.class);
				return ownerAttributeView.getOwner().getName();
			}catch(final Exception e) {
				return UNKNOWN;
			}
		}
	};
	public final static Comparator<FileEntry> OWNER_SORT=new AttComparator<String>(OWNER_ATAC);

	public final static AttAccessor<String> PERM_ATAC=new AttAccessor<String>() {
		public String get(final FileEntry file) {
			final StringBuilder perms=new StringBuilder(Files.isDirectory(file.getPath())?"d":"-");
			final BasicFileAttributes attrs=file.getAttrs();
			if(attrs instanceof PosixFileAttributes) {
				final PosixFileAttributes pattrs=(PosixFileAttributes)attrs;
				final Set<PosixFilePermission> permSet=pattrs.permissions();
				perms.append(permSet.contains(PosixFilePermission.OWNER_READ)?"r":"-")
					.append(permSet.contains(PosixFilePermission.OWNER_WRITE)?"w":"-")
					.append(permSet.contains(PosixFilePermission.OWNER_EXECUTE)?"x":"-")
					.append(permSet.contains(PosixFilePermission.GROUP_READ)?"r":"-")
					.append(permSet.contains(PosixFilePermission.GROUP_WRITE)?"w":"-")
					.append(permSet.contains(PosixFilePermission.GROUP_EXECUTE)?"x":"-")
					.append(permSet.contains(PosixFilePermission.OTHERS_READ)?"r":"-")
					.append(permSet.contains(PosixFilePermission.OTHERS_WRITE)?"w":"-")
					.append(permSet.contains(PosixFilePermission.OTHERS_EXECUTE)?"x":"-");
				return perms.toString();
			} else {
				perms.append(Files.isReadable(file.getPath())?"r":"-")
					.append(Files.isWritable(file.getPath())?"w":"-")
					.append(Files.isExecutable(file.getPath())?"x":"-");
			}
			return perms.toString();
		}
	};
	public final static Comparator<FileEntry> PERM_SORT=new AttComparator<String>(PERM_ATAC);

	public int compareTo(final FileEntry fileEntry) {
		return getPath().compareTo(fileEntry.getPath());
	}

	@Override
	public String toString() {
		return getPath().toString();
	}
}
