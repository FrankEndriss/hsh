package com.happypeople.hsh.hsh;

import java.lang.ProcessBuilder.Redirect;

public class HshRedirectionsImpl implements HshRedirections {

	private final static HshRedirection INHERIT=new HshRedirectionImpl(Redirect.INHERIT);

	private final HshRedirection redirIn;
	private final HshRedirection redirOut;
	private final HshRedirection redirErr;

	public HshRedirectionsImpl() {
		this(INHERIT, INHERIT, INHERIT);
	}

	public HshRedirectionsImpl(final HshRedirection redirIn, final HshRedirection redirOut, final HshRedirection redirErr) {
		this.redirIn=redirIn;
		this.redirOut=redirOut;
		this.redirErr=redirErr;
	}

	@Override
	public HshRedirection getStderrRedirection() {
		return redirErr;
	}

	@Override
	public HshRedirection getStdoutRedirection() {
		return redirOut;
	}

	@Override
	public HshRedirection getStdinRedirection() {
		return redirIn;
	}

	@Override
	public HshRedirections createChild(final HshRedirection stdin, final HshRedirection stdout, final HshRedirection stderr) {
		return new HshRedirectionsImpl(stdin==null?this.redirIn:stdin,
				stdout==null?this.redirOut:stdout,
				stderr==null?this.redirErr:stderr);
	}

}
