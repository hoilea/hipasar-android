package com.uberando.hipasar.ui.main.search

import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentSearchQueryBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchQueryFragment : BaseFragment<FragmentSearchQueryBinding>() {

  @Inject lateinit var viewModel: SearchQueryViewModel

  override fun layoutResource() = R.layout.fragment_search_query

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    binding?.searchQueryEditText?.let { editText ->
      editText.requestFocus()
      showKeyboard(editText)
    }
    setupSearchQueryImeAction()
  }

  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenImeActionClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        navigateToSearchResult(viewModel.requireSearchQuery())
      }
    }
    viewModel.showQueryEmptyMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_query_empty)
          .setMessage(getString(R.string.str_msg_query_empty))
          .setPositiveButton(R.string.str_ok) { dialog, _ ->
            dialog.dismiss()
          }
          .create()
          .show()
      }
    })
  }

  private fun setupSearchQueryImeAction() {
    binding?.searchQueryEditText?.setOnEditorActionListener { _, i, _ ->
      if (i == EditorInfo.IME_ACTION_SEARCH) {
        viewModel.onImeActionClick()
      }
      true
    }
  }

  private fun navigateToSearchResult(query: String) {
    SearchQueryFragmentDirections
      .actionSearchQueryFragmentToSearchResultFragment(query)
      .apply { findNavController().navigate(this) }
  }

}