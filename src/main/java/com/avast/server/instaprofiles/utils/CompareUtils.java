package com.avast.server.instaprofiles.utils;

import java.util.HashSet;
import java.util.List;

/**
 * @author Vitasek L.
 */
public class CompareUtils {

    private CompareUtils() {
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
