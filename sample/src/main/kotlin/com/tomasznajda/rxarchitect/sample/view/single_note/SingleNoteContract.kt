package com.tomasznajda.rxarchitect.sample.view.single_note

import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.sample.entity.Note
import io.reactivex.Observable

interface SingleNoteContract {

    interface View : ArchView {
        val backClicks: Observable<Unit>
        val pinClicks: Observable<Unit>
        val unpinClicks: Observable<Unit>
        val deleteClicks: Observable<Unit>
        val undoClicks: Observable<Unit>
        val redoClicks: Observable<Unit>
        val nameChanges: Observable<String>
        val contentChanges: Observable<String>
        val note: Note?
        fun closeScreen()
    }
}