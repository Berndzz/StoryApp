package com.hardus.storyapp.viewModel.splash

import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.splash.SplashViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {


    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var splashViewModel: SplashViewModel

    private val dummyAuthToken = "authentication_token"

    @Before
    fun setup() {
        splashViewModel = SplashViewModel(authRepository)
    }

    @Test
    fun `get Auth Token at Splash - response success`(): Unit = runTest {
        val expected = flowOf(dummyAuthToken)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        splashViewModel.getAuthToken().collect { actual ->
            assertNotNull(actual)
            assertEquals(dummyAuthToken, actual)
        }
        verify(authRepository).getAuthToken()
    }

    @Test
    fun `get Auth Token at Splash - response success but null`(): Unit = runTest {
        val expected = flowOf(null)
        `when`(authRepository.getAuthToken()).thenReturn(expected)
        splashViewModel.getAuthToken().collect { actual ->
            assertNull(actual)
        }

        verify(authRepository).getAuthToken()
    }
}