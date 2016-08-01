package com.happypeople.hsh.fxgui;

import java.util.LinkedList;

import javafx.scene.control.Control;

/** The consoleModel holds the data
 */
public class ConsoleControl extends Control {

	/** Buffer holding the text lines of the current screen */
	private final LinkedList<CharSequence> lineBuffer=new LinkedList<CharSequence>();
	/** max size of the lineBuffer */
	private final int lineBufferSize=40;

	public void addText(final CharSequence text) {
		if(text==null || text.length()==0)
			return;
		final String s=text.toString();

		for(final String line : s.split("\n")) {
			addLine(line);
		}
		if(s.endsWith("\n"))
			addLine("");
	}

	private void addLine(final CharSequence line) {
		lineBuffer.add(line);
		if(lineBuffer.size()>lineBufferSize)
			lineBuffer.removeFirst();
	}

}
