package com.tomasznajda.rxarchitect.scope

import android.view.View
import com.tomasznajda.ktx.junit.assertEquals
import com.tomasznajda.ktx.junit.assertIsInstanceOf
import com.tomasznajda.rxarchitect.ArchPresenter
import com.tomasznajda.rxarchitect.interfaces.ArchView
import com.tomasznajda.rxarchitect.interfaces.ArchViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class ArchScopeStoreTest {

    class ViewModel : ArchViewModel {
        override fun render(view: View) = Unit
    }

    class FirstScope : ArchScope
    class SecondScope : ArchScope
    class FirstPresenter : ArchPresenter<ArchView, ArchViewModel>(ViewModel())
    class SecondPresenter : ArchPresenter<ArchView, ArchViewModel>(ViewModel())

    val firstPresenter = FirstPresenter()
    val secondPresenter = SecondPresenter()

    @Before
    fun setUp() {
        ArchScopeStore.configs.clear()
        ArchScopeStore.scopes.clear()
        ArchScopeStore.presenters.clear()
    }

    @Test
    fun `register adds scope config to the store's config list`() {
        assertEquals(expected = 0, actual = ArchScopeStore.configs.size)
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = true)
        assertEquals(expected = 1, actual = ArchScopeStore.configs.size)
        assertStoreContainsConfig(FirstScope::class, FirstScope(), singleton = true)
        ArchScopeStore.register(SecondScope::class, { SecondScope() }, singleton = false)
        assertEquals(expected = 2, actual = ArchScopeStore.configs.size)
        assertStoreContainsConfig(SecondScope::class, SecondScope(), singleton = false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `attach throws IllegalArgumentException when store's config list does not contain config for requested scope`() {
        ArchScopeStore.register(SecondScope::class, { SecondScope() }, singleton = false)
        assertEquals(expected = false, actual = ArchScopeStore.configs.isEmpty())
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
    }

    @Test
    fun `attach does not throw IllegalArgumentException when store's config list contains config for requested scope`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = false)
        assertEquals(expected = false, actual = ArchScopeStore.configs.isEmpty())
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
    }

    @Test
    fun `attach creates scope when it is not created`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = false)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        assertStoreContainsScope(FirstScope::class)
    }

    @Test
    fun `attach does not create new scope when it is created`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = false)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        val createdScope = ArchScopeStore.scopes[FirstScope::class]
        ArchScopeStore.attach(secondPresenter, FirstScope::class)
        assertStoreContainsScope(FirstScope::class, createdScope)
    }

    @Test
    fun `detach removes scope when detaching presenter is the last one`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = false)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        ArchScopeStore.detach(firstPresenter, FirstScope::class)
        assertStoreDoesNotContainScope(FirstScope::class)
    }

    @Test
    fun `detach does not remove scope when detaching presenter is not the last one`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = false)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        ArchScopeStore.attach(secondPresenter, FirstScope::class)
        ArchScopeStore.detach(firstPresenter, FirstScope::class)
        assertStoreContainsScope(FirstScope::class)
    }

    @Test
    fun `detach does not remove scope when detaching presenter is the last one, but scope should be a singleton`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = true)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        ArchScopeStore.detach(firstPresenter, FirstScope::class)
        assertStoreContainsScope(FirstScope::class)
    }

    @Test(expected = IllegalStateException::class)
    fun `get throws IllegalStateException when presenter is not attached to requested scope`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = true)
        ArchScopeStore.attach(firstPresenter, FirstScope::class)
        ArchScopeStore.get(secondPresenter, FirstScope::class)
    }

    @Test
    fun `get does not throw IllegalArgumentException when presenter is attached to requested scope`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = true)
        ArchScopeStore.attach(secondPresenter, FirstScope::class)
        ArchScopeStore.get(secondPresenter, FirstScope::class)
    }

    @Test
    fun `get returns instance of requested scope when presenter is attached to requested scope`() {
        ArchScopeStore.register(FirstScope::class, { FirstScope() }, singleton = true)
        ArchScopeStore.attach(secondPresenter, FirstScope::class)
        ArchScopeStore.get(secondPresenter, FirstScope::class).assertIsInstanceOf(FirstScope::class)
    }

    fun assertStoreContainsConfig(clazz: KClass<*>,
                                  instance: ArchScope,
                                  singleton: Boolean) {
        assertEquals(expected = instance.javaClass,
                     actual = ArchScopeStore.configs[clazz]!!.factory().javaClass)
        assertEquals(expected = singleton,
                     actual = ArchScopeStore.configs[clazz]!!.singleton)
    }

    fun assertStoreContainsScope(clazz: KClass<*>, instance: ArchScope? = null) {
        assertNotNull(ArchScopeStore.scopes[clazz])
        instance?.let { assertEquals(expected = it, actual = ArchScopeStore.scopes[clazz]) }
    }

    fun assertStoreDoesNotContainScope(clazz: KClass<*>) {
        assertNull(ArchScopeStore.scopes[clazz])
    }
}