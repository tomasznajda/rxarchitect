package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModel
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.scope.ArchScope
import com.tomasznajda.rxarchitect.scope.ArchScopeStore
import com.tomasznajda.rxarchitect.util.Disposables
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

abstract class ArchPresenter<ViewT : ArchView, ModelT : ArchViewModel>(initModel: ModelT)
    : ViewModel() {

    private var _view: WeakReference<ViewT>? = null
    private val modelChanges = BehaviorSubject.createDefault(initModel)
    private var created = false
    private val disposables = mapOf(Disposables.VIEW to CompositeDisposable(),
                                    Disposables.PRESENTER to CompositeDisposable())
    protected open val scopes = emptyList<KClass<*>>()

    protected val view: ViewT?
        get() = _view?.get()
    protected var model: ModelT = initModel
        private set

    protected fun update(model: ModelT, render: Boolean = true) {
        this.model = model
        if (render) modelChanges.onNext(model)
    }

    protected open fun created() = Unit

    protected open fun attached() = Unit

    protected open fun detached() = Unit

    protected open fun destroyed() = Unit

    protected fun Disposable.save(disposables: Disposables) = addTo(this@ArchPresenter.disposables[disposables]!!)

    protected fun <ScopeT : ArchScope> get(scope: KClass<ScopeT>) = ArchScopeStore.get(this, scope)

    internal fun attachView(view: ViewT) {
        _view = WeakReference(view)
        if (created.not()) {
            attachToScopes()
            created(); created = true
        }
        attached()
    }

    internal fun detachView() {
        disposables[Disposables.VIEW]?.clear()
        _view?.clear()
        _view = null
        detached()
    }

    override fun onCleared() {
        disposables[Disposables.PRESENTER]?.clear()
        detachFromScopes()
        destroyed()
    }

    internal fun observe(observer: (ModelT) -> Unit) {
        modelChanges
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(observer)
                .subscribe()
                .save(Disposables.VIEW)
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachToScopes() =
            scopes.forEach { ArchScopeStore.attach(this, it as KClass<ArchScope>) }

    @Suppress("UNCHECKED_CAST")
    private fun detachFromScopes() =
            scopes.forEach { ArchScopeStore.detach(this, it as KClass<ArchScope>) }
}