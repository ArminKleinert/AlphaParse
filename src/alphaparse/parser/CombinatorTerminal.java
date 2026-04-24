package alphaparse.parser;

/**
 *  TODO
 */
public sealed interface CombinatorTerminal extends Combinator permits EpsilonCombinator, TerminalRegexpCombinator, TerminalStringCombinator, TerminalUnicodeCharCombinator {
}
