package com.happypeople.hsh.hsh;

import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;

import com.happypeople.hsh.HshContext;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListener;
import com.happypeople.hsh.hsh.NodeTraversal.TraverseListenerResult;
import com.happypeople.hsh.hsh.l1parser.DollarSubstNode;
import com.happypeople.hsh.hsh.l1parser.ImageHolder;
import com.happypeople.hsh.hsh.l1parser.L1Node;
import com.happypeople.hsh.hsh.l1parser.L1Parser;
import com.happypeople.hsh.hsh.l1parser.L2TokenManager;
import com.happypeople.hsh.hsh.l1parser.SimpleDumpTarget;
import com.happypeople.hsh.hsh.l1parser.SimpleImageHolder;
import com.happypeople.hsh.hsh.l1parser.StringifiableNode;
import com.happypeople.hsh.hsh.parser.CompleteCommand;
import com.happypeople.hsh.hsh.parser.SimpleCommand;

public class HshParserTest {
	public final static Logger log = Logger.getLogger(HshParserTest.class);
	public HshContext context;
	public SimpleDumpTarget dumpTarget;


	@Before
	public void init_setup() {
		context=new HshContextBuilder().create();
		dumpTarget=new SimpleDumpTarget();
	}

	public HshParser setup(final String input) {
		final L2TokenManager tokMgr=new L2TokenManager(new L1Parser(new StringReader(input)));
		final HshParser parser=new HshParser(tokMgr);
		return parser;
	}




	public static String getSubstitutedString(final DollarSubstNode node, final HshContext context) throws Exception {
		final ImageHolder imageHolder=new SimpleImageHolder();
		final L1Node resultNode=node.transformSubstitution(imageHolder, context);
		return resultNode.getImage();
	}



	public static <T> T findFirstNodeOfClass(final CompleteCommand cc, final Class<T> class1) throws Exception {
		final List<T> listT=new ArrayList<T>();

		NodeTraversal.traverse(cc, new TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(class1.isAssignableFrom(node.getClass())) {
					listT.add((T)node);
					return TraverseListenerResult.STOP;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		return listT.size()>0?listT.get(0):null;
	}





	/** This method parses the input to count CompleteCommand and returns these.
	 * All in all it calls parseTo_CompleteCommand count times
	 * @param input an complete command script with at least count complete commands
	 * @return a CoompleteCommand array of length count
	 * @throws ParseException
	 */
	public CompleteCommand[] parseTo_CompleteCommandList(final String input, final int count) throws ParseException {
		final CompleteCommand[] res=new CompleteCommand[count];
		final HshParser p=setup(input);
		log.debug("Test input: "+input);
		for(int i=0; i<count; i++)
			res[i]=p.complete_command();
		for(int i=0; i<count; i++)
			if(res[i]!=null)
				res[i].dump(dumpTarget);
		dumpTarget.debug(log);
		return res;
	}

	/** This method parses the input to one CompleteCommand and returns that
	 * @param input an complete command line
	 * @return a CoompleteCommand object
	 * @throws ParseException
	 */
	public CompleteCommand parseTo_CompleteCommand(final String input) throws ParseException {
		final HshParser p=setup(input);
		log.debug("Test input: "+input);
		final CompleteCommand cc=p.complete_command();
		final SimpleDumpTarget target=new SimpleDumpTarget();
		cc.dump(target);
		log.debug(target.toString());
		return cc;
	}

	public static SimpleCommand findSimpleCommand(final CompleteCommand cc) throws Exception {
		return findSimpleCommands(cc, 1)[0];
	}

	public static SimpleCommand[] findSimpleCommands(final CompleteCommand cc, final int count) throws Exception {
		final int[] c={ 0 };
		final SimpleCommand[] sc=new SimpleCommand[count];
		NodeTraversal.traverse(cc, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof SimpleCommand) {
					c[0]++;
					if(c[0]>count)
						fail("found one more SimpleCommand than: "+count);
					sc[c[0]-1]=(SimpleCommand)node;
				}
				return TraverseListenerResult.CONTINUE;
			}
		});

		if(sc[0]==null)
			fail("didnt found SimpleCommand");

		if(c[0]<count)
			fail("found less SimpleCommands than: "+count);

		return sc;
	}

	public static String node2String(final L1Node node) throws Exception {
		final StringBuilder sb=new StringBuilder();
		NodeTraversal.traverse(node, new NodeTraversal.TraverseListener() {
			@Override
			public TraverseListenerResult node(final L1Node node, final int level) {
				if(node instanceof StringifiableNode)
					((StringifiableNode)node).append(sb);
				return TraverseListenerResult.CONTINUE;
			}
		});
		return sb.toString();
	}

}
