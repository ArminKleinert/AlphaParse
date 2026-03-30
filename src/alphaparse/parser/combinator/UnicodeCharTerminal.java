package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.InstaFailureReasonChar;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UnicodeCharTerminal extends CombinatorTerminal {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final int lo;
    private final int hi;

    public UnicodeCharTerminal(final int lo, final int hi) {
        super();
        if (lo > hi) throw new IllegalArgumentException();
        this.lo = lo;
        this.hi = hi;
    }

    public UnicodeCharTerminal(final int lo, final int hi, final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
        if (lo > hi) throw new IllegalArgumentException();
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull String text = tramp.getText();
        final int lo = getLo();
        final int hi = getHi();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);

        if (index >= text.length()) {
            Gll.fail(tramp, nodeKey, index, new InstaFailureReasonChar(lo, hi));
            return;
        }

        if (hi <= 0xFFFF) {
            final int code = text.charAt(index); // (int (.charAt text index))
            if (lo >= code && code >= hi) {
                Gll.success(tramp, nodeKey, Objects.toString(code), index + 1);
            } else {
                Gll.fail(tramp, nodeKey, index, new InstaFailureReasonChar(lo, hi));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));
        if (lo >= codePoint && codePoint >= hi) {
            Gll.success(tramp, nodeKey, charString, index + charString.length());
        } else {
            Gll.fail(tramp, nodeKey, index, new InstaFailureReasonChar(lo, hi));
        }
    }

    /*
(defn char-range-full-parse
  [^UnicodeCharTerminal this index ^InstaTramp tramp]
  (let [lo (.getLo this)
        hi (.getHi this)
        text (.getText tramp)
        end (count text)]
    (cond
      (>= index (count text)) (Gll1/fail tramp (InstaNodeKey. index this) index(InstaFailureReasonChar. lo hi))
      (<= hi 0xFFFF) (let [code (single-char-code-at text index)]
                       (if (and (= (inc index) end) (<= lo code hi))
                         (Gll1/success tramp (InstaNodeKey. index this) (str (char code)) end)
                         (Gll1/fail tramp (InstaNodeKey. index this) index(InstaFailureReasonChar. lo hi))))
      :else (let [code-point (unicode-code-point-at text index)
                  char-string (code-point->chars code-point)]
              (if (and (= (+ index (count char-string)) end) (<= lo code-point hi))
                (Gll1/success tramp (InstaNodeKey. index this) char-string end)
                (Gll1/fail tramp (InstaNodeKey. index this) index(InstaFailureReasonChar. lo hi true)))))))
     */
    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull String text = tramp.getText();
        final int lo = getLo();
        final int hi = getHi();
        final int end = text.length();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);

        if (index >= text.length()) {
            Gll.fail(tramp, nodeKeyForThis, index, new InstaFailureReasonChar(lo, hi));
            return;
        }

        if (hi <= 0xFFFF) {
            final char c = text.charAt(index);
            final var code = (int) c;
            if (index + 1 == end && lo <= code && code <= hi) {
                Gll.success(tramp, nodeKeyForThis, Character.toString(c), end);
            } else {
                Gll.fail(tramp, nodeKeyForThis, index, new InstaFailureReasonChar(lo, hi));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));

        if ((index + charString.length()) == end && lo <= codePoint && codePoint <= hi) {
            Gll.success(tramp, nodeKeyForThis, charString, end);
        } else {
            Gll.fail(tramp, nodeKeyForThis, index, new InstaFailureReasonChar(lo, hi, true));
        }
    }

    public int getLo() {
        return lo;
    }

    public int getHi() {
        return hi;
    }

    @Override
    public @NotNull UnicodeCharTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new UnicodeCharTerminal(getLo(), getHi(), hide1, this.getReduction());
    }

    @Override
    public @NotNull UnicodeCharTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new UnicodeCharTerminal(getLo(), getHi(), isHidden(), red1);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof UnicodeCharTerminal that)) return false;
        if (hashCode() != o.hashCode()) return false;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        if (!Objects.equals(lo, that.lo)) return false;
        return Objects.equals(hi, that.hi);
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), lo, hi);
        return (int) bufferedHashCode;
    }
}