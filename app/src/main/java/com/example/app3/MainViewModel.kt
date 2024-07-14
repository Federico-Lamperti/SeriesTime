package com.example.app3

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _serieState = mutableStateOf(ReplyState())
    val seriesState: State<ReplyState> = _serieState

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
                val response = seriesService.getMostPopular()
                _serieState.value = _serieState.value.copy(
                    obj = response,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _serieState.value = _serieState.value.copy(
                    loading = false,
                    error = "Error fetching most popular series ${e.message}"
                )
            }
        }
    }

    fun fetchDetailPage(id: String) {
        viewModelScope.launch {
            try {
                val resp = seriesService.getDetailPage(id.toLong())
                _detailState.value = _detailState.value.copy(
                    obj = resp.tvShow,
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
                val response = seriesService.getSearchPage(query)
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
        val obj: Reply = Reply("", -1, -1, emptyList()),
        val error: String? = null
    )

    data class DetailState(
        val loading: Boolean = true,
        val obj: Details = Details("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", Episode(0,0,"",""), emptyList(), emptyList(), emptyList()),
        val error: String? = null
    )
}