package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.L1Node;

/** A L2Token extends Token to have:
 * -a list of L1Nodes as childs
 * -an image
 * A L2Token has an building phase and a usage phase. While building the image is in an StringBuilder, afterwards
 * in an String();
 *
 */
public class L2Token extends Token implements L1Node {
	private List<L1Node> parts=new ArrayList<L1Node>();
	private StringBuilder sb=new StringBuilder();

	public L2Token() {
		super(HshParserConstants.WORD, null);
	}

	// TODO copy constructor to copy error-line information
	// L2Token(Token t) {...}

	/** Adds part as a new Child
	 * @param part
	 * @return index of the added part
	 */
	public int addPart(final L1Node part) {
		parts.add(part);
		return parts.size()-1;
	}

	public L1Node getPart(final int idx) {
		return parts.get(idx);
	}

	public int getPartCount() {
		return parts.size();
	}

	/** This method splits all but the first part from this and inserts the rest
	 * after this as a new L2Token. (this.next==<new L2Token>)
	 * This is only done when this has more than one part, else nothing happens.
	 * @return true if split was done, false otherwise
	 */
	public boolean splitFirstPart() {
		if(getPartCount()==0)
			throw new RuntimeException("cannot split empty L2Token");

		if(getPartCount()==1)
			return false;

		final L1Node firstPart=getPart(0);
		final int partLen=firstPart.getLen();

		final L2Token tok=new L2Token();
		tok.kind=kind;

		for(int i=1; i<getPartCount(); i++) {
			final L1Node lPart=getPart(i);
			// adjust the offsets of the subtree of parts
			NodeTraversal.traverse(lPart, new NodeTraversal.TraverseListener() {
				@Override
				public TraverseListenerResult node(final L1Node node, final int level) {
					node.addOff(-partLen);
					return TraverseListenerResult.CONTINUE;
				}
			});
			tok.addPart(lPart);
		}
		// remove all but the first part
		parts.clear();
		parts.add(firstPart);

		tok.append(image.substring(partLen, image.length()));
		tok.finishImage();
		image=image.substring(0, partLen);
		tok.next=next;
		next=tok;

		return true;
	}

	@Override
	public Iterator<L1Node> iterator() {
		return parts.iterator();
	}

	/** Creates a printout of the node-tree
	 * @param level the level of the tree this node lives in
	 */
	@Override
	public void dump(final int level) {
		for(int i=0; i<level; i++)
			System.out.print("\t");
		System.out.println(getClass().getName()+" kind="+kind+" image="+getImage());
		for(final L1Node child : parts)
			child.dump(level+1);
	}

	/** Switches from building phase to usage phase.
	 */
	public void finishImage() {
		image=sb.toString();
		sb=null;
	}

	/** Call in usage phase.
	 * @return the image of this token
	 */
	@Override
	public String getImage() {
		return image;
	}

	@Override
	public int getLen() {
		return sb.length();
	}

	/** Appends a String to the image and returns this.
	 * @param str
	 */
	public L2Token append(final String str) {
		sb.append(str);
		return this;
	}

	/** Removes all parts starting at idx i
	 * @param i the starting idx
	 */
	public void removePartsFrom(final int i) {
		parts=parts.subList(0, i-1);
	}

	@Override
	public void addLen(final int increment) {
		throw new RuntimeException("not implemented in L2Token");
	}

	@Override
	public int getOff() {
		throw new RuntimeException("not implemented in L2Token");
	}

	@Override
	public void addOff(final int increment) {
		throw new RuntimeException("not implemented in L2Token");
	}

}
