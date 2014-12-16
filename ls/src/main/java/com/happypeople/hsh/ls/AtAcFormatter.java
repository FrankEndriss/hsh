package com.happypeople.hsh.ls;

import com.happypeople.hsh.ls.FileEntry.AttAccessor;

/** Combines an AttAccessor with formatted output.
 * The output can be LEFT or RIGHT adjusted, and be of a minWidth.
 * The Implementation adds blanks to the stringified form of the accessed value to achieve this.
 */
public class AtAcFormatter {
	private final AttAccessor<?> atac;
	private final Adjustment adjust;

	public AtAcFormatter(FileEntry.AttAccessor<?> atac, Adjustment adjust) {
		this.atac=atac;
		this.adjust=adjust;
	}
	
	public Adjustment getAdjustment() {
		return adjust;
	}

	public String get(FileEntry fileEntry, final int minWidth) {
		final StringBuilder sb=new StringBuilder(""+atac.get(fileEntry));
		while(sb.length()<minWidth)
			if(Adjustment.RIGHT==adjust)
				sb.insert(0, ' ');
			else if(Adjustment.LEFT==adjust)
				sb.append(' ');
			else 
				break;

		return sb.toString();
	}

}
