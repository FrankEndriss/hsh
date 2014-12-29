Hsh-Parsing is a multi-level prozess

Level:
1. Input is filtered for escaped NEWLINES, and "\r\n" are converted to "\n"
2. L1Parser/Tokenizer finds/creates parts of words, each one represented by a tree of L1Nodes
3. L2TokenManager facades the trees of L1Nodes into L2Token, grouped by defined rules into WORDs
4. HshParser reads WORDs-Token from L2TokenManager
5. HshParser creates commands from these Token according to Sh-Grammar
6. Expansion/Substitution takes place on the trees of L1Nodes
7. HshParser constructs are executed


new HshParser(new L2TokenManager(new L1Parser(new EscapedNewlineFilterReader(System.in))));

//new Parser(new Tokenizer(new SimplePushbackInput(new EscapedNewlineFilterReader(System.in))));
