package com.happypeople.hsh.ls;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.ls.FileEntry.AttAccessor;

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

		// Output style
		OutputStyle outputStyle=COLS_VERTICAL;
		if(cmd.hasOption("l") || cmd.hasOption("1"))
			outputStyle=FLOAT;

		// what to list in dir arguments
		FileFilter fileFilter=null;
		if(cmd.hasOption("a"))
			fileFilter=new FileFilter() {
				public boolean accept(final File pathname) {
					return true;
				}
			};
		else if(cmd.hasOption("A"))
			fileFilter=new FileFilter() {
				public boolean accept(final File pathname) {
					return ! (".".equals(pathname.getName()) || "..".equals(pathname.getName()));
				}
			};
		else
			fileFilter=new FileFilter() {
				public boolean accept(final File pathname) {
					return !pathname.getName().startsWith(".");
				}
			};

		// all other args are filenames
		final List<String> fargs=new ArrayList<String>(Arrays.asList(cmd.getArgs()));
		final List<AttAccessor<?>> accList=new ArrayList<AttAccessor<?>>();

		// TODO implement other options
		if(cmd.hasOption("l")) {
			accList.add(FileEntry.PERM_ATAC);
			accList.add(FileEntry.OWNER_ATAC);
			accList.add(FileEntry.GROUP_ATAC);
			accList.add(FileEntry.SIZE_ATAC);
			accList.add(FileEntry.MODIFIED_TIME_ATAC);
		}

		accList.add(FileEntry.NAME_ATAC);

		final List<Comparator<? super FileEntry>> sortList=new ArrayList<Comparator<? super FileEntry>>();
		sortList.add(FileEntry.NAME_SORT);

		if(fargs.size()==0)
			fargs.add(".");

		final List<FileEntry> fileEntryList=new ArrayList<FileEntry>();
		for(final String arg : fargs)
			fileEntryList.add(new FileEntry(new File(arg)));

		final Comparator<FileEntry> comp=new Comparator<FileEntry>() {
			public int compare(final FileEntry o1, final FileEntry o2) {
				for(final Comparator<? super FileEntry> c : sortList) {
					final int res=c.compare(o1, o2);
					if(res!=0)
						return res;
				}
				return 0;
			}
		};
		Collections.sort(fileEntryList, comp);

		final boolean withNamePrefix=fargs.size()>1 || cmd.hasOption("R");

		boolean first=true;
		for(final FileEntry fileEntry : fileEntryList) {
			if(!first)
				hsh.getStdOut().println();
			if(fileEntry.getFile().exists()) {
				if(fileEntry.getFile().isDirectory()) {
					listDir(fileEntry, hsh, withNamePrefix, outputStyle, fileFilter, accList, comp);
				} else {
					outputStyle.printFile(fileEntry, hsh.getStdOut(), accList);
				}
			} else
				System.out.println("does not exists: "+fileEntry);
			outputStyle.doOutput(hsh.getStdOut(), hsh.getCols());
			first=false;
		}

		return 0;
	}

	private void listDir(final FileEntry dir, final HshContext hsh, final boolean withNamePrefix, final OutputStyle style,
			final FileFilter fileFilter, final List<AttAccessor<?>> accessors, final Comparator<FileEntry> comp)
	throws Exception {
		if(withNamePrefix)
			hsh.getStdOut().println(FileEntry.NAME_ATAC.get(dir)+":");
		final List<FileEntry> dirents=dir.listFiles(fileFilter);
		Collections.sort(dirents, comp);
		for(final FileEntry fe : dirents)
			style.printFile(fe, hsh.getStdOut(), accessors);
		style.doOutput(hsh.getStdOut(), hsh.getCols());
	}

	/** There are three output styles */
	private static interface OutputStyle {
		/** Prints the output or puts it into a buffer
		 * @param f the file to print
		 * @param pw target PrintStream
		 * @param attAccessors fields to print
		 * @param sort indexes into attAccessors of sorting
		 */
		public void printFile(FileEntry f, PrintStream pw, List<AttAccessor<?>> attAccessors);

		/** The COLS_*-Styles require to process a list of filenames. The names
		 * are collected while the calls to printFile. And finally printed to
		 * the printWriter using this method.
		 * @param pw target PrintStream
		 * @throws Exception
		 */
		public void doOutput(PrintStream pw, int screenWidth) throws Exception;
	}

	private static String getOutputString(final FileEntry fe, final List<AttAccessor<?>> attAccessors) {
		final StringBuilder sb=new StringBuilder();
		final boolean first=true;
		for(final AttAccessor<?> atac : attAccessors) {
			if(!first)
				sb.append(" ");
			sb.append(atac.get(fe));
		}
		return sb.toString();
	}

	/** Print one entry after the other, separated by a separator, ie newline */
	private static OutputStyle FLOAT=new OutputStyle() {
		private List<AttAccessor<?>> attAccessorList;
		private final List<FileEntry> fileEntryList=new ArrayList<FileEntry>();
		private final List<Integer> fieldWidths=new ArrayList<Integer>();

		public void printFile(final FileEntry fe, final PrintStream pw, final List<AttAccessor<?>> attAccessors) {
			while(fieldWidths.size()<attAccessors.size())
				fieldWidths.add(0);
			for(int i=0; i<attAccessors.size(); i++) {
				final int fieldLen=(""+attAccessors.get(i).get(fe)).length();
				if(fieldWidths.get(i)<fieldLen)
					fieldWidths.set(i, fieldLen);
			}
			attAccessorList=attAccessors;
			fileEntryList.add(fe);
		}

		public void doOutput(final PrintStream pw, final int screenWidth) {
			for(final FileEntry fe : fileEntryList) {
				for(int i=0; i<attAccessorList.size(); i++) {
					if(i>0)
						pw.print(' ');	// separator
					printWidth(pw, fieldWidths.get(i), ""+attAccessorList.get(i).get(fe));
				}
				pw.println();
			}
			attAccessorList=null;
			fileEntryList.clear();
			fieldWidths.clear();
		}
	};

	/** Print in columns sorted vertically. Default.*/
	private static OutputStyle COLS_VERTICAL=new OutputStyle() {
		final List<String> names=new ArrayList<String>();

		/** separator between file names */
		final static String SEPARATOR="  ";
		/** length of separator between file names */
		final int SEPARATOR_LEN=SEPARATOR.length();

		public void printFile(final FileEntry fe, final PrintStream pw, final List<AttAccessor<?>> attAccessors) {
			// ignore attAccessors
			final String s=fe.getFile().getName();
			names.add(s);
		}

		public void doOutput(final PrintStream pw, final int screenWidth) throws Exception {
			if(names.size()==0)
				return;

			try {
				// find the number of cols by brute force.
				// start at a trivial maximum.
				int cols=Math.min(screenWidth/(SEPARATOR_LEN+1), names.size());

				ColsFormat colsFormat=null;
				for(; cols>0; cols--) {
					if(DEBUG)
						System.err.println("check cols: "+cols);
					// compute the number of rows according to cols
					int numColsLastRow=names.size()%cols;
					if(numColsLastRow==0)
						numColsLastRow=cols;
					final int rows=(names.size()/cols)+(numColsLastRow==cols?0:1);

					boolean fitsFlag=true;
					colsFormat=new ColsFormat(cols, SEPARATOR);
					for(int row=0; row<rows; row++) {
						for(int col=0; col<cols; col++) {
							final int dataidx=computeDataIdx(rows, row, col, numColsLastRow);
							if(dataidx<names.size()) { // check dataidx caused by last row
								colsFormat.updateColWidth(col, names.get(dataidx).length());
								if(colsFormat.getRowWidth()>screenWidth) {
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

				int numColsLastRow=names.size()%cols;
				if(numColsLastRow==0)
					numColsLastRow=cols;
				final int rows=(names.size()/cols)+(numColsLastRow==cols?0:1);

				// finally do the output
				for(int row=0; row<rows; row++) {
					for(int col=0; col<cols; col++) {
						final int dataidx=computeDataIdx(rows, row, col, numColsLastRow);
						if(dataidx<names.size()) {
							if(col>0)
								pw.print(SEPARATOR);
							printWidth(pw, colsFormat.getColWidth(col), names.get(dataidx));
						}
					}
					pw.println();
				}
			}catch(final Exception e) {
				throw e;
			}finally {
				names.clear();
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
				System.err.println("rows:"+rows+" row:"+row+" col:"+col+" numOnMore:"+numColsLastRow+" datalen:"+names.size()+" ret="+ret);
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
	private static OutputStyle COLS_HORIZONTAL=new OutputStyle() {

		public void printFile(final FileEntry fe, final PrintStream pw, final List<AttAccessor<?>> attAccessors) {
			throw new RuntimeException("COLS_HORIZONTAL not implemented");
		}

		public void doOutput(final PrintStream pw, final int screenWidth) {
			throw new RuntimeException("COLS_HORIZONTAL not implemented");
		}
	};


	private static void printWidth(final PrintStream pw, final int width, final String str) {
		final StringBuilder sb=new StringBuilder(str);
		while(sb.length()<width)
			sb.append(' ');
		pw.print(sb.toString());
	}


}
