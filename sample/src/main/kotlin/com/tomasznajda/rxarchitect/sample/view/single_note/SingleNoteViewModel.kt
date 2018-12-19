package com.tomasznajda.rxarchitect.sample.view.single_note

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.tomasznajda.ktx.android.format
import com.tomasznajda.ktx.android.isVisible
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.sample.R
import com.tomasznajda.rxarchitect.sample.entity.Note
import kotlinx.android.synthetic.main.activity_single_note.view.*
import java.util.*

data class SingleNoteViewModel(val id: Long = System.currentTimeMillis(),
                               val name: String = "",
                               val content: String = "",
                               val pinned: Boolean = false,
                               val updateInMillis: Long = System.currentTimeMillis(),
                               val historyUndo: Stack<Note> = Stack(),
                               val historyRedo: Stack<Note> = Stack()) : ArchViewModel {

    override fun render(view: View) = with(view) {
        fldName.setTextDistinct(name)
        fldContent.setTextDistinct(content)
        btnPin.isVisible = pinned.not()
        btnUnpin.isVisible = pinned
        txtModificationTime.text = context.getString(
                R.string.single_note_last_modification,
                Date(updateInMillis).format("dd.MM.yyyy HH:mm"))
        txtModificationTime.isVisible = historyUndo.isEmpty() && historyRedo.isEmpty()
        groupHistoryButtons.isVisible = historyUndo.isNotEmpty() || historyRedo.isNotEmpty()
        btnUndo.isEnabled = historyUndo.isNotEmpty()
        btnRedo.isEnabled = historyRedo.isNotEmpty()
    }

    private fun EditText.setTextDistinct(text: String) {
        if(this.text.toString() != text) {
            setText(text)
            setSelection(text.length)
        }
    }
}