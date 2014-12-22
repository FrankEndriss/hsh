package com.happypeople.hsh.hsh;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.happypeople.hsh.hsh.SimplePushbackInput.Transaction;
import com.happypeople.hsh.hsh.Tokenizer.Tok;

/** Parser of hsh. Implements the grammar described in
 * 
%token  WORD
%token  ASSIGNMENT_WORD
%token  NAME
%token  NEWLINE
%token  IO_NUMBER


%token  AND_IF    OR_IF    DSEMI
//      '&&'      '||'     ';;'


%token  DLESS  DGREAT  LESSAND  GREATAND  LESSGREAT  DLESSDASH
//      '<<'   '>>'    '<&'     '>&'      '<>'       '<<-'


%token  CLOBBER
//      '>|'


// The following are the reserved words.


%token  If    Then    Else    Elif    Fi    Do    Done
//      'if'  'then'  'else'  'elif'  'fi'  'do'  'done'


%token  Case    Esac    While    Until    For
//      'case'  'esac'  'while'  'until'  'for'


// These are reserved words, not operator tokens, and are
// recognized when reserved words are recognized.


%token  Lbrace    Rbrace    Bang
//      '{'       '}'       '!'


%token  In
//      'in'


// -------------------------------------------------------
//   The Grammar
// -------------------------------------------------------


%start  complete_command
%%
complete_command : list separator
                 | list
                 ;
list             : list separator_op and_or
                 |                   and_or
                 ;
and_or           :                         pipeline
                 | and_or AND_IF linebreak pipeline
                 | and_or OR_IF  linebreak pipeline
                 ;
pipeline         :      pipe_sequence
                 | Bang pipe_sequence
                 ;
pipe_sequence    :                             command
                 | pipe_sequence '|' linebreak command
                 ;
command          : simple_command
                 | compound_command
                 | compound_command redirect_list
                 | function_definition
                 ;
compound_command : brace_group
                 | subshell
                 | for_clause
                 | case_clause
                 | if_clause
                 | while_clause
                 | until_clause
                 ;
subshell         : '(' compound_list ')'
                 ;
compound_list    :              term
                 | newline_list term
                 |              term separator
                 | newline_list term separator
                 ;
term             : term separator and_or
                 |                and_or
                 ;
for_clause       : For name linebreak                            do_group
                 | For name linebreak in          sequential_sep do_group
                 | For name linebreak in wordlist sequential_sep do_group
                 ;
name             : NAME                     // Apply rule 5
                 ;
in               : In                       // Apply rule 6
                 ;
wordlist         : wordlist WORD
                 |          WORD
                 ;
case_clause      : Case WORD linebreak in linebreak case_list    Esac
                 | Case WORD linebreak in linebreak case_list_ns Esac
                 | Case WORD linebreak in linebreak              Esac
                 ;
case_list_ns     : case_list case_item_ns
                 |           case_item_ns
                 ;
case_list        : case_list case_item
                 |           case_item
                 ;
case_item_ns     :     pattern ')'               linebreak
                 |     pattern ')' compound_list linebreak
                 | '(' pattern ')'               linebreak
                 | '(' pattern ')' compound_list linebreak
                 ;
case_item        :     pattern ')' linebreak     DSEMI linebreak
                 |     pattern ')' compound_list DSEMI linebreak
                 | '(' pattern ')' linebreak     DSEMI linebreak
                 | '(' pattern ')' compound_list DSEMI linebreak
                 ;
pattern          :             WORD         // Apply rule 4
                 | pattern '|' WORD         // Do not apply rule 4
                 ;
if_clause        : If compound_list Then compound_list else_part Fi
                 | If compound_list Then compound_list           Fi
                 ;
else_part        : Elif compound_list Then else_part
                 | Else compound_list
                 ;
while_clause     : While compound_list do_group
                 ;
until_clause     : Until compound_list do_group
                 ;
function_definition : fname '(' ')' linebreak function_body
                 ;
function_body    : compound_command                // Apply rule 9
                 | compound_command redirect_list  // Apply rule 9
                 ;
fname            : NAME                            // Apply rule 8
                 ;
brace_group      : Lbrace compound_list Rbrace
                 ;
do_group         : Do compound_list Done           // Apply rule 6
                 ;
simple_command   : cmd_prefix cmd_word cmd_suffix
                 | cmd_prefix cmd_word
                 | cmd_prefix
                 | cmd_name cmd_suffix
                 | cmd_name
                 ;
cmd_name         : WORD                   // Apply rule 7a
                 ;
cmd_word         : WORD                   // Apply rule 7b
                 ;
cmd_prefix       :            io_redirect
                 | cmd_prefix io_redirect
                 |            ASSIGNMENT_WORD
                 | cmd_prefix ASSIGNMENT_WORD
                 ;
cmd_suffix       :            io_redirect
                 | cmd_suffix io_redirect
                 |            WORD
                 | cmd_suffix WORD
                 ;
redirect_list    :               io_redirect
                 | redirect_list io_redirect
                 ;
io_redirect      :           io_file
                 | IO_NUMBER io_file
                 |           io_here
                 | IO_NUMBER io_here
                 ;
io_file          : '<'       filename
                 | LESSAND   filename
                 | '>'       filename
                 | GREATAND  filename
                 | DGREAT    filename
                 | LESSGREAT filename
                 | CLOBBER   filename
                 ;
filename         : WORD                      // Apply rule 2
                 ;
io_here          : DLESS     here_end
                 | DLESSDASH here_end
                 ;
here_end         : WORD                      // Apply rule 3
                 ;
newline_list     :              NEWLINE
                 | newline_list NEWLINE
                 ;
linebreak        : newline_list
                 | // empty
                 ;
separator_op     : '&'
                 | ';'
                 ;
separator        : separator_op linebreak
                 | newline_list
                 ;
sequential_sep   : ';' linebreak
                 | newline_list
                 ;

 *
 *
 *... and so on.
 * Better try using JavaCC
 */
