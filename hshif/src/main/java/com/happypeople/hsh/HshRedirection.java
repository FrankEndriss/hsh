package com.happypeople.hsh;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;


/** Objects of this class encapsulate the (meta) information given in one redirection, ie ">outfile" or "0<&3"
 * Immutable.
 */
public class HshRedirection {
	public enum TargetType {
		/** redirection from or to a file */
		FILE,
		/** redirection to another FD */
		OTHER_IO
	}

	public enum OperationType {
		READ,
		WRITE,
		APPEND
	}

	private final File file;
	private final Integer otherIO;
	private final int fd;
	private final TargetType targetType;
	private final OperationType operationType;

	/** Creates a redirection to append to a file */
	public HshRedirection(final int fd, final File file) {
		this.fd=fd;
		this.operationType=OperationType.WRITE;
		this.targetType=TargetType.FILE;
		this.file=file;
		this.otherIO=null;

		if(file==null)
			throw new RuntimeException("file must not be null with this constructor");
		if(fd<0)
			throw new RuntimeException("fd must not be less than 0");
	}

	/** Creates a redirection to read from a file or write to a file or append to a file. */
	public HshRedirection(final int fd, final OperationType operationType, final File file) {
		this.fd=fd;
		this.operationType=operationType;
		this.targetType=TargetType.FILE;
		this.file=file;
		this.otherIO=null;

		if(file==null)
			throw new RuntimeException("file must not be null with this constructor");
		if(fd<0)
			throw new RuntimeException("fd must not be less than 0");
	}

	/** Creates a redirection to read from or write to another FD */
	public HshRedirection(final int fd, final OperationType operationType, final Integer otherIO) {
		this.fd=fd;
		this.operationType=operationType;
		this.targetType=TargetType.OTHER_IO;
		this.file=null;
		this.otherIO=otherIO;

		if(operationType==OperationType.APPEND)
			throw new RuntimeException("cannot APPEND to otherIO, use READ or WRITE");
		if(otherIO==null)
			throw new RuntimeException("otherIO must not be null with this constructor");
		if(fd<0)
			throw new RuntimeException("fd must not be less than 0");
	}

	/** If target type is FILE this method returns the File, else null.
	 * @return the target File to read from, or write or append to
	 */
	public File getFile() {
		return file;
	}

	/** If target type is OTHER_IO this method returns the FD of the target.
	 * @return the target FD
	 */
	public Integer getOtherIO() {
		return otherIO;
	}

	/** Every HshRedirection redirects exactly one FD.
	 * @return the FD of this HshRedirection
	 */
	public int getFD() {
		return fd;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	/** Setup the processBuilder to execute a process using this redirection.
	 * This is possible only if this is a FILE redirection and this refers to one of the
	 * standard streams
	 * @param processBuilder
	 * @return true if a redirection was set, else false
	 */
	public boolean setupFileRedirection(final ProcessBuilder processBuilder) {
		if(getTargetType()==TargetType.FILE) {
			if(getFD()==HshFDSet.STDIN && getOperationType()==OperationType.READ) {
				processBuilder.redirectInput(getFile());
				return true;
			} else if(getFD()==HshFDSet.STDOUT) {
				if(getOperationType()==OperationType.WRITE) {
					processBuilder.redirectOutput(getFile());
					return true;
				} else if(getOperationType()==OperationType.APPEND) {
					processBuilder.redirectOutput(Redirect.appendTo(getFile()));
					return true;
				}
			} else if(getFD()==HshFDSet.STDERR) {
				if(getOperationType()==OperationType.WRITE) {
					processBuilder.redirectError(getFile());
					return true;
				} else if(getOperationType()==OperationType.APPEND) {
					processBuilder.redirectError(Redirect.appendTo(getFile()));
					return true;
				}
			}
		} // else ignore
		return false;
	}
}
