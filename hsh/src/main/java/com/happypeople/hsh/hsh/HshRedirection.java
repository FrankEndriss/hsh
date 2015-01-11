package com.happypeople.hsh.hsh;


/** Objects of this class combines a ProcessBuilder.Redirect with an optional Stream.
 */
public interface HshRedirection {
	ProcessBuilder.Redirect getType();

	HshInput getIn();
	/** Must not be called more than once and only if getType()==Redirect.PIPE
	 * @param in
	 */
	void setIn(HshInput in);

	public HshOutput getOut();
	/** Must not be called more than once and only if getType()==Redirect.PIPE
	 * @param out
	 */
	public void setOut(HshOutput out);

}
