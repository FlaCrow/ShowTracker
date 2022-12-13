package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.flacrow.showtracker.presentation.viewModels.ViewModelFactory
import javax.inject.Inject

abstract class BaseFragment<VBinding : ViewBinding, VModel : ViewModel>(
    private val bindingInflater: (inflater: LayoutInflater) -> VBinding
) : Fragment() {

    protected abstract val viewModel: VModel
    protected abstract fun setupDependencies()
    private var _binding: VBinding? = null
    protected val binding: VBinding
        get() = _binding!!

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory


    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupDependencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        _binding ?: throw IllegalArgumentException("Binding is null")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
    }

}