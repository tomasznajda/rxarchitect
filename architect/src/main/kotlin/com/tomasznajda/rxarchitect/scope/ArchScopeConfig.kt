package com.tomasznajda.rxarchitect.scope

data class ArchScopeConfig<ScopeT : ArchScope>(val factory: () -> ScopeT,
                                               val singleton: Boolean)