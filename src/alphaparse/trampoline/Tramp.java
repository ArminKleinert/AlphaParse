package alphaparse.trampoline;

import alphaparse.parser.Grammar;
import alphaparse.functions.NegativeListener;
import alphaparse.functions.Procedure;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

public final class Tramp {
    private final @NotNull Grammar grammar;
    private final @NotNull String text;
    private final @NotNull String segment;
    private final int failIndex;
    private final @NotNull List<@NotNull Procedure> stack;
    private final @NotNull List<@NotNull Procedure> nextStack;
    private int generation;
    private final @NotNull SequencedMap<@NotNull Integer, @NotNull NegativeListener> negativeListeners;
    private final @NotNull SequencedMap<@NotNull TrampolineMsgCacheKey, @NotNull Integer> msgCache;
    private final @NotNull SequencedMap<@NotNull TrampolineListenerKey, @NotNull TrampolineListenerNode> nodes;
    private @Nullable AlphaParseSuccess success;
    private @Nullable AlphaParseFailure failure;

    public Tramp(final @NotNull Grammar grammar, final @NotNull String text) {
        this(grammar, text, text, -1);
    }

     public Tramp(final @NotNull Grammar grammar, final @NotNull String text, final int failIndex) {
        this(grammar, text, text, failIndex);
    }

//     Tramp(final @NotNull Grammar grammar, final @NotNull String text, final @NotNull String segment) {
//        this(grammar, text, segment, -1);
//    }

    private Tramp(final @NotNull Grammar grammar, final @NotNull String text, final @NotNull String segment, final int failIndex) {
        this.grammar = grammar;
        this.text = text;
        this.segment = segment;
        this.failIndex = failIndex;
        this.stack = new ArrayList<>();
        this.nextStack = new ArrayList<>();
        this.generation = 0;
        this.negativeListeners = new TreeMap<>(Comparator.comparingInt((Integer o) -> o).reversed());
        //this.negativeListeners = new LinkedHashMap<>();
        this.msgCache = new LinkedHashMap<>();
        this.nodes = new LinkedHashMap<>();
        this.success = null;
        this.failure = null;
    }

    public @NotNull Grammar getGrammar() {
        return grammar;
    }

    public @NotNull String getText() {
        return text;
    }

    public @NotNull String getSegment() {
        return segment;
    }

    public int getFailIndex() {
        return failIndex;
    }

    public @NotNull List<@NotNull Procedure> getStack() {
        return stack;
    }

    public int getGeneration() {
        return generation;
    }

    public @NotNull SequencedMap<@NotNull Integer, @NotNull NegativeListener> getNegativeListeners() {
        return negativeListeners;
    }

    public @Nullable AlphaParseSuccess getSuccess() {
        return success;
    }

    public @Nullable AlphaParseFailure getFailure() {
        return failure;
    }

    public void incGeneration() {
        generation = generation + 1;
    }

    public void addToStack(final @NotNull Procedure frame) {
        this.stack.add(frame);
    }

    public void popStack() {
        stack.removeLast();
    }

    public void swapStack() {
        stack.clear();
        stack.addAll(nextStack);
        nextStack.clear();
    }

    public void addToNextStack(final @NotNull Procedure frame) {
        nextStack.add(frame);
    }

    public int getFromMsgCache(final @NotNull TrampolineMsgCacheKey key, final int defaultVal) {
        return msgCache.getOrDefault(key, defaultVal);
    }

    public void addToMsgCache(final @NotNull TrampolineMsgCacheKey key, final int defaultVal) {
        msgCache.put(key, defaultVal);
    }

    public void setFailure(final @NotNull AlphaParseFailure failure) {
        this.failure = failure;
    }

    public void setSuccess(final @Nullable AlphaParseSuccess success) {
        this.success = success;
    }

    public @Nullable TrampolineListenerNode getNode(final @NotNull TrampolineListenerKey nodeKey) {
        return nodes.get(nodeKey);
    }

    public void addToNodes(final @NotNull TrampolineListenerKey key,
                           final @NotNull TrampolineListenerNode node) {
        nodes.put(key, node);
    }

    @Override
    public String toString() {
        return "Tramp{" +
                "grammar=" + grammar +
                ", text='" + text + '\'' +
                ", segment=" + segment +
                ", failIndex=" + failIndex +
                ", stack=" + stack +
                ", nextStack=" + nextStack +
                ", generation=" + generation +
                ", negativeListeners=" + negativeListeners +
                ", msgCache=" + msgCache +
                ", nodes=" + nodes +
                ", success=" + success +
                ", failure=" + failure +
                '}';
    }
}
