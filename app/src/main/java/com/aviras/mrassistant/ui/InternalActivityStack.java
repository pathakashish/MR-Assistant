package com.aviras.mrassistant.ui;

import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

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
    private static final List<SoftReference<AppCompatActivity>> sActivityStack = new ArrayList<>();

    private InternalActivityStack() {
    }

    /**
     * Use this method in onCreate to push activity instance on internal stack
     *
     * @param activity instance to be pushed to stack
     */
    public static void pushActivity(AppCompatActivity activity) {
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
    public static void removeActivity(AppCompatActivity activity) {
        SoftReference<AppCompatActivity> foundActivityRef = findSoftReferenceFor(activity);
        sActivityStack.remove(foundActivityRef);
        if (sActivityStack.size() == 0) {
            Realm.getDefaultInstance().removeAllChangeListeners();
            Realm.getDefaultInstance().close();
        }
    }

    private static SoftReference<AppCompatActivity> findSoftReferenceFor(AppCompatActivity activity) {
        SoftReference<AppCompatActivity> foundActivityRef = null;
        for (SoftReference<AppCompatActivity> activityRef : sActivityStack) {
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
                SoftReference<AppCompatActivity> activityRef = sActivityStack.get(i);
                AppCompatActivity activity = activityRef.get();
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
    public static void finishAllActivitiesExcept(AppCompatActivity activity) {
        synchronized (sActivityStack) {

            int i = sActivityStack.size() - 1;
            do {
                if (i < 0) {
                    break;
                }
                SoftReference<AppCompatActivity> activityRef = sActivityStack.get(i);
                AppCompatActivity activityInSoftRef = activityRef.get();
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
                SoftReference<AppCompatActivity> activityRef = sActivityStack.get(i);
                AppCompatActivity activityInSoftRef = activityRef.get();
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
            for (SoftReference<AppCompatActivity> activityRef : sActivityStack) {
                AppCompatActivity activityInSoftRef = activityRef.get();
                if (activityInSoftRef instanceof BaseActivity) {
                    if (!anyActivityInForeground) {
                        anyActivityInForeground = ((BaseActivity) activityInSoftRef).isInForeground();
                    }
                }
            }
        }
        return anyActivityInForeground;
    }

    public static FragmentActivity getActivityAtTop() {
        if (sActivityStack.size() > 0) {
            return sActivityStack.get(sActivityStack.size() - 1).get();
        }
        return null;
    }
}
