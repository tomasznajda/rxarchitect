package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import kotlin.reflect.KClass

internal class ArchPresentersHolder<ViewT : ArchView> {

    internal var presenters = emptyList<KClass<ArchPresenter<ViewT, ArchViewModel>>>()

    @Suppress("UNCHECKED_CAST")
    fun <PresenterT : ArchPresenter<ViewT, ArchViewModel>> add(presenter: KClass<PresenterT>) {
        presenters = presenters
                .toMutableList()
                .apply { add(presenter as KClass<ArchPresenter<ViewT, ArchViewModel>>) }
                .toList()
    }

    fun forEach(modelProvider: ViewModelProvider,
                action: (ArchPresenter<ViewT, ArchViewModel>) -> Unit) =
            presenters.forEach { action(get(modelProvider, it)) }

    private fun get(modelProvider: ViewModelProvider,
                    presenterClass: KClass<ArchPresenter<ViewT, ArchViewModel>>) =
            modelProvider.get(presenterClass.java)
}