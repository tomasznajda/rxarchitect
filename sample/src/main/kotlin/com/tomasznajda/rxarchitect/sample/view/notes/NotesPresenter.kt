package com.tomasznajda.rxarchitect.sample.view.notes

import com.tomasznajda.ktx.rxjava2.retrySubscribe
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.rxarchitect.sample.scopes.NotesScope
import com.tomasznajda.rxarchitect.util.Disposables

class NotesPresenter(initModel: NotesViewModel)
    : ArchPresenter<NotesContract.View, NotesViewModel>(initModel) {

    override val scopes = listOf(NotesScope::class)

    override fun created() {
        get(NotesScope::class)
                .noteCreatedEvents
                .doOnNext { addNote(it) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.PRESENTER)
        get(NotesScope::class)
                .noteUpdatedEvents
                .doOnNext { updateNote(it) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.PRESENTER)
        get(NotesScope::class)
                .noteDeletedEvents
                .doOnNext { deleteNote(it) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.PRESENTER)
    }

    override fun attached() {
        view!!
                .singleNoteClicks
                .doOnNext { view?.startEditNoteScreen(it) }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
        view!!
                .createNoteClicks
                .doOnNext { view?.startCreateNoteScreen() }
                .retrySubscribe(onError = { it.printStackTrace() })
                .save(Disposables.VIEW)
    }

    private fun addNote(note: Note) {
        val notes = model.notes.toMutableList().apply { add(note) }
        update(model.copy(notes = notes.sortedByDescending { it.updateInMillis }))
    }

    private fun updateNote(note: Note) {
        val position = model.notes.indexOfFirst { it.id == note.id }
        val notes = model.notes.toMutableList().apply { removeAt(position); add(note) }
        update(model.copy(notes = notes.sortedByDescending { it.updateInMillis }))
    }

    private fun deleteNote(note: Note) {
        val position = model.notes.indexOfFirst { it.id == note.id }
        val notes = model.notes.toMutableList().apply { removeAt(position) }
        update(model.copy(notes = notes.sortedByDescending { it.updateInMillis }))
    }
}