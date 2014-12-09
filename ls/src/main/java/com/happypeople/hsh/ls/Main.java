package com.happypeople.hsh.ls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {

	public static void main(final String[] margs) throws IOException {
		System.out.println("ls args: "+Arrays.asList(margs));
		final List<String> args=new ArrayList<String>(Arrays.asList(margs));
		args.remove(0);

		final List<String> fargs=new ArrayList<String>();

		final LsOptions opts=new LsOptions();
		for(final String arg : args) {
			if("-l".equals(arg))
				opts.setLFlag();
			else if("-1".equals(arg))
				opts.set1Flag();
			else
				fargs.add(arg);
		}

		if(fargs.size()==0)
			fargs.add(".");

		for(final String arg : fargs) {
			final File f=new File(arg);
			if(f.exists()) {
				if(f.isDirectory()) {
					opts.listDir(f);
				} else {
					opts.printFile(f);
				}
			} else
				System.out.println("does not exists: "+arg);
		}
	}

	private static class LsOptions {
		private boolean lFlag=false;
		private boolean _1Flag=false;

		void setLFlag() {
			this.lFlag=true;
		}

		void set1Flag() {
			this._1Flag=true;
		}

		void listDir(final File file) throws IOException {
			System.out.println(file.getName());
			for(final File f : file.listFiles())
				printFile(f);
		}

		void printFile(final File file) throws IOException {
			if(lFlag) {
				String perms=file.isDirectory()?"d":"-";
				try {
					final PosixFileAttributes attrs = Files.readAttributes(file.toPath(), PosixFileAttributes.class);
					final String owner=attrs.owner().getName();
					final String grp=attrs.group().getName();
					final Set<PosixFilePermission> permSet=attrs.permissions();
					perms=perms
							+(permSet.contains(PosixFilePermission.OWNER_READ)?"r":"-")
							+(permSet.contains(PosixFilePermission.OWNER_WRITE)?"w":"-")
							+(permSet.contains(PosixFilePermission.OWNER_EXECUTE)?"x":"-")
							+(permSet.contains(PosixFilePermission.GROUP_READ)?"r":"-")
							+(permSet.contains(PosixFilePermission.GROUP_WRITE)?"w":"-")
							+(permSet.contains(PosixFilePermission.GROUP_EXECUTE)?"x":"-")
							+(permSet.contains(PosixFilePermission.OTHERS_READ)?"r":"-")
							+(permSet.contains(PosixFilePermission.OTHERS_WRITE)?"w":"-")
							+(permSet.contains(PosixFilePermission.OTHERS_EXECUTE)?"x":"-");

					System.out.println(perms+" "+owner+" "+grp+" "+attrs.size()+" "+attrs.lastModifiedTime()+" "+file.getName());

				} catch(final Exception e) {
					final BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
					perms=perms+(file.canRead()?"r":"-")
							+(file.canWrite()?"w":"-")
							+(file.canExecute()?"x":"-");

					System.out.println(perms+" "+attrs.size()+" "+attrs.lastModifiedTime()+" "+file.getName());
				}
			} else {
				if(_1Flag)
					System.out.println(file.getName());
				else
					System.out.print(file.getName()+"  ");
			}


		}
	}
}
