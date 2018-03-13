package com.tschuchort.retainedproperties.sample

import android.support.v4.app.FragmentActivity
import com.tschuchort.retainedproperties.retained


class TestActivity : FragmentActivity() {

    companion object {
        val initialValue = 1
    }

    var retainedProperty by retained { initialValue }
    var regularProperty = initialValue
}