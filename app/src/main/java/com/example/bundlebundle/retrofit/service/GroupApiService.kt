package com.example.bundlebundle.retrofit.service

import com.example.bundlebundle.retrofit.dataclass.group.GroupIdVO
import com.example.bundlebundle.retrofit.dataclass.group.GroupMemberVO
import com.example.bundlebundle.retrofit.dataclass.group.GroupNicknameVO
import com.example.bundlebundle.retrofit.dataclass.group.GroupVO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GroupApiService {

    @GET("member/group-check")
    fun checkIfGroupIsPresent(): Call<GroupIdVO>

    @POST("group")
    fun createGroup(
        @Body groupNicknameVO: GroupNicknameVO
    ): Call<GroupVO>

    @POST("member/group-join")
    fun joinGroup(
        @Body groupMemberVO: GroupMemberVO
    ): Call<GroupMemberVO>

}