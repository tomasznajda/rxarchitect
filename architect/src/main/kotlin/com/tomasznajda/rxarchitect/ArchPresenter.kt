package com.tomasznajda.rxarchitect

import android.arch.lifecycle.ViewModel
import android.provider.Telephony
import android.util.Log
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
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
    private val disposables = CompositeDisposable()
    private val viewRelatedDisposables = CompositeDisposable()

    protected val view: ViewT?
        get() = _view?.get()
    protected open var model: ModelT = initModel
        set(value) {
            field = value
            modelChanges.onNext(value)
        }

    protected open fun created() = Unit

    protected open fun attached() = Unit

    protected open fun detached() = Unit

    protected open fun destroyed() = Unit

    protected fun Disposable.saveAsViewRelated() = addTo(viewRelatedDisposables)

    protected fun Disposable.save() = addTo(disposables)

    internal fun attachView(view: ViewT) {
        _view = WeakReference(view)
        if (created.not()) {
            created(); created = true
        }
        attached()
    }

    internal fun detachView() {
        viewRelatedDisposables.clear()
        _view?.clear()
        _view = null
        detached()
    }

    override fun onCleared() {
        disposables.clear()
        destroyed()
    }

    internal fun observe(observer: (ModelT) -> Unit) {
        modelChanges
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { observer(it) }
                .saveAsViewRelated()
    }
}