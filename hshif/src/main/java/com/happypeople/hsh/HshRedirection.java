package com.happypeople.hsh;

import java.io.File;


/** Objects of this class encapsulate the (meta) information given in one redirection, ie ">outfile" or "0<&3"
 * Immutable.
 * TODO refactor this class to be an interface
 */
public class HshRedirection {
	/** Redirection may go to a file or to another FD, ie "2>err.txt" or "3>&1" */
	public enum TargetType {
		/** redirection from or to a targetFile */
		FILE,
		/** redirection to another FD */
		ANOTHER_FD,
		/** redirection to another stream/thread/process */
		ANOTHER_STREAM
	}

	/** According to the redirection symbols "<", ">" and ">>" there are three types of operations. */
	public enum OperationType {
		READ,
		WRITE,
		APPEND
	}

	/** Type of the target of this redirection. */
	private final TargetType targetType;
	/** If targetType==FILE, this is the files name. */
	private final File targetFile;
	/** If targetType==ANOTHER_FD, this is the number of it. */
	private final Integer targetFD;

	/** This is the number of the redirected FD. */
	private final int redirectedFD;
	/** This is the operation which should be executed on the redirection. */
	private final OperationType operationType;
	/** This is the other stream in case of TargetType==ANOTHER_STREAM. Used to create piped command chains. */
	private final HshPipe anotherStream;

	public HshRedirection(final int redirectedFD, final OperationType operationType, final HshPipe anotherStream) {
		this.targetType=TargetType.ANOTHER_STREAM;
		this.redirectedFD=redirectedFD;
		this.anotherStream=anotherStream;
		this.operationType=operationType;
		this.targetFile=null;
		this.targetFD=null;

		if(anotherStream==null)
			throw new IllegalArgumentException("anotherStream must not be null with this constructor");
	}
	/** Creates a redirection to read from a targetFile or write to a targetFile or append to a targetFile.
	 * @param redirectedFD the FD which should be redirected
	 * @param operationType the operation of the FD which should be redirected
	 * @param targetFile the files name
	 */
	public HshRedirection(final int redirectedFD, final OperationType operationType, final File targetFile) {
		this.targetType=TargetType.FILE;
		this.redirectedFD=redirectedFD;
		this.operationType=operationType;
		this.targetFile=targetFile;
		this.targetFD=null;
		this.anotherStream=null;

		if(targetFile==null)
			throw new IllegalArgumentException("targetFile must not be null with this constructor");
		if(redirectedFD<0)
			throw new IllegalArgumentException("fd must not be less than 0");
	}

	/** Creates a redirection to read from or write to another FD */
	public HshRedirection(final int redirectedFD, final OperationType operationType, final Integer otherIO) {
		this.targetType=TargetType.ANOTHER_FD;
		this.redirectedFD=redirectedFD;
		this.operationType=operationType;
		this.targetFD=otherIO;
		this.targetFile=null;
		this.anotherStream=null;

		if(operationType==OperationType.APPEND)
			throw new IllegalArgumentException("cannot APPEND to FD, use READ or WRITE or a file");
		if(otherIO==null)
			throw new IllegalArgumentException("targetFD must not be null with this constructor");
		if(redirectedFD<0)
			throw new IllegalArgumentException("fd must not be less than 0");
	}

	/** If target type is FILE this method returns the File, else null.
	 * @return the target File to read from, or write or append to
	 */
	public File getTargetFile() {
		return targetFile;
	}

	/** If target type is ANOTHER_FD this method returns the FD of the target.
	 * If target type is not ANOTHER_FD, this method most likely throws a RuntimeException.
	 * @return the target FD
	 * TODO: this feature is still unused, ie not implemented in all executors
	 */
	public int getTargetFD() {
		return targetFD;
	}

	private HshPipe getAnotherStream() {
		return anotherStream;
	}

	/** Every HshRedirection redirects exactly one FD.
	 * @return the FD of this HshRedirection
	 */
	public int getRedirectedFD() {
		return redirectedFD;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public OperationType getOperationType() {
		return operationType;
	}


}
