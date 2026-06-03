package alphaparse.parsing.rule_factory;

import alphaparse.Sym;
import alphaparse.parsing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * As long as this buffer exists, the following is true:
 * {@code if (Objects.equals(x, y)) { buffer.getOrAdd(x) == buffer.getOrAdd(y); }}
 */
final class BufferForRules {
    BufferForRules() {
    }

    @NotNull Rule getOrAdd(final @NotNull Rule c1) {
        return switch (c1) {
            case NonTerminal nonTerminal -> getOrAdd(nonTerminal);
            case RegexTerm regexTerm -> getOrAdd(regexTerm);
            case StringTerm stringTerm -> getOrAdd(stringTerm);
            case AlternationRule alternationRule -> getOrAdd(alternationRule);
            case ConcatRule concatRule -> getOrAdd(concatRule);
            case OptionalRule optionalRule -> getOrAdd(optionalRule);
            case OrderedChoiceRule orderedChoiceRule -> getOrAdd(orderedChoiceRule);
            case OnceOrMoreRule onceOrMoreRule -> getOrAdd(onceOrMoreRule);
            case VariableRepetitionRule variableRepetitionRule -> getOrAdd(variableRepetitionRule);
            case ZeroOrMoreRule zeroOrMoreRule -> getOrAdd(zeroOrMoreRule);
            case ValueRangeTerm valueRangeTerm -> getOrAdd(valueRangeTerm);
            case NegativeLookaheadRule negativeLookaheadRule -> getOrAdd(negativeLookaheadRule);
            case LookaheadRule lookaheadRule -> getOrAdd(lookaheadRule);
            case EpsilonTerm epsilonTerm -> epsilonTerm;
            case SpecialSequenceRule specialSequenceRule -> specialSequenceRule;
            case ExclusionRule exclusionRule -> getOrAdd(exclusionRule);
            case EOFTerm eofTerm -> eofTerm;
        };
    }

    private final @NotNull Map<@NotNull NonTerminal, @NotNull NonTerminal>
            nonTerminalMap = new HashMap<>();

    @NotNull NonTerminal getOrAdd(final @NotNull NonTerminal nonTerminal) {
        final var temp = nonTerminalMap.putIfAbsent(nonTerminal, nonTerminal);
        return temp == null ? nonTerminal : temp;
    }

    private final @NotNull Map<@NotNull ExclusionRule, @NotNull ExclusionRule>
            exclusionRuleMap = new HashMap<>();

    @NotNull ExclusionRule getOrAdd(final @NotNull ExclusionRule exclusionRule) {
        final var temp = exclusionRuleMap.putIfAbsent(exclusionRule, exclusionRule);
        return temp == null ? exclusionRule : temp;
    }

    private final @NotNull Map<@NotNull RegexTerm, @NotNull RegexTerm>
            regexTermMap = new HashMap<>();

    @NotNull RegexTerm getOrAdd(final @NotNull RegexTerm rule) {
        final var temp = regexTermMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull StringTerm, @NotNull StringTerm>
            stringTermMap = new HashMap<>();

    @NotNull StringTerm getOrAdd(final @NotNull StringTerm rule) {
        final var temp = stringTermMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull AlternationRule, @NotNull AlternationRule>
            alternationRuleMap = new HashMap<>();

    @NotNull AlternationRule getOrAdd(final @NotNull AlternationRule rule) {
        final var temp = alternationRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull ConcatRule, @NotNull ConcatRule>
            concatRuleMap = new HashMap<>();

    @NotNull ConcatRule getOrAdd(final @NotNull ConcatRule rule) {
        final var temp = concatRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull OptionalRule, @NotNull OptionalRule>
            optionalRuleMap = new HashMap<>();

    @NotNull OptionalRule getOrAdd(final @NotNull OptionalRule rule) {
        final var temp = optionalRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull OrderedChoiceRule, @NotNull OrderedChoiceRule>
            orderedChoiceRuleMap = new HashMap<>();

    @NotNull OrderedChoiceRule getOrAdd(final @NotNull OrderedChoiceRule rule) {
        final var temp = orderedChoiceRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull OnceOrMoreRule, @NotNull OnceOrMoreRule>
            onceOrMoreRuleMap = new HashMap<>();

    @NotNull OnceOrMoreRule getOrAdd(final @NotNull OnceOrMoreRule rule) {
        final var temp = onceOrMoreRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull VariableRepetitionRule, @NotNull VariableRepetitionRule>
            variableRepetitionRuleMap = new HashMap<>();

    @NotNull VariableRepetitionRule getOrAdd(final @NotNull VariableRepetitionRule rule) {
        final var temp = variableRepetitionRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull ZeroOrMoreRule, @NotNull ZeroOrMoreRule>
            zeroOrMoreRuleMap = new HashMap<>();

    @NotNull ZeroOrMoreRule getOrAdd(final @NotNull ZeroOrMoreRule rule) {
        final var temp = zeroOrMoreRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull ValueRangeTerm, @NotNull ValueRangeTerm>
            valueRangeTermMap = new HashMap<>();

    @NotNull ValueRangeTerm getOrAdd(final @NotNull ValueRangeTerm rule) {
        final var temp = valueRangeTermMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull EpsilonTerm, @NotNull EpsilonTerm>
            epsilonTermMap = new HashMap<>();

    @NotNull EpsilonTerm getOrAdd(final @NotNull EpsilonTerm rule) {
        final var temp = epsilonTermMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull NegativeLookaheadRule, @NotNull NegativeLookaheadRule>
            negativeLookaheadRuleMap = new HashMap<>();

    @NotNull NegativeLookaheadRule getOrAdd(final @NotNull NegativeLookaheadRule rule) {
        final var temp = negativeLookaheadRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull LookaheadRule, @NotNull LookaheadRule>
            lookaheadRuleMap = new HashMap<>();

    @NotNull LookaheadRule getOrAdd(final @NotNull LookaheadRule rule) {
        final var temp = lookaheadRuleMap.putIfAbsent(rule, rule);
        return temp == null ? rule : temp;
    }

    private final @NotNull Map<@NotNull Sym, @NotNull NonTerminal>
            symToNtSet = new HashMap<>();

    @Nullable NonTerminal nt(@NotNull Sym keyword) {
        return symToNtSet.get(keyword);
    }

    void putNt(@NotNull NonTerminal temp) {
        symToNtSet.put(temp.getKeyword(), temp);
    }
}
