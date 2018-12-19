package com.tomasznajda.rxarchitect.sample

import android.app.Application
import com.tomasznajda.rxarchitect.sample.scopes._base.ScopeRegistry
import net.danlew.android.joda.JodaTimeAndroid

class NotesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initScopes()
        initJodaTime()
    }

    private fun initScopes() = ScopeRegistry().registerScopes()

    private fun initJodaTime() = JodaTimeAndroid.init(this)
}