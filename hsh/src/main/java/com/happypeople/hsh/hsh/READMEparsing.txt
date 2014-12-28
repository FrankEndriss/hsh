Parsing is a multi-level prozess

Level:
1. Input is filtered for escaped NEWLINES, and "\r\n" are converted to "\n"
2. L1Tokenizer finds $-constructs
3. L1Parser splits input into WORDs and Token
4. HshParser reads WORDs and Token from L1Parser
4a. Substitution takes place, WORDs are expanded
4b. Parser constructs are executed


new HshParser(new L2TokenManager(new L1Parser(new EscapedNewlineFilterReader(System.in))));

//new Parser(new Tokenizer(new SimplePushbackInput(new EscapedNewlineFilterReader(System.in))));
