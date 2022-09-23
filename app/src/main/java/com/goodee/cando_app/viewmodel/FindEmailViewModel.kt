package com.goodee.cando_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goodee.cando_app.model.UserRepository
import com.goodee.cando_app.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindEmailViewModel: ViewModel() {
    companion object {
        private const val TAG: String = "로그"
    }

    private val _userName: MutableLiveData<String> = MutableLiveData()
    val userName: LiveData<String>
        get() = _userName

    private val _userPhoneNumber: MutableLiveData<String> = MutableLiveData()
    val userPhoneNumber: LiveData<String>
        get() = _userPhoneNumber

    private val _listOfUserEmail: MutableLiveData<Resource<List<DocumentSnapshot>>> = MutableLiveData()
    val listOfUserEmail: LiveData<Resource<List<DocumentSnapshot>>>
        get() = _listOfUserEmail

    fun setName(name: String) {
        Log.d(TAG,"FindEmailViewModel - setName() called")
        _userName.postValue(name)
    }

    fun setPhone(phone: String) {
        Log.d(TAG,"FindEmailViewModel - setPhone() called")
        _userPhoneNumber.postValue(phone)
    }

    // 아이디 찾기
    fun findUserEmail(name: String, phone: String) {
        Log.d(TAG,"FindEmailViewModel - findUserEmail() called")
        _listOfUserEmail.postValue(Resource.Loading())

        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "findUserEmail: ", error)
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            val result = UserRepository.findUserEmail(name, phone)
            if (result.isEmpty()) {
                _listOfUserEmail.postValue(Resource.Error(null, "There is no user matched to name and phone."))
            } else if (result.size > 1) {
                _listOfUserEmail.postValue(Resource.Error(result, "There are too many members matched to info."))
            } else {
                _listOfUserEmail.postValue(Resource.Success(result))
            }
        }
    }
}