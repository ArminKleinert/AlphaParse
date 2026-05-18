package alphaparse.parser;

import alphaparse.Alpha;
import alphaparse.flat.FlatSeq;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.Sym;
import alphaparse.functions.Listener;
import alphaparse.functions.NegativeListener;
import alphaparse.functions.Procedure;
import alphaparse.reduction.ReductionType;
import alphaparse.result.*;
import alphaparse.result.failure.FailureUtil;
import alphaparse.result.success.AlphaParseMessage;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineMsgCacheKey;
import alphaparse.trampoline.TrampolineListenerNode;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.Tramp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.SequencedSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides the general parsing algorithms.
 */
public final class Gll {
    private final @NotNull Tramp tramp;
    private final boolean iterativeDeepening;

    Tramp tramp() {
        return tramp;
    }
    boolean iterativeDeepening() {
        return iterativeDeepening;
    }

    private Gll(final @NotNull Tramp tramp, final boolean iterativeDeepening) {
        this.tramp = tramp;
        this.iterativeDeepening = iterativeDeepening;
    }

    private @NotNull TrampolineListenerNode getOrCreateListenerNode(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        @Nullable TrampolineListenerNode node = tramp.getNode(nodeKey);

        if (node != null)
            return node;

        node = new TrampolineListenerNode();
        tramp.addToNodes(nodeKey, node);
        return node;
    }

    private boolean listenerExists_Q(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty();
    }

    private boolean fullListenerExists_Q(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty() || !node.fullListeners().isEmpty();
    }

