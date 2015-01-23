package com.happypeople.hsh.hsh.l1parser;

/** Marker interface for L1Nodes with quoted content.
 * The sematics are: This node contains only quoted chars, they must not be interpreted as special chars
 * whereever they could be special.
 * Of course, there is a exception: the slash ("/") is allways a slash in pattern matching, there is no
 * way to quote a slash in pathnames. (And therefore there is no pattern to match a filename containing
 * a slash).
 * The method appendUnquoted(StringBuilder) could be moved to this interface, since on not quoted nodes
 * it returns the same as getImage().
 *
 * As of 2015-01-23 there are three quoted L1Node classes:
 * BackslashQuotedL1Node
 * DQuotedL1Node
 * SQuotedL1Node
 */
public interface QuotedL1Node {

}
