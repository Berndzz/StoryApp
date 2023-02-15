package com.hardus.storyapp.viewModel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.main.ListStoryAdapter
import com.hardus.storyapp.main.MainViewModel
import com.hardus.storyapp.utils.CoroutinesTest
import com.hardus.storyapp.utils.DataDummy
import com.hardus.storyapp.utils.PagedTestData
import com.hardus.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTest()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mainViewModel: MainViewModel

    private val dummyStories = DataDummy.generateDummyDetailStory()
    private val dummyAuthToken = "authentication_token"

    @Before
    fun setup() {
        mainViewModel = MainViewModel(storyRepository, authRepository)
    }

    @Test
    fun `get Auth Token - response success`() = runTest {
        val expected = flowOf(dummyAuthToken)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        mainViewModel.getAuthToken().collect { actual ->
            assertNotNull(actual)
            assertEquals(dummyAuthToken, actual)
        }

        verify(authRepository).getAuthToken()
        verifyNoInteractions(storyRepository)
    }

    @Test
    fun `get Auth Token - response success but null`() = runTest {
        val expected = flowOf(null)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        mainViewModel.getAuthToken().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getAuthToken()
        verifyNoInteractions(storyRepository)
    }

    @Test
    fun `get All Stories - response success`() = runTest {
        val data = PagedTestData.snapshot(dummyStories)
        val stories = flow { emit(data) }
        `when`(storyRepository.getAllStories(dummyAuthToken)).thenReturn(stories)

        val actual: PagingData<EntityStory> =
            mainViewModel.getAllStories(dummyAuthToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actual)

        verify(storyRepository).getAllStories(dummyAuthToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }


    @Test
    fun `get All Stories - response error`() = runTest {
        val data = PagedTestData.snapshot(dummyStories)
        val expected = flow { emit(data) }
        `when`(storyRepository.getAllStories(dummyAuthToken)).thenReturn(expected)

        val actual = mainViewModel.getAllStories(dummyAuthToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actual)

        verify(storyRepository).getAllStories(dummyAuthToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}

