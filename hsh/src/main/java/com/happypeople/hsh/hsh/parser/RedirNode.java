package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.hsh.L2Token;
import com.happypeople.hsh.hsh.l1parser.DumpTarget;

public class RedirNode extends L2Node {

	private L2Token operator;
	private L2Token filename;
	private String ioNumber;

	public void setOperator(final L2Token operator) {
		this.operator=operator;
	}

	public L2Token getOperator() {
		return operator;
	}

	public void setFilename(final L2Token filename) {
		this.filename=filename;
	}

	public L2Token getFilename() {
		return filename;
	}

	public void setIoNumber(final String ioNumber) {
		this.ioNumber=ioNumber;
	}

	public String getIoNumber() {
		return ioNumber;
	}

	@Override
	public void dump(final DumpTarget target) {
		target.add(getClass().getName());
		target.add("filename: "+filename);
		target.add("ioNumber: "+ioNumber);
		operator.dump(target.incLevel());
		target.decLevel();
	}
}
