package com.example.komyuniti.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedViewModel : ViewModel() {

    val feedList = MutableLiveData<Array<FeedData>>().apply {
        value = emptyArray()
    }

    fun getFeedFromDatabase() {
        var newFeedList = arrayOf(FeedData())
        feedList.value = newFeedList

    }

}