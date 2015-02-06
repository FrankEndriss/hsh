package com.happypeople.hsh;

import java.io.IOException;
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

	public boolean isStream() {
		return inputStream!=null;
	}

	public InputStream getStream() {
		return inputStream;
	}

	public SelectableChannel getChannel() {
		return inputChannel;
	}

	public void close() {
		try {
			if(isStream())
				inputStream.close();
			else
				inputChannel.close();
		}catch(final IOException e) {
			e.printStackTrace();
		}
	}
}
