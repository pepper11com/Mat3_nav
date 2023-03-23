package com.example.mat3_nav.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mat3_nav.model.Profile
import com.example.mat3_nav.repository.ProfileRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application)  {

    var scrollPosition = 0
    fun onScrollPositionChanged(position: Int) {
        scrollPosition = position
    }
    private val _selectedUser = mutableStateOf(-1)
    val selectedUser: State<Int> = _selectedUser

    private val _previousSelectedUser = mutableStateOf(-1)
    val previousSelectedUser: State<Int> = _previousSelectedUser

    // Other properties and methods

    fun setSelectedUser(value: Int) {
        _selectedUser.value = value
    }


    private val TAG = "FIRESTORE"
    private val profileRepository: ProfileRepository = ProfileRepository()
    var imageUri by mutableStateOf<String?>(null)
    val bitmap = mutableStateOf<Bitmap?>(null)
    val profile: LiveData<Profile> = profileRepository.profile
    val createSuccess: LiveData<Boolean> = profileRepository.createSuccess

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    private val _authenticationResult: MutableLiveData<Boolean> = MutableLiveData()
    val authenticationResult: LiveData<Boolean>
        get() = _authenticationResult

    private val _userId: MutableLiveData<String?> = MutableLiveData()
    val userId: LiveData<String?>
        get() = _userId

    val allProfiles: LiveData<List<Profile>>
        get() = profileRepository.allProfiles


    init {
        fetchAllProfiles()
    }

    fun fetchAllProfiles(userId: String? = null) {
        viewModelScope.launch {
            try {
                if (userId != null) {
                    profileRepository.getAllProfiles(userId)
                }
            } catch (ex: ProfileRepository.ProfileRetrievalError) {
                val errorMsg = "Something went wrong while retrieving all profiles"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }




    fun authenticate(username: String, password: String, setLoading: (Boolean) -> Unit) {
        setLoading(true)
        viewModelScope.launch {
            val userId = profileRepository.authenticateUser(username, password)

            if (userId != null) {
                _authenticationResult.value = true

                setUserId(userId)
                getProfile(userId)
                setLoading(false)
            } else {
                _authenticationResult.value = false
                setLoading(false)
            }
        }
    }


    fun getProfile(userId: String) {
        viewModelScope.launch {
            try {
                profileRepository.getProfile(userId)
            } catch (ex: ProfileRepository.ProfileRetrievalError) {
                val errorMsg = "Something went wrong while retrieving profile"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun setUserId(userId: String) {
        _userId.value = userId // This line will update the userId LiveData

        viewModelScope.launch {
            try {
                profileRepository.setUserId(userId)
            } catch (ex: ProfileRepository.ProfileUpdateError) {
                val errorMsg = "Something went wrong while updating the userId"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }

    fun createProfile(
        username: String, password: String, firstName: String, lastName: String,
                      description: String, imageUri: String?
    ) {
        // persist data to firestore
        val profile = Profile(username, password, firstName, lastName, description, imageUri)

        viewModelScope.launch {
            try {
                profileRepository.createProfile(profile)
                println("CreateProfile: $profile")
            } catch (ex: ProfileRepository.ProfileSaveError) {
                val errorMsg = "Something went wrong while saving the profile"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
    fun logout(userId1: String) {

        //set the userId to null
        _userId.value = null

        println("USERID:  $userId")

        viewModelScope.launch {
            try {
                profileRepository.logout(userId1)
            } catch (ex: ProfileRepository.ProfileUpdateError) {
                val errorMsg = "Something went wrong while updating the userId"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
    fun reset() {
        _errorText.value = ""
        profileRepository.reset()
    }
}