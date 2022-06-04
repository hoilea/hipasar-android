package com.uberando.hipasar.ui.address.modify.select

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAddressSelectLocationBinding
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.Kelurahan
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.address.select.AddressSelectLocationAdapter
import com.uberando.hipasar.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddressSelectLocationFragment : BaseFragment<FragmentAddressSelectLocationBinding>() {

  @Inject lateinit var viewModel: AddressSelectLocationViewModel

  private val arguments: AddressSelectLocationFragmentArgs by navArgs()

  private val kecamatanAdapter by lazy {
    AddressSelectLocationAdapter<Kecamatan>(ClickListener {
      setupSavedState(Constants.KEY_GET_LOCATION, listOf(it.id.toString(), it.name))
      findNavController().navigateUp()
    })
  }

  private val kelurahanAdapter by lazy {
    AddressSelectLocationAdapter<Kelurahan>(ClickListener {
      setupSavedState(Constants.KEY_GET_LOCATION, listOf(it.id.toString(), it.name))
      findNavController().navigateUp()
    })
  }

  override fun layoutResource() = R.layout.fragment_address_select_location

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    determineToolbarTitleAndAdapter()
  }

  override fun interactWithViewModel() {

    viewModel.setupDataSelection(arguments.kecamatanId)
    viewModel.kecamatanList.observe(viewLifecycleOwner) { kecamatanList ->
      kecamatanAdapter.submitListData(kecamatanList)
    }
    viewModel.kelurahanList.observe(viewLifecycleOwner) { kelurahanList ->
      kelurahanAdapter.submitListData(kelurahanList)
    }
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
  }

  private fun determineToolbarTitleAndAdapter() {
    arguments.kecamatanId.let { kecamatanId ->
      if (kecamatanId != -1L) {
        viewModel.setupToolbarTitle(getString(R.string.str_select_kelurahan))
        setupRecyclerView(kelurahanAdapter)
      } else {
        viewModel.setupToolbarTitle(getString(R.string.str_select_kecamatan))
        setupRecyclerView(kecamatanAdapter)
      }
    }
  }

  private fun <T> setupRecyclerView(adapter: AddressSelectLocationAdapter<T>) {
    binding?.locationList?.adapter = adapter
  }

}