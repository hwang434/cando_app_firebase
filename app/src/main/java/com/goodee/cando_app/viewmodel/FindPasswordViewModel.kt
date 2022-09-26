package com.goodee.cando_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.model.UserRepository
import com.goodee.cando_app.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindPasswordViewModel: ViewModel() {

    companion object {
        private val TAG: String = "로그"
    }

    private val userRepository = UserRepository
    // Resources data is for Email.
    private val _isExistNameAndEmail: MutableLiveData<Resource<String>> = MutableLiveData()
    val isExistNameAndEmail: LiveData<Resource<String>>
        get() = _isExistNameAndEmail

    private val _isPasswordResetEmailSent: MutableLiveData<Resource<String>> = MutableLiveData()
    val isPasswordResetEmailSent: LiveData<Resource<String>>
        get() = _isPasswordResetEmailSent

    fun isExistNameAndEmail(name: String, email: String) {
        Log.d(TAG,"FindPasswordViewModel - isExistNameAndEmail() called")
        _isExistNameAndEmail.postValue(Resource.Loading())
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "isExistNameAndEmail: ", error)
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            if (!userRepository.isExistNameAndEmail(name, email)) {
                _isExistNameAndEmail.postValue(Resource.Error(null, "There is no user matched to name and email."))
                return@launch
            }

            _isExistNameAndEmail.postValue(Resource.Success(email))
        }
    }

    fun sendPasswordResetEmail(email: String) {
        Log.d(TAG,"FindPasswordViewModel - sendPasswordResetEmail() called")
        _isPasswordResetEmailSent.postValue(Resource.Loading())
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "sendPasswordResetEmail: ", error)
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            if (!userRepository.sendPasswordResetEmail(email)) {
                _isPasswordResetEmailSent.postValue(Resource.Error(null, "Fail to send the password reset email."))
                return@launch
            }
            _isPasswordResetEmailSent.postValue(Resource.Success(email))
        }
    }
}