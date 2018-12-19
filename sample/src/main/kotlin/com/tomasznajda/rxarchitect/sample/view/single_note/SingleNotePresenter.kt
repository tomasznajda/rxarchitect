package com.tomasznajda.rxarchitect.sample.view.single_note

import com.tomasznajda.ktx.kotlin.isNotNull
import com.tomasznajda.ktx.kotlin.isNull
import com.tomasznajda.ktx.rxjava2.retrySubscribe
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.rxarchitect.sample.scopes.NotesScope
import com.tomasznajda.rxarchitect.util.Disposables
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import java.util.concurrent.TimeUnit

class SingleNotePresenter(initModel: SingleNoteViewModel)
    : ArchPresenter<SingleNoteContract.View, SingleNoteViewModel>(initModel) {

    override val scopes = listOf(NotesScope::class)

    private val currentNote: Note
        get() = Note(model.id, model.name, model.content, model.pinned, model.updateInMillis)

    override fun attached() {
        view!!
                .nameChanges
                .filter { it != model.name }
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { updateWithHistory(it, model.content) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .contentChanges
                .filter { it != model.content }
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext { updateWithHistory(model.name, it) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .pinClicks
                .doOnNext { update(model.copy(pinned = true)) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .unpinClicks
                .doOnNext { update(model.copy(pinned = false)) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .backClicks
                .map { currentNote }
                .filter { view?.note.isNull() }
                .doOnNext {
                    if (it.name.isNotBlank() || it.content.isNotBlank())
                        get(NotesScope::class).noteCreated(currentNote)
                }
                .doOnNext { view?.closeScreen() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .backClicks
                .filter { view?.note.isNotNull() }
                .doOnNext { get(NotesScope::class).noteUpdated(currentNote) }
                .doOnNext { view?.closeScreen() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .deleteClicks
                .doOnNext { view?.note?.let { get(NotesScope::class).noteDeleted(it) } }
                .doOnNext { view?.closeScreen() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .undoClicks
                .doOnNext { undo() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .redoClicks
                .doOnNext { redo() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
    }

    private fun updateWithHistory(name: String, content: String) {
        model.historyUndo.push(currentNote)
        model.historyRedo.clear()
        update(model.copy(name = name,
                          content = content,
                          updateInMillis = DateTimeUtils.currentTimeMillis()))
    }

    private fun undo() {
        val previous = model.historyUndo.pop()
        model.historyRedo.push(currentNote)
        update(model.copy(name = previous.name,
                          content = previous.content,
                          updateInMillis = DateTimeUtils.currentTimeMillis()))
    }

    private fun redo() {
        val next = model.historyRedo.pop()
        model.historyUndo.push(currentNote)
        update(model.copy(name = next.name,
                          content = next.content,
                          updateInMillis = DateTimeUtils.currentTimeMillis()))
    }
}