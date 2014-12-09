package com.happypeople.hsh.hsh;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jline.console.ConsoleReader;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 */
public class Main {
	private final static Map<String, String> predefs=init_predefines();
	private static PrintWriter log;
	private static Collection<File> path=new ArrayList<File>();

	public static void main(String[] args) throws IOException {
		
		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

		parsePath();

		// ignore args
		
		final ConsoleReader br = new ConsoleReader();
		br.setPrompt("> ");
		PrintWriter out=new PrintWriter(br.getOutput());

//		final BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		try {
			while((line=br.readLine())!=null) {
				if(line.equals("quit"))
					System.exit(0);
				execute(line);
				//out.println("------------>"+line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/** This method parses the environment var PATH and
	 * places that list of directories in the class var path.
	 */
	private static void parsePath() {
		String lpath=System.getenv().get("PATH");
		if(lpath==null)
			lpath=System.getProperty("PATH");
		System.out.println("PATH: "+lpath);
		
		Collection<String> lpaths=Arrays.asList(lpath.split(File.pathSeparator));
		for(String p : lpaths) {
			final File f=new File(p);
			if(f.isDirectory()) {
				path.add(f);
				System.out.println("adding path: "+f);
			}
		}
	}

	private static void execute(final String line) throws Exception {
		final String[] token=hsh_parse_and_substitution(line);
		
		if(token.length<1)
			return;	// empty line
		
		//log.println("cmd-line:>"+line+"<");
		//log.flush();

		final String buildin=predefs.get(token[0]);
		if(buildin!=null)
			exec_buildin_Main(buildin, token);
		else
			exec_extern_synchron(token);
	}
	
	/** This method parses, substitutes and splits the command line line.
	 * @param line cmd-line as typed by the user
	 * @return tokenized line
	 */
	private static String[] hsh_parse_and_substitution(String line) {
		// TODO real implementation
		// -handle splitted lines, which end with \
		// -handle explicit separated tokens like "foo bar" and "foo""bar" 
		// -handle pipes like "ls|grep foo"
		// -substitute variables like $x
		// -substitute executions like $(x)
		
		// simple implementation
		line=line.trim();
		if(line.length()<1)
			return new String[0];
		return line.split("\\s"); // split by whitespace
	}

	/** Executes the cmd line given in args.
	 * args[0] is the command to execute.
	 * $PATH is resolved to find that program.
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static int exec_extern_synchron(String[] args) {
		// TODO handle piped commands and stdstream redirection
		try {
			ProcessBuilder builder=new ProcessBuilder();
			args[0]=resolveCmd(args[0]);
			builder.command(Arrays.asList(args));
			builder.redirectError(Redirect.INHERIT);
			builder.redirectOutput(Redirect.INHERIT);
			builder.redirectInput(Redirect.INHERIT);
		
			Process p=builder.start();
			p.waitFor();
			return p.exitValue();
		}catch(Exception e) {
			e.printStackTrace(System.err);
			return -1;
		}
	}

	/** Resolves a String to an executable file, using path
	 * @param string a command name
	 * @return File to the executable
	 */
	private static String resolveCmd(String cmd) {
		final File f=new File(cmd);
		if(f.isAbsolute())
			return cmd;
		
		for(File p : path) {
			final File r=new File(p, cmd);
			if(r.exists() && r.isFile())
				return r.getAbsolutePath();

			// honor windows
			final File w=new File(p, cmd+".exe");
			if(w.exists() && w.isFile())
				return w.getAbsolutePath();
		}
			
		return cmd;
	}

	/** Executes the main of buildinClass
	 * @param buildinClass
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private static void exec_buildin_Main(String buildinClass, String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		try {
			// TODO remove first arg
			Class.forName(buildinClass).getMethod("main", new Class[]{ args.getClass()}).invoke(
					null, new Object[] { args });
			System.out.flush();
			System.err.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, String> init_predefines() {
		Map<String, String> predefs=new HashMap<String, String>();
		predefs.put("find", "com.happypeople.hsh.find.Main");
		predefs.put("ls", "com.happypeople.hsh.ls.Main");
		predefs.put("tail", "com.happypeople.hsh.tail.Main");
		return predefs;
	};
}
