package gov.nga.utils;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CaseInsensitiveSubstringMatcher extends TypeSafeMatcher<String> {

    private final String subString;

    private CaseInsensitiveSubstringMatcher(final String subString) {
        this.subString = subString;
    }

    @Override
    protected boolean matchesSafely(final String actualString) {
        return actualString.toLowerCase().contains(this.subString.toLowerCase());
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("containing substring \"" + this.subString + "\"");
    }

    @Factory
    public static Matcher<String> containsStringCaseInsensitive(final String subString) {
        return new CaseInsensitiveSubstringMatcher(subString);
    }
}