package alphaparse.parser;

public sealed interface CombinatorTerminal extends Combinator permits CombinatorEpsilon, CombinatorTerminalRegexp, CombinatorTerminalString, CombinatorTerminalUnicodeChar {

}
