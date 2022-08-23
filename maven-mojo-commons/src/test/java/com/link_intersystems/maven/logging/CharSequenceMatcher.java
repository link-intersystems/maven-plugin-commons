package com.link_intersystems.maven.logging;

import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.internal.matchers.ContainsExtraTypeInfo;
import org.mockito.internal.matchers.Equality;
import org.mockito.internal.matchers.text.ValuePrinter;

import java.io.Serializable;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class CharSequenceMatcher implements ArgumentMatcher<CharSequence>, ContainsExtraTypeInfo, Serializable {

    public static CharSequence eq(CharSequence wanted) {
        return Mockito.argThat(new CharSequenceMatcher(wanted));
    }

    private final CharSequence wanted;

    public CharSequenceMatcher(CharSequence wanted) {
        this.wanted = wanted;
    }

    @Override
    public boolean matches(CharSequence actual) {
        String wantedString = String.valueOf(this.wanted);
        String actualString = String.valueOf(actual);
        return Equality.areEqual(wantedString, actualString);
    }

    @Override
    public String toString() {
        return describe(wanted);
    }

    private String describe(Object object) {
        return ValuePrinter.print(object);
    }

    @Override
    public final Object getWanted() {
        return wanted;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CharSequenceMatcher)) {
            return false;
        }
        CharSequenceMatcher other = (CharSequenceMatcher) o;
        return (this.wanted == null && other.wanted == null)
                || this.wanted != null && this.wanted.equals(other.wanted);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toStringWithType(String className) {
        return "(" + className + ") " + describe(wanted);
    }

    @Override
    public boolean typeMatches(Object target) {
        return wanted != null && target != null && target.getClass() == wanted.getClass();
    }
}
