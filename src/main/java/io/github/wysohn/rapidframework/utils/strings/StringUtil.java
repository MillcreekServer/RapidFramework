package io.github.wysohn.rapidframework.utils.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern numberPattern = Pattern
	    .compile("^([\\+\\-]){0,1}(([0-9]+)|([0-9]+\\.)|([0-9]+\\.[0-9]+))$");

    public static boolean isNumber(String str) {
	Matcher matcher = numberPattern.matcher(str);
	return matcher.matches();
    }

    /**
     * Repeat the given String for given number of times. If "abc" is repeated 3
     * times, it will be "abcabcabc"
     * 
     * @param str   the string to repeat
     * @param count numbers to repeat. value less than 2 will return itself.
     * @return the repeated String.
     */
    public static String repeat(String str, int count) {
	if (count < 2)
	    return str;

	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < count; i++)
	    builder.append(str);
	return builder.toString();
    }
}
