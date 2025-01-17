package com.example.app3

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _seriesState = mutableStateOf(ReplyState())
    val seriesState: State<ReplyState> = _seriesState

    private val _detailState = mutableStateOf(DetailState())
    val detailState: State<DetailState> = _detailState

    private val _searchState = mutableStateOf(ReplyState())
    val searchState: State<ReplyState> = _searchState

    init {
        fetchMostPopular()
    }

    private fun fetchMostPopular(){
        viewModelScope.launch{
            try{
                val response = userService.getMostPopular()
                _seriesState.value = _seriesState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _seriesState.value = _seriesState.value.copy(
                    loading = false,
                    error = "Error fetching most popular series ${e.message}"
                )
            }
        }
    }

    fun fetchDetailPage(id: String) {
        viewModelScope.launch {
            try {
                val resp = userService.getDetailPage(id.toLong())
                _detailState.value = _detailState.value.copy(
                    obj = resp,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    loading = false,
                    error = "Error fetching series details ${e.message}"
                )
            }
        }
    }


    fun fetchSearch(query: String){
        viewModelScope.launch{
            try{
                val response = userService.getSearchPage(query)
                _searchState.value = _searchState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
            }catch (e: Exception){
                _searchState.value = _searchState.value.copy(
                    loading = false,
                    error = "Error fetching searched series ${e.message}"
                )
            }
        }
    }

    data class ReplyState(
        val loading: Boolean = true,
        val obj: List<Details> = emptyList(),
        val error: String? = null
    )

    data class DetailState(
        val loading: Boolean = true,
        val obj: Details = Details("", "", "", "", "", "", "", "", emptyList(), Episode(0,-1,"",""), emptyList()),
        val error: String? = null
    )
}