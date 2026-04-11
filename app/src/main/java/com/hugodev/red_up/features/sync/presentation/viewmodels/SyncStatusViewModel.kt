package com.hugodev.red_up.features.sync.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.local.SyncDao
import com.hugodev.red_up.core.sync.SyncWork
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SyncStatusUiState(
    val lastSyncAt: Long? = null,
    val pendingCount: Int = 0,
    val unreadNotifications: Int = 0,
    val lastError: String? = null,
    val isSyncing: Boolean = false
)

@HiltViewModel
class SyncStatusViewModel @Inject constructor(
    private val syncDao: SyncDao,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncStatusUiState())
    val uiState: StateFlow<SyncStatusUiState> = _uiState.asStateFlow()

    init {
        observeStatus()
    }

    private fun observeStatus() {
        viewModelScope.launch {
            combine(syncDao.observeSyncStatus(), syncDao.observeNotificationSummary()) { status, summary ->
                SyncStatusUiState(
                    lastSyncAt = status?.lastSyncAt,
                    pendingCount = status?.pendingCount ?: 0,
                    unreadNotifications = summary?.unreadCount ?: 0,
                    lastError = status?.lastError,
                    isSyncing = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun runManualSync() {
        _uiState.value = _uiState.value.copy(isSyncing = true)
        SyncWork.enqueueImmediateSync(appContext)
    }
}
