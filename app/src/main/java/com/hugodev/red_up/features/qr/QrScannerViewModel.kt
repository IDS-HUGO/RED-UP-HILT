package com.hugodev.red_up.features.qr

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class QrScannerUiState(
    val scannedValue: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class QrScannerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState = _uiState.asStateFlow()

    fun onQrScanned(value: String) {
        if (_uiState.value.scannedValue != value) {
            // Log informativo para ver el ID en la consola
            Log.i("RED_UP_QR", "-----------------------------------------")
            Log.i("RED_UP_QR", "CÓDIGO QR DETECTADO")
            Log.i("RED_UP_QR", "ID / CONTENIDO: $value")
            Log.i("RED_UP_QR", "-----------------------------------------")
            
            _uiState.value = _uiState.value.copy(scannedValue = value)
        }
    }

    fun clearScannedValue() {
        _uiState.value = _uiState.value.copy(scannedValue = null)
    }
}
