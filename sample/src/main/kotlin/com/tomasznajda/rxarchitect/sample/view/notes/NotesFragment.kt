package com.tomasznajda.rxarchitect.sample.view.notes

import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.tomasznajda.rxarchitect.ArchFragment
import com.tomasznajda.rxarchitect.sample.R
import com.tomasznajda.rxarchitect.sample.entity.Category
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.rxarchitect.sample.view.notes.recyclerview.CategoryViewHolder
import com.tomasznajda.rxarchitect.sample.view.notes.recyclerview.NoteViewHolder
import com.tomasznajda.rxarchitect.sample.view.single_note.SingleNoteStarter
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment
    : ArchFragment<NotesContract.View>(R.layout.fragment_notes),
      NotesContract.View {

    override val presenters = mapOf(NotesPresenter::class createdBy { NotesPresenter(NotesViewModel()) })

    override val singleNoteClicks = PublishSubject.create<Note>()
    override val createNoteClicks by lazy { btnCreate.clicks() }

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(Note::class, R.layout.viewholder_note) { NoteViewHolder(it, singleNoteClicks) }
        addViewHolder(Category::class, R.layout.viewholder_category) { CategoryViewHolder(it) }
    }

    override fun injectViews() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun startCreateNoteScreen() = SingleNoteStarter().start(requireContext())

    override fun startEditNoteScreen(note: Note) = SingleNoteStarter().start(requireContext(), note)
}