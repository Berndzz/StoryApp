package com.hardus.storyapp.viewModel.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.location.MapsViewModel
import com.hardus.storyapp.response.StoryResponse
import com.hardus.storyapp.utils.CoroutinesTest
import com.hardus.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTest()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel

    private var dummyStoriesResponse = DataDummy.generateDummyStoryResponse()
    private val dummyAuthToken = "authentication_token"

    @Before
    fun setup() {
        mapsViewModel = MapsViewModel(authRepository, storyRepository)
    }

    @Test
    fun `get Auth Token at location response success`() = runTest {
        val expected = flowOf(dummyAuthToken)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        mapsViewModel.getAuthToken().collect { actual ->
            assertNotNull(actual)
            assertEquals(dummyAuthToken, actual)
        }
        verify(authRepository).getAuthToken()
        verifyNoInteractions(storyRepository)
    }

    @Test
    fun `get Auth Token at location - response success but null`() = runTest {
        val expected = flowOf(null)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        mapsViewModel.getAuthToken().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getAuthToken()
        verifyNoInteractions(storyRepository)
    }


    @Test
    fun `get AllStories with location - response success`() = runTest {
        val expected = flowOf(Result.success(dummyStoriesResponse))
        `when`(storyRepository.getAllStoriesWithLocation(dummyAuthToken)).thenReturn(expected)
        mapsViewModel.getAllStories(dummyAuthToken).collect { result ->
            assertTrue(result.isSuccess)
            assertFalse(result.isFailure)

            result.onSuccess { actual ->
                assertNotNull(actual)
                assertEquals(dummyStoriesResponse, actual)
            }
        }
        verify(storyRepository).getAllStoriesWithLocation(dummyAuthToken)
        verifyNoInteractions(authRepository)
    }

    @Test
    fun `get AllStories with location - response failed`() = runTest {
        val expected: Flow<Result<StoryResponse>> = flowOf(Result.failure(Exception("Failed")))
        `when`(storyRepository.getAllStoriesWithLocation(dummyAuthToken)).thenReturn(expected)
        mapsViewModel.getAllStories(dummyAuthToken).collect { result ->
            assertFalse(result.isSuccess)
            assertTrue(result.isFailure)

            result.onFailure { actual ->
                assertNotNull(actual)
            }
        }
        verify(storyRepository).getAllStoriesWithLocation(dummyAuthToken)
        verifyNoInteractions(authRepository)
    }
}



