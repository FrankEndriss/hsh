package com.happypeople.hsh.tail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

public class jtail {
	
	public static void main(String[] args) {
		if(args.length<1)
			usage();
		
		// parse args
		boolean _fFlag=false;
		int lines=10; // default 10
		String filename=null;
		for(int i=0; i<args.length; i++) {
			if(args[i].equals("-f"))
				_fFlag=true;
			else if(args[i].equals("-n")) {
				lines=Integer.parseInt(args[i+1]);
				i++;
			} else
				filename=args[i];
			
		}

		if(filename==null)
			usage();
		
		try {
			run(lines, filename, _fFlag);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void run(final int lines, final String fileName, final boolean _fFlag) throws IOException {
		final RandomAccessFile raf=new RandomAccessFile(fileName, "r");
		long lseek=raf.length();
		raf.seek(lseek);
		// List of seeks to EOLs
		final List<Long> lineEnds=new ArrayList<Long>();
		
		final int bufsz=1024*1024;
		byte[] buf=new byte[bufsz];
		
		while(lseek>0 && lineEnds.size()<lines+1) {
			lseek-=buf.length;
			int doRead=(int)(lseek<0?buf.length+lseek:buf.length);
			if(lseek<0)
				lseek=0;
			raf.seek(lseek);
			raf.readFully(buf, 0, doRead);
			for(int i=0; i<doRead; i++) {
				if(buf[i]=='\n')
					lineEnds.add(lseek+i);
			}
		}

		Collections.sort(lineEnds);
		
		final int toOut=Math.min(lines+1, lineEnds.size());
		raf.seek(lineEnds.get(lineEnds.size()-toOut)+1);
		for(int i=0; i<toOut; i++)
			dumpLine(raf);

		raf.close();
		lineEnds.clear();
		buf=null;
		
		if(_fFlag) {
			Tailer tailer=new Tailer(new File(fileName), new MyTailerListener(), 500, true);
			new Thread(tailer).start();
		}
	}
	
	  private static class MyTailerListener extends TailerListenerAdapter {
	      public void handle(String line) {
	          System.out.println(line);
	      }
	  }
	  
	private static void dumpLine(RandomAccessFile raf) throws IOException {
		byte b;
		do {
			// if file ends with NL last read gets an EOF exception, prevent this
			if(raf.getFilePointer()<raf.length()) {
				b=raf.readByte();
				System.out.print((char)b);
			} else
				break;
		} while(b!='\n');
	}
	

	private static void usage() {
		System.out.println("usage: "+jtail.class.getName()+" [-f] [-n <lines>] <fileName>");
		System.out.println("tails lines lines of file fileName");
		System.exit(1);
	}

}
