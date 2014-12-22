package com.happypeople.hsh.hsh;

/** thrown if parsing of a construct was not successfull
 */
public class NotParsedException extends Exception {
	public NotParsedException() {
		super("parse failed");
	}
}