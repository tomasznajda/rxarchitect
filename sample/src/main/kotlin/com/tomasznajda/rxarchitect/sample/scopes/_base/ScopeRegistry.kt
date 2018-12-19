package com.tomasznajda.rxarchitect.sample.scopes._base

import com.tomasznajda.rxarchitect.sample.scopes.NotesScope
import com.tomasznajda.rxarchitect.scope.ArchScopeStore

class ScopeRegistry {

    fun registerScopes() {
        ArchScopeStore.register(NotesScope::class, { NotesScope() }, singleton = true)
    }
}