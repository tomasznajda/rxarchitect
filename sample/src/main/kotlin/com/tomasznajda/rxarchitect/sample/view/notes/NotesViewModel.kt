package com.tomasznajda.rxarchitect.sample.view.notes

import android.view.View
import com.tomasznajda.ktx.android.isVisible
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.sample.entity.Category
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.fragment_notes.view.*

data class NotesViewModel(val notes: List<Note> = emptyList()) : ArchViewModel {

    private val pinnedNotes: List<Note>
        get() = notes.filter { it.pinned }
    private val otherNotes: List<Note>
        get() = notes.filter { it.pinned.not() }

    override fun render(view: View) = with(view) {
        groupEmpty.isVisible = pinnedNotes.isEmpty() && otherNotes.isEmpty()
        recyclerView.isVisible = pinnedNotes.isNotEmpty() || otherNotes.isNotEmpty()
        (recyclerView.adapter as BasicSrvAdapter).items = combineNotes()
    }

    private fun combineNotes() =
            mutableListOf<Any>()
                    .apply { if (pinnedNotes.isNotEmpty()) add(Category.PINNED); addAll(pinnedNotes) }
                    .apply { if (pinnedNotes.isNotEmpty() && otherNotes.isNotEmpty()) add(Category.OTHERS) }
                    .apply { addAll(otherNotes) }
                    .toList()
}