package com.happypeople.hsh.hsh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.happypeople.hsh.HshEnvironment;

public class HshEnvironmentImpl implements HshEnvironment {
	private HshEnvironment parent;
	private Map<String, String> vars=new HashMap<String, String>();
	private Set<ChangeListener> listeners=new HashSet<ChangeListener>();

	/** 
	 * @param parent read-only delegate of this environment, can be null
	 */
	public HshEnvironmentImpl(HshEnvironment parent) {
		this.parent=parent;
		if(parent!=null)
			parent.addListener(new ChangeListener() {
				@Override
				public void varChanged(String name, String oldValue) {
					// copy on write to parent
					if(!vars.containsKey(name))
						vars.put(name, oldValue);
				}
			});
	}

	@Override
	public void setVar(String name, String value) {
		final String oldVar=vars.containsKey(name)?vars.get(name):UNDEFINED;
		vars.put(name, value);
		fireChange(name, oldVar);
	}
	
	public boolean issetVar(String name) {
		String value=vars.get(name);
		if(value==null)
			return parent==null?false:parent.issetVar(name);
		return value!=null && value!=UNDEFINED;
	}

	@Override
	public void unsetVar(String name) {
		final String oldVar=vars.containsKey(name)?vars.get(name):UNDEFINED;
		vars.remove(name);
		fireChange(name, oldVar);
	}

	@Override
	public String getVar(String name) {
		return vars.containsKey(name)? vars.get(name) : parent!=null?parent.getVar(name) : null;
	}

	public void addListener(ChangeListener listener) {
		listeners.add(listener);
	}

	private void fireChange(final String name, final String oldVal) {
		for(ChangeListener listener : listeners)
			listener.varChanged(name, oldVal);
	}
}
