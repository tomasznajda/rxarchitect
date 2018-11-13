package com.tomasznajda.rxarchitect

import android.view.View
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

class ArchPresenterTest_ViewModel {

    class TestView : ArchView
    data class TestViewModel(val value: Int) : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class TestPresenter : ArchPresenter<TestView, TestViewModel>(TestViewModel(0)) {
        fun testUpdate(model: TestViewModel, render: Boolean) = super.update(model, render)
    }

    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `observe emits default view model when update has not been invoked yet`() {
        val presenter = TestPresenter()
        val subject = PublishSubject.create<TestViewModel>()
        val observer = subject.test()
        presenter.observe { subject.onNext(it) }
        observer.assertValues(TestViewModel(0))
    }

    @Test
    fun `observe emits new view model after update invocation with render as true`() {
        val presenter = TestPresenter()
        val subject = PublishSubject.create<TestViewModel>()
        val observer = subject.test()
        presenter.observe { subject.onNext(it) }
        presenter.testUpdate(TestViewModel(1), render = true)
        observer.assertValues(TestViewModel(0), TestViewModel(1))
    }

    @Test
    fun `observe does not emit new view model after update invocation with render as false`() {
        val presenter = TestPresenter()
        val subject = PublishSubject.create<TestViewModel>()
        val observer = subject.test()
        presenter.observe { subject.onNext(it) }
        presenter.testUpdate(TestViewModel(1), render = false)
        observer.assertValues(TestViewModel(0))
    }

    @Test
    fun `observe does not emit new view model after update invocation after view detach`() {
        val presenter = TestPresenter()
        val subject = PublishSubject.create<TestViewModel>()
        val observer = subject.test()
        presenter.observe { subject.onNext(it) }
        presenter.detachView()
        presenter.testUpdate(TestViewModel(1), render = true)
        observer.assertValues(TestViewModel(0))
    }

    @Test
    fun `observe emits only last view model when update been invoked many times before observing`() {
        val presenter = TestPresenter()
        val subject = PublishSubject.create<TestViewModel>()
        val observer = subject.test()
        presenter.testUpdate(TestViewModel(1), render = true)
        presenter.testUpdate(TestViewModel(2), render = true)
        presenter.testUpdate(TestViewModel(3), render = true)
        presenter.observe { subject.onNext(it) }
        observer.assertValues(TestViewModel(3))
    }
}