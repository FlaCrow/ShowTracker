package com.flacrow.showtracker.presentation.ViewModels

import androidx.lifecycle.ViewModel
import com.flacrow.showtracker.data.repository.Repository
import javax.inject.Inject

class ShowPickedListViewModel @Inject constructor(private var repository: Repository) : ViewModel() {

}
