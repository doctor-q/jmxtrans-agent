package org.jmxtrans.agent.elastic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexNameBuilder {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%\\{(.+)}");
    private final String indexNamePattern;

    public IndexNameBuilder(String indexNamePattern) {
        this.indexNamePattern = indexNamePattern;
    }

    public String build(Date date) {
        StringBuffer sb = new StringBuffer();
        Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(indexNamePattern);
        while (placeholderMatcher.find()) {
            placeholderMatcher.appendReplacement(sb, format(placeholderMatcher.group(1), date));
        }
        placeholderMatcher.appendTail(sb);
        return sb.toString();
    }

    private String format(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }
}
