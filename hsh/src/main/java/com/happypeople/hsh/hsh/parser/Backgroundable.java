package com.happypeople.hsh.hsh.parser;

/** Interface for HshNodes wich can be executed in background.
 * Used in the HshParser when a UPPERSANT("&") is parsed.
 */
public interface Backgroundable {
	public void setBackground(boolean background);
	public boolean isBackground();

}
