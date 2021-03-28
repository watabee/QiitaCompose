package com.github.watabee.qiitacompose.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.watabee.qiitacompose.api.QiitaApiResult
import com.github.watabee.qiitacompose.api.response.SuccessResponse
import com.github.watabee.qiitacompose.api.response.SuccessResponseWithPagination
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.datastore.UserDataStore
import com.github.watabee.qiitacompose.repository.QiitaRepository
import com.github.watabee.qiitacompose.util.throttleFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val qiitaRepository: QiitaRepository,
    dataStore: UserDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(State(isLoading = true))
    val state: StateFlow<State> = _state.asStateFlow()

    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    val event: Flow<Event> = eventChannel.receiveAsFlow()

    private val actionFollowOrUnfollowUser =
        MutableSharedFlow<Action>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    val dispatchAction: (Action) -> Unit = { action ->
        when (action) {
            is Action.GetUserInfo -> {
                viewModelScope.launch { getUserInfo(action.userId) }
            }
            is Action.FollowUser, is Action.UnfollowUser -> {
                actionFollowOrUnfollowUser.tryEmit(action)
            }
        }
    }

    val isLoggedIn: StateFlow<Boolean> = dataStore.accessTokenFlow
        .map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    init {
        actionFollowOrUnfollowUser.throttleFirst(1000L)
            .onEach { action ->
                when (action) {
                    is Action.FollowUser -> followUser(action.userId)
                    is Action.UnfollowUser -> unfollowUser(action.userId)
                    else -> {
                        // do nothing.
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun getUserInfo(userId: String) {
        supervisorScope {
            _state.emit(State(isLoading = true))
            val (isFollowingUserResult, fetchUserFollowingTagsResult) = awaitAll(
                async { qiitaRepository.isFollowingUser(userId) },
                async { qiitaRepository.fetchUserFollowingTags(userId) }
            )
            @Suppress("UNCHECKED_CAST")
            if (isFollowingUserResult is QiitaApiResult.Success && fetchUserFollowingTagsResult is QiitaApiResult.Success) {
                val isFollowingUser = (isFollowingUserResult.response as SuccessResponse<Boolean>).response
                val followingTags = (fetchUserFollowingTagsResult.response as SuccessResponseWithPagination<List<Tag>>).response
                _state.emit(State(isFollowingUser = isFollowingUser, followingTags = followingTags))
            } else {
                _state.emit(State(getUserInfoError = false))
            }
        }
    }

    private suspend fun followUser(userId: String) {
        when (qiitaRepository.followUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.emit(State(isFollowingUser = true, followingTags = _state.value.followingTags))
            }
            is QiitaApiResult.Failure -> {
                eventChannel.send(Event.ShowFollowUserError)
            }
        }
    }

    private suspend fun unfollowUser(userId: String) {
        when (qiitaRepository.unfollowUser(userId)) {
            is QiitaApiResult.Success -> {
                _state.emit(State(isFollowingUser = false, followingTags = _state.value.followingTags))
            }
            is QiitaApiResult.Failure -> {
                eventChannel.send(Event.ShowUnfollowUserError)
            }
        }
    }

    data class State(
        val isLoading: Boolean = false,
        val getUserInfoError: Boolean = false,
        val isFollowingUser: Boolean = false,
        val followingTags: List<Tag> = emptyList()
    )

    sealed class Event {
        object ShowFollowUserError : Event()
        object ShowUnfollowUserError : Event()
    }

    sealed class Action {
        data class GetUserInfo(val userId: String) : Action()

        data class FollowUser(val userId: String) : Action()

        data class UnfollowUser(val userId: String) : Action()
    }
}