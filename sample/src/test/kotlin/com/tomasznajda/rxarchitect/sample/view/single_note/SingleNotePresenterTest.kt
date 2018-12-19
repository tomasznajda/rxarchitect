package com.tomasznajda.rxarchitect.sample.view.single_note

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.tomasznajda.rxarchitect.sample._util.overrideSchedulers
import com.tomasznajda.rxarchitect.sample._util.resetSchedulers
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.rxarchitect.sample.scopes._base.ScopeRegistry
import com.tomasznajda.rxarchitect.test.test
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTimeUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class SingleNotePresenterTest {

    val view = mock<SingleNoteContract.View>()
    val backClicks = PublishSubject.create<Unit>()
    val pinClicks = PublishSubject.create<Unit>()
    val unpinClicks = PublishSubject.create<Unit>()
    val deleteClicks = PublishSubject.create<Unit>()
    val undoClicks = PublishSubject.create<Unit>()
    val redoClicks = PublishSubject.create<Unit>()
    val nameChanges = PublishSubject.create<String>()
    val contentChanges = PublishSubject.create<String>()

    @Before
    fun setUp() {
        whenever(view.backClicks).doReturn(backClicks)
        whenever(view.pinClicks).doReturn(pinClicks)
        whenever(view.unpinClicks).doReturn(unpinClicks)
        whenever(view.deleteClicks).doReturn(deleteClicks)
        whenever(view.undoClicks).doReturn(undoClicks)
        whenever(view.redoClicks).doReturn(redoClicks)
        whenever(view.nameChanges).doReturn(nameChanges)
        whenever(view.contentChanges).doReturn(contentChanges)
        overrideSchedulers(Schedulers.trampoline())
        ScopeRegistry().registerScopes()
    }

    @After
    fun tearDown() {
        resetSchedulers()
    }

    @Test
    fun `nameChange emits model with new name when name is not the same as previous`() {
        val model = SingleNoteViewModel(name = "name12")
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name123") }
                .assertModel { it.name == "name123" }
    }

    @Test
    fun `nameChange does not emit any model when name is the same as previous`() {
        val model = SingleNoteViewModel(name = "name12")
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name12") }
                .assertNoModels()
    }

    @Test
    fun `nameChange emits model with updated update in millis to now when name is not the same as previous`() {
        DateTimeUtils.setCurrentMillisFixed(999)
        val model = SingleNoteViewModel(name = "name12", updateInMillis = 0)
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name123") }
                .assertModel { it.updateInMillis == 999L }
        DateTimeUtils.setCurrentMillisSystem()
    }

    @Test
    fun `nameChange emits model with unchanged content when name is not the same as previous`() {
        val model = SingleNoteViewModel(name = "name12", content = "content")
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name123") }
                .assertModel { it.content == "content" }
    }

    @Test
    fun `nameChange emits model with previous note inside undo history when name is not the same as previous`() {
        val model = SingleNoteViewModel(name = "name12")
        val note = model.toNote()
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name123") }
                .assertModel { it.historyUndo.first() == note }
    }

    @Test
    fun `nameChange emits model with cleared redo history when name is not the same as previous`() {
        val model = SingleNoteViewModel(name = "name12")
        model.historyRedo.push(model.toNote().copy(name = "name1"))
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .observe()
                .also { nameChanges.onNext("name123") }
                .assertModel { it.historyRedo.isEmpty() }
    }

    @Test
    fun `nameChange emits only last model when the gap before previous ones was less than 500 millis`() {
        val scheduler = TestScheduler()
        overrideSchedulers(scheduler)
        val model = SingleNoteViewModel(name = "name12")
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .also { scheduler.triggerActions() }
                .observe()
                .also { nameChanges.onNext("name123") }
                .also { scheduler.advanceTimeBy(499, TimeUnit.MILLISECONDS) }
                .also { nameChanges.onNext("name1234") }
                .also { scheduler.advanceTimeBy(499, TimeUnit.MILLISECONDS) }
                .also { nameChanges.onNext("name12345") }
                .also { scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS) }
                .assertModel { it.name == "name12345" }
    }

    @Test
    fun `nameChange emits all models when the gap between each one were at least 500 millis`() {
        val scheduler = TestScheduler()
        overrideSchedulers(scheduler)
        val model = SingleNoteViewModel(name = "name12")
        SingleNotePresenter(model)
                .test(view)
                .create()
                .attach()
                .also { scheduler.triggerActions() }
                .observe()
                .also { nameChanges.onNext("name123") }
                .also { scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS) }
                .also { nameChanges.onNext("name1234") }
                .also { scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS) }
                .also { nameChanges.onNext("name12345") }
                .also { scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS) }
                .assertModels(
                        { it.name == "name123" },
                        { it.name == "name1234" },
                        { it.name == "name12345" })
    }

    fun SingleNoteViewModel.toNote() = Note(id, name, content, pinned, updateInMillis)
}