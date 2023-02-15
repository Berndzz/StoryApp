package com.hardus.storyapp.utils

import com.hardus.storyapp.database.entity.EntityStory
import com.hardus.storyapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {


    fun generateDummySignUpResponse(): SignUpResponse {
        return SignUpResponse(
            error = false, message = "User Created"
        )
    }

    fun generateDummyFileUploadResponse(): FileUploadResponse {
        return FileUploadResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Arif Faizin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )

        return LoginResponse(
            loginResult = loginResult, error = false, message = "success"
        )
    }

    fun generateDummyStoryResponse(): StoryResponse {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<ListStory>()

        for (i in 0 until 10) {
            val story = ListStory(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Dimas",
                description = "Lorem Ipsum",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                lat = 10.212,
                lon = -16.002
            )
            listStory.add(story)
        }
        return StoryResponse(error, listStory, message)
    }

    fun generateDummyDetailStory(): List<EntityStory> {
        val data = arrayListOf<EntityStory>()
        for (i in 0 until 10) {
            val story = EntityStory(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Dimas",
                description = "Lorem Ipsum",
                createdAt = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                photoUrl = "2022-01-08T06:34:18.598Z",
                lon = -10.212,
                lat = -16.002
            )
            data.add(story)
        }
        return data
    }

}