package com.tomasznajda.rxarchitect.util

import android.view.View
import com.tomasznajda.ktx.junit.assertEquals
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import org.junit.Test

class ArchPresenterFactoriesHolderTest {

    class ViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    data class FirstPresenter(val value: Int) : ArchPresenter<ArchView, ArchViewModel>(ViewModel())
    data class SecondPresenter(val value: Int) : ArchPresenter<ArchView, ArchViewModel>(ViewModel())

    @Test(expected = IllegalArgumentException::class)
    fun `create throw IllegalArgumentException when `() {
        val factoriesHolder = ArchPresenterFactoriesHolder(mapOf(
                FirstPresenter::class to { FirstPresenter(123) }
        ))
        factoriesHolder.create(SecondPresenter::class.java)
    }

    @Test
    fun `create invokes factory`() {
        var invocation = 0
        val factoriesHolder = ArchPresenterFactoriesHolder(mapOf(
                FirstPresenter::class to { invocation++; FirstPresenter(123) }
        ))
        factoriesHolder.create(FirstPresenter::class.java)
        assertEquals(expected = 1, actual = invocation)
    }

    @Test
    fun `create creates presenter of given class`() {
        var invocation = 0
        val factoriesHolder = ArchPresenterFactoriesHolder(mapOf(
                FirstPresenter::class to { invocation++; FirstPresenter(997) }
        ))
        assertEquals(expected = FirstPresenter(997),
                     actual = factoriesHolder.create(FirstPresenter::class.java))
    }
}