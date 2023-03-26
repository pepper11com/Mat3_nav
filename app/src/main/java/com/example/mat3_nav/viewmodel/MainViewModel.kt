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
import com.example.mat3_nav.BuildConfig
import com.example.mat3_nav.model.Profile
import com.example.mat3_nav.repository.ProfileRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application)  {

    val apiKey = BuildConfig.API_KEY

    var scrollPosition = 0
    fun onScrollPositionChanged(position: Int) {
        scrollPosition = position
    }
    private val _selectedUser = mutableStateOf(-1)
    val selectedUser: State<Int> = _selectedUser

    private val _previousSelectedUser = mutableStateOf(-1)
    val previousSelectedUser: State<Int> = _previousSelectedUser

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
    fun authenticate(email: String, password: String, setLoading: (Boolean) -> Unit) {
        setLoading(true)
        viewModelScope.launch {
            val userId = profileRepository.authenticateUser(email, password)
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
    fun updateProfile(userId: String, updatedProfile: Profile) {
        viewModelScope.launch {
            try {
                profileRepository.updateProfile(userId, updatedProfile)
            } catch (ex: ProfileRepository.ProfileUpdateError) {
                val errorMsg = "Something went wrong while updating the profile"
                Log.e(TAG, ex.message ?: errorMsg)
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
        _userId.value = userId
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
        email: String, password: String, firstName: String, lastName: String,
                      description: String, imageUri: String?
    ) {
        val profile = Profile(email, password, firstName, lastName, description, imageUri)
        viewModelScope.launch {
            try {
                profileRepository.createProfile(profile)
            } catch (ex: ProfileRepository.ProfileSaveError) {
                val errorMsg = "Something went wrong while saving the profile"
                Log.e(TAG, ex.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
    fun logout(userId1: String) {
        _userId.value = null
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
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            val user = profileRepository.findUserByEmail(email)
            if (user != null) {
                val subject = "Password Reset"
                val body = "Please follow the instructions to reset your password."
               profileRepository.sendEmail(apiKey, user.email, "pepperherman3@gmail.com", subject, body)
            } else {
                _errorText.value = "User not found with the provided email."
            }
        }
    }

    fun reset() {
        _errorText.value = ""
        profileRepository.reset()
    }
}