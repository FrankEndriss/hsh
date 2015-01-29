package com.happypeople.hsh;

import java.io.OutputStream;
import java.nio.channels.SelectableChannel;

/** This is a union of OutputStream and SelectableChannel (of type output).
 * Immutable.
 */
public class HshOutput {
	private OutputStream outputStream;
	private SelectableChannel outputChannel;

	public HshOutput(final OutputStream outputStream) {
		this.outputStream=outputStream;
	}

	public HshOutput(final SelectableChannel outputChannel) {
		this.outputChannel=outputChannel;
	}

	public OutputStream getStream() {
		return outputStream;
	}

	public SelectableChannel getChannel() {
		return outputChannel;
	}
}
