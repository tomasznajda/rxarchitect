package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.util.ArchViewDelegate
import kotlin.reflect.KClass

abstract class ArchActivity<ViewT : ArchView>(@LayoutRes private val layoutId: Int) :
        AppCompatActivity() {

    private val delegate = ArchViewDelegate<ViewT>()

    protected fun <PresenterT : ArchPresenter<ViewT, *>> inject(presenterClass: KClass<PresenterT>) =
            delegate.inject(presenterClass)

    open fun injectPresenters() = Unit

    open fun injectViews() = Unit

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        injectPresenters()
        injectViews()
        delegate.observe(findViewById<View>(android.R.id.content), ViewModelProviders.of(this))
        delegate.attach(this as ViewT, ViewModelProviders.of(this))
    }

    override fun onDestroy() {
        delegate.detach(ViewModelProviders.of(this))
        super.onDestroy()
    }
}