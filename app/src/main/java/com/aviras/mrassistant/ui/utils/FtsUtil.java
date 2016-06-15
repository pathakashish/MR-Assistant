package com.aviras.mrassistant.ui.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * FTS util will create list of all possible searches for given search string. Since Realm does not
 * support FTS natively, this is my hack to support it.
 * <p/>
 * Created by ashish on 15/6/16.
 */
public class FtsUtil {

    private static final int MIN_QUERY_LENGTH = 1;

    public static List<CharSequence> getAllPossibleSearchStrings(CharSequence searchString) {
        List<CharSequence> ftsList = new ArrayList<>();
        if (null != searchString && searchString.length() >= MIN_QUERY_LENGTH) {
            int strLength = searchString.length();
            if (1 == strLength) {
                ftsList.add(searchString.subSequence(0, 1));
            }
            for (int index = 2; index <= strLength; index++) {
                ftsList.add(searchString.subSequence(0, index));
            }
        }
        return ftsList;
    }
}
