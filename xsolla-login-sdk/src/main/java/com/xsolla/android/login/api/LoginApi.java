package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.AuthUserSocialBody;
import com.xsolla.android.login.entity.request.OauthAuthUserBody;
import com.xsolla.android.login.entity.request.OauthRegisterUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.AuthSocialResponse;
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse;
import com.xsolla.android.login.entity.response.OauthAuthResponse;
import com.xsolla.android.login.entity.response.OauthLinkForSocialAuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginApi {

    // JWT

    @POST("/api/user")
    Call<Void> registerUser(@Query("projectId") String projectId, @Body RegisterUserBody registerUserBody);

    @POST("/api/login")
    Call<AuthResponse> login(@Query("projectId") String projectId, @Body AuthUserBody authUserBody);

    @POST("/api/social/{providerName}/login_with_token")
    Call<AuthSocialResponse> loginSocial(@Path("providerName") String providerName, @Query("projectId") String projectId, @Body AuthUserSocialBody authUserSocialBody);

    @GET("/api/social/{providerName}/login_url")
    Call<LinkForSocialAuthResponse> getLinkForSocialAuth(@Path("providerName") String providerName, @Query("projectId") String projectId);


    // OAuth 2.0

    @POST("/api/oauth2/user")
    Call<Void> oauthRegisterUser(
            @Query("response_type") String responseType,
            @Query("client_id") int clientId,
            @Query("scope") String scope,
            @Query("state") String state,
            @Query("redirect_uri") String redirectUri,
            @Body OauthRegisterUserBody body
    );

    @POST("/api/oauth2/login/token")
    Call<OauthAuthResponse> oauthLogin(
            @Query("client_id") int clientId,
            @Query("scope") String scope,
            @Body OauthAuthUserBody oauthAuthUserBody
    );

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    Call<OauthAuthResponse> oauthRefreshToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType,
            @Field("client_id") int clientId,
            @Field("redirect_uri") String redirectUri
    );

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    Call<OauthAuthResponse> oauthGetTokenByCode(
            @Field("code") String code,
            @Field("grant_type") String grantType,
            @Field("client_id") int clientId,
            @Field("redirect_uri") String redirectUri
    );

    @GET("/api/oauth2/social/{providerName}/login_url")
    Call<OauthLinkForSocialAuthResponse> oauthGetLinkForSocialAuth(
            @Path("providerName") String providerName,
            @Query("client_id") int clientId,
            @Query("state") String state,
            @Query("redirect_uri") String redirectUri,
            @Query("response_type") String responseType,
            @Query("scope") String scope
    );


    // General

    @POST("/api/password/reset/request")
    Call<Void> resetPassword(@Query("projectId") String projectId, @Body ResetPasswordBody resetPasswordBody);
}
