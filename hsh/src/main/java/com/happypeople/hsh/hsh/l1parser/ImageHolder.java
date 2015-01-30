package com.happypeople.hsh.hsh.l1parser;

/** Class to encapsulate an immutable, extensible String.
 * The part of the image of an ImageHolder at a point of time is immutable.
 * But one can append() more data, which is then immutable, too.
 */
public interface ImageHolder {

	/**
	 * @return the image as a String
	 */
	public String getImage();

	// TODO this optimizes the the code in generic CharSequences, since these have to call
	// getImage().substring(startIdx, endIdx)
	// public CharSequence getImage(int startIdx, int endIdx);

	/**
	 * @return getImage().length()
	 */
	public int getLen();

	public ImageHolder append(CharSequence s);

	public ImageHolder append(char[] buf, int off, int len);

}
