package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.util.ArchPresenterFactoriesHolder
import com.tomasznajda.rxarchitect.util.ArchViewDelegate
import kotlin.reflect.KClass

abstract class ArchFragment<ViewT : ArchView>(@LayoutRes private val layoutId: Int) : Fragment() {

    private val delegate = ArchViewDelegate<ViewT>()
    private val presenterFactoriesHolder by lazy { ArchPresenterFactoriesHolder(presenters) }
    private val viewModelProvider: ViewModelProvider
        get() = ViewModelProviders.of(this, presenterFactoriesHolder)

    protected open val presenters = emptyMap<KClass<*>, () -> ArchPresenter<*, *>>()

    open fun injectViews() = Unit

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenters.forEach { delegate.addPresenter(it as KClass<ArchPresenter<ViewT, *>>) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(layoutId, container, false)!!

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        injectViews()
        delegate.observe(view, viewModelProvider)
        delegate.attach(this as ViewT, viewModelProvider)
    }

    override fun onDestroyView() {
        delegate.detach(viewModelProvider)
        super.onDestroyView()
    }

    protected infix fun <PresenterT : ArchPresenter<*, *>> KClass<PresenterT>.createdBy(factory: () -> PresenterT) =
            Pair(this as KClass<*>, factory as () -> ArchPresenter<*, *>)
}