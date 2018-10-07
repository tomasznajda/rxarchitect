package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import kotlin.reflect.KClass

internal class ArchViewDelegate<ViewT : ArchView> {

    private val presenters = ArchPresentersHolder<ViewT>()

    internal fun attach(view: ViewT, modelProvider: ViewModelProvider) {
        observeModels(view, modelProvider)
        attachPresenters(view, modelProvider)
    }

    internal fun detach(modelProvider: ViewModelProvider) {
        detachPresenters(modelProvider)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <PresenterT : ArchPresenter<ViewT, *>> inject(presenterClass: KClass<PresenterT>) {
        presenters.addClass(presenterClass as KClass<ArchPresenter<ViewT, ArchViewModel<ViewT>>>)
    }

    private fun observeModels(view: ViewT, modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.observe { model -> model.render(view) } }

    private fun attachPresenters(view: ViewT, modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.attachView(view) }

    private fun detachPresenters(modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.detachView() }
}