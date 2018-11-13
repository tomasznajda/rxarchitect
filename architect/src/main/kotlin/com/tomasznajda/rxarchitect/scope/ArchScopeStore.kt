package com.tomasznajda.rxarchitect.scope

import com.tomasznajda.rxarchitect.ArchPresenter
import kotlin.reflect.KClass

object ArchScopeStore {

    internal val configs = mutableMapOf<KClass<*>, ArchScopeConfig<*>>()
    internal val scopes = mutableMapOf<KClass<*>, ArchScope>()
    internal val presenters = mutableMapOf<KClass<*>, List<ArchPresenter<*, *>>>()

    fun <ScopeT : ArchScope> register(clazz: KClass<ScopeT>,
                                      factory: () -> ScopeT,
                                      singleton: Boolean = false) {
        configs[clazz] = ArchScopeConfig(factory, singleton)
    }

    internal fun <ScopeT : ArchScope> get(presenter: ArchPresenter<*, *>,
                                          scope: KClass<ScopeT>): ScopeT {
        if (presenters[scope].orEmpty().contains(presenter).not())
            throw IllegalStateException("${presenter.javaClass.simpleName} is not attached to $scope")
        return get(scope)
    }

    internal fun <ScopeT : ArchScope> attach(presenter: ArchPresenter<*, *>,
                                             scope: KClass<ScopeT>) {
        if (shouldCreate(scope)) create(scope)
        presenters[scope] = presenters[scope].orEmpty().toMutableList().apply { add(presenter) }
    }

    internal fun <ScopeT : ArchScope> detach(presenter: ArchPresenter<*, *>,
                                             scope: KClass<ScopeT>) {
        presenters[scope] = presenters[scope].orEmpty().toMutableList().apply { remove(presenter) }
        if (shouldDestroy(scope)) destroy(scope)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <ScopeT : ArchScope> get(scope: KClass<ScopeT>) =
            scopes[scope] as? ScopeT ?: throw IllegalArgumentException("${scope.java.simpleName} is not created")

    private fun <ScopeT : ArchScope> create(scope: KClass<ScopeT>) {
        scopes[scope] = getConfig(scope).factory.invoke()
    }

    private fun <ScopeT : ArchScope> destroy(scope: KClass<ScopeT>) {
        scopes.remove(scope)
    }

    private fun <ScopeT : ArchScope> getConfig(scope: KClass<ScopeT>) =
            configs[scope] ?: throw IllegalArgumentException("${scope.java.simpleName} configuration is not registered")

    private fun <ScopeT : ArchScope> shouldCreate(scope: KClass<ScopeT>) =
            isScopeCreated(scope).not()

    private fun <ScopeT : ArchScope> shouldDestroy(scope: KClass<ScopeT>) =
            hasAttachedPresenters(scope).not() && getConfig(scope).singleton.not()

    private fun <ScopeT : ArchScope> isScopeCreated(scope: KClass<ScopeT>) =
            scopes.contains(scope)

    private fun <ScopeT : ArchScope> hasAttachedPresenters(scope: KClass<ScopeT>) =
            presenters[scope].orEmpty().isNotEmpty()
}