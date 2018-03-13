package com.tschuchort.retainedproperties.sample

import android.app.Activity
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor
import android.support.test.runner.lifecycle.Stage


/**
 * Gets an Activity in the RESUMED stage.
 *
 *
 * This method should never be called from the Main thread. In certain situations there might
 * be more than one Activities in RESUMED stage, but only one is returned.
 * See [ActivityLifecycleMonitor].
 */
@Throws(IllegalStateException::class)
fun getCurrentActivity(): Activity {
    // The array is just to wrap the Activity and be able to access it from the Runnable.
    val resumedActivity = arrayOfNulls<Activity>(1)

    getInstrumentation().runOnMainSync {
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
        if (resumedActivities.iterator().hasNext()) {
            resumedActivity[0] = resumedActivities.iterator().next() as Activity
        }
        else {
            throw IllegalStateException("No Activity in stage RESUMED")
        }
    }
    return resumedActivity.first()!!
}

