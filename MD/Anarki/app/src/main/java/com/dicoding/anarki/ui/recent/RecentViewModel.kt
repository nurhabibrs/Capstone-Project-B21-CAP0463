package com.dicoding.anarki.ui.recent

import androidx.lifecycle.ViewModel
import com.dicoding.anarki.data.PredictRepository

class RecentViewModel(private val predictRepository: PredictRepository) : ViewModel() {

    fun getHistory() = predictRepository.getHistory()

    fun deleteHistory() = predictRepository.deleteHistory()
}