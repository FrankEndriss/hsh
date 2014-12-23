Parsing is a multi-level prozess

Level:
1. Input is filtered for escaped NEWLINES, and "\r\n" are converted to "\n"
2. ($IFS)+ is converted to <blank> ???
3. Tokenizer splits input into WORDs and Token
4. Parser parses WORDs and Token
4a. Substitution takes place, WORDs are expanded
4b. Parser constructs are executed

new Parser(new Tokenizer(new SimplePushbackInput(new EscapedNewlineFilterReader(System.in))));
