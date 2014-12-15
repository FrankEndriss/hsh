package com.happypeople.hsh.ls;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** Encapsulates a File and its Attributes
 */
public class FileEntry implements Comparable<FileEntry> {
	private final static List<Class<? extends BasicFileAttributes>> clsList=
			new ArrayList<Class<? extends BasicFileAttributes>>();
	static {
		clsList.add(PosixFileAttributes.class);
		clsList.add(DosFileAttributes.class);
		clsList.add(BasicFileAttributes.class);
	};

	private final File file;
	private BasicFileAttributes attrs;

	FileEntry(final File file) {
		if(file==null)
			throw new IllegalArgumentException("must not be null");
		this.file=file;
	}

	public File getFile() {
		return file;
	}

	public BasicFileAttributes getAttrs() {
		if(attrs==null) {
			for(final Class<? extends BasicFileAttributes> cls : clsList) {
				try {
					attrs=Files.readAttributes(file.toPath(), cls);
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

	public List<FileEntry> listFiles(final FileFilter ff) {
		final List<FileEntry> list=new ArrayList<FileEntry>();
		for(final File file : getFile().listFiles(ff))
			list.add(new FileEntry(file));
		return list;
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
			return file.getFile().getName();
		}
	};
	public final static Comparator<FileEntry> NAME_SORT=new AttComparator<String>(NAME_ATAC);

	public final static DateFormat DEFAULT_DATE_FORMAT=new SimpleDateFormat("d M HH:mm");
	public final static AttAccessor<FileTime> MODIFIED_TIME_ATAC=new AttAccessor<FileTime>() {
		public FileTime get(final FileEntry file) {
			return file.getAttrs().lastModifiedTime();
		}
	};
	public final static Comparator<FileEntry> MODIFIED_TIME_SORT=new AttComparator<FileTime>(MODIFIED_TIME_ATAC);

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
			final BasicFileAttributes attrs=file.getAttrs();
			if(attrs instanceof PosixFileAttributes)
				return ((PosixFileAttributes)attrs).owner().getName();
			return UNKNOWN;
		}
	};
	public final static Comparator<FileEntry> OWNER_SORT=new AttComparator<String>(OWNER_ATAC);

	public final static AttAccessor<String> PERM_ATAC=new AttAccessor<String>() {
		public String get(final FileEntry file) {
			final StringBuilder perms=new StringBuilder(file.getFile().isDirectory()?"d":"-");
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
				perms.append(file.getFile().canRead()?"r":"-")
					.append(file.getFile().canWrite()?"w":"-")
					.append(file.getFile().canExecute()?"x":"-");
			}
			return perms.toString();
		}
	};
	public final static Comparator<FileEntry> PERM_SORT=new AttComparator<String>(PERM_ATAC);

	public int compareTo(final FileEntry fileEntry) {
		return getFile().compareTo(fileEntry.getFile());
	}
	
	public String toString() {
		return file.toString();
	}
}
