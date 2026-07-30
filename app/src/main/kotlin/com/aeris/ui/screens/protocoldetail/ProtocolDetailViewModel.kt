package com.aeris.ui.screens.protocoldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.domain.model.Protocol
import com.aeris.domain.repository.ProtocolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProtocolDetailViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository
) : ViewModel() {

    private val _protocol = MutableStateFlow<Protocol?>(null)
    val protocol: StateFlow<Protocol?> = _protocol.asStateFlow()

    fun loadProtocol(id: String) {
        viewModelScope.launch {
            protocolRepository.getProtocolById(id).first()?.let {
                _protocol.value = it
            }
        }
    }
}
