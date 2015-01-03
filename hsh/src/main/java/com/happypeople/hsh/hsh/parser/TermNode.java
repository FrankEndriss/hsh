package com.happypeople.hsh.hsh.parser;

public class TermNode extends L2Node implements Backgroundable {
	private boolean background;

	public boolean isBackground() {
		return background;
	}

	@Override
	public void setBackground(final boolean background) {
		this.background=background;
	}
}
