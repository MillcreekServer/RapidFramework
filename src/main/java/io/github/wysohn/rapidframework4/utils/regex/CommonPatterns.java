package io.github.wysohn.rapidframework4.utils.regex;

import java.util.regex.Pattern;

public class CommonPatterns {
    public static final Pattern INTEGER = Pattern.compile("^[0-9]+$");
    public static final Pattern DOUBLE = Pattern.compile("^([0-9]+\\.[0-9]+)$|^([0-9]+)$");
}
