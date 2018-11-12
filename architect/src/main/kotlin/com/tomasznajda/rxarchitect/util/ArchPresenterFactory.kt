package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tomasznajda.rxarchitect.ArchPresenter
import kotlin.reflect.KClass

class ArchPresenterFactoriesHolder(private val factories: Map<KClass<*>, () -> ArchPresenter<*, *>>)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            createOrNull(modelClass) ?: throw IllegalArgumentException("Factory for $modelClass is not registered")

    @Suppress("UNCHECKED_CAST")
    private fun <T : ViewModel?> createOrNull(modelClass: Class<T>) =
            factories.keys.find { it.java == modelClass }?.let { factories[it] }?.invoke() as? T
}