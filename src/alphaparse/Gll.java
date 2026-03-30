package alphaparse;

import alphaparse.functions.Listener;
import alphaparse.functions.NegativeListener;
import alphaparse.functions.Procedure;
import alphaparse.reduction.ReductionType;
import alphaparse.result.ParseTree;
import alphaparse.parser.combinator.Combinator;
import alphaparse.parser.Grammar;
import alphaparse.parser.Reduction;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import alphaparse.result.TotalParsesFailureNode;
import alphaparse.result.failure.FailureUtil;
import alphaparse.result.AlphaFailure;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.success.InstaSuccess;
import alphaparse.result.failure.failureReason.InstaFailureReason;
import alphaparse.trampoline.TrampolineMsgCacheKey;
import alphaparse.trampoline.TrampolineListenerNode;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.InstaTramp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Gll {
    private static @NotNull TrampolineListenerNode nodeGet(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        @Nullable TrampolineListenerNode node = tramp.getNode(nodeKey);

        if (node != null)
            return node;

        node = new TrampolineListenerNode();
        tramp.addToNodes(nodeKey, node);
        return node;
    }

    private static boolean listenerExists_Q(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty();
    }

    private static boolean fullListenerExists_Q(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = tramp.getNode(nodeKey);
        if (node == null) return false;
        return !node.listeners().isEmpty() || !node.fullListeners().isEmpty();
    }

    public static boolean resultExists_Q(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = tramp.getNode(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty() || !node.results().isEmpty();
    }

    private static boolean fullResultExists_Q(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final Map<TrampolineListenerKey, TrampolineListenerNode> nodes = tramp.getNodes();
        final TrampolineListenerNode node = nodes.get(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty();
    }

    private static boolean totalSuccess_Q(
            final @NotNull InstaTramp tramp,
            final @NotNull InstaSuccess success) {
        return tramp.getText().length() == success.getIndex();
    }

    public static @NotNull CharSequence subSequence(
            final @NotNull CharSequence text,
            final int start) {
        return text.subSequence(start, text.length());
    }

    public static @NotNull CharSequence subSequence(
            final @NotNull CharSequence text,
            final int start,
            final int end) {
        return text.subSequence(start, end);
    }

    public static void pushNegativeListener(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey creator,
            final @NotNull NegativeListener negativeListener) {
        tramp.getNegativeListeners().put(creator.index(), List.of(negativeListener));
    }

    private static void pushMessage(
            final @NotNull InstaTramp tramp,
            final @NotNull Listener listener,
            final @NotNull InstaSuccess result) {
        final int i = result.getIndex();
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

    private static void pushStack(final @NotNull InstaTramp tramp,
                                  final @NotNull Procedure item) {
        tramp.addToStack(item);
    }

    private static void step(final @NotNull InstaTramp tramp) {
        final Procedure top = tramp.getStack().getLast();
        tramp.popStack();
        top.execute();
    }

    private static @NotNull AlphaParsesResult.LazyResultList run(
            final @NotNull InstaTramp tramp) {
        return run(tramp, Integer.MAX_VALUE);
    }

    private static @NotNull AlphaParsesResult.LazyResultList run(
            final @NotNull InstaTramp tramp, final int maxResults) {
        final var foundResult = new AtomicBoolean(false);
        return new AlphaParsesResult.LazyResultList(() -> run(tramp, foundResult), maxResults);
    }

    private static @Nullable ParseTree run(
            final @NotNull InstaTramp tramp,
            final @NotNull AtomicBoolean foundResult) {
        do {
            if (tramp.getSuccess() != null) {
                final Object successResult = tramp.getSuccess();
                if (!(successResult instanceof InstaSuccess.InstaSuccessParseResult))
                    throw new IllegalStateException();
                tramp.setSuccess(null);
                foundResult.set(true);
                return ((InstaSuccess.InstaSuccessParseResult) successResult).getResult();
            }
            final List<Procedure> stack = tramp.getStack();
            if (!stack.isEmpty()) {
                step(tramp);
                continue; // Take it to the top.
            }
            if (!tramp.getNegativeListeners().isEmpty()) {
                final @NotNull Iterator<Map.Entry<Integer, List<NegativeListener>>> iter =
                        tramp.getNegativeListeners().entrySet().iterator();
                final @NotNull Map.Entry<Integer, List<NegativeListener>> a = iter.next();
                final @NotNull Integer index = a.getKey();
                final @NotNull List<NegativeListener> listeners = a.getValue();
                final @NotNull NegativeListener listener = listeners.getLast();

                listener.execute();

                if (listeners.size() == 1) {
                    tramp.getNegativeListeners().remove(index);
                } else {
                    listeners.removeLast();
                }
                continue; // Take it to the top.
            }
            if (foundResult.get()) {
                tramp.swapStack();
                tramp.incGeneration();
                foundResult.set(false);
                continue; // Take it to the top.
            }
            return null; // Fail
        } while (true);
    }

    public static void pushListener(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        final boolean listenerAlreadyExists = listenerExists_Q(tramp, nodeKey);
        final @NotNull TrampolineListenerNode node = nodeGet(tramp, nodeKey);
        final @NotNull List<Listener> listeners = node.listeners();
        listeners.add(listener);
        for (final @NotNull InstaSuccess result : node.results()) {
            pushMessage(tramp, listener, result);
        }
        for (final @NotNull InstaSuccess fullResult : node.fullResults()) {
            pushMessage(tramp, listener, fullResult);
        }
        if (!listenerAlreadyExists) {
            pushStack(tramp, () -> nodeKey.parser().parse(nodeKey.index(), tramp));
        }
    }

    public static void pushFullListener(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final @NotNull Listener listener) {
        //GllParsers.pushFullListenerCallback.invoke(tramp, nodeKey, listener);
        final var fullListenerAlreadyExists = fullListenerExists_Q(tramp, nodeKey);
        final @NotNull var node = nodeGet(tramp, nodeKey);
        final @NotNull var listeners = node.fullListeners();
        listeners.add(listener);
        for (final @NotNull InstaSuccess fullResult : node.fullResults()) {
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
     */
    public static void pushResult(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            @NotNull InstaSuccess result) {
        final @NotNull TrampolineListenerNode node = nodeGet(tramp, nodeKey);
        final @NotNull Combinator parser = nodeKey.parser();
        result = parser.isHidden()
                ? result.withResult(null)
                : result;
        if (parser.getReduction().getReductionType() != ReductionType.ReductionTypesAvailable.NONE) {
            final var resultR = Reduction.applyReduction(
                    parser.getReduction(),
                    Objects.requireNonNull(result.getResult()));
            result = InstaSuccess.create(result.getIndex(), resultR);
        }
        final boolean isTotal = totalSuccess_Q(tramp, result);
        final @NotNull Set<@NotNull InstaSuccess> results = isTotal ? node.fullResults() : node.results();

        var resultExisted = !results.add(result);
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
            final @NotNull InstaTramp tramp,
            final @NotNull Combinator parser,
            final boolean partial) {
        if (partial) {
            pushListener(tramp, new TrampolineListenerKey(0, parser), tramp::setSuccess);
        } else {
            pushFullListener(tramp, new TrampolineListenerKey(0, parser), tramp::setSuccess);
        }
    }

    public static @NotNull AlphaParsesResult parses(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var tramp = new InstaTramp(grammar, text);
        var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        var allParses = run(tramp);
        return AlphaParsesResult.make(allParses);
    }

    public static @NotNull AlphaParsesResult parsesOrFailure(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var tramp = new InstaTramp(grammar, text);
        var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        var allParses = run(tramp);
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            return new AlphaParsesResult.ParsesFailureResult(FailureUtil.augmentFailure(tramp.getFailure(), text));
        }
        return AlphaParsesResult.make(allParses);
    }

    static @NotNull AlphaParseResult parse(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var tramp = new InstaTramp(grammar, text);
        var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        var allParses = run(tramp, 1);
        if (allParses.isEmpty()) {
            if (tramp.getFailure() == null)
                throw new IllegalStateException();
            return AlphaParseResult.make(FailureUtil.augmentFailure(tramp.getFailure(), text));
        }
        return AlphaParseResult.make(allParses.getFirst());
    }

    public static void success(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final Object result,
            final int end) {
        pushResult(tramp, nodeKey, InstaSuccess.create(end, result));
    }

    public static void fail(
            final @NotNull InstaTramp tramp,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
            final int index,
            final @NotNull InstaFailureReason reason) {
        //Objects.requireNonNull(tramp.getFailure());
        tramp.setFailure(FailureUtil.modifyFailureByIndex(tramp.getFailure(), reason, index));
        if (index == tramp.getFailIndex()) {
            final @NotNull String subSeq = subSequence(tramp.getText(), index).toString();
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

    private static @NotNull TotalParsesFailureNode buildTotalFailureNode(
            final @NotNull Keyword key,
            final @NotNull String text) {
        //return buildFailureNode(key, text, 0, text.length());
        return new TotalParsesFailureNode(text, key, 0, text.length());
    }

    private static @NotNull AlphaParsesResult parsesTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var tramp = new InstaTramp(grammar, text, 0);
        var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        var allParses = run(tramp);
        return AlphaParsesResult.make(allParses);
    }

    public static @NotNull AlphaParsesResult parsesTotal(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var allParses = parses(grammar, start, text, partial);
        if (allParses.castToParsesSuccess().iterator().hasNext()) return AlphaParsesResult.make(allParses);
        return parsesTotalAfterFail(grammar, start, text, partial);
    }

    private static @NotNull AlphaParseResult parseTotalAfterFail(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final int failIndex,
            final boolean partial) {
        var tramp = new InstaTramp(grammar, text, failIndex);
        var parser = CombinatorsSource.staticMakeNonTerminal(start);
        startParser(tramp, parser, partial);
        var allParses = run(tramp, 1);
        if (!allParses.isEmpty())
            return AlphaParseResult.make(allParses.getFirst());
        return buildFailureNode(start, text, 0, text.length());
    }

    public static @NotNull AlphaParseResult parseTotal(
            final @NotNull Grammar grammar,
            final @NotNull Keyword start,
            final @NotNull String text,
            final boolean partial) {
        var result = parse(grammar, start, text, partial);
        if (!(result instanceof AlphaFailure)) return result;
        return parseTotalAfterFail(grammar, start, text, ((AlphaFailure) result).getIndex(), partial);
    }
}
