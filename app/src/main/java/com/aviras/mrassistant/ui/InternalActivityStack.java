package com.aviras.mrassistant.ui;

import android.app.Activity;
import android.app.Application;

import com.aviras.mrassistant.MrAssistantApp;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Maintains internal stack of activities which can be used to finish all activities at any point.
 * <p/>
 * Created by ashish on 3/2/16.
 */
public class InternalActivityStack {
    private static final List<SoftReference<Activity>> sActivityStack = new ArrayList<>();

    private InternalActivityStack() {
    }

    /**
     * Use this method in onCreate to push activity instance on internal stack
     *
     * @param activity instance to be pushed to stack
     */
    public static void pushActivity(Activity activity) {
        if (sActivityStack.size() == 0) {
            Application app = activity.getApplication();
            if (app instanceof MrAssistantApp) {
                ((MrAssistantApp) app).initPresenters();
            }
        }
        sActivityStack.add(new SoftReference<>(activity));
    }

    /**
     * Use this method in onDestroy to remove activity instance from internal stack
     *
     * @param activity instance to be removed from stack
     */
    public static void removeActivity(Activity activity) {
        SoftReference<Activity> foundActivityRef = findSoftReferenceFor(activity);
        sActivityStack.remove(foundActivityRef);
        if (sActivityStack.size() == 0) {
            Realm.getDefaultInstance().removeAllChangeListeners();
            Realm.getDefaultInstance().close();
        }
    }

    private static SoftReference<Activity> findSoftReferenceFor(Activity activity) {
        SoftReference<Activity> foundActivityRef = null;
        for (SoftReference<Activity> activityRef : sActivityStack) {
            if (activityRef.get() == activity) {
                foundActivityRef = activityRef;
                break;
            }
        }
        return foundActivityRef;
    }

    /**
     * Finishes and removes recently added activity
     */
    public static void finishTopActivityIfExists() {
        synchronized (sActivityStack) {
            int i = sActivityStack.size() - 1;
            if (i >= 0) {
                SoftReference<Activity> activityRef = sActivityStack.get(i);
                Activity activity = activityRef.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                    sActivityStack.remove(activityRef);
                }
            }
        }
    }

    /**
     * Finishes all activities which were launched except supplied activity
     *
     * @param activity actvity instance which should not be finished
     */
    public static void finishAllActivitiesExcept(Activity activity) {
        synchronized (sActivityStack) {

            int i = sActivityStack.size() - 1;
            do {
                if (i < 0) {
                    break;
                }
                SoftReference<Activity> activityRef = sActivityStack.get(i);
                Activity activityInSoftRef = activityRef.get();
                if (activityInSoftRef != null && !activityInSoftRef.isFinishing() && activityInSoftRef != activity) {
                    activityInSoftRef.finish();
                    sActivityStack.remove(activityRef);
                }
                i--;
            } while (true);
        }
    }

    /**
     * Finishes all activities which were launched
     */
    public static boolean finishAllActivities() {
        boolean anyActivityInForeground = false;
        synchronized (sActivityStack) {
            int i = sActivityStack.size() - 1;
            do {
                if (i < 0) {
                    break;
                }
                SoftReference<Activity> activityRef = sActivityStack.get(i);
                Activity activityInSoftRef = activityRef.get();
                if (activityInSoftRef != null && !activityInSoftRef.isFinishing()) {
                    if (activityInSoftRef instanceof BaseActivity) {
                        if (!anyActivityInForeground) {
                            anyActivityInForeground = ((BaseActivity) activityInSoftRef).isInForeground();
                        }
                    }
                    activityInSoftRef.finish();
                    sActivityStack.remove(activityRef);
                }
                i--;
            } while (true);
        }
        return anyActivityInForeground;
    }

    public static boolean isAnyActivityInForeground() {
        boolean anyActivityInForeground = false;
        synchronized (sActivityStack) {
            for (SoftReference<Activity> activityRef : sActivityStack) {
                Activity activityInSoftRef = activityRef.get();
                if (activityInSoftRef instanceof BaseActivity) {
                    if (!anyActivityInForeground) {
                        anyActivityInForeground = ((BaseActivity) activityInSoftRef).isInForeground();
                    }
                }
            }
        }
        return anyActivityInForeground;
    }
}
