package com.uberando.hipasar.ui.main.account.setting

import android.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountSettingBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AccountSettingFragment : BaseFragment<FragmentAccountSettingBinding>() {

  @Inject
  lateinit var viewModel: AccountSettingViewModel

  private val arguments: AccountSettingFragmentArgs by navArgs()

  private var navigationIndicator = 0

  private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
    handleImageResult(result)
  }

  override fun layoutResource(): Int = R.layout.fragment_account_setting

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    viewModel.setUserProperty(arguments.user)
    viewModel.whenNavigationIconClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    })
    viewModel.whenChangePhotoClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        startChooseImage()
      }
    })
    viewModel.whenChangeNameClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToChangeName()
      }
    })
    viewModel.whenChangeEmailClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToChangeEmail()
      }
    })
    viewModel.whenChangePasswordClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        if (viewModel.requirePasswordAvailable()) {
          navigateToChangePassword()
        } else {
          navigateToSetPassword()
        }
      }
    })
    viewModel.whenUploadImageFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        AlertDialog.Builder(requireContext())
          .setMessage(R.string.str_msg_upload_image_failed)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.whenUpdateProfileSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_msg_update_profile_success),
          Snackbar.LENGTH_SHORT
        )
          .show()
      }
    })
  }

  override fun onResume() {
    super.onResume()
    if (navigationIndicator > 0) {
      viewModel.quietlyUpdateProfile()
      navigationIndicator -= 1
    }
  }

  /**
   * Corp image library
   */
  private fun handleImageResult(result: CropImageView.CropResult) {
    if (result.isSuccessful) {
      result.getUriFilePath(requireContext())?.let { path ->
        viewModel.requireToSetNewPhoto(File(path))
      }
    } else {
      AlertDialog.Builder(requireContext())
        .setMessage(R.string.str_msg_take_image_not_successful)
        .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
          dialogInterface.dismiss()
        }
        .create()
        .show()
    }
  }

  private fun startChooseImage() {
    cropImageLauncher.launch(options {
      setGuidelines(CropImageView.Guidelines.ON)
      setFixAspectRatio(false)
      setAllowFlipping(false)
      setAllowRotation(false)
      setCropMenuCropButtonTitle(getString(R.string.str_save))
      setActivityMenuIconColor(R.color.white)
    })
  }

  /**
   * Navigation
   */
  private fun navigateToChangeEmail() {
    navigationIndicator += 1
    AccountSettingFragmentDirections
      .actionAccountSettingFragmentToAccountChangeEmailFragment(arguments.user)
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToChangeName() {
    navigationIndicator += 1
    AccountSettingFragmentDirections
      .actionAccountSettingFragmentToAccountChangeNameFragment(arguments.user)
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToChangePassword() {
    navigationIndicator += 1
    AccountSettingFragmentDirections
      .actionAccountSettingFragmentToAccountChangePasswordFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToSetPassword() {
    navigationIndicator += 1
    AccountSettingFragmentDirections
      .actionAccountSettingFragmentToAccountSetPasswordFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

}