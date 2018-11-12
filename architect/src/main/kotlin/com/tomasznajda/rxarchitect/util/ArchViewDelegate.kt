package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import android.view.View
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import kotlin.reflect.KClass

internal class ArchViewDelegate<ViewT : ArchView> {

    private val presenters = ArchPresentersHolder<ViewT>()

    @Suppress("UNCHECKED_CAST")
    internal fun <PresenterT : ArchPresenter<ViewT, *>> addPresenter(presenterClass: KClass<PresenterT>) {
        presenters.addClass(presenterClass as KClass<ArchPresenter<ViewT, ArchViewModel>>)
    }

    internal fun observe(view: View, modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.observe { model -> model.render(view) } }

    internal fun attach(view: ViewT, modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.attachView(view) }

    internal fun detach(modelProvider: ViewModelProvider) =
            presenters.forEach(modelProvider) { it.detachView() }
}