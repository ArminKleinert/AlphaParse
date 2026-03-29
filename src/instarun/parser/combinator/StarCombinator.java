package instarun.parser.combinator;

import instarun.Gll;
import instarun.GllParserListeners;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import instarun.flat.AutoFlattenSeq;
import instarun.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public final class StarCombinator extends CombinatorWithParser {
    public StarCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public StarCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull InstaNodeKey nodeKeyForStar = new InstaNodeKey(index, this);
        Gll.pushListener(
                tramp, new InstaNodeKey(index, combinator),
                GllParserListeners.plusListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, tramp)
        );
        Gll.success(tramp, nodeKeyForStar, null, index);
    }

    /*
(defn star-full-parse
  [^StarCombinator this index ^InstaTramp tramp]
  (let [parser (.getParser this)]
    (if (= index (count (.getText tramp)))
      (Gll1/success tramp (InstaNodeKey. index this) nil index)
      (Gll1/pushListener tramp
                         (InstaNodeKey. index parser)
                         (GllParsers/PlusFullListener (AutoFlattenSeq/make) parser index (InstaNodeKey. index this) tramp)))))
     */
    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull InstaNodeKey nodeKeyForStar = new InstaNodeKey(index, this);
        if (index == tramp.getText().length()) {
            Gll.success(tramp, nodeKeyForStar, null, index);
        } else {
            Gll.pushListener(
                    tramp, new InstaNodeKey(index, combinator),
                    GllParserListeners.plusFullListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, tramp));
        }
    }

    @Override
    public @NotNull StarCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StarCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull StarCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StarCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull StarCombinator withParser(final @NotNull Combinator parser) {
        return new StarCombinator(parser, isHidden(), getReduction());
    }
}
