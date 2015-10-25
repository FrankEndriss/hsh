package com.happypeople.hsh.hsh;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.happypeople.hsh.HshFDSet;
import com.happypeople.hsh.HshPipe;
import com.happypeople.hsh.HshRedirection;
import com.happypeople.hsh.HshRedirection.OperationType;

/** Two Sets of HshPipeImpl, indexed by integer.
 * One for input streams, one for output streams.
 **/
public class HshFDSetImpl implements HshFDSet {
	private final Map<Integer, HshPipe> pipes=new HashMap<Integer, HshPipe>();

	public HshFDSetImpl() {
		// empty
	}

	/** Private copy constructor
	 * @param copySource
	 */
	private HshFDSetImpl(final Map<Integer, HshPipe> copySource) {
		for(final Map.Entry<Integer, HshPipe> entry : copySource.entrySet())
			pipes.put(entry.getKey(), entry.getValue());
	}

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
	public HshFDSet createCopy() {
		return new HshFDSetImpl(pipes);
	}

	@Override
	public void addRedirection(final HshRedirection redir) throws IOException {
		HshPipe newPipe=null;
		switch(redir.getTargetType()) {
		case ANOTHER_FD:
			newPipe=getPipe(redir.getTargetFD()).createCopy();
			break;
		case FILE:
			newPipe=redir.getOperationType()==OperationType.READ?
					new HshPipeImpl(new FileInputStream(redir.getTargetFile())):
					new HshPipeImpl(new PrintStream(new FileOutputStream(redir.getTargetFile())));
			break;
		default:
			throw new IllegalStateException("redirection of unknown TargetType");
		}
		closePipe(redir.getRedirectedFD());
		setPipe(redir.getRedirectedFD(), newPipe);
	}
}
