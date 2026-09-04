package com.mineinspect.app.data.remote

import com.mineinspect.app.data.remote.dto.MineDto
import retrofit2.http.GET
import retrofit2.http.Query

/** plan §12-13, endpoint #3. */
interface MineApi {
    @GET("mines")
    suspend fun getMines(@Query("assignedTo") inspectorId: String): List<MineDto>
}
