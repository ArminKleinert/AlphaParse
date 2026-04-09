package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.Reduction;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.Tramp;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class GllParserListeners {
    static @NotNull Listener nodeListener(final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Tramp tramp) {
        return result -> Gll.pushResult(tramp, nodeKey, result);
    }

    static @NotNull Listener lookListener(final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Tramp tramp) {
        return ignored -> Gll.success(tramp, nodeKey, null, nodeKey.index());
    }

    static @NotNull Listener plusListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                          final @NotNull Combinator parser,
                                          final int prevIndex,
                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.getIndex();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty()) {
                    Gll.success(tramp, nodeKey, null, continueIndex);
                }
                return;
            }
            final AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            Gll.pushListener(tramp, new TrampolineListenerKey(continueIndex, parser), plusListener(newResultsSoFar, parser, continueIndex, nodeKey, tramp));
            Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
        };
    }

    static @NotNull Listener catListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                         final @NotNull List<Combinator> parserSequence,
                                         final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                         final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.getIndex();
            final @NotNull AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.isEmpty()) {
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            } else {
                Gll.pushListener(
                        tramp,
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catListener(
                                newResultsSoFar,
                                parserSequence.subList(1, parserSequence.size()),
                                nodeKey,
                                tramp)
                );
            }
        };
    }

    static @NotNull Listener repListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                         final int nResultsSoFar,
                                         final @NotNull RepetitionCombinator parser,
                                         final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey, final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.getIndex();

            final @NotNull AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            final int newNResultsSoFar = nResultsSoFar + 1;

            if (Integer.max(parser.getMin(), 0) <= newNResultsSoFar && newNResultsSoFar <= parser.getMax())
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);

            if (newNResultsSoFar < parser.getMax())
                Gll.pushListener(
                        tramp,
                        new TrampolineListenerKey(continueIndex, parser.getParser()),
                        repListener(newResultsSoFar, newNResultsSoFar, parser, nodeKey, tramp)
                );
        };
    }


    static @NotNull Listener plusFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                              final @NotNull Combinator parser,
                                              final int prevIndex,
                                              final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                              final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.getIndex();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty())
                    Gll.success(tramp, nodeKey, null, continueIndex);
            } else {
                final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                        ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                        : resultsSoFar.append(parsedResult);
                if (continueIndex == tramp.getText().length()) {
                    Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
                } else {
                    Gll.pushListener(tramp, new TrampolineListenerKey(continueIndex, parser),
                            plusFullListener(newResultsSoFar, parser, continueIndex, nodeKey, tramp));
                }
            }
        };
    }

    static @NotNull Listener catFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                             final @NotNull List<Combinator> parserSequence,
                                             final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                             final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.getIndex();
            final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (Reduction.isSingleton(parserSequence)) {
                Gll.pushFullListener(tramp, new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, List.of(), nodeKey, tramp));
            } else if (!parserSequence.isEmpty()) {
                Gll.pushListener(tramp, new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, parserSequence.subList(1, parserSequence.size()), nodeKey, tramp));
            } else {
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            }
        };
    }

    static @NotNull Listener repFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                             final int nResultsSoFar,
                                             final @NotNull Combinator parser,
                                             final int m,
                                             final int n,
                                             final int prevIndex,
                                             final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                             final @NotNull Tramp tramp) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final int continueIndex = result.getIndex();
            final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            final int newNResultsSoFar = nResultsSoFar + 1;
            if (continueIndex == tramp.getText().length()) {
                if (m <= newNResultsSoFar && newNResultsSoFar <= n)
                    Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            } else {
                if (newNResultsSoFar < n) {
                    final @NotNull var listener = repFullListener(
                            newResultsSoFar, newNResultsSoFar,
                            parser, m, n, continueIndex, nodeKey, tramp);
                    Gll.pushListener(tramp, new TrampolineListenerKey(continueIndex, parser), listener);
                }
            }
        };
    }
}
