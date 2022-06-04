package com.uberando.hipasar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

  var binding: B? = null
  
  val loadingDialog by lazy {
    AlertDialog.Builder(requireContext())
      .setView(R.layout.layout_loading_dialog)
      .setCancelable(false)
      .create()
  }

  /**
   * Current backstack entry to get saved state from
   * popped fragment.
   */
  private val currentBackstackEntry by lazy {
    findNavController().currentBackStackEntry!!
  }
  
  private val currentSavedStateHandle by lazy {
    currentBackstackEntry.savedStateHandle
  }
  
  /**
   * Previous backstack entry to set saved state to
   * previous fragment
   */
  private val previousBackstackEntry by lazy {
    findNavController().previousBackStackEntry!!
  }
  
  private val previousSavedStateHandle by lazy {
    previousBackstackEntry.savedStateHandle
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, layoutResource(), container, false)
    binding?.apply {
      this.lifecycleOwner = viewLifecycleOwner
      this.executePendingBindings()
    }
    interactWithLayout()
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    interactWithViewModel()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding?.unbind()
    binding = null
  }

  @LayoutRes
  abstract fun layoutResource(): Int
  abstract fun interactWithLayout()
  abstract fun interactWithViewModel()
  
  fun <T> receiveSavedState(key: String, observe: (result: T) -> Unit) {
    currentSavedStateHandle.getLiveData<T>(key)
      .observe(currentBackstackEntry) { result ->
        observe(result)
      }
  }
  
  fun <T> setupSavedState(key: String, value: T) {
    previousSavedStateHandle.set(key, value)
  }
  
  fun hideKeyboard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    binding?.root.also {
      imm.hideSoftInputFromWindow(it?.windowToken, 0)
    }
  }

  fun showKeyboard(view: View) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
  }

  fun handleBackButtonClick(action: () -> Unit) {
    requireActivity()
      .onBackPressedDispatcher
      .addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() { action() }
        }
      )
  }

}