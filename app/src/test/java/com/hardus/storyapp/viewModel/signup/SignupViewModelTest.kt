package com.hardus.storyapp.viewModel.signup

import com.hardus.storyapp.authentication.AuthRepository
import com.hardus.storyapp.authentication.signup.SignupViewModel
import com.hardus.storyapp.response.SignUpResponse
import com.hardus.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
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
class SignupViewModelTest {

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var signupViewModel: SignupViewModel

    private val dummySignUp = DataDummy.generateDummySignUpResponse()
    private val dummyName = "Name"
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setup() {
        signupViewModel = SignupViewModel(authRepository)
    }

    @Test
    fun `signUp - response success`(): Unit = runTest {
        val expected = flowOf(Result.success(dummySignUp))
        `when`(authRepository.signUpUser(dummyName, dummyEmail, dummyPassword)).thenReturn(expected)

        signupViewModel.signUpUser(dummyName, dummyEmail, dummyPassword).collect { result ->
            assertTrue(result.isSuccess)
            assertFalse(result.isFailure)

            result.onSuccess { actual ->
                assertNotNull(actual)
                assertEquals(dummySignUp, actual)
            }
        }
        verify(authRepository).signUpUser(dummyName, dummyEmail, dummyPassword)
    }


    @Test
    fun `signUp - response failed`(): Unit = runTest {
        val expectedResponse: Flow<Result<SignUpResponse>> =
            flowOf(Result.failure(Exception("failed")))

        `when`(authRepository.signUpUser(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        val response = signupViewModel.signUpUser(dummyName, dummyEmail, dummyPassword).single()

        assertFalse(response.isSuccess)
        assertTrue(response.isFailure)

        response.onFailure { error ->
            assertNotNull(error)
        }
    }

}