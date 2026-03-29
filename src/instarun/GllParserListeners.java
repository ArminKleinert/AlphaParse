package instarun;

import instarun.flat.AutoFlattenSeq;
import instarun.functions.Listener;
import instarun.parser.Reduction;
import instarun.parser.combinator.Combinator;
import instarun.parser.combinator.RepetitionCombinator;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GllParserListeners {
    public static @NotNull Listener nodeListener(final @NotNull InstaNodeKey nodeKey,
                                                 final @NotNull InstaTramp tramp) {
        return result -> Gll.pushResult(tramp, nodeKey, result);
    }

    public static @NotNull Listener lookListener(final @NotNull InstaNodeKey nodeKey,
                                                 final @NotNull InstaTramp tramp) {
        return ignored -> Gll.success(tramp, nodeKey, null, nodeKey.getIndex());
    }

    public static @NotNull Listener topListener(final @NotNull InstaTramp tramp) {
        return tramp::setSuccess; // result -> tramp.setSuccess(result);
    }

    public static @NotNull Listener plusListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                 final @NotNull Combinator parser,
                                                 final int prevIndex,
                                                 final @NotNull InstaNodeKey nodeKey,
                                                 final @NotNull InstaTramp tramp) {
        return result -> {
            Object parsedResult = result.getResult();
            int continueIndex = result.getIndex();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty()) {
                    Gll.success(tramp, nodeKey, null, continueIndex);
                }
                return;
            }
            AutoFlattenSeq<Object> newResultsSoFar = resultsSoFar.conjFlat(parsedResult);
            Gll.pushListener(tramp, new InstaNodeKey(continueIndex, parser), plusListener(newResultsSoFar, parser, continueIndex, nodeKey, tramp));
            Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
        };
    }

    public static @NotNull Listener catListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                final @NotNull List<Combinator> parserSequence,
                                                final @NotNull InstaNodeKey nodeKey,
                                                final @NotNull InstaTramp tramp) {
        return result -> {
            assert result.getResult() != null;
            final @NotNull Object parsedResult = result.getResult();
            final int continueIndex = result.getIndex();
            final @NotNull AutoFlattenSeq<Object> newResultsSoFar = resultsSoFar.conjFlat(parsedResult);
            if (parserSequence.isEmpty()) {
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            } else {
                Gll.pushListener(
                        tramp,
                        new InstaNodeKey(continueIndex, parserSequence.getFirst()),
                        catListener(
                                newResultsSoFar,
                                parserSequence.subList(1, parserSequence.size()),
                                nodeKey,
                                tramp)
                );
            }
        };
    }

    public static @NotNull Listener repListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                final int nResultsSoFar,
                                                final @NotNull RepetitionCombinator parser,
                                                final @NotNull InstaNodeKey nodeKey, final @NotNull InstaTramp tramp) {
        return result -> {
            assert result.getResult() != null;
            final @NotNull Object parsedResult = result.getResult();
            final int continueIndex = result.getIndex();

            final @NotNull AutoFlattenSeq<Object> newResultsSoFar =
                    resultsSoFar.conjFlat(parsedResult);

            final int newNResultsSoFar = nResultsSoFar + 1;

            if (Integer.max(parser.getMin(), 0) <= newNResultsSoFar && newNResultsSoFar <= parser.getMax())
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);

            if (newNResultsSoFar < parser.getMax())
                Gll.pushListener(
                        tramp,
                        new InstaNodeKey(continueIndex, parser.getParser()),
                        repListener(newResultsSoFar, newNResultsSoFar, parser, nodeKey, tramp)
                );
        };
    }


    public static @NotNull Listener plusFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                     final @NotNull Combinator parser,
                                                     final int prevIndex,
                                                     final @NotNull InstaNodeKey nodeKey,
                                                     final @NotNull InstaTramp tramp) {
        return result -> {
            var parsedResult = result.getResult();
            var continueIndex = result.getIndex();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty())
                    Gll.success(tramp, nodeKey, null, continueIndex);
            } else {
                var newResultsSoFar = resultsSoFar.conjFlat(parsedResult);
                if (continueIndex == tramp.getText().length()) {
                    Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
                } else {
                    Gll.pushListener(tramp, new InstaNodeKey(continueIndex, parser),
                            plusFullListener(newResultsSoFar, parser, continueIndex, nodeKey, tramp));
                }
            }
        };
    }

    public static @NotNull Listener catFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                    final @NotNull List<Combinator> parserSequence,
                                                    final @NotNull InstaNodeKey nodeKey,
                                                    final @NotNull InstaTramp tramp) {
        return result -> {
            final var parsedResult = result.getResult();
            final var continueIndex = result.getIndex();
            final @NotNull var newResultsSoFar = resultsSoFar.conjFlat(parsedResult);

            if (Reduction.isSingleton(parserSequence)) {
                Gll.pushFullListener(tramp, new InstaNodeKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, List.of(), nodeKey, tramp));
            } else if (!parserSequence.isEmpty()) {
                Gll.pushListener(tramp, new InstaNodeKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, parserSequence.subList(1, parserSequence.size()), nodeKey, tramp));
            } else {
                Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            }
        };
    }

    public static @NotNull Listener repFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                                    final int nResultsSoFar,
                                                    final @NotNull Combinator parser,
                                                    final int m,
                                                    final int n,
                                                    final int prevIndex,
                                                    final @NotNull InstaNodeKey nodeKey,
                                                    final @NotNull InstaTramp tramp) {
        return result -> {
            final var parsedResult = result.getResult();
            final int continueIndex = result.getIndex();
            final @NotNull var newResultsSoFar = resultsSoFar.conjFlat(parsedResult);
            final int newNResultsSoFar = nResultsSoFar + 1;
            if (continueIndex == tramp.getText().length()) {
                if (m <= newNResultsSoFar && newNResultsSoFar <= n)
                    Gll.success(tramp, nodeKey, newResultsSoFar, continueIndex);
            } else {
                if (newNResultsSoFar < n) {
                    final @NotNull var listener = repFullListener(
                            newResultsSoFar, newNResultsSoFar,
                            parser, m, n, continueIndex, nodeKey, tramp);
                    Gll.pushListener(tramp, new InstaNodeKey(continueIndex, parser), listener);
                }
            }
        };
    }
}
