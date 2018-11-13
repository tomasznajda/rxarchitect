package com.tomasznajda.rxarchitect.util

import android.arch.lifecycle.ViewModelProvider
import android.view.View
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.tomasznajda.ktx.junit.assertEquals
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import org.junit.Test

class ArchPresentersHolderTest {

    class ViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    data class FirstPresenter(val value: Int) : ArchPresenter<ArchView, ArchViewModel>(ViewModel())
    data class SecondPresenter(val value: Int) : ArchPresenter<ArchView, ArchViewModel>(ViewModel())

    @Test
    fun `add adds presenter class to presenters list`() {
        val holder = ArchPresentersHolder<ArchView>()
        holder.add(FirstPresenter::class)
        assertEquals(expected = listOf(FirstPresenter::class), actual = holder.presenters)
        holder.add(SecondPresenter::class)
        assertEquals(expected = listOf(FirstPresenter::class, SecondPresenter::class),
                     actual = holder.presenters)
    }

    @Test
    fun `forEach invokes action on each presenter instance`() {
        val result = mutableListOf<ArchPresenter<ArchView, ArchViewModel>>()
        val modelProvider = mock<ViewModelProvider> {
            on { get(FirstPresenter::class.java) }.doReturn(FirstPresenter(997))
            on { get(SecondPresenter::class.java) }.doReturn(SecondPresenter(666))
        }
        val holder = ArchPresentersHolder<ArchView>()
        holder.add(FirstPresenter::class)
        holder.add(SecondPresenter::class)
        holder.forEach(modelProvider) { result.add(it) }
        assertEquals(expected = listOf(FirstPresenter(997), SecondPresenter(666)), actual = result)
    }
}