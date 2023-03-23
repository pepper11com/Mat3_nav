package com.example.mat3_nav.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mat3_nav.model.Profile
import com.example.mat3_nav.util.PasswordUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class ProfileRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var profilesCollection = firestore.collection("profiles")
    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    val profile: LiveData<Profile>
        get() = _profile

    private val _createSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val createSuccess: LiveData<Boolean>
        get() = _createSuccess
    private val _authenticationResult: MutableLiveData<Boolean> = MutableLiveData()
    val authenticationResult: LiveData<Boolean>
        get() = _authenticationResult

    private val _allProfiles: MutableLiveData<List<Profile>> = MutableLiveData()
    val allProfiles: LiveData<List<Profile>>
        get() = _allProfiles


    suspend fun getAllProfiles() {
        try {
            withTimeout(5_000) {
                val data = profilesCollection
                    .get()
                    .await()

                val profiles = data.documents.map { document ->
                    val username = document.getString("username").toString()
                    val password = document.getString("password").toString()
                    val firstName = document.getString("firstName").toString()
                    val lastName = document.getString("lastName").toString()
                    val description = document.getString("description").toString()
                    val imageUri = document.getString("imageUri").toString()

                    Profile(username, password, firstName, lastName, description, imageUri)
                }
                _allProfiles.value = profiles
            }
        } catch (e: Exception) {
            throw ProfileRetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }


    suspend fun getProfile(userId: String) {
        try {
            withTimeout(5_000) {
                val data = profilesCollection
                    .document(userId)
                    .get()
                    .await()

                val username = data.getString("username").toString()
                val password = data.getString("password").toString()
                val firstName = data.getString("firstName").toString()
                val lastName = data.getString("lastName").toString()
                val description = data.getString("description").toString()
                val imageUri = data.getString("imageUri").toString()

                _profile.value = Profile(username, password, firstName, lastName, description, imageUri)
            }
        } catch (e: Exception) {
            throw ProfileRetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }
    suspend fun createProfile(profile: Profile) {
        // Hash the password before saving
        val hashedPassword = PasswordUtils.hashPassword(profile.password)
        val profileWithHashedPassword = profile.copy(password = hashedPassword)

        // Persist data to Firestore
        try {
            withTimeout(5_000) {
                val newProfileDocument = profilesCollection.document() // Create a new document with an auto-generated ID
                newProfileDocument
                    .set(profileWithHashedPassword)
                    .await()
                _createSuccess.value = true
            }
        } catch (e: Exception) {
            throw ProfileSaveError(e.message.toString(), e)
        }
    }

    suspend fun authenticateUser(username: String, password: String): String? {
        try {
            return withTimeout(5_000) {
                // Retrieve the user document by username
                val userQuerySnapshot = profilesCollection
                    .whereEqualTo("username", username)
                    .get()
                    .await()
                if (userQuerySnapshot.isEmpty) {
                    _authenticationResult.value = false
                    null
                } else {
                    val userDocument = userQuerySnapshot.documents[0]
                    val storedHashedPassword = userDocument.getString("password").toString()

                    // Verify the provided password against the stored hashed password
                    val isPasswordValid = PasswordUtils.verifyPassword(password, storedHashedPassword)

                    _authenticationResult.value = isPasswordValid
                    if (isPasswordValid) {
                        userDocument.id
                    } else {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            throw ProfileAuthenticationError(e.message.toString(), e)
        }
    }


    suspend fun setUserId(userId: String) {
        _profile.value?.userId = userId
        _profile.postValue(_profile.value) // This line will notify the ViewModel that the value has been updated

        // Update the userId field in the Firestore document
        try {
            withTimeout(5_000) {
                profilesCollection
                    .document(userId)
                    .update("userId", userId)
                    .await()
            }
        } catch (e: Exception) {
            throw ProfileUpdateError(e.message.toString(), e)
        }
    }

    suspend fun logout(userId: String) {
        _profile.value?.userId = null
        _profile.postValue(_profile.value)
        println("Logged out!!!!!!!!!!!!!!!")
        try {
            withTimeout(5_000) {
                profilesCollection
                    .document(userId)
                    .update("userId", null)
                    .await()
            }
        } catch (e: Exception) {
            throw ProfileUpdateError(e.message.toString(), e)
        }
    }

    class ProfileUpdateError(message: String, cause: Throwable) : Exception(message, cause)


    fun reset() {
        _createSuccess.value = false
    }
    class ProfileSaveError(message: String, cause: Throwable) : Exception(message, cause)
    class ProfileRetrievalError(message: String) : Exception(message)
    class ProfileAuthenticationError(message: String, cause: Throwable) : Exception(message, cause)
}
