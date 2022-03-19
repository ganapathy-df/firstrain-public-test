package com.firstrain.frapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContentTokeHandler {

    private ContentTokeHandler() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String processContent(String value, String token) {
        Pattern ptn = Pattern.compile("\\s+");
        String tokenized = value.replaceAll(token, "");
        Matcher mtch = ptn.matcher(tokenized);
        return mtch.replaceAll(" ");
    }
}
