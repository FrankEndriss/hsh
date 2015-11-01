package com.happypeople.hsh.exit;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.happypeople.hsh.HshCmd;
import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.HshMessage;

public class Exit implements HshCmd {
	private static Logger log=Logger.getLogger(Exit.class);

	// no main since exit needs the HshContext
	@Override
	public int execute(final HshContext context, final ArrayList<String> args) throws Exception {
		int exitCode=0;
		try {
			exitCode=Integer.parseInt(args.get(1));
		} catch(final Exception e) {
			// ignore
		}
		context.msg(new HshMessage() {
			@Override
			public Type getType() {
				return HshMessage.Type.Finish;
			}

			@Override
			public Object getPayload() {
				return null;
			}
		});

		log.info("exit, exitCode="+exitCode);
		return exitCode;
	}
}
