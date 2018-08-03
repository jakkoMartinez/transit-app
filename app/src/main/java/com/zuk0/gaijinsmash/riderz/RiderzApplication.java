package com.zuk0.gaijinsmash.riderz;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.zuk0.gaijinsmash.riderz.di.component.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/*
    This will create Dagger object
 */
public class RiderzApplication extends Application implements HasActivityInjector{

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
        Log.i("onCreate", "app component initialized");
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

}