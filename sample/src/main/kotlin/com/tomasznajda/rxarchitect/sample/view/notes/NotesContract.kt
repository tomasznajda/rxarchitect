package com.tomasznajda.rxarchitect.sample.view.notes

import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.sample.entity.Note
import io.reactivex.Observable

interface NotesContract {

    interface View : ArchView {
        val singleNoteClicks: Observable<Note>
        val createNoteClicks: Observable<Unit>
        fun startCreateNoteScreen()
        fun startEditNoteScreen(note: Note)
    }
}