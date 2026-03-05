package com.socialscreencontrol.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.socialscreencontrol.core.model.AccountabilityGroup
import com.socialscreencontrol.core.model.ChatMessage
import com.socialscreencontrol.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: GroupRepository
) : ViewModel() {
    private val uid = auth.currentUser?.uid.orEmpty()
    private val selectedGroupId = MutableStateFlow<String?>(null)

    val groups: StateFlow<List<AccountabilityGroup>> = repository.observeGroups(uid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val chat: StateFlow<List<ChatMessage>> = selectedGroupId.flatMapLatest {
        if (it == null) kotlinx.coroutines.flow.flowOf(emptyList()) else repository.observeGroupChat(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createGroup(name: String, members: List<String>) = viewModelScope.launch {
        repository.createGroup(name, members)
    }

    fun selectGroup(groupId: String) {
        selectedGroupId.value = groupId
    }

    fun sendMessage(content: String) = viewModelScope.launch {
        selectedGroupId.value?.let { repository.sendChatMessage(it, content) }
    }
}
