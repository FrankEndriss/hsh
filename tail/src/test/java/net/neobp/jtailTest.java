package net.neobp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import com.happypeople.hsh.tail.Main;

public class jtailTest {

	private final static int LINES=100123; // More than 1MB

	private File writeFile() throws IOException {
		File file=File.createTempFile("jtail", "dat");
		FileOutputStream fos=new FileOutputStream(file);
		PrintWriter pw=new PrintWriter(fos);
		
		for(int i=1; i<LINES+1; i++)
			pw.println("Line: "+i);
		pw.close();
		
		return file;
	}

	@Test
	public void test() throws IOException {
		File file=writeFile();
		Main.main(new String[]{ "-f", "-n", "100", file.getAbsolutePath() });
		file.delete();
	}
	
	@Test
	public void test_f() throws IOException, InterruptedException {
		File file=writeFile();
		Main.main(new String[]{ "-f", "-n", "10", file.getAbsolutePath() });
		PrintWriter pw=new PrintWriter(new FileWriter(file));
		pw.println("line for tail -f: blah...");
		pw.println("another one line...");
		pw.close();
		Thread.sleep(1500);
		file.delete();
	}

}
