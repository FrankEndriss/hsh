package com.happypeople.hsh;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This is a union of OutputStream and SelectableChannel (of type output).
 * Immutable.
 */
public class HshOutput {
    private OutputStream outputStream;
    private SelectableChannel outputChannel;
    private PrintStream printStream;

    public HshOutput(final OutputStream outputStream) {
        if (outputStream == null)
            throw new IllegalArgumentException("outputStream must not be null");
        this.outputStream = outputStream;
    }

    public HshOutput(final SelectableChannel outputChannel) {
        if (outputChannel == null)
            throw new IllegalArgumentException("outputChannel must not be null");
        if (!(outputChannel instanceof WritableByteChannel))
            throw new IllegalArgumentException("outputChannel must be of class WritableByteChannel");
        this.outputChannel = outputChannel;
    }

    public boolean isStream() {
        return outputStream != null;
    }

    public OutputStream getStream() {
        return outputStream;
    }

    public PrintStream getPrintStream() {
        if (this.printStream == null)
            synchronized (this) {
                if (this.printStream == null)
                    printStream = createPrintStream();
            }
        return printStream;
    }

    private PrintStream createPrintStream() {
        final OutputStream out = getStream();
        if (out != null) {
            if (out instanceof PrintStream)
                return (PrintStream) out;
            else
                return new PrintStream(out);
        }

        return new PrintStream(Channels.newOutputStream((WritableByteChannel) getChannel()));
    }

    public SelectableChannel getChannel() {
        return outputChannel;
    }

    public void close() {
        try {
            if (isStream())
                outputStream.close();
            else
                outputChannel.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
