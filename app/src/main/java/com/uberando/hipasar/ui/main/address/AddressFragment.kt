package com.uberando.hipasar.ui.main.address

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAddressBinding
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.address.AddressAdapter
import com.uberando.hipasar.ui.adapter.address.AddressListener
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.uberando.hipasar.ui.address.AddressActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AddressFragment : BaseFragment<FragmentAddressBinding>() {

  @Inject lateinit var viewModel: AddressViewModel

  private val addressAdapter: AddressAdapter by lazy {
    AddressAdapter(
      AddressListener(
        onAddressClicked = {
          Timber.i("onAddressClicked: $it")
          navigateToModifyAddress(it)
        },
        onAddressSetPrimary = {
          Timber.i("onAddressSetPrimary: $it")
          actionAfterConfirmation(getString(R.string.str_question_set_primary_address)) {
            viewModel.requireToSetPrimaryAddress(it)
          }
        },
        onAddressRemoved = {
          Timber.i("onAddressRemoved: $it")
          actionAfterConfirmation(getString(R.string.str_question_remove_address)) {
            viewModel.requireToRemoveAddress(it.id)
          }
        }
      )
    )
  }

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }

  override fun layoutResource(): Int = R.layout.fragment_address

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.addressTypes.observe(viewLifecycleOwner) { addresses ->
      addressAdapter.submitList(addresses)
    }
    viewModel.whenCreateButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToModifyAddress()
      }
    })
    viewModel.updateAddressSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        showMessageSnackbar(getString(R.string.str_msg_update_primary_address_successful))
      } else {
        showMessageSnackbar(getString(R.string.str_msg_update_primary_address_failed))
      }
    })
    viewModel.removeAddressSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        showMessageSnackbar(getString(R.string.str_msg_remove_address_successful))
      } else {
        showMessageSnackbar(getString(R.string.str_msg_remove_address_failed))
      }
    })
  }

  private fun handleActivityResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.ADDRESS_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, Constants.FAILED)) {
            Constants.SUCCESS -> {
              viewModel.requireToRefreshAddress()
            }
            Constants.FAILED -> {
              TwsSnackbar(
                binding!!.root,
                getString(R.string.str_msg_modify_address_failed),
                Snackbar.LENGTH_SHORT
              ).show()
            }
            Constants.CANCELED -> Unit
            else -> Unit
          }
        }
      }
    }
  }

  private fun setupRecyclerView() {
    binding?.addressRecyclerView?.adapter = addressAdapter
  }
  
  private fun showMessageSnackbar(message: String) {
    TwsSnackbar(binding!!.root, message, Snackbar.LENGTH_SHORT).show()
  }
  
  private fun actionAfterConfirmation(message: String, action: () -> Unit) {
    AlertDialog.Builder(requireContext())
      .setMessage(message)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        action()
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }

  private fun navigateToModifyAddress(address: Address? = null) {
    Intent(requireActivity(), AddressActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_ADDRESS, Bundle().apply { putParcelable(Constants.INTENT_EXTRA_ADDRESS, address) })
      activityResultLauncher.launch(this)
    }
  }

}

