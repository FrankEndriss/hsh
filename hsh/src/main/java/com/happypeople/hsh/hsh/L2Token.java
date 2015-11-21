package com.happypeople.hsh.hsh;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.ComplexL1Node;
import com.happypeople.hsh.hsh.l1parser.DumpTarget;
import com.happypeople.hsh.hsh.l1parser.GenericComplexL1Node;
import com.happypeople.hsh.hsh.l1parser.ImageHolder;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.QuotedL1Node;
import com.happypeople.hsh.hsh.l1parser.SimpleImageHolder;

/** A L2Token extends Token to have:
 * -a list of L1Nodes as childs
 * -an image
 * A L2Token has an building phase and a usage phase. While building the image is in an StringBuilder, afterwards
 * in an String();
 *
 * TODO L2Token should not extend L1Node. Instead, the interface L1Node should be split into the Image-Part and the Node-part,
 * then the Node-part should be defined as a Interface HshNode.
 * Then, all Nodes should implement HshNode like they do implement L1Node by now.
 */
public class L2Token extends Token implements L1Node {
	private final static boolean DEBUG=false;
	private List<L1Node> parts=new ArrayList<L1Node>();
	//private final StringBuilder sb=new StringBuilder();

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
		final L2Token tok=new L2Token();
		tok.kind=kind;

		// copy all parts but the first to new Token
		for(int i=1; i<getPartCount(); i++)
			tok.addPart(getPart(i));

		// remove all but the first part from this
		parts.clear();
		parts.add(firstPart);

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
	public void dump(final DumpTarget target) {
		target.add(getClass().getName()+" kind="+kind+" image="+getImage());
		target.incLevel();
		for(final L1Node child : parts)
			child.dump(target);
		target.decLevel();
	}

	/** Switches from building phase to usage phase.
	 */
	/*
	public void finishImage() {
		image=sb.toString();
		sb=null;
	}
	*/

	@Override
	public String toString() {
		return getClass().getSimpleName()+" image="+getImage();
	}

	/** Call in usage phase.
	 * @return the image of this token
	 */
	@Override
	public String getImage() {
		final StringBuilder sb=new StringBuilder();
		for(final L1Node part : parts)
			sb.append(part.getImage());
		return sb.toString();
		//return sb!=null?sb.toString():image;
	}

	@Override
	public int getLen() {
		return getImage().length();
		//return sb!=null?sb.length():image.length();
	}

	/** Appends a String to the image and returns this.
	 * @param str
	@Override
	 */
	/*
	public L2Token append(final CharSequence str) {
		sb.append(str);
		return this;
	}
	*/

	/** Appends the buffer content to the image and returns this.
	 * @param buf
	 * @param off
	 * @param len
	 * See StringBuilder.append(buf, off, len)
	@Override
	 */
	/*
	public L2Token append(final char[] buf, final int off, final int len) {
		sb.append(buf, off, len);
		return this;
	}
	*/

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
	 ************************************************
	 * Implementation:
	 * The implementation is done implemented as some transformations on the tree, with a last step
	 * of transforming the tree nodes into Strings.
	 * ************************************************
	 * @return the expanded WORD as a list of fields
	 * @throws Exception
	 */
	public List<String> doExpansion(final HshContext context) throws Exception {
		final L1Node substituted=transformSubstitution(new SimpleImageHolder(), context);
		final List<? extends L1Node> splitted=substituted.transformSplit(context);
		return pathnameExpand(splitted, context);
	}

	/** Transforms this node-tree into n trees. These n trees are the splitted ones.
	 * @param context context of splitting (IFS)
	 * @return a list of at least on L1Node. If it is one most likely the one is this.
	 * If more than one, this L1Node was splitted and the list contains the parts.
	 * This method uses GenericComplexL1Node to group childrens splits.
	 */
	@Override
	public List<? extends L1Node> transformSplit(final HshContext context) {
		// optimization
		if(getPartCount()==1)
			return getPart(0).transformSplit(context);

		final List<L1Node> ret=new ArrayList<L1Node>();
		GenericComplexL1Node currentSplit=new GenericComplexL1Node(new SimpleImageHolder(), 0, 0);
		for(final L1Node child : this) {
			final List<? extends L1Node> splits=child.transformSplit(context);
			if(splits.get(0).getLen()==0) {
				ret.add(currentSplit);
				currentSplit=new GenericComplexL1Node(new SimpleImageHolder(), 0, 0);
			}
			for(final L1Node childSplit : splits) {
				if(childSplit.getLen()>0) {
					currentSplit.add(childSplit);
					currentSplit.addLen(childSplit.getLen());
				} else if(currentSplit.getChildCount()>0) {
					ret.add(currentSplit);
					currentSplit=new GenericComplexL1Node(new SimpleImageHolder(), 0, 0);
				}
			}
		}
		if(ret.size()==0 || currentSplit.getChildCount()>0)
			ret.add(currentSplit);

		// note ret maybe includes empty L1 nodes here
		return ret;
	}

