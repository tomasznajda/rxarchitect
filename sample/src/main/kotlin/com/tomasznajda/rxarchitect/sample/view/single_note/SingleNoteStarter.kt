package com.tomasznajda.rxarchitect.sample.view.single_note

import android.content.Context
import android.content.Intent
import com.tomasznajda.rxarchitect.sample.entity.Note

const val NOTE_EXTRA = "NOTE_EXTRA"

class SingleNoteStarter {

    fun start(context: Context, note: Note? = null) {
        Intent(context, SingleNoteActivity::class.java)
                .putExtra(NOTE_EXTRA, note)
                .let { context.startActivity(it) }
    }
}