package com.happypeople.hsh.hsh;

import java.util.HashMap;
import java.util.Map;

import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshPipe;

/** Two Sets of HshPipeImpl, indexed by integer.
 * One for input streams, one for output streams.
 **/
public class HshFDSetImpl implements HshFDSet {
	private final Map<Integer, HshPipe> inputMap=new HashMap<Integer, HshPipe>();
	private final Map<Integer, HshPipe> outputMap=new HashMap<Integer, HshPipe>();
	private final HshFDSet parent;

	public HshFDSetImpl(final HshFDSet parent) {
		this.parent=parent;
	}

	public void setInput(final int fd, final HshPipe pipe) {
		// TODO close a previous contained pipe?
		inputMap.put(fd, pipe);
	}

	public void setOutput(final int fd, final HshPipe pipe) {
		// TODO close a previous contained pipe?
		outputMap.put(fd, pipe);
	}

	@Override
	public HshPipe getInput(final int fd) {
		final HshPipe ret=inputMap.get(fd);
		if(ret==null && parent!=null)
			return parent.getInput(fd);
		return ret;
	}

	@Override
	public HshPipe getOutput(final int fd) {
		final HshPipe ret=outputMap.get(fd);
		if(ret==null && parent!=null)
			return parent.getOutput(fd);
		return ret;
	}

	@Override
	public void close() {
		// close Streams?
	}
}
