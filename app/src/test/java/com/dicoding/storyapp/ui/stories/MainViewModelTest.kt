package com.dicoding.storyapp.ui.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.response.ListStoryItem
import com.dicoding.storyapp.ui.stories.StoryAdapter.Companion.DIFF_CALLBACK
import com.dicoding.storyapp.utilis.DataDummy
import com.dicoding.storyapp.utilis.MainDispatcherRule
import com.dicoding.storyapp.utilis.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStoriesResponse = DataDummy.generateDummyStories()

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storyRepository)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        `when`(storyRepository.getStory(anyString())).thenReturn(expectedStories)

        val actualStories: PagingData<ListStoryItem> = mainViewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().size)
        assertEquals(dummyStoriesResponse.listStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data = PagingData.empty<ListStoryItem>()
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        `when`(storyRepository.getStory(anyString())).thenReturn(expectedStories)

        val actualStories: PagingData<ListStoryItem> = mainViewModel.getStories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}