package com.tschuchort.retainedproperties.sample

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.assertThat
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.tschuchort.retainedproperties.sample.OrientationChangeAction.orientationLandscape
import com.tschuchort.retainedproperties.sample.OrientationChangeAction.orientationPortrait
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class RetainedPropertyTest {

    @get:Rule
    val activityRule = ActivityTestRule(TestActivity::class.java, false, true)

    protected val activity get() =  activityRule.activity!!

    @Test
    fun property_value_retained_across_config_change() {
        require(activity.retainedProperty == TestActivity.initialValue)

        val newValue = 3
        activity.retainedProperty = newValue


        // ensure activity is in portrait first
        onView(isRoot()).perform(orientationPortrait())

        // then change orientation
        onView(isRoot()).perform(orientationLandscape())

        sleep(1500)

        assertEquals((getCurrentActivity() as TestActivity).retainedProperty, newValue)
    }

    @Test
    fun property_value_lost_across_config_change() {
        require(activity.regularProperty == TestActivity.initialValue)

        val newValue = 3
        activity.regularProperty = newValue

        // ensure activity is in portrait first
        onView(isRoot()).perform(orientationPortrait())

        // then change orientation
        onView(isRoot()).perform(orientationLandscape())

        sleep(1500)

        assertEquals((getCurrentActivity() as TestActivity).regularProperty, TestActivity.initialValue)
    }
}