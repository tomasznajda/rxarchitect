package com.tomasznajda.rxarchitect.sample.scopes

import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.rxarchitect.scope.ArchScope
import io.reactivex.subjects.PublishSubject

class NotesScope : ArchScope {

    val noteCreatedEvents = PublishSubject.create<Note>()
    val noteUpdatedEvents = PublishSubject.create<Note>()
    val noteDeletedEvents = PublishSubject.create<Note>()

    fun noteCreated(note: Note) = noteCreatedEvents.onNext(note)
    fun noteUpdated(note: Note) = noteUpdatedEvents.onNext(note)
    fun noteDeleted(note: Note) = noteDeletedEvents.onNext(note)
}