    void pushNegativeListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey creator,
            final @NotNull NegativeListener negativeListener) {
        tramp.getNegativeListeners().put(creator.index(), negativeListener);
    }

    private void pushMessage(
            final @NotNull Listener listener,
            final @NotNull AlphaParseMessage result) {
        final int i = result.index();
        final TrampolineMsgCacheKey k = new TrampolineMsgCacheKey(i, listener);
        final int c = tramp.getFromMsgCache(k, 0);
        final Procedure f = () -> listener.execute(result);
        if (c > tramp.getGeneration()) {
            tramp.addToNextStack(f);
        } else {
            tramp.addToStack(f);
        }
        tramp.addToMsgCache(k, c + 1);
    }

    private void pushStack(final @NotNull Procedure item) {
        tramp.addToStack(item);
    }

    private void step() {
        final Procedure top = tramp.getStack().getLast();
        tramp.popStack();
        top.execute();
    }

    private @NotNull AlphaParsesResult.LazyResultList run() {
        return run(Integer.MAX_VALUE);
    }

    private @NotNull AlphaParsesResult.LazyResultList run(
            final int maxResults) {
        final var foundResult = new AtomicBoolean(false);
        return new AlphaParsesResult.LazyResultList((i) -> run(foundResult), maxResults);
    }

    private @Nullable ParseTree run(
            final @NotNull AtomicBoolean foundResult) {
        for (; ; ) {
            if (tramp.getSuccess() != null) {
                final @NotNull var successResult = tramp.getSuccess();
                final var resultTree = successResult.getResult();
                if (!(resultTree instanceof ParseTree))
                    throw new IllegalStateException(successResult.toString());
                tramp.setSuccess(null);
                foundResult.set(true);
                return (ParseTree) resultTree;
            }
            final @NotNull List<@NotNull Procedure> stack = tramp.getStack();
            if (!stack.isEmpty()) {
                step();
                continue; // Take it to the top.
            }
            var lastNegativeListener = tramp.pollAndRemovePreviousNegativeListener();
            if (lastNegativeListener != null) {
                lastNegativeListener.execute();
                continue; // Take it to the top.
            }
            if (foundResult.get()) {
                tramp.nextGeneration();
                foundResult.set(false);
                continue; // Take it to the top.
            }
            return null; // Fail
        }
    }

    void pushListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        final boolean listenerAlreadyExists = listenerExists_Q(nodeKey);
        final @NotNull TrampolineListenerNode node = getOrCreateListenerNode(nodeKey);
        final @NotNull List<Listener> listeners = node.listeners();
        listeners.add(listener);
        for (final @NotNull AlphaParseMessage result : node.results()) {
            pushMessage(listener, result);
        }
        for (final @NotNull AlphaParseMessage fullResult : node.fullResults()) {
            pushMessage(listener, fullResult);
        }
        if (!listenerAlreadyExists) {
            pushStack(() -> nodeKey.parser().parse(nodeKey.index(), this));
        }
    }

    void pushFullListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        //GllParsers.pushFullListenerCallback.invoke(tramp, nodeKey, listener);
        final var fullListenerAlreadyExists = fullListenerExists_Q(nodeKey);
        final @NotNull var node = getOrCreateListenerNode(nodeKey);
        final @NotNull var listeners = node.fullListeners();
        listeners.add(listener);
        for (final @NotNull AlphaParseMessage fullResult : node.fullResults()) {
            pushMessage(listener, fullResult);
        }
        if (!fullListenerAlreadyExists) {
            pushStack(() -> nodeKey.parser().fullParse(nodeKey.index(), this));
        }
    }

    /**
     * Pushes a result into the trampoline's node.
     * Categorizes as either result or full result.
     * Schedules notification to all existing listeners of result.
     * (Full listeners only get notified about full results)
     */
    private void pushResultHelper(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            @NotNull AlphaParseMessage result) {
        final @NotNull TrampolineListenerNode node = getOrCreateListenerNode(nodeKey);
        final @NotNull Combinator parser = nodeKey.parser();
        if (parser.isHidden()) {
            result = result.reset();
        }
        if (parser.getReduction().getReductionType() != ReductionType.ReductionTypesAvailable.INITIAL) {
            final ParseTree tree = ParseTree.create(
                    parser.getReduction().getKey(),
                    result.getResult());
            result = AlphaParseMessage.create(result.index(), tree);
        }

        final boolean reachedEndOfInput = tramp.getText().length() == result.index();
        final @NotNull SequencedSet<@NotNull AlphaParseMessage> results =
                reachedEndOfInput ? node.fullResults() : node.results();

        final var resultExisted = !results.add(result);
        if (resultExisted) {
            return;
        }

        for (final @NotNull Listener listener : node.listeners()) {
            pushMessage(listener, result);
        }

        if (!reachedEndOfInput) {
            return;
        }

        for (final @NotNull Listener fullListener : node.fullListeners()) {
            pushMessage(fullListener, result);
        }
    }

    private void startParser(
            final @NotNull Tramp tramp,
            final @NotNull Combinator parser,
            final boolean partial) {
        if (partial) {
            pushListener(new TrampolineListenerKey(0, parser), tramp::setSuccess);
        } else {
            pushFullListener(new TrampolineListenerKey(0, parser), tramp::setSuccess);
        }
    }

    void pushErrorMessage(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull ParseFailureNode result,
            final int end) {
        pushResultHelper(nodeKey, AlphaParseMessage.create(end, result));
    }

    void pushSuccessMessage(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull String result,
            final int end) {
        final @NotNull AlphaParseMessage aps;
        aps = AlphaParseMessage.create(end, result);
        pushResultHelper(nodeKey, aps);
    }

    void pushSuccessMessageWithoutValue(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final int end) {
        pushResultHelper(nodeKey, AlphaParseMessage.create(end));
    }

    <T> void pushSuccessMessage(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull FlatSeq<T> result,
            final int end) {
        final @NotNull AlphaParseMessage aps;
        aps = AlphaParseMessage.create(end, result);
        pushResultHelper(nodeKey, aps);
    }

    void fail(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final int index,
            final @NotNull ParseFailureReason reason) {
        //Objects.requireNonNull(tramp.getFailure());
        tramp.setFailure(FailureUtil.modifyFailureByIndex(tramp.getFailure(), reason, index));
        if (index == tramp.getFailIndex()) {
            final @NotNull String subSeq = tramp.getText().substring(index);
            final int textLen = tramp.getText().length();
            pushErrorMessage(
                    nodeKey,
                    buildFailureNode(Sym.sym("failure"), subSeq, index, tramp.getText().length()),
                    textLen);
        }
    }

    private @NotNull ParseFailureNode buildFailureNode(
            final @NotNull Sym key,
            final @NotNull String text,
            final int start,
            final int end) {
        return new ParseFailureNode(text, key, start, end);
    }

    private static @NotNull AlphaParsesResult parsesTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var tramp = new Tramp(grammar, text, 0);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp, iterativeDeepening);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run();
        return AlphaParsesResult.make(allParses);
    }

    @NotNull Listener nodeListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        return result -> pushResultHelper(nodeKey, result);
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parses(Parser, String)} or {@link Alpha#parses(Parser, String, ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest.
     * @see Alpha#parses(Parser, String)
     * @see Alpha#parse(Parser, String, ParsingOptions)
     */
    public static @NotNull AlphaParsesResult parses(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp, iterativeDeepening);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run();
        return AlphaParsesResult.make(allParses);
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parsesOrFailure(Parser, String, ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest or failure.
     * @see Alpha#parsesOrFailure(Parser, String, ParsingOptions)
     */
    public static @NotNull AlphaParsesResult parsesOrFailure(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp, iterativeDeepening);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run();
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            @NotNull AlphaParseFailure apf = FailureUtil.augmentFailure(tramp.getFailure(), text);
            return AlphaParsesResult.make(apf);
        }
        return AlphaParsesResult.make(allParses);
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parse(Parser, String)} or {@link Alpha#parse(Parser, String, ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse tree or failure.
     * @see Alpha#parse(Parser, String)
     * @see Alpha#parse(Parser, String, ParsingOptions)
     */
    public static @NotNull AlphaParseResult parse(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var tramp = new Tramp(grammar, text);
        var gll = new Gll(tramp, iterativeDeepening);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run(1);
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            return AlphaParseResult.make(FailureUtil.augmentFailure(tramp.getFailure(), text));
        }
        return AlphaParseResult.make(allParses.getFirst());
    }

    /**
     * 4This method should not be called directly. Use {@link Alpha#parses(Parser, String, ParsingOptions)} with {@link ParsingOptions#isTotal()} set to true instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest.
     * @see Alpha#parses(Parser, String, ParsingOptions)
     * @see ParsingOptions#isTotal()
     */
    public static @NotNull AlphaParsesResult parsesTotal(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var allParses = parses(grammar, start, text, partial, iterativeDeepening);
        if (!allParses.castToParsesSuccess().isEmpty()) return AlphaParsesResult.make(allParses);
        return parsesTotalAfterFail(grammar, start, text, partial, iterativeDeepening);
    }

    private static @NotNull AlphaParseResult parseTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final int failIndex,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var tramp = new Tramp(grammar, text, failIndex);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp, iterativeDeepening);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run(1);
        if (!allParses.isEmpty())
            return AlphaParseResult.make(allParses.getFirst());
        return gll.buildFailureNode(start, text, 0, text.length());
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parse(Parser, String, ParsingOptions)} with {@link ParsingOptions#isTotal()} set to true instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse tree or failure.
     * @see Alpha#parse(Parser, String, ParsingOptions)
     * @see ParsingOptions#isTotal()
     */
    public static @NotNull AlphaParseResult parseTotal(
            final @NotNull Grammar grammar,
            final @NotNull Sym start,
            final @NotNull String text,
            final boolean partial,
            final boolean iterativeDeepening) {
        final @NotNull var result = parse(grammar, start, text, partial, iterativeDeepening);
        if (!(result instanceof AlphaParseFailure)) return result;
        return parseTotalAfterFail(grammar, start, text, ((AlphaParseFailure) result).index(), partial, iterativeDeepening);
    }
}
