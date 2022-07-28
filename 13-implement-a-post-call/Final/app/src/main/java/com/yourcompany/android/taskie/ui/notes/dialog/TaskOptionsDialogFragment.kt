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

package com.yourcompany.android.taskie.ui.notes.dialog

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.yourcompany.android.taskie.App
import com.yourcompany.android.taskie.R
import com.yourcompany.android.taskie.databinding.FragmentDialogTaskOptionsBinding
import com.yourcompany.android.taskie.networking.NetworkStatusChecker

/**
 * Displays the options to delete or complete a task.
 */
class TaskOptionsDialogFragment : DialogFragment() {

  private var taskOptionSelectedListener: TaskOptionSelectedListener? = null
  private val networkStatusChecker by lazy {
    NetworkStatusChecker(activity?.getSystemService(ConnectivityManager::class.java))
  }

  private val remoteApi = App.remoteApi

  // This property is only valid between onCreateView and
  // onDestroyView and it is used to inflate and destroy the view
  private var _binding: FragmentDialogTaskOptionsBinding? = null

  // Used throughout the class
  private val binding get() = _binding!!

  companion object {

    private const val KEY_TASK_ID = "task_id"

    fun newInstance(taskId: String): TaskOptionsDialogFragment = TaskOptionsDialogFragment().apply {
      arguments = Bundle().apply {
        putString(KEY_TASK_ID, taskId)
      }
    }
  }

  interface TaskOptionSelectedListener {

    fun onTaskDeleted(taskId: String)

    fun onTaskCompleted(taskId: String)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FragmentDialogTheme)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    _binding = FragmentDialogTaskOptionsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUi()
  }

  private fun initUi() {
    val taskId = arguments?.getString(KEY_TASK_ID) ?: ""
    if (taskId.isEmpty()) dismissAllowingStateLoss()

    binding.deleteTask.setOnClickListener {
      remoteApi.deleteTask { error ->
        if (error == null) {
          taskOptionSelectedListener?.onTaskDeleted(taskId)
        }
        dismissAllowingStateLoss()
      }
    }

    binding.completeTask.setOnClickListener {
      networkStatusChecker.performIfConnectedToInternet {
        remoteApi.completeTask(taskId) { error ->
          activity?.runOnUiThread {
            if (error == null) {
              taskOptionSelectedListener?.onTaskCompleted(taskId)
            }
            dismissAllowingStateLoss()
          }
        }
      }
    }
  }

  fun setTaskOptionSelectedListener(taskOptionSelectedListener: TaskOptionSelectedListener) {
    this.taskOptionSelectedListener = taskOptionSelectedListener
  }
}