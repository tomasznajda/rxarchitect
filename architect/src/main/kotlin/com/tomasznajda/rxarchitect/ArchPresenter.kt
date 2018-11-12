package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModel
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.util.Disposables
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import java.lang.ref.WeakReference

abstract class ArchPresenter<ViewT : ArchView, ModelT : ArchViewModel<*>>(initModel: ModelT) :
        ViewModel() {

    private var _view: WeakReference<ViewT>? = null
    private val modelChanges = BehaviorSubject.createDefault(initModel)
    private var created = false
    private val disposables = mapOf(Disposables.VIEW to CompositeDisposable(),
                                    Disposables.PRESENTER to CompositeDisposable())

    protected val view: ViewT?
        get() = _view?.get()
    protected var model: ModelT = initModel
        private set

    protected open fun created() = Unit

    protected open fun attached() = Unit

    protected open fun detached() = Unit

    protected open fun destroyed() = Unit

    protected fun Disposable.save(disposables: Disposables) = addTo(this@ArchPresenter.disposables[disposables]!!)

    protected fun <ScopeT : ArchScope> get(scope: KClass<ScopeT>) = ArchScopeStore.get(this, scope)

    protected fun update(model: ModelT, render: Boolean = true) {
        this.model = model
        if (render) modelChanges.onNext(model)
    }

    internal fun attachView(view: ViewT) {
        _view = WeakReference(view)
        if (created.not()) {
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
        destroyed()
    }

    internal fun observe(observer: (ModelT) -> Unit) {
        modelChanges
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(observer)
                .subscribe()
                .save(Disposables.VIEW)
    }
}