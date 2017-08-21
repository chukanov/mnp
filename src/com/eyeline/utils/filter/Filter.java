package com.eyeline.utils.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chukanov
 */
public interface Filter {
    public String filter(String title);

    public static class Chain implements Filter{
        private List<Filter> filters = new ArrayList<>();

        public void add(Filter filter) {
            filters.add(filter);
        }

        @Override
        public String filter(String title) {
            String result = title;
            for (Filter filter: filters) {
                result = filter.filter(title);
                if (!result.equals(title)) {
                    return result;
                }
            }
            return result;
        }
    }
}
