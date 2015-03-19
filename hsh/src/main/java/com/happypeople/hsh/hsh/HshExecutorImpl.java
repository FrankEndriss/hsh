package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshExecutor;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.VariableParameter;

public class HshExecutorImpl implements HshExecutor, HshEnvironmentImpl.ChangeListener {
	private final static Map<String, String> predefs=init_predefines();
	//private final HshRedirections hshRedirections;
	//private final HshExecutor delegate;
	//private final Map<Integer, HshInput> inFD=new HashMap<Integer, HshInput>();
	//private final Map<Integer, HshOutput> outFD=new HashMap<Integer, HshOutput>();

	@Override
	public int execute(final String[] command, final HshContext context, final List<HshRedirection> redirs) throws Exception {
		if(command.length<1)
			return 0;	// empty line

		final String buildin=predefs.get(command[0]);
		if(buildin!=null)
			return exec_buildin_Main(buildin, command, context);
		else
			return exec_extern_synchron(command, context);
	}

	/** Executes the cmd line given in args and waits for it to finish execution.
	 * args[0] is the command to execute.
	 * $PATH is resolved to find that program.
	 * @param args the arg vector
	 * @return exit status of the created process
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private int exec_extern_synchron(final String[] args, final HshContext context) {
		return -1;
	}

	/** Executes a buildin class/cmd.
	 * If the class implements HshCmd that interface is used, else main(String[] args) is called.
	 * @param buildinClass full qualified name of class implementing the command args[0]
	 * @param args command arg vector unix style, ie args[0] is the called command
	 * @throws ClassNotFoundException if the class cannot be loaded or called
	 * @throws SecurityException if the class cannot be loaded or called
	 * @throws NoSuchMethodException if the class cannot be loaded or called
	 * @throws InvocationTargetException if the class cannot be loaded or called
	 * @throws IllegalArgumentException if the class cannot be loaded or called
	 * @throws IllegalAccessException if the class cannot be loaded or called
	 */
	private int exec_buildin_Main(final String buildinClass, final String[] args, final HshContext context)
	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		try {
			final Class<?> cls=Class.forName(buildinClass);
			if(HshCmd.class.isAssignableFrom(cls)) { // cls implements HshCmd
				final HshCmd hshCmd=(HshCmd) cls.newInstance();
				return hshCmd.execute(context, new ArrayList<String>(Arrays.asList(args)));
			} else {
				Class.forName(buildinClass).getMethod("main", new Class[]{ args.getClass()}).invoke(
					null, new Object[] { args });
				return 0;
			}
		}catch(final Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	private static Map<String, String> init_predefines() {
		final Map<String, String> predefs=new HashMap<String, String>();
		predefs.put("exit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("find",	"com.happypeople.hsh.find.Find");
		predefs.put("ls", 	"com.happypeople.hsh.ls.Ls");
		predefs.put("quit",	"com.happypeople.hsh.exit.Exit");
		predefs.put("tail",	"com.happypeople.hsh.tail.Main");
		return predefs;
	}


	/*
	@Override
	public HshRedirections getRedirecions() {
		return hshRedirections;
	}

	@Override
	public HshExecutor createChild(final HshContext context, final HshRedirections hshRedirections) {
		return new HshExecutorImpl(this,
				context!=null?context:this.hshContext,
				hshRedirections!=null?hshRedirections:this.hshRedirections,
				this);
	}
	*/

	// end HshEnvirionment-Listener

	@Override
	public void close() {
		/*
		for(final HshOutput out : outFD.values())
			out.close();
		for(final HshInput in : inFD.values())
			in.close();
			*/
	}

	@Override
	public boolean canExecute(final String[] command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void created(final Parameter parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removed(final Parameter parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exported(final Parameter parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changed(final VariableParameter parameter, final String oldValue) {
		// TODO Auto-generated method stub

	}
}
