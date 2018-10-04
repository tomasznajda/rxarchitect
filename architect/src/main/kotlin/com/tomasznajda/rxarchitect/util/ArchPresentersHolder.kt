package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import kotlin.reflect.KClass

internal class ArchPresentersHolder<ViewT : ArchView> {

    private var presenters = emptyList<KClass<ArchPresenter<ViewT, ArchViewModel<ViewT>>>>()

    fun addClass(presenter: KClass<ArchPresenter<ViewT, ArchViewModel<ViewT>>>) {
        presenters = presenters.toMutableList().apply { add(presenter) }.toList()
    }

    fun forEach(modelProvider: ViewModelProvider,
                action: (ArchPresenter<ViewT, ArchViewModel<ViewT>>) -> Unit) =
            presenters.forEach { action(get(modelProvider, it)) }

    private fun get(modelProvider: ViewModelProvider,
                    presenterClass: KClass<ArchPresenter<ViewT, ArchViewModel<ViewT>>>) =
            modelProvider.get(presenterClass.java.name, presenterClass.java)
}