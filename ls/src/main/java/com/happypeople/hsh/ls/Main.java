package com.happypeople.hsh.ls;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		if(args.length==0)
			args=new String[] { "." };
		
		for(String arg : args) { 
			final File f=new File(arg);
			if(f.exists()) {
				if(f.isDirectory())
					for(String s : f.list())
						System.out.println(s);
				else
					System.out.println(arg);
			}
		}
	}
	
}
