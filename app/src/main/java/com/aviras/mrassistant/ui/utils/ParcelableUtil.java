package com.aviras.mrassistant.ui.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for dealing with Parcelables. Handles null checks for objects, and parcels some common non-Parcelable classes
 * <p/>
 * Copied from https://gist.github.com/keyboardr/5563038
 */
public class ParcelableUtil {
    public static void write(Parcel dest, String string) {
        dest.writeByte((byte) (string == null ? 0 : 1));
        if (string != null) {
            dest.writeString(string);
        }
    }

    public static String readString(Parcel source) {
        if (source.readByte() == 1) {
            return source.readString();
        }
        return null;
    }

    public static void write(Parcel dest, Parcelable parcelable, int flags) {
        dest.writeByte((byte) (parcelable == null ? 0 : 1));
        if (parcelable != null) {
            dest.writeParcelable(parcelable, flags);
        }
    }

    public static <T extends Parcelable> T readParcelable(Parcel source) {
        if (source.readByte() == 1) {
            return source.readParcelable(null);
        }
        return null;
    }

    public static void write(Parcel dest, Map<String, String> strings) {
        if (strings == null) {
            dest.writeInt(-1);
        }
        {
            dest.writeInt(strings.keySet().size());
            for (String key : strings.keySet()) {
                dest.writeString(key);
                dest.writeString(strings.get(key));
            }
        }
    }

    public static Map<String, String> readStringMap(Parcel source) {
        int numKeys = source.readInt();
        if (numKeys == -1) {
            return null;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < numKeys; i++) {
            String key = source.readString();
            String value = source.readString();
            map.put(key, value);
        }
        return map;
    }

    public static <T extends Parcelable> void write(Parcel dest,
                                                    Map<String, T> objects, int flags) {
        if (objects == null) {
            dest.writeInt(-1);
        } else {
            dest.writeInt(objects.keySet().size());
            for (String key : objects.keySet()) {
                dest.writeString(key);
                dest.writeParcelable(objects.get(key), flags);
            }
        }
    }

    public static <T extends Parcelable> Map<String, T> readParcelableMap(
            Parcel source) {
        int numKeys = source.readInt();
        if (numKeys == -1) {
            return null;
        }
        HashMap<String, T> map = new HashMap<String, T>();
        for (int i = 0; i < numKeys; i++) {
            String key = source.readString();
            T value = source.readParcelable(null);
            map.put(key, value);
        }
        return map;
    }

    public static void write(Parcel dest, URL url) {
        dest.writeString(url.toExternalForm());
    }

    public static URL readURL(Parcel source) {
        try {
            return new URL(source.readString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(Parcel dest, Date date) {
        dest.writeByte((byte) (date == null ? 0 : 1));
        if (date != null) {
            dest.writeLong(date.getTime());
        }
    }

    public static Date readDate(Parcel source) {
        if (source.readByte() == 1) {
            return new Date(source.readLong());
        }
        return null;
    }

    public static <T extends Enum<T>> void write(Parcel dest, Enum<T> enu) {
        if (enu == null) {
            dest.writeString("");
        } else {
            dest.writeString(enu.name());
        }
    }

    public static <T extends Enum<T>> T readEnum(Parcel dest, Class<T> clazz) {
        String name = dest.readString();
        if ("".equals(name)) {
            return null;
        }
        return Enum.valueOf(clazz, name);
    }

    public static void write(Parcel dest, boolean bool) {
        dest.writeByte((byte) (bool ? 1 : 0));
    }

    public static boolean readBoolean(Parcel source) {
        return source.readByte() == 1;
    }

    public static void write(Parcel dest, float val) {
        dest.writeFloat(val);
    }

    public static float readFloat(Parcel in) {
        return in.readFloat();
    }

    public static void write(Parcel dest, int val) {
        dest.writeInt(val);
    }

    public static int readInt(Parcel in) {
        return in.readInt();
    }

    public static void write(Parcel dest, long val) {
        dest.writeFloat(val);
    }

    public static long readLong(Parcel in) {
        return in.readLong();
    }

    public static <T extends Parcelable> void write(Parcel dest,
                                                    List<T> fields, int flags) {
        if (fields == null) {
            dest.writeInt(-1);
        } else {
            dest.writeInt(fields.size());
            for (T field : fields) {
                dest.writeParcelable(field, flags);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> List<T> readParcelableList(
            Parcel source) {
        int size = source.readInt();
        if (size == -1) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            list.add((T) source.readParcelable(null));
        }
        return list;
    }
}
