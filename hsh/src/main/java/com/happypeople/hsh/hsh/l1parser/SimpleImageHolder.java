package com.happypeople.hsh.hsh.l1parser;

public class SimpleImageHolder implements ImageHolder {
	private final StringBuilder sb=new StringBuilder();

	@Override
	public String getImage() {
		return sb.toString();
	}

	public CharSequence getImage(final int startIdx, final int endIdx) {
		return sb.subSequence(startIdx, endIdx);
	}

	@Override
	public int getLen() {
		return sb.length();
	}

	@Override
	public ImageHolder append(final CharSequence s) {
		sb.append(s);
		return this;
	}

	@Override
	public ImageHolder append(final char[] buf, final int off, final int len) {
		sb.append(buf, off, len);
		return this;
	}
}
