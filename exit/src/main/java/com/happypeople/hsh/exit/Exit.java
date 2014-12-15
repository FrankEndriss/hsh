package com.happypeople.hsh.exit;

import java.util.ArrayList;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;

public class Exit implements HshCmd {
	// no main since exit needs the HshContext
	public int execute(HshContext hsh, ArrayList<String> args) throws Exception {
		int exitCode=0;
		try {
			exitCode=Integer.parseInt(args.get(1));
		} catch(Exception e) {
			// ignore
		}
		hsh.finish();
		return exitCode;
	}
}
