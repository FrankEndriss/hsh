package com.happypeople.hsh.hsh.parser;

public class And_orNode extends L2Node implements Backgroundable {
	boolean background;

	@Override
	public boolean isBackground() {
		return background;
	}

	@Override
	public void setBackground(final boolean background) {
		this.background = background;
	}
}
