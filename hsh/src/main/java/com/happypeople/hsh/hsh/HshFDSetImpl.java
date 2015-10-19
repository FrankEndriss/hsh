package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshPipe;

/** Two Sets of HshPipeImpl, indexed by integer.
 * One for input streams, one for output streams.
 **/
public class HshFDSetImpl implements HshFDSet {
	private final Map<Integer, HshPipe> pipes=new HashMap<Integer, HshPipe>();

	@Override
	public void setPipe(final int fd, final HshPipe pipe) throws IOException {
		final HshPipe oldPipe=pipes.put(fd, pipe);
		if(oldPipe!=null)
			oldPipe.close();
	}

	@Override
	public HshPipe getPipe(final int fd) {
		return pipes.get(fd);
	}

	@Override
	public void closePipe(final int fd) throws IOException {
		final HshPipe pipe=pipes.remove(fd);
		if(pipe!=null)
			pipe.close();
	}

	@Override
	public void close() throws IOException {
		for(final HshPipe pipe : pipes.values())
			pipe.close();
		pipes.clear();
	}

	@Override
	public HshFDSet createCopy() throws IOException {
		final HshFDSetImpl copy=new HshFDSetImpl();
		for(final Map.Entry<Integer, HshPipe> entry : pipes.entrySet())
			copy.setPipe(entry.getKey(), entry.getValue());

		return copy;
	}
}
