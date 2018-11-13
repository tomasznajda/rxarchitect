package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import android.view.View
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class ArchViewDelegateTest {

    data class TestView(val value: Int) : ArchView
    class ViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class FirstPresenter : ArchPresenter<TestView, ViewModel>(ViewModel()) {
        fun modelUpdate(model: ViewModel) = update(model)
    }

    class SecondPresenter : ArchPresenter<TestView, ViewModel>(ViewModel()) {
        fun modelUpdate(model: ViewModel) = update(model)
    }

    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `observe observes model changes each presenter and render it on view`() {
        val view = mock<View>()
        val firstModel = spy<ViewModel>()
        val secondModel = spy<ViewModel>()
        val firstPresenter = FirstPresenter()
        val secondPresenter = SecondPresenter()
        val delegate = ArchViewDelegate<TestView>()
        val modelProvider = mock<ViewModelProvider> {
            on { get(FirstPresenter::class.java) }.doReturn(firstPresenter)
            on { get(SecondPresenter::class.java) }.doReturn(secondPresenter)
        }
        delegate.addPresenter(FirstPresenter::class)
        delegate.addPresenter(SecondPresenter::class)
        delegate.observe(view, modelProvider)
        firstPresenter.modelUpdate(firstModel)
        verify(firstModel).render(view)
        secondPresenter.modelUpdate(secondModel)
        verify(secondModel).render(view)
    }

    @Test
    fun `attach attaches view to each presenter`() {
        val firstPresenter = mock<FirstPresenter>()
        val secondPresenter = mock<SecondPresenter>()
        val delegate = ArchViewDelegate<TestView>()
        val modelProvider = mock<ViewModelProvider> {
            on { get(FirstPresenter::class.java) }.doReturn(firstPresenter)
            on { get(SecondPresenter::class.java) }.doReturn(secondPresenter)
        }
        delegate.addPresenter(FirstPresenter::class)
        delegate.addPresenter(SecondPresenter::class)
        delegate.attach(TestView(673), modelProvider)
        verify(firstPresenter).attachView(TestView(673))
        verify(secondPresenter).attachView(TestView(673))
    }

    @Test
    fun `detach detaches view from each presenter`() {
        val firstPresenter = mock<FirstPresenter>()
        val secondPresenter = mock<SecondPresenter>()
        val delegate = ArchViewDelegate<TestView>()
        val modelProvider = mock<ViewModelProvider> {
            on { get(FirstPresenter::class.java) }.doReturn(firstPresenter)
            on { get(SecondPresenter::class.java) }.doReturn(secondPresenter)
        }
        delegate.addPresenter(FirstPresenter::class)
        delegate.addPresenter(SecondPresenter::class)
        delegate.detach(modelProvider)
        verify(firstPresenter).detachView()
        verify(secondPresenter).detachView()
    }
}