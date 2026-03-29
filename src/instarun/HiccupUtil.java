package instarun;

import instarun.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HiccupUtil {
    private static final @NotNull Keyword ALT=Keyword.intern("alt");
    private static final @NotNull Keyword CAT=Keyword.intern("cat");
    private static final @NotNull Keyword CHAR=Keyword.intern("char");
    private static final @NotNull Keyword EPSILON=Keyword.intern("epsilon");
    private static final @NotNull Keyword LOOK=Keyword.intern("look");
    private static final @NotNull Keyword NEG=Keyword.intern("neg");
    private static final @NotNull Keyword NT=Keyword.intern("nt");
    private static final @NotNull Keyword OPT=Keyword.intern("opt");
    private static final @NotNull Keyword ORD=Keyword.intern("ord");
    private static final @NotNull Keyword PLUS=Keyword.intern("plus");
    private static final @NotNull Keyword REGEXP=Keyword.intern("regexp");
    private static final @NotNull Keyword REP=Keyword.intern("rep");
    private static final @NotNull Keyword STAR=Keyword.intern("star");
    private static final @NotNull Keyword STRING=Keyword.intern("string");
    private static final @NotNull Keyword STRING_CI=Keyword.intern("string-ci");

    // For ALT and CAT
    private static final @NotNull Keyword parsers=Keyword.intern("parsers");

    // For CHAR
    private static final @NotNull Keyword lo=Keyword.intern("lo");
    private static final @NotNull Keyword hi=Keyword.intern("hi");

    // For LOOK, NEG, OPT, PLUS, STAR
    private static final @NotNull Keyword parser=Keyword.intern("parser");

    // For NT
    private static final @NotNull Keyword keyword=Keyword.intern("keyword");

    // For ORD
    private static final @NotNull Keyword parser1=Keyword.intern("parsers");

    // For REGEXP
    private static final @NotNull Keyword regexp=Keyword.intern("regexp");

    // For REP
    private static final @NotNull Keyword min=Keyword.intern("min");
    private static final @NotNull Keyword max=Keyword.intern("max");

    // For STRING and STRING_CI
    private static final @NotNull Keyword string=Keyword.intern("string");





//    public static ParseTree fromHiccup(final @NotNull List<@NotNull Object> tree) {
//    }
}
