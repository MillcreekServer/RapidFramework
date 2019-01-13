package io.github.wysohn.rapidframework.utils.strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern numberPattern
            = Pattern.compile("^([\\+\\-]){0,1}(([0-9]+)|([0-9]+\\.)|([0-9]+\\.[0-9]+))$");

    public static boolean isNumber(String str){
        Matcher matcher = numberPattern.matcher(str);
        return matcher.matches();
    }
}