	/** This replaces all childs of this by
	 * the result of child.transformSubstitution(imageHolder, context).
	 * @param tok the image holder of
	 * @param context
	 * @return for syntactic reasons tok is returned.
	 * @throws Exception
	 */
	@Override
	public L2Token transformSubstitution(final ImageHolder imageHolder, final HshContext context) throws Exception {
		for(int i=0; i<parts.size(); i++)
			parts.set(i, parts.get(i).transformSubstitution(imageHolder, context));
		return this;
	}

	/**
	 * @param trees
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static List<String> pathnameExpand(final List<? extends L1Node> trees, final HshContext context) throws Exception {
		final List<String> ret=new ArrayList<String>();
		for(final L1Node tree : trees) {
			ret.addAll(pathnameExpand(tree, context));
		}
		return ret;
	}

	private static void finishCurrentPattern(final ArrayList<Path> matchedPaths, final StringBuilder pattern) throws IOException {
		if(DEBUG)
			System.out.println("finishCurrentPattern, pattern="+pattern);
		// on first call, matchedPaths is empty. Find to start on "/" or on "."
		boolean removePwd=false;
		if(matchedPaths.isEmpty()) {
			if(pattern.charAt(0)=='/')
				matchedPaths.add(Paths.get("/"));
			else {
				matchedPaths.add(Paths.get("."));
				removePwd=true;
			}
		}

		if(DEBUG)
			System.out.println("finishCurrentPattern, matchedPaths="+matchedPaths);

		final List<Path> nextMatches=new ArrayList<Path>();
		for(final Path dir : matchedPaths)
			if(Files.isDirectory(dir))
				try(DirectoryStream<Path> ds=Files.newDirectoryStream(dir, pattern.toString())) {
					for(final Path path : ds)
						nextMatches.add(path);
				}
		if(DEBUG)
			System.out.println("finishCurrentPattern, nextMatches="+nextMatches);
		pattern.setLength(0);
		matchedPaths.clear();
		for(final Path p : nextMatches)
			matchedPaths.add(removePwd?p.subpath(1, p.getNameCount()):p);
	}

	private static Collection<String> pathnameExpand(final L1Node tree, final HshContext context) throws Exception {
		// **************************************************
		// *. Break tree into parts separated by slashes (escaped or unescaped), since the slash separates
		//    patterns and directories. Translate these parts into java glob patterns (these are fairly the same
		//    as the posix shell ones).
		//    Translate escaped parts by simply escape any char with a backslash.

		// *. If the first part is empty start at root ("/"), else start at CWD.

		// while breaking the tree, for every part:
		// *. If on the last part list files and directories, else only directories.
		// *. List the files/directories using the part as glob pattern.
		//    If none found, finish.
		//    If found, use the found directories for the next part and so on.

		// **************************************************

		final ArrayList<Path> matchedPaths=new ArrayList<Path>();
		final StringBuilder currentPattern=new StringBuilder();

		NodeTraversal.traverse(tree, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) throws IOException {
				// make a String out of child
				if(node instanceof QuotedL1Node) {
					final StringBuilder sb=new StringBuilder();
					node.appendUnquoted(sb);
					final String[] parts=sb.toString().split("/");
					if(parts.length==1) { // no match
						currentPattern.append(sb);
					} else {
						for(int i=0; i<parts.length-1; i++) {	// for all but the last part
							currentPattern.append(parts[i]);
							finishCurrentPattern(matchedPaths, currentPattern);
						}
						currentPattern.append(parts[parts.length-1]);
					}
				} else if(!(node instanceof ComplexL1Node)) { // node isLeaf()
					final String str=node.getImage();
					final String[] parts=str.split("/");
					if(parts.length==1) { // no match
						currentPattern.append(str);
					} else {
						for(int i=0; i<parts.length-1; i++) {	// for all but the last part
							currentPattern.append(parts[i]);
							finishCurrentPattern(matchedPaths, currentPattern);
						}
						currentPattern.append(parts[parts.length-1]);
					}
				} // else it is another Subtree, go on iterate the children
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(currentPattern.length()>0)
			finishCurrentPattern(matchedPaths, currentPattern);

		final List<String> ret=new ArrayList<String>();
		if(matchedPaths.isEmpty()) {
			final StringBuilder sb=new StringBuilder();
			tree.appendUnquoted(sb);
			ret.add(sb.toString());
		} else
			for(final Path path : matchedPaths)
				ret.add(path.toString());
		return ret;
	}

	@Override
	public void appendUnquoted(final StringBuilder sb) {
		for(final L1Node child : this)
			child.appendUnquoted(sb);
	}

	@Override
	public L2Token copySubtree() {
		final L2Token ret=new L2Token();
		//ret.append(getImage());
		//if(sb==null)
		//	ret.finishImage();
		for(final L1Node child : this)
			ret.addPart(child.copySubtree());
		return ret;
	}

}
