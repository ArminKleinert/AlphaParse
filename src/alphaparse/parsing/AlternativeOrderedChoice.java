//package alphaparse.parsing;
//
//import alphaparse.reduction.ReductionType;
//
//import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
//
//import alphaparse.trampoline.TrampolineListenerNode;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This class is an alternative to the {@link AlternationRule}.
// * <p>
// * It represents the ABNF choice operator {@code (p1 / p2)} (where p1 and p2 are instances of {@link Rule})
// * and should work like the PEG extension which makes it "ordered".
// * <p>
// * As of now, it does not work right,
// * so it can be considered a worse alternative to the {@link AlternationRule}.
// */
//public final class OrderedChoiceRule extends RuleWithManyChildren {
//    private OrderedChoiceRule(final @NotNull List<Rule> rules,
//                              final boolean hide,
//                              final @NotNull ReductionType red) {
//        super(hide, red, rules);
//    }
//
//    /**
//     * Create a new instance. Depending on the implementation, allows for buffering or create a different type of rule.
//     *
//     * @param rules The wrapped symbol.
//     * @return A rule.
//     */
//    public static @NotNull Rule create(final @NotNull List<Rule> rules) {
//        if (rules.isEmpty())
//            return EpsilonTerm.getDefault();
//        if (rules.size() == 1)
//            return rules.getFirst();
//
//        var compressedRules = new ArrayList<Rule>();
//
//        for (@NotNull Rule rule : rules) {
//            if (rule instanceof OrderedChoiceRule cc) {
//                compressedRules.addAll(cc.getRules());
//            } else {
//                compressedRules.add(rule);
//            }
//        }
//
//        return new OrderedChoiceRule(
//                compressedRules.stream().distinct().toList(),
//                defaultHidden, defaultReductionType);
//    }
//
//    @Override
//    public void parse(final int index, final @NotNull Gll runner) {
//        ocListener(index, rules, new TrampolineListenerKey(index, this), runner);
//    }
//
//    @Override
//    public void fullParse(final int index, final @NotNull Gll runner) {
//        ocFullListener(index, rules, new TrampolineListenerKey(index, this), runner);
//    }
//
//    private void ocListener(final int index,
//                            final @NotNull List<Rule> rules1,
//                            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
//                            final @NotNull Gll runner) {
//        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
//                new TrampolineListenerKey(index, rules1.getFirst());
//        runner.pushListener(nodeKeyForComb1, runner.nodeListener(nodeKey));
//
//        var restRules = rules1.subList(1, rules1.size());
//        if (!restRules.isEmpty())
//            runner.pushNegativeListener(
//                    nodeKeyForComb1,
//                    () -> ocListener(index, restRules, nodeKey, runner));
//    }
//
//    private void ocFullListener(final int index,
//                                final @NotNull List<Rule> rules1,
//                                final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
//                                final @NotNull Gll runner) {
//        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
//                new TrampolineListenerKey(index, rules1.getFirst());
//        runner.pushFullListener(nodeKeyForComb1, runner.nodeListener(nodeKey));
//
//        var restRules = rules1.subList(1, rules1.size());
//        if (!restRules.isEmpty())
//            runner.pushNegativeListener(
//                    nodeKeyForComb1,
//                    () -> ocFullListener(index, restRules, nodeKey, runner));
//    }
//
//    @Override
//    public @NotNull OrderedChoiceRule withHideTag(final boolean hide) {
//        return isHidden() == hide ? this : new OrderedChoiceRule(getRules(), hide, this.getReduction());
//    }
//
//    @Override
//    public @NotNull OrderedChoiceRule withReduction(final @NotNull ReductionType red) {
//        return getReduction() == red ? this : new OrderedChoiceRule(getRules(), isHidden(), red);
//    }
//
//    @Override
//    public @NotNull OrderedChoiceRule withRules(final @NotNull List<@NotNull Rule> rules) {
//        return new OrderedChoiceRule(rules, isHidden(), getReduction());
//    }
//}