public class Parser {

	/** newline_list	:	NEWLINE
	 * 					| newline_list NEWLINE
	 * ;
	 */
	public class Newline_list extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			Tok.NEWLINE.parse(in);
			try {
				while(true)
					Tok.NEWLINE.parse(in);
			}catch(NotParsedException e) {
				// empty since optional
			}
		}
	}

	/** linebreak	: newline_list
	 * 				| // empty
	 * ;
	 * Its an optional newline_list
	 */
	public class Linebreak extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			new Newline_list().parseTransactional();
		}
	}

	/** separator	: separator_op linebreak
	 * 				| newline_list
	 * ;
	 */
	public class Separator extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			// rule1
			Transaction trans=in.transactionStart();
			try {
				Node separator_op=new Separator_op();
				separator_op.parse();
				Node linebreak=new Linebreak();
				linebreak.parse();
				addChild(separator_op);
				addChild(linebreak);
				trans.commit();
				return;
			}catch(NotParsedException e) {
				trans.rollback();
			}
			
			// else it must be a newline_list
			SimpleNode newline_list=new Newline_list();
			newline_list.parse();
			addChild(newline_list);
		}
	}

	public class Simple_command extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			throw new RuntimeException("not implemented");
		}
	}

	public class Compound_command extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			throw new RuntimeException("not implemented");
		}
	}

	public class Redirect_list extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			throw new RuntimeException("not implemented");
		}
	}

	/** command		: simple_command
	 * 				| compound_command
	 * 				| compound_command redirect_list
	 * 				| function_definition
	 * ;
	 */
	public class Command extends SimpleNode {

		@Override
		public void parse() throws NotParsedException {
			Transaction trans=in.transactionStart();
			try { // rule1
				final SimpleNode simple_command=new Simple_command();
				simple_command.parse();
				trans.commit();
				addChild(simple_command);
				return;
			} catch(NotParsedException e) {
				trans.rollback();
			}

			trans=in.transactionStart();
			try { // rule2 + rule3
				final SimpleNode compound_command=new Compound_command();
				compound_command.parse();
				trans.commit();
				addChild(compound_command);

				// optional Redirect_list()
				try {
					final SimpleNode redirect_list=new Redirect_list();
					redirect_list.parseTransactional();
					addChild(redirect_list);
				}catch(NotParsedException e2) {
					// ignore since optional
				}

				return;
			} catch (NotParsedException e) {
				trans.rollback();
			}

			// TODO functional_definition
		}

	}

	/** original:
	 * pipe_sequence	:	command
	 * 					| pipe_sequence '|' linebreak command
	 * ;
	 * rewritten to:
	 * pipe_sequence	:	command
	 * 					| command '|' linebreak pipe_sequence
	 * ;
	 */
	public class Pipe_sequence extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			SimpleNode command=new Command();
			command.parse();
			addChild(command);
			
			Transaction trans=in.transactionStart();
			try {
				Tok.PIPE.parse(in);
				new Linebreak().parse();
				SimpleNode pipe_sequence=new Pipe_sequence();
				pipe_sequence.parse();
				addChild(pipe_sequence);
				trans.commit();
			}catch(NotParsedException e) {
				trans.rollback();
			}
		}
	}

	/** pipeline	:	pipe_sequence
	 * 				| Bang pipe_sequence
	 * 	;
	 * Optional Bang followed by a pipe_sequence
	 */
	public class Pipeline extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			try {
				Tok.BANG.parse(in);
				addChild(new TokenNode(Tok.BANG));
			}catch(NotParsedException e) {
				// ignore since optional
			}
			SimpleNode pipe_sequence=new Pipe_sequence();
			pipe_sequence.parse();
			addChild(pipe_sequence);
		}
	}

	/** original: 
	 * and_or :		pipeline
	 * 				| and_or AND_IF linebreak pipeline
	 * 				| and_or OR_IF  linebreak pipeline
	 *  ;
	 *  
	 *  rewritten to:
	 * and_or :		pipeline
	 * 				| pipeline AND_IF linebreak and_or
	 * 				| pipeline OR_IF  linebreak and_or
	 *  ;
	 *
	 */
	public class And_Or extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			final Node pipeline=new Pipeline();
			pipeline.parse();
			addChild(pipeline);
			
			Transaction trans=in.transactionStart();
			try {

				SimpleNode tokNode=null;
				try {
					Tok.AND_IF.parse(in);
					tokNode=new TokenNode(Tok.AND_IF);
				}catch(NotParsedException e2) {
					Tok.AND_OR.parse(in);
					tokNode=new TokenNode(Tok.AND_OR);
				}

				final SimpleNode linebreak=new Linebreak();
				linebreak.parse();
				final SimpleNode and_or=new And_Or();
				and_or.parse();
				addChild(tokNode);
				addChild(linebreak);
				addChild(and_or);
			}catch(NotParsedException e) {
				trans.rollback();
			}
		}
	}

	/** Tries to read what from in.
	 * If false is returnded, all read chars are pushback()ed
	 * @param what what to read
	 * @return true if what was read, else false
	private boolean tryRead(String what) {
		final char[] toRead=what.toCharArray();
		for(int i=0; i<toRead.length; i++) {
			char c=in.read();
			if(c!=toRead[i]) {
				in.pushback(c);
				for(int j=i-1; j>=0; j--)
					in.pushback(toRead[j]);
				return false;
			}
		}
		return true;
	}
	 */

	/** separator_op     : '&' | ';'
	 * Child of Separator_op is a single TokenNode with a Tok of
	 * one of the above values.
	 */
	public class Separator_op extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			final char c=in.read();
			if(c==Tok.UPPERSANT.value()[0]) 
				addChild(new TokenNode(Tok.UPPERSANT));
			else if(c==Tok.SEMICOLON.value()[0])
				addChild(new TokenNode(Tok.SEMICOLON));
			else
				throw new NotParsedException();
		}
	}

	/** orig grammar:
	 * list 	: list separator_op and_or | and_or ;
	 * rewritten to:
	 * list 	: and_or separator_op list | and_or ;
	 */
	public class NList extends SimpleNode {

		@Override
		public void parse() throws NotParsedException {
			final And_Or and_or=new And_Or();
			and_or.parse();
			addChild(and_or);

			// optional separator_op list
			Transaction trans=null;
			try {
				trans=in.transactionStart();
				final Separator_op separator_op=new Separator_op();
				separator_op.parse();
				final NList sublist=new NList();
				sublist.parse();

				addChild(separator_op);
				addChild(sublist);
				trans.commit();
			}catch(final NotParsedException e) {
				trans.rollback();
			}
		}
	}

	/**
	 * complete_command : list separator | list ;
	 */
	public class CompleteCommand extends SimpleNode {
		@Override
		public void parse() throws NotParsedException {
			final NList nlist=new NList();
			nlist.parse();
			addChild(nlist);

			// optional separator
			final Separator lseparator=new Separator();
			try {
				lseparator.parseTransactional();
				addChild(nlist);
			}catch(final NotParsedException e) {
				// ignore, optional
			}
		}
	}

	/** Node used for "simple" Tokens
	 *
	 */
	public class TokenNode extends SimpleNode {
		private final Tok tok;

		public TokenNode(final Tok tok) {
			this.tok=tok;
		}

		@Override
		public void parse() throws NotParsedException {
			tok.parse(in);
		}
	}

	private final SimplePushbackInput in;

	public Parser(final Reader in) {
		this.in=new SimplePushbackInput(in);
	}

	public abstract class SimpleNode implements Node {
		private final List<Node> childs=new ArrayList<Node>();
		@Override
		public List<Node> children() {
			return childs;
		}

		public void addChild(final Node child) {
			childs.add(child);
		}
		
		/** This calls parse() within an Transaction.
		 * If NotParsedException is thrown the Transaction is rollbacked()ed. But the exception is rethrown either.
		 * Useful for optional constructs.
		 */
		public void parseTransactional() throws NotParsedException {
			Transaction trans=null;
			try {
				trans=in.transactionStart();
				parse();
				trans.commit();
			}catch(NotParsedException e) {
				trans.rollback();
				throw e;
			}
		}
	}

	/** Parser starts parsing here
	 * @return a CompleteCommand, ready to execute
	 * @throws NotParsedException
	 */
	public CompleteCommand start() throws NotParsedException {
		// TODO error-handling and recovery
		final CompleteCommand cc=new CompleteCommand();
		cc.parse();
		return cc;
	}

	public char read() throws EOFException {
		return in.read();
	}

}
