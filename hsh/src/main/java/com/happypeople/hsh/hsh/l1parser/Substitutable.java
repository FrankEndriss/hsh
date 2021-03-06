package com.happypeople.hsh.hsh.l1parser;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.ParseException;


/** A L1Node which can and should be substituted to another String
 */
public interface Substitutable {
	/***********************************************************************************************
	 * 2.6 Word Expansions
	 * This section describes the various expansions that are performed on words. Not all expansions are performed on every word,
	 * as explained in the following sections.
	 * Tilde expansions, parameter expansions, command substitutions, arithmetic expansions, and quote removals that occur
	 * within a single word expand to a single field.
	 * It is only field splitting or pathname expansion that can create multiple fields from a single word. The single
	 * exception to this rule is the expansion of the special parameter '@' within double-quotes, as described in Special
	 * Parameters.
	 *
	 * The order of word expansion shall be as follows:
	 * Tilde expansion (see Tilde Expansion), parameter expansion (see Parameter Expansion), command substitution
	 * (see Command Substitution), and arithmetic expansion (see Arithmetic Expansion) shall be performed, beginning to end.
	 * See item 5 in Token Recognition.
	 *
	 * Field splitting (see Field Splitting) shall be performed on the portions of the fields generated by step 1,
	 * unless IFS is null.
	 * Pathname expansion (see Pathname Expansion) shall be performed, unless set -f is in effect.
	 * Quote removal (see Quote Removal) shall always be performed last.
	 * The expansions described in this section shall occur in the same shell environment as that in which the command
	 * is executed.
	 * If the complete expansion appropriate for a word results in an empty field, that empty field shall be deleted from
	 * the list of fields that form the completely expanded command, unless the original word contained single-quote or
	 * double-quote characters.
	 * The '$' character is used to introduce parameter expansion, command substitution, or arithmetic evaluation. If an
	 * unquoted '$' is followed by a character that is either not numeric, the name of one of the special parameters
	 * (see Special Parameters), a valid first character of a variable name, a left curly brace ( '{' ) or a left
	 * parenthesis, the result is unspecified.
	 * @return a stream of Strings, the result of the substitution
	 * @throws com.happypeople.hsh.hsh.l1parser.ParseException if any subcommand cannot be parsed, or
	 * @throws ParseException if any subcommand cannot be parsed if any subcommand cannot be parsed.
	 * ***********************************************************************************************
	 * This method returns what getImage() returns, but with substitutions substituted.
	 * @param env the environment
	 * @return the substituted version of this node
	 * @throws Exception shouldnt happen, but there could be Exceptions while execution of substitutions.
	 */
	public String getSubstitutedString(HshContext env) throws Exception;
}
