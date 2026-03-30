package alphaparse.util;

import alphaparse.Keyword;

public class KeywordSetup {
    public static void initKeywords() {
        Keyword.intern("NOT");
        Keyword.intern("alt");
        Keyword.intern("ord");
        Keyword.intern("cat");
        Keyword.intern("\0\0\0\0");
        Keyword.intern("neg");
        Keyword.intern("string");
        Keyword.intern("char");
        Keyword.intern("string-ci");
        Keyword.intern("epsilon");
        Keyword.intern("look");
        Keyword.intern("nt");
        Keyword.intern("opt");
        Keyword.intern("plus");
        Keyword.intern("regexp");
        Keyword.intern("rep");
        Keyword.intern("star");
        Keyword.intern("lookahead");
        Keyword.intern("negative-look");
        Keyword.intern("regex");
        Keyword.intern("optional");
    }
}
