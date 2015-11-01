package com.happypeople.hsh;

/** Simple generic messages
 */
public interface HshMessage {
	public enum Type {
		Finish
	};

	/**
	 * @return the type of the message (metadata)
	 */
	public Type getType();


	/** The payload to the message.
	 * @return the payload
	 */
	public Object getPayload();

}
