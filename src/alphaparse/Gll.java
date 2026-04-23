package alphaparse;

import alphaparse.functions.Listener;
import alphaparse.functions.NegativeListener;
import alphaparse.functions.Procedure;
import alphaparse.reduction.ReductionType;
import alphaparse.result.ParseTree;
import alphaparse.parser.combinator.Combinator;
import alphaparse.parser.Grammar;
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
    private Gll() {
    }

    private static @NotNull TrampolineListenerNode nodeGet(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        @Nullable TrampolineListenerNode node = tramp.getNode(nodeKey);

        if (node != null)
            return node;

        node = new TrampolineListenerNode();
        tramp.addToNodes(nodeKey, node);
        return node;
    }

    private static boolean listenerExists_Q(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty();
    }

    private static boolean fullListenerExists_Q(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty() || !node.fullListeners().isEmpty();
    }

    /**
     * TODO
     *
     * @param tramp   TODO
     * @param nodeKey TODO
     * @return TODO
     */
    public static boolean resultExists_Q(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = tramp.getNode(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty() || !node.results().isEmpty();
    }

    private static boolean totalSuccess_Q(
            final @NotNull Tramp tramp,
            final @NotNull AlphaParseSuccess success) {
        return tramp.getText().length() == success.index();
    }

    /**
     * TODO
     *
     * @param tramp            TODO
     * @param creator          TODO
     * @param negativeListener TODO
     */
    public static void pushNegativeListener(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey creator,
            final @NotNull NegativeListener negativeListener) {
        tramp.getNegativeListeners().put(creator.index(), negativeListener);
    }

    private static void pushMessage(
            final @NotNull Tramp tramp,
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

    private static void pushStack(final @NotNull Tramp tramp,
                                  final @NotNull Procedure item) {
        tramp.addToStack(item);
    }

    private static void step(final @NotNull Tramp tramp) {
        final Procedure top = tramp.getStack().getLast();
        tramp.popStack();
        top.execute();
    }

    private static @NotNull AlphaParsesResult.LazyResultList run(
            final @NotNull Tramp tramp) {
        return run(tramp, Integer.MAX_VALUE);
    }

    private static @NotNull AlphaParsesResult.LazyResultList run(
            final @NotNull Tramp tramp, final int maxResults) {
        final var foundResult = new AtomicBoolean(false);
        return new AlphaParsesResult.LazyResultList(() -> run(tramp, foundResult), maxResults);
    }

    private static @Nullable ParseTree run(
            final @NotNull Tramp tramp,
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
                step(tramp);
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
                tramp.swapStack();
                tramp.incGeneration();
                foundResult.set(false);
                continue; // Take it to the top.
            }
            return null; // Fail
        }
    }

    /**
     * TODO
     *
     * @param tramp    TODO
     * @param nodeKey  TODO
     * @param listener TODO
     */
    public static void pushListener(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        final boolean listenerAlreadyExists = listenerExists_Q(tramp, nodeKey);
        final @NotNull TrampolineListenerNode node = nodeGet(tramp, nodeKey);
        final @NotNull List<Listener> listeners = node.listeners();
        listeners.add(listener);
        for (final @NotNull AlphaParseSuccess result : node.results()) {
            pushMessage(tramp, listener, result);
        }
        for (final @NotNull AlphaParseSuccess fullResult : node.fullResults()) {
            pushMessage(tramp, listener, fullResult);
        }
        if (!listenerAlreadyExists) {
            pushStack(tramp, () -> nodeKey.parser().parse(nodeKey.index(), tramp));
        }
    }

    /**
     * TODO
     *
     * @param tramp    TODO
     * @param nodeKey  TODO
     * @param listener TODO
     */
    public static void pushFullListener(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        //GllParsers.pushFullListenerCallback.invoke(tramp, nodeKey, listener);
        final var fullListenerAlreadyExists = fullListenerExists_Q(tramp, nodeKey);
        final @NotNull var node = nodeGet(tramp, nodeKey);
        final @NotNull var listeners = node.fullListeners();
        listeners.add(listener);
        for (final @NotNull AlphaParseSuccess fullResult : node.fullResults()) {
            pushMessage(tramp, listener, fullResult);
        }
        if (!fullListenerAlreadyExists) {
            pushStack(tramp, () -> nodeKey.parser().fullParse(nodeKey.index(), tramp));
        }
    }

    /**
     * Pushes a result into the trampoline's node.
     * Categorizes as either result or full-result.
     * Schedules notification to all existing listeners of result
     * (Full listeners only get notified about full results)
     *
     * @param tramp   TODO
     * @param nodeKey TODO
     * @param result  TODO
     */
    public static void pushResult(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            @NotNull AlphaParseSuccess result) {
        final @NotNull TrampolineListenerNode node = nodeGet(tramp, nodeKey);
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
        final boolean isTotal = totalSuccess_Q(tramp, result);
        final @NotNull SequencedSet<@NotNull AlphaParseSuccess> results =
                isTotal ? node.fullResults() : node.results();

        final var resultExisted = !results.add(result);
        if (resultExisted) {
            return;
        }

        for (final @NotNull Listener listener : node.listeners()) {
            pushMessage(tramp, listener, result);
        }

        if (!isTotal) {
            return;
        }

        for (final @NotNull Listener fullListener : node.fullListeners()) {
            pushMessage(tramp, fullListener, result);
        }
    }

    private static void startParser(
            final @NotNull Tramp tramp,
            final @NotNull Combinator parser,
            final boolean partial) {
        if (partial) {
            pushListener(tramp, new TrampolineListenerKey(0, parser), tramp::setSuccess);
        } else {
            pushFullListener(tramp, new TrampolineListenerKey(0, parser), tramp::setSuccess);
        }
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param start   TODO
     * @param text    TODO
     * @param partial TODO
     * @return TODO
     */
    public static @NotNull AlphaParsesResult parses(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        final @NotNull var allParses = run(tramp);
        return AlphaParsesResult.make(allParses);
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param start   TODO
     * @param text    TODO
     * @param partial TODO
     * @return TODO
     */
    public static @NotNull AlphaParsesResult parsesOrFailure(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        final @NotNull var allParses = run(tramp);
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            @NotNull AlphaParseFailure apf = FailureUtil.augmentFailure(tramp.getFailure(), text);
            return AlphaParsesResult.make(apf);
        }
        return AlphaParsesResult.make(allParses);
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param start   TODO
     * @param text    TODO
     * @param partial TODO
     * @return TODO
     */
    static @NotNull AlphaParseResult parse(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        final @NotNull var tramp = new Tramp(grammar, text);
        final @NotNull var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        final @NotNull var allParses = run(tramp, 1);
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            return AlphaParseResult.make(FailureUtil.augmentFailure(tramp.getFailure(), text));
        }
        return AlphaParseResult.make(allParses.getFirst());
    }

    /**
     * TODO
     *
     * @param tramp   TODO
     * @param nodeKey TODO
     * @param result  TODO
     * @param end     TODO
     */
    public static void success(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final Object result,
            final int end) {
        pushResult(tramp, nodeKey, AlphaParseSuccess.create(end, result));
    }

    /**
     * TODO
     *
     * @param tramp   TODO
     * @param nodeKey TODO
     * @param index   TODO
     * @param reason  TODO
     */
    public static void fail(
            final @NotNull Tramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final int index,
            final @NotNull ParseFailureReason reason) {
        //Objects.requireNonNull(tramp.getFailure());
        tramp.setFailure(FailureUtil.modifyFailureByIndex(tramp.getFailure(), reason, index));
        if (index == tramp.getFailIndex()) {
            final @NotNull String subSeq = tramp.getText().substring(index);
            final int textLen = tramp.getText().length();
            success(tramp,
                    nodeKey,
                    buildFailureNode(Keyword.intern("failure"), subSeq, index, tramp.getText().length()),
                    textLen);
        }
    }

    private static @NotNull ParseFailureNode buildFailureNode(
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
        final @NotNull var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        final @NotNull var allParses = run(tramp);
        return AlphaParsesResult.make(allParses);
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param start   TODO
     * @param text    TODO
     * @param partial TODO
     * @return TODO
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
        final @NotNull var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        final @NotNull var allParses = run(tramp, 1);
        if (!allParses.isEmpty())
            return AlphaParseResult.make(allParses.getFirst());
        return buildFailureNode(start, text, 0, text.length());
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param start   TODO
     * @param text    TODO
     * @param partial TODO
     * @return TODO
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
