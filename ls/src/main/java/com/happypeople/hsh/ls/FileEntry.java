package com.happypeople.hsh.ls;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import com.happypeople.hshutil.util.ConcatIterable;
import com.happypeople.hshutil.util.ConvertedIterable;
import com.happypeople.hshutil.util.OneToOneConverter;

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

	/** Class implementing DirectoryStream<FileEntry> as a wrapper arround an object returned by
	 * Files.newDirectoryStream(...)
	 */
	private class FileEntryStream extends ConvertedIterable<Path, FileEntry> implements DirectoryStream<FileEntry> {
		public FileEntryStream(final Iterable<Path> delegate,
				final DirectoryStream.Filter<Path> ff,
				final OneToOneConverter<Path, FileEntry> converter)
		{
			super(new ConcatIterable<Path>(new SpecialPathEntries(getPath(), ff), delegate), converter);
		}
	}

	private static class SpecialPathEntries implements Iterable<Path> {
		private final Path dir;
		private final DirectoryStream.Filter<Path> ff;
		/** Returns an Iterable<Path> which produces the two paths "." and ".." until these are
		 * filtered by the Filter ff.
		 * @param dir the produces Paths resolve agains dir
		 * @param ff a FileFilter to reduce the produced Paths
		 */
		SpecialPathEntries(final Path dir, final DirectoryStream.Filter<Path> ff) {
			this.dir=Files.isDirectory(dir)?dir:null;
			this.ff=ff;
		}


		@Override
		public Iterator<Path> iterator() {
			final Stack<Path> spezials=new Stack<Path>();
			try {
				final Path path1=dir.resolve(".");
				if(ff.accept(path1))
					spezials.push(path1);
				final Path path2=dir.resolve("..");
				if(ff.accept(path2))
					spezials.push(path1);
			} catch (final IOException e) {
				// ignore
			}

			return new Iterator<Path>() {
				@Override
				public boolean hasNext() {
					return spezials.size()>0;
				}

				@Override
				public Path next() {
					if(spezials.size()>0)
						return spezials.pop();
					throw new NoSuchElementException("called more than two times");
				}

				@Override
				public void remove() {
					// TODO throw the right exception
					throw new RuntimeException("cannot remove something");
				}
			};
		}
	}

	/** See Files.newDirectoryStream(Path, DirectoryStream.Filter<Path>)
	 * @param ff the Filter<Path> to use
	 * @return a new DirectoryStream streaming FileEntries
	 * @throws IOException
	 */
	public DirectoryStream<FileEntry> listFiles(final DirectoryStream.Filter<Path> ff) throws IOException {
		final DirectoryStream<Path> ds= ff==null?
				Files.newDirectoryStream(getPath()):
				Files.newDirectoryStream(getPath(), ff);
		return new FileEntryStream(ds, ff, new OneToOneConverter<Path, FileEntry>() {
			@Override
			public FileEntry convert(final Path input) {
				return new FileEntry(input);
			}
		});
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

		@Override
		public int compare(final FileEntry o1, final FileEntry o2) {
			return atac.get(o1).compareTo(atac.get(o2));
		}
	}

	public final static AttAccessor<String> NAME_ATAC=new AttAccessor<String>() {
		@Override
		public String get(final FileEntry file) {
			return file.getPath().toFile().getName();
		}
	};
	public final static Comparator<FileEntry> NAME_SORT=new AttComparator<String>(NAME_ATAC);


	public final static AttAccessor<FileTimeWrapper> MODIFIED_TIME_ATAC=new AttAccessor<FileTimeWrapper>() {
		@Override
		public FileTimeWrapper get(final FileEntry file) {
			return new FileTimeWrapper(file.getAttrs().lastModifiedTime());
		}
	};

	public final static Comparator<FileEntry> MODIFIED_TIME_SORT=new AttComparator<FileTimeWrapper>(MODIFIED_TIME_ATAC);

	public final static AttAccessor<Long> SIZE_ATAC=new AttAccessor<Long>() {
		@Override
		public Long get(final FileEntry file) {
			return file.getAttrs().size();
		}
	};
	public final static Comparator<FileEntry> SIZE_SORT=new AttComparator<Long>(SIZE_ATAC);

	public final static AttAccessor<String> GROUP_ATAC=new AttAccessor<String>() {
		@Override
		public String get(final FileEntry file) {
			final BasicFileAttributes attrs=file.getAttrs();
			if(attrs instanceof PosixFileAttributes)
				return ((PosixFileAttributes)attrs).group().getName();
			return UNKNOWN;
		}
	};
	public final static Comparator<FileEntry> GROUP_SORT=new AttComparator<String>(GROUP_ATAC);

	public final static AttAccessor<String> OWNER_ATAC=new AttAccessor<String>() {
		@Override
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
		@Override
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

	@Override
	public int compareTo(final FileEntry fileEntry) {
		return getPath().compareTo(fileEntry.getPath());
	}

	@Override
	public String toString() {
		return getPath().toString();
	}
}
