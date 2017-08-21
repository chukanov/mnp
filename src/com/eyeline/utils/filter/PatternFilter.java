package com.eyeline.utils.filter;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Chukanov
 */
public class PatternFilter implements Filter {
    private Pattern find;
    private String replace;

    public PatternFilter(String find, String to) {
        this.find = Pattern.compile(find.toLowerCase(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        this.replace = to;
    }

    public String filter(String title) {
        if (find.matcher(title.toLowerCase()).matches()) {
            return replace;
        } else {
            return title;
        }
    }

    public static Filter loadFromProperties(Properties properties) {
        Chain res = new Chain();
        for (String name: properties.stringPropertyNames()) {
            res.add(new PatternFilter(name, properties.getProperty(name)));
        }
        return res;
    }

    @Override
    public String toString() {
        return "PatternFilter{" +
                "find=" + find +
                ", replace='" + replace + '\'' +
                '}';
    }
}
