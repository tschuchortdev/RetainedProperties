package com.tschuchort.retainedproperties

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * property delegate that retains a property of an Activity in an arch ViewModel
 *
 * the creation function is executed lazily or never (if assigned to before the first read)
 */
fun <T> FragmentActivity.retained(initialize: () -> T)
        = retained(ViewModelProviders::of, initialize)

/**
 * property delegate that retains a property of a Fragment in an arch ViewModel
 *
 * the creation function is executed lazily or never (if assigned to before the first read)
 */
fun <T> Fragment.retained(initialize: () -> T)
        = retained(ViewModelProviders::of, initialize)

inline fun <T,S> S.retained(crossinline getVmProvider:  (S) -> ViewModelProvider, noinline initialize: () -> T): ReadWriteProperty<S, T>
        = object : RetainedProperty<S,T>(initialize) {

    // ViewModel needs to be provided lazily instead of ctor arg
    // because it's only available after onCreate
    override val retainer by lazy {
        getVmProvider(this@retained).get(RetainerViewModel::class.java)
    }
}

abstract class RetainedProperty<in O,T>(
        private inline val initialize: () -> T) : ReadWriteProperty<O,T> {

    protected abstract val retainer: RetainerViewModel

    // hold on to the map entry so we don't have to search the map with every access
    // to the delegated property
    private var valueHolder:  ValueHolder<T>? = null

    override fun setValue(thisRef: O, property: KProperty<*>, value: T) {
        if(valueHolder != null) {
            valueHolder!!.value = value
        }
        else {
            val propName = property.name

            val holder = retainer.properties[propName]

            if(holder != null) {
                @Suppress("UNCHECKED_CAST")
                valueHolder = holder as ValueHolder<T>
                valueHolder!!.value = value
            }
            else {
                valueHolder = ValueHolder(value)
                retainer.properties[propName] = valueHolder!!
            }
        }
    }

    override fun getValue(thisRef: O, property: KProperty<*>): T {
        if(valueHolder == null) {
            val propName = property.name

            val holder = retainer.properties[propName]

            if(holder != null) {
                @Suppress("UNCHECKED_CAST")
                valueHolder = holder as ValueHolder<T>
            }
            else {
                valueHolder = ValueHolder(initialize())
                retainer.properties[propName] = valueHolder!!
            }
        }

        return valueHolder!!.value
    }
}

internal data class ValueHolder<R>(var value: R)

class RetainerViewModel : ViewModel() {
    /* the property values are stored in holder classes to add another level of indirection so we
        can keep a reference to the holder and don't have to access the HashMap every time while
        still being able to change the value in the holder. In theory, holding on to the Map.Entry
        would accomplish the same thing, but the map entries are not guaranteed to stay valid when
        the backing map is changed
       */
    internal val properties = hashMapOf<String, ValueHolder<*>>()
}