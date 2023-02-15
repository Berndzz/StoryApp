package com.hardus.storyapp.viewModel.addstory

import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.addstory.AddStoryViewModel
import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.database.StoryRepository
import com.hardus.storyapp.response.FileUploadResponse
import com.hardus.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val dummyAuthToken = "authentication_token"
    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        addStoryViewModel = AddStoryViewModel(authRepository, storyRepository)
    }

    @Test
    fun `get Auth Token - response success`() = runTest {
        val expected = flowOf(dummyAuthToken)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        addStoryViewModel.getAuthToken().collect { actual ->
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
        addStoryViewModel.getAuthToken().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getAuthToken()
        verifyNoInteractions(storyRepository)
    }

    @Test
    fun `upload story - response success`() = runTest {
        val expected = flowOf(Result.success(dummyUploadResponse))
        `when`(
            storyRepository.uploadImage(
                dummyAuthToken, dummyMultipart, dummyDescription, null, null
            )
        ).thenReturn(expected)

        addStoryViewModel.uploadImage(dummyAuthToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                assertTrue(result.isSuccess)
                assertFalse(result.isFailure)

                result.onSuccess { actual ->
                    assertNotNull(actual)
                    assertEquals(dummyUploadResponse, actual)
                }
            }
        verify(storyRepository).uploadImage(
            dummyAuthToken,
            dummyMultipart,
            dummyDescription,
            null,
            null
        )
        verifyNoInteractions(authRepository)
    }

    @Test
    fun `upload story - response failed`() = runTest {
        val expected: Flow<Result<FileUploadResponse>> = flowOf(Result.failure(Exception("failed")))
        `when`(
            storyRepository.uploadImage(
                dummyAuthToken,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expected)

        addStoryViewModel.uploadImage(dummyAuthToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                assertFalse(result.isSuccess)
                assertTrue(result.isFailure)

                result.onFailure { actual ->
                    assertNotNull(actual)
                }
            }

        verify(storyRepository).uploadImage(
            dummyAuthToken,
            dummyMultipart,
            dummyDescription,
            null,
            null
        )
        verifyNoInteractions(authRepository)
    }

}