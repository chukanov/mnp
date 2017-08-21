package com.eyeline.mnp.parser;

import java.util.function.Consumer;
/**
 * @author Chukanov
 */
public interface MnpParser {
    public void parseAllPorted(Consumer<Number> consumer) throws Exception;
    public void parseLastestPorted(Consumer<Number> consumer) throws Exception;
    public void parseReturned(Consumer<Number> consumer) throws Exception;

    class Number {
        private String number;
        private String title;

        public Number(String subscriber, String title) {
            this.number = subscriber;
            this.title = title;
        }

        public String getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }
    }

    interface WatchDog {
        public void watch(Runnable onAllUpdates,
                          Runnable onLastUpdates,
                          Runnable onReturnedUpdates) throws Exception;
    }
}
