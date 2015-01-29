package com.happypeople.hsh;

import java.io.InputStream;
import java.nio.channels.SelectableChannel;

/** This is a union of InputStream and SelectableChannel (of type input).
 * Immutable.
 */
public class HshInput {
	private InputStream inputStream;
	private SelectableChannel inputChannel;

	public HshInput(final InputStream inputStream) {
		this.inputStream=inputStream;
	}

	public HshInput(final SelectableChannel inputChannel) {
		this.inputChannel=inputChannel;
	}

	public InputStream getStream() {
		return inputStream;
	}

	public SelectableChannel getChannel() {
		return inputChannel;
	}
}
