package com.example.calculator

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.calculator.databinding.ActivityRegisterBinding
import com.example.calculator.ui.login.LoggedInUserView
import com.example.calculator.ui.login.LoginActivity
import com.example.calculator.ui.login.LoginViewModel
import com.example.calculator.ui.login.LoginViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class Register : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        val username = binding.username
        val password = binding.password
        val confirmPassword: EditText = binding.confirmPassword
        val registerButton = binding.registerButton
        val loading = binding.loading

        loginViewModel.loginFormState.observe(this@Register, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            registerButton.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@Register, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                            )
                }
                false
            }

        }

        confirmPassword.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString()
            )
//            if (confirmPassword.text.toString() != password.text.toString()){
//                confirmPassword.setError("password and confirm password should be same", null)
//            }
//            if(confirmPassword.text.toString() == password.text.toString()){
//                registerButton.isEnabled=true
//            }

        }
        registerButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        val intent = Intent(this@Register, LoginActivity::class.java).apply {
                            putExtra("Username", username.text.toString())
                        }
                        Toast.makeText(
                            this@Register, "Registration successful",
                            Toast.LENGTH_LONG
                        ).show()
                        loading.visibility = View.GONE
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@Register, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    println("called afterTextChanged")
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}


//        confirmPassword.addTextChangedListener(object : TextWatcher {
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//            override fun beforeTextChanged(
//                s: CharSequence, start: Int, count: Int,
//                after: Int
//            ) {
//                // TODO Auto-generated method stub
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                if (s.toString().length > 6) {
//                    confirmPassword.setError("", null)
//                } else {
//                    confirmPassword.setError("", null)
//                }
//            }
//        })