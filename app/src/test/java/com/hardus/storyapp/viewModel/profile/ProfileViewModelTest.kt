package com.hardus.storyapp.viewModel.profile

import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.profile.ProfileViewModel
import com.hardus.storyapp.utils.CoroutinesTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTest()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var profileViewModel: ProfileViewModel

    private val dummyAuthToken = "authentication_token"
    private val dummyAuthName = "authentication_name"


    @Before
    fun setup() {
        profileViewModel = ProfileViewModel(authRepository)
    }

    @Test
    fun `get Auth Token at Profile - response success`(): Unit = runTest {
        val expected = flowOf(dummyAuthToken)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        profileViewModel.getAuthToken().collect { actual ->
            assertNotNull(actual)
            assertEquals(dummyAuthToken, actual)
        }
        verify(authRepository).getAuthToken()
    }

    @Test
    fun `get Auth Token at Profile - response success but null`(): Unit = runTest {
        val expected = flowOf(null)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        profileViewModel.getAuthToken().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getAuthToken()
    }

    @Test
    fun `get Name User at Profile - response success`(): Unit = runTest {
        val expected = flowOf(dummyAuthName)
        `when`(authRepository.getNameUser()).thenReturn(expected)
        profileViewModel.getNameUser().collect { actual ->
            assertNotNull(actual)
            assertEquals(dummyAuthName, actual)
        }
        verify(authRepository).getNameUser()
    }

    @Test
    fun `get Name User at Profile - response success but null`(): Unit = runTest {
        val expected = flowOf(null)
        `when`(authRepository.getNameUser()).thenReturn(expected)
        profileViewModel.getNameUser().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getNameUser()
    }

    @Test
    fun `save Auth Token at Profile - response success`(): Unit = runTest {
        profileViewModel.saveAuthToken(dummyAuthToken)
        verify(authRepository).saveAuthToken(dummyAuthToken)

    }

    @Test
    fun `save Name User at Profile - response success`(): Unit = runTest {
        profileViewModel.saveNameUser(dummyAuthName)
        verify(authRepository).saveNameUser(dummyAuthName)
    }

}