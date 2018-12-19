package com.tomasznajda.rxarchitect.test

import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import io.reactivex.subjects.PublishSubject

fun <ViewT : ArchView,
        ModelT : ArchViewModel,
        PresenterT : ArchPresenter<ViewT, ModelT>> PresenterT.test(view: ViewT) = TestArchPresenter(this, view)

class TestArchPresenter<ViewT : ArchView,
        ModelT : ArchViewModel,
        PresenterT : ArchPresenter<ViewT, ModelT>>(val presenter: PresenterT,
                                                   val view: ViewT) {
    private val modelChanges = PublishSubject.create<ModelT>()
    var observer = modelChanges.test()

    init {
        presenter.observe { modelChanges.onNext(it) }
    }

    fun create() = apply { presenter.create() }

    fun attach() = apply { presenter.attach(view) }

    fun detach() = apply { presenter.detach() }

    fun destroy() = apply { presenter.destroy() }

    fun observe() = apply { observer = modelChanges.test() }

    fun assertModel(model: ModelT) = observer.assertValue(model)

    fun assertModel(predicate: (ModelT) -> Boolean) = observer.assertValue { predicate(it) }

    fun assertModels(vararg models: ModelT) = observer.assertValueSequence(models.toList())

    fun assertModels(vararg predicates: (ModelT) -> Boolean) = predicates.forEachIndexed { index, predicate -> observer.assertValueAt(index) { predicate(it) } }

    fun assertNoModels() = observer.assertNoValues()
}