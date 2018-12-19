package com.tomasznajda.rxarchitect.sample.view.single_note

import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.tomasznajda.ktx.rxjava2.merge
import com.tomasznajda.rxarchitect.ArchActivity
import com.tomasznajda.rxarchitect.sample.R
import com.tomasznajda.rxarchitect.sample.entity.Note
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_single_note.*

class SingleNoteActivity
    : ArchActivity<SingleNoteContract.View>(R.layout.activity_single_note),
      SingleNoteContract.View {

    override val presenters =
            mapOf(SingleNotePresenter::class createdBy {
                SingleNotePresenter(SingleNoteViewModel(
                        id = note?.id ?: System.currentTimeMillis(),
                        name = note?.name ?: "",
                        content = note?.content ?: "",
                        pinned = note?.pinned ?: false,
                        updateInMillis = note?.updateInMillis ?: System.currentTimeMillis()
                ))
            })

    override val backClicks by lazy { merge(btnBack.clicks(), backPresses).share()!! }
    override val pinClicks by lazy { btnPin.clicks() }
    override val unpinClicks by lazy { btnUnpin.clicks() }
    override val deleteClicks by lazy { btnDelete.clicks() }
    override val undoClicks by lazy { btnUndo.clicks() }
    override val redoClicks by lazy { btnRedo.clicks() }
    override val nameChanges by lazy { fldName.textChanges().skipInitialValue().map { it.toString() }!! }
    override val contentChanges by lazy { fldContent.textChanges().skipInitialValue().map { it.toString() }!! }
    override val note: Note? by lazy { intent.getParcelableExtra<Note>(NOTE_EXTRA) }

    private val backPresses = PublishSubject.create<Unit>()

    override fun closeScreen() = finish()

    override fun onBackPressed() = backPresses.onNext(Unit)
}