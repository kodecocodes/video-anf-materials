/*
 * Copyright (c) 2022 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * These materials have been reviewed and are updated as of 7/2022.
 */

package com.yourcompany.android.taskie.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.android.taskie.databinding.ActivityRegisterBinding
import com.yourcompany.android.taskie.model.request.UserDataRequest
import com.yourcompany.android.taskie.networking.RemoteApi
import com.yourcompany.android.taskie.utils.gone
import com.yourcompany.android.taskie.utils.toast
import com.yourcompany.android.taskie.utils.visible

/**
 * Displays the Register screen, with the options to register, or head over to Login!
 */
class RegisterActivity : AppCompatActivity() {

  private val remoteApi = RemoteApi()

  private lateinit var binding: ActivityRegisterBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRegisterBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
    initUi()
  }

  private fun initUi() {
    binding.register.setOnClickListener {
      processData(binding.nameInput.text.toString(), binding.emailInput.text.toString(),
        binding.passwordInput.text.toString())
    }
  }

  private fun processData(username: String, email: String, password: String) {
    if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
      remoteApi.registerUser(UserDataRequest(email, password, username)) { message, error ->
        if (message != null) {
          toast(message)
          onRegisterSuccess()
        } else if (error != null) {
          onRegisterError()
        }
      }
    } else {
      onRegisterError()
    }
  }

  private fun onRegisterSuccess() {
    binding.errorText.gone()
    finish()
  }

  private fun onRegisterError() {
    binding.errorText.visible()
  }
}