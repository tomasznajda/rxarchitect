package com.tomasznajda.rxarchitect

import android.view.View
import com.tomasznajda.ktx.junit.assertEquals
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.util.Disposables
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.Test
import java.util.concurrent.TimeUnit

class ArchPresenterTest_Disposables {

    class TestView : ArchView
    class TestViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class TestPresenter : ArchPresenter<TestView, TestViewModel>(TestViewModel()) {
        fun testSave(disposable: Disposable, disposables: Disposables) =
                disposable.save(disposables)
        public override fun onCleared() = super.onCleared()
    }

    @Test
    fun `detachView disposes all saved view related disposables`() {
        val presenter = TestPresenter()
        val viewDisposable = Observable.interval(1, TimeUnit.SECONDS).subscribe()
        val presenterDisposable = Observable.interval(1, TimeUnit.SECONDS).subscribe()
        presenter.testSave(viewDisposable, Disposables.VIEW)
        presenter.testSave(presenterDisposable, Disposables.PRESENTER)
        presenter.detachView()
        assertEquals(expected = true, actual = viewDisposable.isDisposed)
        assertEquals(expected = false, actual = presenterDisposable.isDisposed)
    }

    @Test
    fun `onCleared disposes all saved presenter related disposables`() {
        val presenter = TestPresenter()
        val viewDisposable = Observable.interval(1, TimeUnit.SECONDS).subscribe()
        val presenterDisposable = Observable.interval(1, TimeUnit.SECONDS).subscribe()
        presenter.testSave(viewDisposable, Disposables.VIEW)
        presenter.testSave(presenterDisposable, Disposables.PRESENTER)
        presenter.onCleared()
        assertEquals(expected = false, actual = viewDisposable.isDisposed)
        assertEquals(expected = true, actual = presenterDisposable.isDisposed)
    }
}