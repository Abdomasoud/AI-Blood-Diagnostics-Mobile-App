package com.aiblooddiagnostics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiblooddiagnostics.data.repository.BloodDiagnosticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DoctorsViewModel @Inject constructor(
    private val repository: BloodDiagnosticsRepository
) : ViewModel() {

    val doctors = repository.getAllDoctors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}