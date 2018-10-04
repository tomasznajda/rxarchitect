package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.util.ArchViewDelegate
import kotlin.reflect.KClass

abstract class ArchFragment<ViewT : ArchView>(@LayoutRes private val layoutId: Int) : Fragment() {

    private val delegate = ArchViewDelegate<ViewT>()

    protected fun <PresenterT : ArchPresenter<ViewT, *>> inject(presenterClass: KClass<PresenterT>) =
            delegate.inject(presenterClass)

    open fun injectPresenters() = Unit

    open fun injectViews() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectPresenters()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(layoutId, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        injectViews()
        delegate.onCreate(ViewModelProviders.of(this))
    }

    override fun onDestroyView() {
        delegate.onDestroy(ViewModelProviders.of(this))
        super.onDestroyView()
    }
}