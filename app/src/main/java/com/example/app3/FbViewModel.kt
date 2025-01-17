package com.example.app3

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FbViewModel @Inject constructor(
    val auth: FirebaseAuth
) : ViewModel() {

    val signedIn = mutableStateOf(false)
    private val inProgress = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    private val _userState = mutableStateOf(UserReplyState())
    val userState: State<UserReplyState> = _userState

    private val _profilePicState = mutableStateOf(ProfilePictureState())

    private val _favoriteState = mutableStateOf(FavoriteReplyState())
    val favoriteState: State<FavoriteReplyState> = _favoriteState

    private val _modifyFavoriteState = mutableStateOf(ModifyFavoriteState())

    private val _watchedState = mutableStateOf(WatchedReplyState())
    val watchedState: State<WatchedReplyState> = _watchedState

    private val _addWatchedState = mutableStateOf(ModifyWatchedState())

    private val _removeWatchedState = mutableStateOf(ModifyWatchedState())

    fun onSignUp(name: String, email: String, pass: String, profilePicture: String?) {
        val body = mapOf(
            "id" to null,
            "full_name" to name,
            "email" to email,
            "password" to pass,
            "meta_api_key" to null,
            "google_api_key" to null,
            "profile_picture" to profilePicture
        )
        inProgress.value = true

        viewModelScope.launch{
            try{
                val response = userService.postNewUser(body)
                _userState.value = _userState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
                signedIn.value = true
            }catch (e: Exception){
                _userState.value = _userState.value.copy(
                    loading = false,
                    error = "Error adding new user ${e.message}"
                )
            }
            inProgress.value = false
        }
    }

    fun login(email: String, pass: String) {
        inProgress.value = true

        viewModelScope.launch{
            try{
                val response = userService.loginUser(email, pass)
                _userState.value = _userState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
                signedIn.value = true
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    loading = false,
                    error = "Error adding new user ${e.message}"
                )
            }
            inProgress.value = false
        }
    }

    fun addUserFromExternalService(body: Map<String, String?>) {
        inProgress.value = true

        viewModelScope.launch{
            try{
                val response = userService.postNewUser(body)
                _userState.value = _userState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
                signedIn.value = true
            }catch (e: Exception){
                _userState.value = _userState.value.copy(
                    loading = false,
                    error = "Error adding new user ${e.message}"
                )
            }
            inProgress.value = false
        }
    }

    fun updateProfilePicture(userId: Long, url: String) {
        viewModelScope.launch{
            try{
                val body = mapOf(
                    "userId" to userId.toString(),
                    "profilePicture" to url
                )
                userService.updateProfilePicture(body)

                _profilePicState.value = _profilePicState.value.copy(
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _profilePicState.value = _profilePicState.value.copy(
                    loading = false,
                    error = "Error updating picture ${e.message}"
                )
            }
        }
    }

    fun clearError(){
        _userState.value = _userState.value.copy(error = null)
    }

    fun getFavorites(userId: Long) {
        viewModelScope.launch{
            try{
                val response = userService.getFavourite(userId)

                _favoriteState.value = _favoriteState.value.copy(
                    list = response,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _favoriteState.value = _favoriteState.value.copy(
                    loading = false,
                    error = "Error fetching favorite series ${e.message}"
                )
            }
        }
    }

    fun addFavoriteSeries(userId: Long, seriesId: Long){
        viewModelScope.launch{
            try{
                val body = mapOf(
                    "userId" to userId,
                    "seriesId" to seriesId
                )
                userService.addFavorite(body)

                _modifyFavoriteState.value = _modifyFavoriteState.value.copy(
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _favoriteState.value = _favoriteState.value.copy(
                    loading = false,
                    error = "Error adding series ${e.message}"
                )
            }
        }
    }

    fun removeFavoriteSeries(userId: Long, seriesId: Long){
        viewModelScope.launch{
            try{
                userService.removeFavorite(userId, seriesId)

                _modifyFavoriteState.value = _modifyFavoriteState.value.copy(
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _favoriteState.value = _favoriteState.value.copy(
                    loading = false,
                    error = "Error removing series ${e.message}"
                )
            }
        }
    }

    fun getWatched(userId: Long, seriesId: Long){
        viewModelScope.launch{
            try{
                val response = userService.getWatchedEpisodes(userId, seriesId)

                _watchedState.value = _watchedState.value.copy(
                    list = response,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _watchedState.value = _watchedState.value.copy(
                    loading = false,
                    error = "Error fetching watched episodes ${e.message}"
                )
            }
        }
    }

    fun addWatchedEpisode(userId: Long, seriesId: Long, season: Long, episode: Long){
        viewModelScope.launch{
            try{
                val body = mapOf(
                    "userId" to userId,
                    "seriesId" to seriesId,
                    "season" to season,
                    "episode" to episode
                )
                userService.addWatchedEpisode(body)

                _addWatchedState.value = _addWatchedState.value.copy(
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _addWatchedState.value = _addWatchedState.value.copy(
                    loading = false,
                    error = "Error adding episode ${e.message}"
                )
            }
        }
    }

    fun removeWatchedEpisode(userId: Long, seriesId: Long, season: Long, episode: Long){
        viewModelScope.launch{
            try{
                userService.removeWatchedEpisode(userId, seriesId, season, episode)

                _removeWatchedState.value = _removeWatchedState.value.copy(
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _removeWatchedState.value = _removeWatchedState.value.copy(
                    loading = false,
                    error = "Error removing episode ${e.message}"
                )
            }
        }
    }

    /*fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
    }*/

    data class UserReplyState(
        val loading: Boolean = true,
        val obj: UserAuthReply = UserAuthReply(-1, "", "", "", "", "", ""),
        var error: String? = null
    )

    data class ProfilePictureState(
        val loading: Boolean = true,
        val url: String? = null,
        val error: String? = null
    )

    data class FavoriteReplyState(
        var loading: Boolean = true,
        val list: List<Details> = emptyList(),
        val error: String? = null
    )

    data class ModifyFavoriteState(
        val loading: Boolean = true,
        val error: String? = null
    )

    data class WatchedReplyState(
        val loading: Boolean = true,
        val list: List<EpisodeReply> = emptyList(),
        val error: String? = null
    )

    data class ModifyWatchedState(
        val loading: Boolean = true,
        val error: String? = null
    )
}