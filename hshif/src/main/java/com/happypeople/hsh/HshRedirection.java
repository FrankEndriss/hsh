package com.happypeople.hsh;


/** Objects of this class combines a ProcessBuilder.Redirect with an optional Stream or Channel.
 * For HshRedirection of type Redirect.PIPE an HshInput and and HshOutput can (and should) be
 * set once.
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
