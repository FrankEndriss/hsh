package com.happypeople.hsh.hsh;

import com.happypeople.hsh.HshMessage;

public class HshMessageImpl implements HshMessage {
	private final Type type;
	private final Object payload;

	public HshMessageImpl(final Type type, final Object payload) {
		this.type=type;
		this.payload=payload;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Object getPayload() {
		return payload;
	}

}
