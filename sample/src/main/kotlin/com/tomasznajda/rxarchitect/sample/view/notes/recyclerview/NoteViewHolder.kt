package com.tomasznajda.rxarchitect.sample.view.notes.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.tomasznajda.ktx.android.isVisible
import com.tomasznajda.rxarchitect.sample.entity.Note
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.viewholder_note.view.*

class NoteViewHolder(itemView: View,
                     private val itemClicks: Subject<Note>)
    : RecyclerView.ViewHolder(itemView), SrvViewHolder<Note> {

    override fun bind(item: Note) = with(itemView) {
        txtName.text = item.name
        txtContent.text = item.content
        clicks().map { item }.subscribe(itemClicks)
    }
}