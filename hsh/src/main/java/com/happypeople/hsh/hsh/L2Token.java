package com.happypeople.hsh.hsh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;
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

	/** This does the expansion of a WORD described as:
	 * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_06
	 * 2.6 Word Expansions
	 * This section describes the various expansions that are performed on words. Not all expansions are
	 * performed on every word, as explained in the following sections.
	 * Tilde expansions, parameter expansions, command substitutions, arithmetic expansions, and quote removals that
	 * occur within a single word expand to a single field. It is only field splitting or pathname expansion that
	 * can create multiple fields from a single word. The single exception to this rule is the expansion of the
	 * special parameter '@' within double-quotes, as described in Special Parameters.
	 *
	 * The order of word expansion shall be as follows:
	 *
	 * 1. Tilde expansion (see Tilde Expansion), parameter expansion (see Parameter Expansion),
	 * command substitution (see Command Substitution), and arithmetic expansion (see Arithmetic Expansion)
	 * shall be performed, beginning to end. See item 5 in Token Recognition.
	 *
	 * 2. Field splitting (see Field Splitting) shall be performed on the portions of the fields generated by step 1,
	 * unless IFS is null.
	 *
	 * 3. Pathname expansion (see Pathname Expansion) shall be performed, unless set -f is in effect.
	 *
	 * 4. Quote removal (see Quote Removal) shall always be performed last.
	 *
	 * The expansions described in this section shall occur in the same shell environment as that in which the
	 * command is executed.
	 *
	 * If the complete expansion appropriate for a word results in an empty field, that empty field shall be
	 * deleted from the list of fields that form the completely expanded command, unless the original word
	 * contained single-quote or double-quote characters.
	 *
	 * The '$' character is used to introduce parameter expansion, command substitution, or arithmetic evaluation. If an unquoted '$' is followed by a character that is either not numeric, the name of one of the special parameters (see Special Parameters), a valid first character of a variable name, a left curly brace ( '{' ) or a left parenthesis, the result is unspecified.
	 *
	 * NOTE FE: Step 3. and 4. are not in correct order:
	 * The quotes have to be remove prior to or as part of Pathname expansion!!!
	 * i.e 'ls "x"* ' should list the same as 'ls x* ', and does with all shells i know.
	 * On the other hand, 'ls x\* ' should NOT list alls files starting with x.
	 * @return the expanded WORD as a list of fields
	 * @throws Exception
	 */
	public String[] doExpansion(final HshContext context) throws Exception {
		final List<String> ret=new ArrayList<String>();
		final List<String> pathExpansions=new ArrayList<String>();
		//final String s=NodeTraversal.substituteSubtree(this, context);
		String currentPortion="";
		for(final L1Node child : this) {
			final List<Portion> portions=new ArrayList<Portion>();
			final String substituted=child.substituteAndSplit(context, portions);
			for(int i=0; i<portions.size(); i++) {
				final Portion por=portions.get(i);
				currentPortion+=substituted.substring(por.off, por.off+por.len);
				pathExpansions.clear();
				ret.addAll(pathnameExpand(context, currentPortion));
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

	private Collection<String> pathnameExpand(final HshContext context, final String s) {
		// TODO implement Parser to parse pathname Expansions, and execute them
		// The parser need only implement all pathname patterns (/, *, ? and [...]).
		// Since the String s contains all characters
		return Arrays.asList(s);
	}

	@Override
	public String substituteAndSplit(final HshContext context, final List<Portion> portions) throws Exception {
		throw new RuntimeException("must not call this method on L2Token (need to refactor interfaces) :/");
	}
}
