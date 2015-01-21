package com.happypeople.hsh.hsh.l1parser;

import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.Portion;



/** Interface for Nodes in L1Parser constructed node trees.
 * iterator() creates an iterator over the children.
 **/
public interface L1Node extends Iterable<L1Node> {
	public void dump(int level);

	/**
	 * @return the len of the image of this node
	 */
	public int getLen();

	/** Change the image length
	 * @param increment
	 */
	public void addLen(int increment);

	/**
	 * @return the offset of the image of this node
	 */
	public int getOff();

	/** Move the nodes image index.
	 * @param increment
	 */
	public void addOff(int increment);

	/**
	 * @return the image of this node as parsed/read
	 */
	public String getImage();

	/** Splitting while WORD expansion.
	 * see http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_06_05
	 * Usually WORDs are splitted, and this is a part, but in L2Token these fields are
	 * re-concatenated if they belong to one word. We cannot split on word-level, because
	 * different nodes do different splittings. (Quoted-Nodes do no splitting)
	 * examples, IFS=" "
	 * x returns x, portions(0)=0, 1
	 * "x" returns "x", portions(0)=1, 1
	 * x=bla "$x" returns "bla", portions(0)=1, 3
	 * x="bla laber" "$x" returns "bla laber", portions(0)=1, 3, portions(1)=4, 5
	 * x=" bla" $x returns " bla", portions(0)=1, 0, portions(1)=2, 3
	 * x=" bla " $x returns " bla ", portions(0)=1, 0, portions(1)=2, 3, portions(2)=4, 0
	 * x="'bla'" $x returns "'bla'", portions(0)=1, 5
	 * @param context the execution context
	 * @param portions the portions of the returned String after splitting (according to contexts IFS)
	 * If the returned String starts with a field separator part, the first portion references the
	 * empty String (ie portion.off=0, portion.len=0). The same is true at the end of the String.
	 * @return the image of this node as getImage, but with substitutions substituted. Tilde, $x etc
	 * @throws Exception
	 */
	public String substituteAndSplit(HshContext context, List<Portion> portions) throws Exception;

}