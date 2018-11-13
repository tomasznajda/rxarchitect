package com.tomasznajda.rxarchitect

import android.view.View
import com.tomasznajda.ktx.junit.assertIsInstanceOf
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import com.tomasznajda.rxarchitect.scope.ArchScope
import com.tomasznajda.rxarchitect.scope.ArchScopeStore
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class ArchPresenterTest_Scopes {

    class TestScope : ArchScope
    class TestView : ArchView
    class TestViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class TestPresenter(override val scopes: List<KClass<*>>)
        : ArchPresenter<TestView, TestViewModel>(TestViewModel()) {

        fun <ScopeT : ArchScope> getScope(scope: KClass<ScopeT>) = get(scope)
        public override fun onCleared() = super.onCleared()
    }

    @Before
    fun setUp() {
        ArchScopeStore.register(TestScope::class, { TestScope() }, singleton = false)
    }

    @Test(expected = IllegalStateException::class)
    fun `get throws IllegalStateException before first view attach`() {
        val presenter = TestPresenter(listOf(TestScope::class))
        presenter.getScope(TestScope::class)
    }

    @Test
    fun `get returns scope of given class when view is attached`() {
        val presenter = TestPresenter(listOf(TestScope::class))
        presenter.attachView(TestView())
        presenter.getScope(TestScope::class).assertIsInstanceOf(TestScope::class)
    }

    @Test
    fun `get returns scope of given class when view is detached, but presenter is not destroyed`() {
        val presenter = TestPresenter(listOf(TestScope::class))
        presenter.attachView(TestView())
        presenter.detachView()
        presenter.getScope(TestScope::class).assertIsInstanceOf(TestScope::class)
    }

    @Test(expected = IllegalStateException::class)
    fun `get throws IllegalStateException when view is detached and presenter is destroyed`() {
        val presenter = TestPresenter(listOf(TestScope::class))
        presenter.attachView(TestView())
        presenter.detachView()
        presenter.onCleared()
        presenter.getScope(TestScope::class)
    }

    @Test(expected = IllegalStateException::class)
    fun `get throws IllegalStateException when view is attached, but presenter is not added to given scope`() {
        val presenter = TestPresenter(emptyList())
        presenter.attachView(TestView())
        presenter.getScope(TestScope::class).assertIsInstanceOf(TestScope::class)
    }
}