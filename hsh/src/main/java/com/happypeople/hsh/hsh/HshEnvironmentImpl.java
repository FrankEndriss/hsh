package com.happypeople.hsh.hsh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.happypeople.hsh.HshEnvironment;
import com.happypeople.hsh.Parameter;
import com.happypeople.hsh.VariableParameter;

/** Set of Parameters and a parent environment.
 * Parameters can be set and unset.
 * Initially all exported parameters from the parent environment are visible.
 * No write to parents context, parent is readonly.
 * On change of the parent environment copies of Parameters are created.
 * TODO readOnly flag of parameters
 * TODO make thread-save
 */
public class HshEnvironmentImpl implements HshEnvironment {
	private final HshEnvironment parent;
	private final Map<String, Parameter> vars=new HashMap<String, Parameter>();
	private final Set<ChangeListener> listeners=new HashSet<ChangeListener>();

	/** Number of positional parameters set in this environment. -1==undefined */
	private int positionalCount=-1;

	/**
	 * @param parent read-only delegate of this environment, can be null
	 */
	public HshEnvironmentImpl(final HshEnvironment parent) {
		this.parent=parent;
		if(parent!=null) {
			// copy on write to parent
			parent.addListener(new ChangeListener() {

				@Override
				public void created(final Parameter parameter) {
					// if a Parameter is created in parents context after copy of the context,
					// that Parameter is still undefined in the copy.
					if(!vars.containsKey(parameter.getName()))
						vars.put(parameter.getName(), UNDEFINED);
				}

				@Override
				public void removed(final Parameter parameter) {
					// if a Parameter is removed in parents context after copy of the context,
					// that Parameter is still defined (or undefined) in the copy.
					if(parameter.isExport()) {
						// check if copy exists
						final Parameter lParam=vars.get(parameter.getName());
						// copy if the removed param was exported
						if(lParam==null && parameter.isExport())
							vars.put(parameter.getName(), parameter);
					}
				}

				@Override
				public void changed(final VariableParameter parameter, final String oldValue) {
					// if a Parameter value is changed in parents context after copy of the context,
					// that Parameter is still defined with the old value in the copy.
					if(parameter.isExport()) {
						final Parameter lParam=vars.get(parameter.getName());
						if(lParam==null)
							vars.put(parameter.getName(), parameter.createCopy());
					}
				}

				@Override
				public void exported(final Parameter parameter) {
					// if a Parameter is exported in parents context after copy of the context,
					// that Parameter is still undefined (or defined) in the copy.
					if(!vars.containsKey(parameter.getName()))
						vars.put(parameter.getName(), UNDEFINED);
				}
			});
		}
	}

	@Override
	public void addListener(final ChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public Parameter getParameter(final String name) {
		final Parameter parameter=vars.get(name);
		if(parameter==UNDEFINED)
			return null;
		else if(parameter==null) {
			final Parameter pparameter=parent!=null?parent.getParameter(name):null;
			if(pparameter!=null && pparameter.isExport())
				return pparameter;
		}

		return parameter;
	}

	@Override
	public void unsetParameter(final String name) {
		final Parameter param=getParameter(name);
		// if parent exports name mark that name undefined
		if(parent!=null && parent.getParameter(name)!=null && parent.getParameter(name).isExport())
			vars.put(name, UNDEFINED);
		else
			vars.remove(name);
		if(param!=null)
			fireRemoved(param);
	}

	@Override
	public boolean issetParameter(final String name) {
		return getParameter(name)!=null;
	}

	@Override
	public void setPositionalValues(final String[] values) {
		for(int i=0; i<values.length; i++)
			setVariableValue(""+i, values[i]);
		positionalCount=values.length;

		if(parent!=null) { // explicit unset all positionals parameters potentially inherited by parent context
			for(int i=positionalCount; i<parent.getPositionalCount(); i++)
				unsetParameter(""+i);
		}
	}

	@Override
	public int getPositionalCount() {
		return positionalCount<0? parent.getPositionalCount() : positionalCount;
	}

	@Override
	public void setVariableValue(final String name, final String value) {
		// if parameter name exists in this context then its value is set, else
		// if parameter is UNDEFINED it is created, else
		// if parameter name isExport in parent context, it is created in this context and oldValue is parents value,
		// else parameter is simply created
		final Parameter parameter=vars.get(name);
		if(parameter!=null && parameter!=UNDEFINED) { // exists in this context
			if(parameter instanceof VariableParameterImpl) {
				final VariableParameterImpl lPara=(VariableParameterImpl)parameter;
				final String oldValue=lPara.getValue();
				lPara.setValue(value);
				fireChanged(lPara, oldValue);
			} else
				throw new RuntimeException("parameter has wrong type (is a function?)");
			return;
		}

		if(parameter==UNDEFINED) {
			final VariableParameterImpl p=new VariableParameterImpl(name, false);
			p.setValue(value);
			vars.put(name, p);
			fireCreated(p);
			return;
		}

		if(parent!=null) {
			final Parameter pparameter=parent.getParameter(name);
			if(pparameter!=null && pparameter.getType()==Parameter.Type.VARIABLE && pparameter.isExport()) {
				final VariableParameterImpl p=new VariableParameterImpl(name, false);
				p.setValue(value);
				vars.put(name, p);
				fireChanged(p, ((VariableParameter)pparameter).getValue());
				return;
			}
		}

		final VariableParameterImpl p=new VariableParameterImpl(name, false);
		p.setValue(value);
		vars.put(name, p);
		fireCreated(p);
	}

	@Override
	public String getVariableValue(final String name) {
		final Parameter p=getParameter(name);
		return p!=null && (p instanceof VariableParameter)?((VariableParameter)p).getValue():null;
	}

	private void fireCreated(final Parameter parameter) {
		for(final ChangeListener listener : listeners)
			listener.created(parameter);
	}

	private void fireRemoved(final Parameter parameter) {
		for(final ChangeListener listener : listeners)
			listener.removed(parameter);
	}

	private void fireChanged(final VariableParameter parameter, final String oldValue) {
		for(final ChangeListener listener : listeners)
			listener.changed(parameter, oldValue);
	}

}
