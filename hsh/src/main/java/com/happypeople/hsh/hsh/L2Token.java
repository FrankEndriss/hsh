package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.hsh.l1parser.L1Node;

/** A L2Token extends Token to have:
 * -a list of L1Nodes as childs
 * -a method doSubstitution(...)
 */
public class L2Token extends Token implements L1Node {
	private final List<L1Node> parts=new ArrayList<L1Node>();

	public L2Token() {
		image="L2Token";
	}

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
		System.out.println(getClass().getName()+" kind="+kind+" image="+image);
		for(final L1Node child : parts)
			child.dump(level+1);
	}

	@Override
	public String getString() {
		final StringBuilder sb=new StringBuilder();
		for(final L1Node part : parts)
			sb.append(part.getString());
		return sb.toString();
	}


}
