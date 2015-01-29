package com.happypeople.hsh.hsh;

import java.lang.ProcessBuilder.Redirect;

import com.happypeople.hsh.HshInput;
import com.happypeople.hsh.HshOutput;
import com.happypeople.hsh.HshRedirection;

public class HshRedirectionImpl implements HshRedirection {
	private final Redirect redirection;
	private HshInput in;
	private HshOutput out;

	public HshRedirectionImpl(final Redirect redirection) {
		this.redirection=redirection;
	}

	@Override
	public Redirect getType() {
		return redirection;
	}

	@Override
	public HshInput getIn() {
		return in;
	}

	@Override
	public void setIn(final HshInput in) {
		if(this.in!=null)
			throw new IllegalStateException("setIn() must not be called more than once");

		if(redirection!=Redirect.PIPE)
			throw new IllegalStateException("setIn() must not be called on a HshRedirection of type other than Redirect.PIPE");

		this.in=in;
		checkConnected();
	}

	@Override
	public HshOutput getOut() {
		return out;
	}

	@Override
	public void setOut(final HshOutput out) {
		if(this.out!=null)
			throw new IllegalStateException("setOut() must not be called more than once");

		if(redirection!=Redirect.PIPE)
			throw new IllegalStateException("setOut() must not be called on a HshRedirection of type other than Redirect.PIPE");

		this.out=out;
		checkConnected();
	}

	private void checkConnected() {
		if(in!=null && out!=null)
			HshIOConnector.add(in, out);
	}
}
