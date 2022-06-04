package com.uberando.hipasar.ui.address.modify

import android.content.Intent
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAddressModifyBinding
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressModifyFragment : BaseFragment<FragmentAddressModifyBinding>() {
  
  @Inject lateinit var viewModel: AddressModifyViewModel

  private val arguments: AddressModifyFragmentArgs by navArgs()

  override fun layoutResource() = R.layout.fragment_address_modify
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }
  
  override fun interactWithViewModel() {
    /**
     * See [AddressSelectLocationFragment] for details
     */
    receiveSavedState<List<String>>(Constants.KEY_GET_LOCATION) {
      if (viewModel.requireLocationType() == 0) {
        viewModel.setupSelectedKecamatan(it[0].toLong(), it[1])
      } else if (viewModel.requireLocationType() == 1) {
        viewModel.setupSelectedKelurahan(it[1])
      }
    }
    requireActivity().intent.getBundleExtra(Constants.INTENT_EXTRA_ADDRESS).let { bundle ->
      val address = bundle?.getParcelable<Address>(Constants.INTENT_EXTRA_ADDRESS)
      if (address != null) {
        viewModel.setupAddressData(address)
        viewModel.setupVisitingContext(Constants.VISITING_CONTEXT_MODIFY)
        viewModel.setupButtonText(getString(R.string.str_update))
        viewModel.setupToolbarTitle(getString(R.string.str_update_address))
      } else {
        viewModel.setupButtonText(getString(R.string.str_create))
        viewModel.setupToolbarTitle(getString(R.string.str_create_address))
      }
    }
    viewModel.showLoadingDialog.observe(viewLifecycleOwner) { show ->
      if (show) {
        loadingDialog.show()
      } else {
        loadingDialog.dismiss()
      }
    }
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        requireActivity().finish()
      }
    }
    viewModel.createAddressSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        setupResult(Constants.SUCCESS)
      } else {
        setupResult(Constants.FAILED)
      }
      requireActivity().finish()
    })
    viewModel.updateAddressSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        setupResult(Constants.SUCCESS)
      } else {
        setupResult(Constants.FAILED)
      }
      requireActivity().finish()
    })
    viewModel.whenGetKecamatanClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSelectLocation()
      }
    })
    viewModel.whenGetKelurahanClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSelectLocation(viewModel.requireKecamatanId())
      }
    })
  }

  private fun setupResult(result: Int) {
    requireActivity().setResult(
      Constants.ADDRESS_REQUEST_CODE,
      Intent().apply {
        putExtra(Constants.INTENT_EXTRA_RESULT_STATE, result)
      }
    )
  }

  private fun navigateToSelectLocation(kecamatanId: Long = -1) {
    AddressModifyFragmentDirections
      .actionAddressModifyFragmentToAddressSelectLocationFragment(kecamatanId)
      .apply { findNavController().navigate(this) }
  }
  
}