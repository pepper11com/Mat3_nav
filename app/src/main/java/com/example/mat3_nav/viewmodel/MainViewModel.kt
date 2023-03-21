package com.example.mat3_nav.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application)  {

    var scrollPosition = 0

    fun onScrollPositionChanged(position: Int) {
        scrollPosition = position
    }

}