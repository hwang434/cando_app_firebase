package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.goodee.cando_app.R
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.model.UserRepository
import com.goodee.cando_app.util.Resource
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "로그"
    }
    private var userRepository: UserRepository
    private val _userLiveData: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
    val userLiveData: LiveData<Resource<FirebaseUser>>
        get() = _userLiveData

    private val _isWithdrawSuccess: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isWithdrawSuccess: LiveData<Resource<Boolean>>
        get() = _isWithdrawSuccess

    private val _isRegisterEmailSent: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isRegisterEmailSent: LiveData<Resource<Boolean>>
        get() = _isRegisterEmailSent

    private val _isExistEmail: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isExistEmail: LiveData<Resource<Boolean>>
        get() = _isExistEmail

    private val _listOfUserEmail: MutableLiveData<Resource<List<DocumentSnapshot>>> = MutableLiveData()
    val listOfUserEmail: LiveData<Resource<List<DocumentSnapshot>>>
        get() = _listOfUserEmail

    // Resources data is for Email.
    private val _isExistNameAndEmail: MutableLiveData<Resource<String>> = MutableLiveData()
    val isExistNameAndEmail: LiveData<Resource<String>>
        get() = _isExistNameAndEmail

    private val _isPasswordResetEmailSent: MutableLiveData<Resource<String>> = MutableLiveData()
    val isPasswordResetEmailSent: LiveData<Resource<String>>
        get() = _isPasswordResetEmailSent

    init {
        userRepository = UserRepository(application)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"UserViewModel - onCleared() called")
    }

    // 회원가입
    fun sendRegisterEmail(email: String, userDto: UserDto, password: String) {
        Log.d(TAG,"UserViewModel - sendRegisterEmail() called")
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "sendRegisterEmail: ", error)
            when (error) {
                is FirebaseAuthUserCollisionException -> {
                    _isRegisterEmailSent.postValue(Resource.Error(false, getApplication<Application>().getString(R.string.toast_is_exist_email)))
                }
                is FirebaseAuthWeakPasswordException -> {
                    _isRegisterEmailSent.postValue(Resource.Error(false, getApplication<Application>().getString(R.string.toast_password_too_easy)))
                }
                else -> {
                    _isRegisterEmailSent.postValue(Resource.Error(false, "System has a error"))
                }
            }
        }
        
        _isRegisterEmailSent.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO + handler) {
            if (userRepository.sendRegisterEmail(email, userDto, password)) {
                _isRegisterEmailSent.postValue(Resource.Success(true))
            }
        }
    }

    // 로그인
    fun login(email: String, password: String) {
        Log.d(TAG,"UserViewModel - login() called")
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "login: ", error)
            _userLiveData.postValue(Resource.Error(null, error.message.toString()))
        }
        _userLiveData.postValue(Resource.Loading())

        viewModelScope.launch(Dispatchers.IO + handler) {
            val result = userRepository.login(email, password)
            _userLiveData.postValue(result)
        }
    }

    // 아이디 찾기
    fun findUserEmail(name: String, phone: String) {
        Log.d(TAG,"UserViewModel - findUserId() called")
        _listOfUserEmail.postValue(Resource.Loading())

        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "findUserEmail: ", error)
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            val result = userRepository.findUserEmail(name, phone)
            if (result.isEmpty()) {
                _listOfUserEmail.postValue(Resource.Error(null, "There is no user matched to name and phone."))
            } else if (result.size > 1) {
                _listOfUserEmail.postValue(Resource.Error(result, "There are too many members matched to info."))
            } else {
                _listOfUserEmail.postValue(Resource.Success(result))
            }
        }
    }

    fun isExistNameAndEmail(name: String, email: String) {
        Log.d(TAG,"UserViewModel - isExistNameAndEmail() called")
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
    
    // 중복 회원 찾기
    fun isExistEmail(email: String) {
        Log.d(TAG,"UserViewModel - isExistEmail() called")
        _isExistEmail.postValue(Resource.Loading())
        
        val handler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "isExistEmail: ", error)
        }
        
        viewModelScope.launch(Dispatchers.IO + handler) {    
            if (!userRepository.isExistEmail(email)) {
                _isExistEmail.postValue(Resource.Success(false))
                return@launch
            }

            _isExistEmail.postValue(Resource.Success(true))
        }
    }

    // 회원 삭제
    fun withdrawUser(email: String, password: String) {
        Log.d(TAG,"UserViewModel - withdrawUser() called")
        val withdrawExceptionHandler = CoroutineExceptionHandler { _, error ->
            Log.w(TAG, "withdrawUser: ", error)
            val errorMessage = when (error) {
                is FirebaseAuthInvalidCredentialsException -> {
                    "Password is not matched."
                }
                else -> {
                    "System has a Error."
                }
            }
            _isWithdrawSuccess.postValue(Resource.Error(false, errorMessage))
        }

        viewModelScope.launch(withdrawExceptionHandler) {
            _isWithdrawSuccess.postValue(Resource.Loading())
            if (userRepository.withdrawUser(email, password)) {
                _isWithdrawSuccess.postValue(Resource.Success(true))
            } else {
                _isWithdrawSuccess.postValue(Resource.Error(false, "Fail to Withdraw."))
            }
        }
    }

    // 중복 로그인 처리
    fun autoSignIn() {
        Log.d(TAG,"UserViewModel - autoSignIn() called")
        _userLiveData.postValue(Resource.Success(FirebaseAuth.getInstance().currentUser!!))
        userRepository.autoLogin()
    }

    // 로그 아웃
    fun signOut() {
        Log.d(TAG,"UserViewModel - signOut() called")
        _userLiveData.postValue(null)
        userRepository.signOut()
    }

    fun sendPasswordResetEmail(email: String) {
        Log.d(TAG,"UserViewModel - sendPasswordResetEmail() called")
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
