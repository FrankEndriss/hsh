package com.happypeople.hsh.hsh.l1parser;


public abstract class AbstractL1Node implements L1Node {
	private final ImageHolder imageHolder;
	private int off;
	private int len;

	public AbstractL1Node(final ImageHolder imageHolder, final int off, final int len) {
		this.imageHolder=imageHolder;
		this.off=off;
		this.len=len;
		if(off<0)
			throw new RuntimeException("off<0");
		if(len<0)
			throw new RuntimeException("len<0");
	}

	@Override
	public int getLen() {
		return len;
	}

	@Override
	public void addLen(final int increment) {
		if(len+increment<0)
			throw new RuntimeException("len<0");
		len+=increment;
	}

	@Override
	public int getOff() {
		return off;
	}

	@Override
	public void addOff(final int increment) {
		if(off+increment<0)
			throw new RuntimeException("off<0");
		off+=increment;
	}

	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName());
		for(int i=0; i<level+1; i++)
			System.out.print("\t");
		System.out.println("image="+getImage());
	}


	@Override
	public String getImage() {
		return imageHolder.getImage().substring(off, off+len);
	}

	protected ImageHolder getImageHolder() {
		return imageHolder;
	}
}
