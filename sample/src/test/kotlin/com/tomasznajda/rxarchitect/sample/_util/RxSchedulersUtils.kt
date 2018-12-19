package com.tomasznajda.rxarchitect.sample._util

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins

fun overrideSchedulers(scheduler: Scheduler) {
    RxJavaPlugins.setIoSchedulerHandler { scheduler }
    RxJavaPlugins.setComputationSchedulerHandler { scheduler }
    RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }
}

fun resetSchedulers() {
    RxJavaPlugins.reset()
    RxAndroidPlugins.reset()
}