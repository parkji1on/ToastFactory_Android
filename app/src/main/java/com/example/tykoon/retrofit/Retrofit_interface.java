package com.example.tykoon.retrofit;

import com.example.tykoon.retrofit.model.BaseResponse;
import com.example.tykoon.retrofit.model.PostMemberReq;
import com.example.tykoon.retrofit.model.UserRank;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Retrofit_interface {
    @GET("rank/my")
    Call<BaseResponse<UserRank>> getUserRank(
            @Query("name") String name);

    @GET("rank/")
    Call<BaseResponse<List<UserRank>>> getRankList(
            @Query("offset") int offset);

    @POST("member/")
    Call<BaseResponse<String>> postMember(
            @Body PostMemberReq postMemberReq);

    @POST("member/login")
    Call<BaseResponse<String>> postMemberLogin(
            @Body PostMemberReq postMemberReq);

    @PATCH("rank/score")
    Call<BaseResponse<String>> patchMemberScore(
            @Query("name") String name,
            @Query("score") Long score);

}
