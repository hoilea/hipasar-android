package com.uberando.hipasar.ui.order.address

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentOrderChangeSelectAddressBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.address.selection.AddressSelectionAdapter
import com.uberando.hipasar.ui.address.AddressActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderChangeSelectAddressFragment : BaseFragment<FragmentOrderChangeSelectAddressBinding>() {
  
  @Inject lateinit var viewModel: OrderChangeSelectAddressViewModel
  
  private lateinit var savedStateHandle: SavedStateHandle
  
  private val arguments: OrderChangeSelectAddressFragmentArgs by navArgs()

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }
  
  private val addressSelectionAdapter by lazy {
    AddressSelectionAdapter(ClickListener {
      savedStateHandle.set(Constants.KEY_GET_ADDRESS, it.id)
      findNavController().navigateUp()
    })
  }
  
  
  override fun layoutResource() = R.layout.fragment_order_change_select_address
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerViewAdapter()
  }
  
  override fun interactWithViewModel() {
    savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
    addressSelectionAdapter.submitList(arguments.addresses.toList())
    viewModel.setupToolbarTitle(arguments.title)
    viewModel.setupAddressAvailable(arguments.addresses.isNotEmpty())
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenCreateAddressClick.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
         navigateToCreateAddress()
       }
    })
  }

  private fun handleActivityResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.ADDRESS_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, Constants.FAILED)) {
            Constants.SUCCESS -> {
              setupSavedState(Constants.KEY_CREATE_ADDRESS, true)
              findNavController().navigateUp()
            }
            Constants.FAILED -> {
              setupSavedState(Constants.KEY_CREATE_ADDRESS, true)
              findNavController().navigateUp()
            }
            Constants.CANCELED -> Unit
            else -> Unit
          }
        }
      }
    }
  }
  
  private fun setupRecyclerViewAdapter() {
    binding?.addressesRecyclerView?.adapter = addressSelectionAdapter
  }
  
  private fun navigateToCreateAddress() {
    Intent(requireActivity(), AddressActivity::class.java).apply {
      activityResultLauncher.launch(this)
    }
  }
  
}