package com.eyeline.mnp.parser;

import com.eyeline.mnp.mask.Mask;

import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Chukanov
 */
public interface MasksParser {
    public void parse(Consumer<MnoInfo> consumer) throws Exception;

    public static class MnoInfo {
        private String title;
        private String area;
        private String country;
        private Set<Mask> masks;

        public MnoInfo(String title, String area, String country, Set<Mask> masks) {
            this.title = title;
            this.area = area;
            this.country = country;
            this.masks = masks;
        }

        public String getTitle() {
            return title;
        }

        public String getArea() {
            return area;
        }

        public Set<Mask> getMasks() {
            return masks;
        }

        public String getCountry() {
            return country;
        }
    }

    interface WatchDog {
        public void watch(Runnable onUpdate) throws Exception;
    }
}
