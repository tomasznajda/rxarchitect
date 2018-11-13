package com.tomasznajda.rxarchitect

import android.view.View
import com.nhaarman.mockitokotlin2.*
import com.tomasznajda.ktx.junit.assertEquals
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import org.junit.Test

class ArchPresenterTest_Lifecycle {

    data class TestView(val value: Int) : ArchView
    class TestViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class TestPresenter : ArchPresenter<TestView, TestViewModel>(TestViewModel()) {
        fun getView() = view
        public override fun created() = super.created()
        public override fun attached() = super.attached()
        public override fun detached() = super.detached()
        public override fun destroyed() = super.destroyed()
        public override fun onCleared() = super.onCleared()
    }

    @Test
    fun `attachView attaches given view to presenter`() {
        val presenter = TestPresenter()
        presenter.attachView(TestView(111))
        assertEquals(expected = TestView(111), actual = presenter.getView())
    }

    @Test
    fun `detachView detaches current view from presenter`() {
        val presenter = TestPresenter()
        presenter.attachView(TestView(111))
        presenter.detachView()
        assertEquals(expected = null, actual = presenter.getView())
    }

    @Test
    fun `attachView invokes created on first view attach`() {
        val presenter = spy<TestPresenter>()
        presenter.attachView(TestView(111))
        verify(presenter).created()
    }

    @Test
    fun `attachView does not invoke created on second view attach`() {
        val presenter = spy<TestPresenter>()
        presenter.attachView(TestView(111))
        clearInvocations(presenter)
        presenter.detachView()
        presenter.attachView(TestView(111))
        verify(presenter, never()).created()
    }

    @Test
    fun `attachView invokes attached on every view attach`() {
        val presenter = spy<TestPresenter>()
        presenter.attachView(TestView(111))
        verify(presenter, times(1)).attachView(TestView(111))
        presenter.detachView()
        presenter.attachView(TestView(111))
        verify(presenter, times(2)).attachView(TestView(111))
    }

    @Test
    fun `attachView invokes detached on every view detach`() {
        val presenter = spy<TestPresenter>()
        presenter.attachView(TestView(111))
        presenter.detachView()
        verify(presenter, times(1)).detached()
        presenter.attachView(TestView(111))
        presenter.detachView()
        verify(presenter, times(2)).detached()
    }

    @Test
    fun `onCleared invokes destroyed`() {
        val presenter = spy<TestPresenter>()
        presenter.attachView(TestView(111))
        presenter.detachView()
        presenter.onCleared()
        verify(presenter).destroyed()
    }
}