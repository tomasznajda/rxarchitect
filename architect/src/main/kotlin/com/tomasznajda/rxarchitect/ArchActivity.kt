package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.util.ArchPresenterFactoriesHolder
import com.tomasznajda.rxarchitect.util.ArchViewDelegate
import kotlin.reflect.KClass

abstract class ArchActivity<ViewT : ArchView>(@LayoutRes private val layoutId: Int)
    : AppCompatActivity() {

    private val delegate = ArchViewDelegate<ViewT>()
    private val presenterFactoriesHolder by lazy { ArchPresenterFactoriesHolder(presenters) }
    private val viewModelProvider: ViewModelProvider
        get() = ViewModelProviders.of(this, presenterFactoriesHolder)

    protected open val presenters = emptyMap<KClass<*>, () -> ArchPresenter<*, *>>()

    open fun injectViews() = Unit

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        presenters.forEach { delegate.addPresenter(it as KClass<ArchPresenter<ViewT, *>>) }
        injectViews()
        delegate.observe(findViewById<View>(android.R.id.content), viewModelProvider)
        delegate.attach(this as ViewT, viewModelProvider)
    }

    override fun onDestroy() {
        delegate.detach(viewModelProvider)
        super.onDestroy()
    }

    protected infix fun <PresenterT : ArchPresenter<*, *>> KClass<PresenterT>.createdBy(factory: () -> PresenterT) =
            Pair(this as KClass<*>, factory as () -> ArchPresenter<*, *>)
}