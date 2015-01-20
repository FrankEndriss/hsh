package com.happypeople.hsh.hsh.parser;

import com.happypeople.hsh.hsh.L2Token;

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
	public void dump(final int level) {
		final StringBuilder sb=new StringBuilder();
		for(int i=0; i<level; i++)
			sb.append("\t");
		final String t=sb.toString();
		System.out.println(t+getClass().getName());

		System.out.println(t+"filename: "+filename);
		System.out.println(t+"ioNumber: "+ioNumber);
		operator.dump(level+1);
	}
}
