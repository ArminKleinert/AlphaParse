package alphaparse.parser;

import alphaparse.Alpha;
import alphaparse.Keyword;
import alphaparse.Reduction;
import alphaparse.functions.Listener;
import alphaparse.functions.NegativeListener;
import alphaparse.functions.Procedure;
import alphaparse.reduction.ReductionType;
import alphaparse.result.ParseTree;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import alphaparse.result.failure.FailureUtil;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.success.AlphaParseSuccess;
import alphaparse.result.failure.failureReason.ParseFailureReason;
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
 * TODO
 */
public final class Gll {
    private final @NotNull Tramp tramp;

    Tramp tramp() {
        return tramp;
    }

    private Gll(final @NotNull Tramp tramp) {
        this.tramp = tramp;
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

    private boolean totalSuccess_Q(
            final @NotNull AlphaParseSuccess success) {
        return tramp.getText().length() == success.index();
    }

    void pushNegativeListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey creator,
            final @NotNull NegativeListener negativeListener) {
        tramp.getNegativeListeners().put(creator.index(), negativeListener);
    }

    private void pushMessage(
            final @NotNull Listener listener,
            final @NotNull AlphaParseSuccess result) {
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
            if (!tramp.getNegativeListeners().isEmpty()) {
                final @NotNull var iter = tramp.getNegativeListeners().entrySet().iterator();
                final @NotNull var a = iter.next();
                final @NotNull Integer index = a.getKey();
                final @NotNull NegativeListener listener = a.getValue();

                listener.execute();

                tramp.getNegativeListeners().remove(index);

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
        for (final @NotNull AlphaParseSuccess result : node.results()) {
            pushMessage(listener, result);
        }
        for (final @NotNull AlphaParseSuccess fullResult : node.fullResults()) {
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
        for (final @NotNull AlphaParseSuccess fullResult : node.fullResults()) {
            pushMessage(listener, fullResult);
        }
        if (!fullListenerAlreadyExists) {
            pushStack(() -> nodeKey.parser().fullParse(nodeKey.index(), this));
        }
    }

    /**
     * Pushes a result into the trampoline's node.
     * Categorizes as either result or full-result.
     * Schedules notification to all existing listeners of result
     * (Full listeners only get notified about full results)
     */
    private void pushResult(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            @NotNull AlphaParseSuccess result) {
        final @NotNull TrampolineListenerNode node = getOrCreateListenerNode(nodeKey);
        final @NotNull Combinator parser = nodeKey.parser();
        result = parser.isHidden()
                ? result.withResult(null)
                : result;
        if (parser.getReduction().getReductionType() != ReductionType.ReductionTypesAvailable.NONE) {
            final ParseTree resultR = Reduction.applyReduction(
                    parser.getReduction(),
                    result.getResult());
            result = AlphaParseSuccess.create(result.index(), resultR);
        }
        final boolean isTotal = totalSuccess_Q(result);
        final @NotNull SequencedSet<@NotNull AlphaParseSuccess> results =
                isTotal ? node.fullResults() : node.results();

        final var resultExisted = !results.add(result);
        if (resultExisted) {
            return;
        }

        for (final @NotNull Listener listener : node.listeners()) {
            pushMessage(listener, result);
        }

        if (!isTotal) {
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

    void success(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final Object result,
            final int end) {
        pushResult(nodeKey, AlphaParseSuccess.create(end, result));
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
            success(
                    nodeKey,
                    buildFailureNode(Keyword.intern("failure"), subSeq, index, tramp.getText().length()),
                    textLen);
        }
    }

    private @NotNull ParseFailureNode buildFailureNode(
            final @NotNull Keyword key,
            final @NotNull String text,
            final int start,
            final int end) {
        return new ParseFailureNode(text, key, start, end);
    }

    private static @NotNull AlphaParsesResult parsesTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text, 0);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run();
        return AlphaParsesResult.make(allParses);
    }

    @NotNull Listener nodeListener(
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        return result -> pushResult(nodeKey, result);
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parses(Parser, String)} or {@link Alpha#parses(Parser, String, Alpha.ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest.
     * @see Alpha#parses(Parser, String)
     * @see Alpha#parse(Parser, String, Alpha.ParsingOptions)
     */
    public static @NotNull AlphaParsesResult parses(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run();
        return AlphaParsesResult.make(allParses);
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parsesOrFailure(Parser, String, Alpha.ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest or failure.
     * @see Alpha#parsesOrFailure(Parser, String, Alpha.ParsingOptions)
     */
    public static @NotNull AlphaParsesResult parsesOrFailure(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp);
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
     * This method should not be called directly. Use {@link Alpha#parse(Parser, String)} or {@link Alpha#parse(Parser, String, Alpha.ParsingOptions)} instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse tree or failure.
     * @see Alpha#parse(Parser, String)
     * @see Alpha#parse(Parser, String, Alpha.ParsingOptions)
     */
    public static @NotNull AlphaParseResult parse(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        var gll = new Gll(tramp);
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
     * 4This method should not be called directly. Use {@link Alpha#parses(Parser, String, Alpha.ParsingOptions)} with {@link Alpha.ParsingOptions#isTotal()} set to true instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse forest.
     * @see Alpha#parses(Parser, String, Alpha.ParsingOptions)
     * @see Alpha.ParsingOptions#isTotal()
     */
    public static @NotNull AlphaParsesResult parsesTotal(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var allParses = parses(grammar, start, text, partial);
        if (allParses.castToParsesSuccess().iterator().hasNext()) return AlphaParsesResult.make(allParses);
        return parsesTotalAfterFail(grammar, start, text, partial);
    }

    private static @NotNull AlphaParseResult parseTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final int failIndex,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text, failIndex);
        final @NotNull var parser = CombinatorFactory.staticMakeNonTerminal(start);
        var gll = new Gll(tramp);
        gll.startParser(tramp, parser, partial);
        final @NotNull var allParses = gll.run(1);
        if (!allParses.isEmpty())
            return AlphaParseResult.make(allParses.getFirst());
        return gll.buildFailureNode(start, text, 0, text.length());
    }

    /**
     * This method should not be called directly. Use {@link Alpha#parse(Parser, String, Alpha.ParsingOptions)} with {@link Alpha.ParsingOptions#isTotal()} set to true instead.
     *
     * @param grammar The grammar.
     * @param start   The name of the start production.
     * @param text    The text.
     * @param partial Whether to include partial results.
     * @return The parse tree or failure.
     * @see Alpha#parse(Parser, String, Alpha.ParsingOptions)
     * @see Alpha.ParsingOptions#isTotal()
     */
    public static @NotNull AlphaParseResult parseTotal(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var result = parse(grammar, start, text, partial);
        if (!(result instanceof AlphaParseFailure)) return result;
        return parseTotalAfterFail(grammar, start, text, ((AlphaParseFailure) result).index(), partial);
    }
}
