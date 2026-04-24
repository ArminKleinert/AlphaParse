package alphaparse.parser;

public sealed interface CombinatorTerminal extends Combinator permits EpsilonCombinator, TerminalRegexpCombinator, TerminalStringCombinator, TerminalUnicodeCharCombinator {

}
