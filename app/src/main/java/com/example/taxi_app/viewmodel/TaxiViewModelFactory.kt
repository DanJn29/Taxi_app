package com.example.taxi_app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaxiViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaxiViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
