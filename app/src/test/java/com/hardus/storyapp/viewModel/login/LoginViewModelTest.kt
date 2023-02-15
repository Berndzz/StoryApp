package com.hardus.storyapp.viewModel.login

import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.authentication.login.LoginViewModel
import com.hardus.storyapp.response.LoginResponse
import com.hardus.storyapp.utils.CoroutinesTest
import com.hardus.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
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
class LoginViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTest()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel


    private val dummyLogin = DataDummy.generateDummyLoginResponse()
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"
    private val dummyAuthToken = "authentication_token"
    private val dummyAuthName = "authentication_name"


    @Before
    fun setup() {
        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `loginUser - response success`(): Unit = runTest {
        val expected = flowOf(Result.success(dummyLogin))
        `when`(authRepository.loginUser(dummyEmail, dummyPassword)).thenReturn(expected)

        loginViewModel.loginUser(dummyEmail, dummyPassword).collect { result ->
            assertTrue(result.isSuccess)
            assertFalse(result.isFailure)

            result.onSuccess { actual ->
                assertNotNull(actual)
                assertEquals(dummyLogin, actual)
            }
        }
        verify(authRepository).loginUser(dummyEmail, dummyPassword)
    }

    @Test
    fun `loginUser - response failed`(): Unit = runTest {
        val expectedResponse: Flow<Result<LoginResponse>> =
            flowOf(Result.failure(Exception("failed")))

        `when`(authRepository.loginUser(dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        val response = loginViewModel.loginUser(dummyEmail, dummyPassword).single()

        assertFalse(response.isSuccess)
        assertTrue(response.isFailure)

        response.onFailure { error ->
            assertNotNull(error)
        }
    }


    @Test
    fun `save Token - response success`(): Unit = runTest {
        loginViewModel.saveToken(dummyAuthToken)
        verify(authRepository).saveAuthToken(dummyAuthToken)
    }

    @Test
    fun `save Name User - response success`(): Unit = runTest {
        loginViewModel.saveNameUser(dummyAuthName)
        verify(authRepository).saveNameUser(dummyAuthName)
    }
}