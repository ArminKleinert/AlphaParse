(ns instaparse.abnf
    "This is the context free grammar that recognizes ABNF notation."
    ;(:refer-clojure :exclude [cat])
    (:require [instaparse.transform :as t]
      [instaparse.cfg :as cfg]
      [instaparse.gll :as gll]
      [instaparse.reduction :as reduction]
      [instaparse.util :refer [throwRuntimeException]]
      [instaparse.combinators-source :refer
       [Epsilon optional-combinator plus-combinator star-combinator repetition-combinator alternation-combinator ordered-choice-combinator cat-combinator string-case-insensitive string-terminal
        string-case-insensitive createRegexTerminal make-non-terminal make-lookahead negate-rule enable-hide-tag hide-tag unicode-char]]
      [clojure.walk :as walk])
    (:import (instaparse.gll Failure)
      (java.util Collection Map)))

(def ^:dynamic *case-insensitive*
  "This is normally set to false, in which case the non-terminals
are treated as case-sensitive, which is NOT the norm
for ABNF grammars. If you really want case-insensitivity,
bind this to true, in which case all non-terminals
will be converted to upper-case internally (which
you'll have to keep in mind when transforming)."
  false)

(def ^Map abnf-core
  {:ALPHA  (createRegexTerminal "[a-zA-Z]")
   :BIT    (createRegexTerminal "[01]")
   :CHAR   (createRegexTerminal "[\\u0001-\\u007F]")
   :CR     (string-terminal "\u000D")
   :CRLF   (string-terminal "\u000D\u000A")
   :CTL    (createRegexTerminal "[\\u0000-\\u001F|\\u007F]")
   :DIGIT  (createRegexTerminal "[0-9]")
   :DQUOTE (string-terminal "\u0022")
   :HEXDIG (createRegexTerminal "[0-9a-fA-F]")
   :HTAB   (string-terminal "\u0009")
   :LF     (string-terminal "\u000A")
   :LWSP   (instaparse.combinators-source/alternation-combinator (instaparse.combinators-source/alternation-combinator (string-terminal "\u0020") (string-terminal "\u0009"))   ;WSP
                                                                 (star-combinator
                                                                   (instaparse.combinators-source/cat-combinator (string-terminal "\u000D\u000A")              ;CRLF
                                                                                                                 (instaparse.combinators-source/alternation-combinator (string-terminal "\u0020") (string-terminal "\u0009"))))) ;WSP
   :OCTET  (createRegexTerminal "[\\u0000-\\u00FF]")
   :SP     (string-terminal "\u0020")
   :VCHAR  (createRegexTerminal "[\\u0021-\\u007E]")
   :WSP    (instaparse.combinators-source/alternation-combinator (string-terminal "\u0020")                           ;SP
                                                                 (string-terminal "\u0009"))})                        ;HTAB

(def ^String abnf-grammar-common
  "
<rulelist> = <opt-whitespace> (rule | hide-tag-rule)+;
rule = rulename-left <defined-as> alternation <opt-whitespace>;
hide-tag-rule = hide-tag <defined-as> alternation <opt-whitespace>;
rulename-left = rulename;
rulename-right = rulename;
<hide-tag> = <'<' opt-whitespace> rulename-left <opt-whitespace '>'>;
defined-as = <opt-whitespace> ('=' | '=/') <opt-whitespace>;
alternation = concatenation (<opt-whitespace '/' opt-whitespace> concatenation)*;
concatenation = repetition (<whitespace> repetition)*;
repetition = [repeat] <opt-whitespace> element;
repeat = NUM | (NUM? '*' NUM?);
<element> = rulename-right | group | hide | option | char-val | num-val
          | look | neg | regexp;
look = <'&' opt-whitespace> element;
neg = <'!' opt-whitespace> element;
<group> = <'(' opt-whitespace> alternation <opt-whitespace ')'>;
option = <'[' opt-whitespace> alternation <opt-whitespace ']'>;
hide = <'<' opt-whitespace> alternation <opt-whitespace '>'>;
char-val = <'\\u0022'> #'[\\u0020-\\u0021\\u0023-\\u007E]'* <'\\u0022'> (* double-quoted strings *)
         | <'\\u0027'> #'[\\u0020-\\u0026\u0028-\u007E]'* <'\\u0027'>;  (* single-quoted strings *)
<num-val> = <'%'> (bin-val | dec-val | hex-val);
bin-val = <'b'> bin-char
          [ (<'.'> bin-char)+ | ('-' bin-char) ];
bin-char = ('0' | '1')+;
dec-val = <'d'> dec-char
          [ (<'.'> dec-char)+ | ('-' dec-char) ];
dec-char = DIGIT+;
hex-val = <'x'> hex-char
          [ (<'.'> hex-char)+ | ('-' hex-char) ];
hex-char = HEXDIG+;
NUM = DIGIT+;
<DIGIT> = #'[0-9]';
<HEXDIG> = #'[0-9a-fA-F]';


(* extra entrypoint to be used by the abnf combinator *)
<rules-or-parser> = rulelist | alternation;
  ")

(def ^String abnfGrammarCljOnly
  "
<rulename> = #'[a-zA-Z][-a-zA-Z0-9]*(?x) #identifier';
opt-whitespace = #'\\s*(?:;.*?(?:\\u000D?\\u000A\\s*|$))*(?x) # optional whitespace or comments';
whitespace = #'\\s+(?:;.*?\\u000D?\\u000A\\s*)*(?x) # whitespace or comments';
regexp = #\"#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'(?x) #Single-quoted regexp\"
       | #\"#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"(?x) #Double-quoted regexp\"
")

(def ^String abnfGrammarCljsOnly
  "
<rulename> = #'[a-zA-Z][-a-zA-Z0-9]*';
opt-whitespace = #'\\s*(?:;.*?(?:\\u000D?\\u000A\\s*|$))*';
whitespace = #'\\s+(?:;.*?\\u000D?\\u000A\\s*)*';
regexp = #\"#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'\"
       | #\"#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"\"
")

#_(defmacro precompile-cljs-grammar
            []
            (let [combinators (reduction/apply-standard-reductions
                                :hiccup (cfg/ebnf (str abnf-grammar-common
                                                       abnfGrammarCljsOnly)))]
                 (walk/postwalk
                   (fn [form]
                       (cond
                         ;; Lists cannot be evaluated verbatim
                         (seq? form)
                         (list* 'list form)

                         ;; Regexp terminals are handled differently in cljs
                         (= :regexp (:tag form))
                         `(merge (createRegexTerminal ~(str (:regexp form)))
                                 ~(dissoc form :tag :regexp))

                         :else form))
                   combinators)))

(def abnfParser (reduction/apply-standard-reductions
                  :hiccup (cfg/ebnf (str abnf-grammar-common
                                         abnfGrammarCljOnly))))

(defn getCharCombinator
      [& nums]
      (cond
        (= "-" (second nums)) (let [[lo _ hi] nums]
                                   (unicode-char lo hi))
        :else (apply instaparse.combinators-source/cat-combinator (for [n nums]
                                                                       (unicode-char n)))))

(defn ^Map restrictMap
      "Restricts map to certain keys"
      [^Map m ^Collection ks]
      (into {}
            (for [k ks
                  :when (contains? m k)]
                 [k (m k)])))

(defn mergeCore
      "Merges abnf-core map in with parsed grammar map"
      [^Map grammar-map]
      (merge
        (restrictMap abnf-core (distinct (mapcat cfg/seq-nt (vals grammar-map))))
        grammar-map))

#_(defn hide-tag?
        "Tests whether parser was constructed with hide-tag"
        [^Map p]
        (= (:red p) reduction/rawNonTerminalReduction))

(defn altPreservingHideTag
      [parser1 parser2]
      (let [hide-tag-p1? (= (:red parser1) reduction/rawNonTerminalReduction)
            hide-tag-p2? (= (:red parser2) reduction/rawNonTerminalReduction)]
           (cond
             (and hide-tag-p1? hide-tag-p2?)
             (hide-tag (instaparse.combinators-source/alternation-combinator (dissoc parser1 :red) (dissoc parser2 :red)))
             hide-tag-p1?
             (hide-tag (instaparse.combinators-source/alternation-combinator (dissoc parser1 :red) parser2))
             hide-tag-p2?
             (hide-tag (instaparse.combinators-source/alternation-combinator parser1 (dissoc parser2 :red)))
             :else
             (instaparse.combinators-source/alternation-combinator parser1 parser2))))

#_(defn parse-int
        ([^String string] (Integer/parseInt string))
        ([^String string ^Integer radix] (Integer/parseInt string radix)))

(def ^Map abnf-transformer
  {
   :rule           hash-map
   :hide-tag-rule  (fn [tag rule] {tag (hide-tag rule)})
   :rulename-left  #(if *case-insensitive*
                      (keyword (clojure.string/upper-case (apply str %&)))
                      (keyword (apply str %&)))
   :rulename-right #(if *case-insensitive*
                      (make-non-terminal (keyword (clojure.string/upper-case (apply str %&))))
                      (make-non-terminal (keyword (apply str %&))))
   ; since rulenames are case insensitive, convert it to upper case internally to be consistent
   :alternation    instaparse.combinators-source/alternation-combinator
   :concatenation  instaparse.combinators-source/cat-combinator
   :repeat         (fn [& items]
                       (case (count items)
                             1 (cond
                                 (= (first items) "*") {}         ; *
                                 :else {:low (first items), :high (first items)}) ; x
                             2 (cond
                                 (= (first items) "*") {:high (second items)} ; *x
                                 :else {:low (first items)})      ; x*
                             3 {:low (first items), :high (nth items 2)})) ; x*y

   :repetition     (fn
                     ([repeat element]
                      (cond
                        (empty? repeat) (star-combinator element)
                        (= (count repeat) 2) (repetition-combinator (:low repeat) (:high repeat) element)
                        (= (:low repeat) 1) (plus-combinator element)
                        (= (:high repeat) 1) (optional-combinator element)
                        :else (repetition-combinator (or (:low repeat) 0)
                                                     (or (:high repeat) Double/POSITIVE_INFINITY)
                                                     element)))
                     ([element]
                      element))
   :option         optional-combinator
   :hide           enable-hide-tag
   :look           make-lookahead
   :neg            negate-rule
   :regexp         (comp createRegexTerminal cfg/process-regexp)
   :char-val       (fn [& cs]
                       (cfg/string+ (apply str cs) true))
   :bin-char       (fn [& cs]
                       (Integer/parseInt (apply str cs) 2))
   :dec-char       (fn [& cs]
                       (Integer/parseInt (apply str cs)))
   :hex-char       (fn [& cs]
                       (Integer/parseInt (apply str cs) 16))
   :bin-val        getCharCombinator
   :dec-val        getCharCombinator
   :hex-val        getCharCombinator
   :NUM            #(Integer/parseInt (apply str %&))})

(defn rules->grammar-map
      [rules]
      (mergeCore (apply merge-with altPreservingHideTag rules)))

(defn abnf
      "Takes an ABNF grammar specification string and returns the combinator version.
    If you give it the right-hand side of a rule, it will return the combinator equivalent.
    If you give it a series of rules, it will give you back a grammar map.
    Useful for combining with other combinators."
      [spec & {:as opts}]
      (binding [cfg/*case-insensitive-literals* (:string-ci opts :default)]
               (let [tree (gll/parse abnfParser :rules-or-parser spec false)]
                    (cond
                      (instance? Failure tree)
                      (throwRuntimeException
                        "Error parsing grammar specification:\n"
                        (with-out-str (println tree)))
                      (= :alternation (ffirst tree))
                      (t/transform abnf-transformer (first tree))
                      :else (rules->grammar-map (t/transform abnf-transformer tree))))))

(defn build-parser
      [spec output-format]
      (let [rule-tree (gll/parse abnfParser :rulelist spec false)]
           (if (instance? Failure rule-tree)
             (throwRuntimeException
               "Error parsing grammar specification:\n"
               (with-out-str (println rule-tree)))
             (let [rules (t/transform abnf-transformer rule-tree)
                   grammar-map (rules->grammar-map rules)
                   start-production (first (first (first rules)))]
                  {:grammar          (cfg/check-grammar (reduction/apply-standard-reductions output-format grammar-map))
                   :start-production start-production
                   :output-format    output-format}))))

