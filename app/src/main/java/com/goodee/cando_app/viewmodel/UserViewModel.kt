package com.goodee.cando_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.goodee.cando_app.R
import com.goodee.cando_app.dto.UserDto
import com.goodee.cando_app.model.UserRepository
import com.goodee.cando_app.util.Resource
import com.google.firebase.auth.*
import com.google.firebase.firestore.QuerySnapshot
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
    suspend fun findUserEmail(name: String, phone: String): QuerySnapshot {
        Log.d(TAG,"UserViewModel - findUserId() called")
        return userRepository.findUserEmail(name, phone)
    }
    
    // 비밀번호 찾기
    suspend fun isExistNameAndEmail(name: String, email: String): Boolean {
        Log.d(TAG,"UserViewModel - isExistNameAndEmail() called")
        return userRepository.isExistNameAndEmail(name, email)
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

    suspend fun sendPasswordResetEmail(email: String) : Boolean {
        Log.d(TAG,"UserViewModel - sendPasswordResetEmail() called")
        return userRepository.sendPasswordResetEmail(email)
    }
}
