package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class ArchViewDelegate<ViewT : ArchView> {

    private val presenters = ArchPresentersHolder<ViewT>()

    internal fun onCreate(modelProvider: ViewModelProvider) {
        observeModels(modelProvider)
        attachPresenters(modelProvider)
    }

    internal fun onDestroy(modelProvider: ViewModelProvider) {
        detachPresenters(modelProvider)
    }

    internal fun <PresenterT : ArchPresenter<ViewT, *>> inject(presenterClass: KClass<PresenterT>) {
        presenters.addClass(presenterClass as KClass<ArchPresenter<ViewT, ArchViewModel<ViewT>>>)
    }

    private fun observeModels(modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.observe { model -> model.render(this as ViewT) } }

    private fun attachPresenters(modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.attachView(this as ViewT) }

    private fun detachPresenters(modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.detachView() }
}