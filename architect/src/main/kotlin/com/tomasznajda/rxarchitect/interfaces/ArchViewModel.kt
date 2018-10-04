package com.tomasznajda.rxarchitect.interfaces

interface ArchViewModel<ViewT : ArchView> {

    fun render(view: ViewT)
}