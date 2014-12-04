package com.happypeople.hsh.hsh;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Happy Shell main.
 * The program reads lines from stdin and executes them.
 */
public class Main {
	private final static Map<String, String> predefs=init_predefines();
	private static PrintWriter log;

	public static void main(String[] args) {
		
		//			log=new PrintWriter(new FileWriter("hsh.log"));
		log=new PrintWriter(System.err);

		// ignore args
		
		final BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		try {
			while((line=br.readLine())!=null) {
				execute(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void execute(final String line) throws Exception {
		final String[] token=hsh_parse_and_substitution(line);
		
		if(token.length<1)
			return;	// empty line
		
		log.println("cmd-line:>"+line+"<");
		log.flush();

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
