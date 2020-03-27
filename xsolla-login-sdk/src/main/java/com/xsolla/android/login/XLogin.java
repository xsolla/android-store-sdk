package com.xsolla.android.login;

import android.content.Context;
import android.os.Build;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.login.api.XLoginSocialCallback;
import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.login.token.TokenUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Entry point for Xsolla Login SDK
 */
public class XLogin {

    private String projectId;
    private String callbackUrl;

    private TokenUtils tokenUtils;
    private LoginApi loginApi;

    private static XLogin instance;

    private XLogin(String projectId, String callbackUrl, TokenUtils tokenUtils, LoginApi loginApi) {
        this.projectId = projectId;
        this.callbackUrl = callbackUrl;
        this.tokenUtils = tokenUtils;
        this.loginApi = loginApi;
    }

    private static XLogin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. Call \"XLogin.init()\" in MainActivity.onCreate()");
        }
        return instance;
    }

    /**
     * Get authentication token
     * @return token
     */
    public static String getToken() {
        return getInstance().tokenUtils.getToken();
    }

    public static void saveToken(String token) {
        getInstance().tokenUtils.saveToken(token);
    }

    /**
     * Initialize SDK
     * @param projectId project id from Publisher Account
     * @param context application context
     */
    public static void init(String projectId, Context context) {
        init(projectId, null, context);
    }

    /**
     * Initialize SDK
     * @param projectId project id from Publisher Account
     * @param callbackUrl callback URL from Publisher Account
     * @param context application context
     */
    public static void init(String projectId, String callbackUrl, Context context) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder()
                        .addHeader("engine", "android")
                        .addHeader("engine_v", Build.VERSION.RELEASE)
                        .addHeader("sdk", "Login")
                        .addHeader("sdk_v", BuildConfig.VERSION_NAME);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);
        TokenUtils tokenUtils = new TokenUtils(context);

        instance = new XLogin(projectId, callbackUrl, tokenUtils, loginApi);
    }

    /**
     * Register new user
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-register">Login API Reference</a>
     * @param username new user's username
     * @param email new user's email
     * @param password new user's password
     * @param callback status callback
     */
    public static void register(String username, String email, String password, XLoginCallback<Void> callback) {
        RegisterUserBody registerUserBody = new RegisterUserBody(username, email, password);
        getInstance().loginApi.registerUser(getInstance().projectId, registerUserBody).enqueue(callback);
    }

    /**
     * Authenticate via username and password
     * @see <a href="https://developers.xsolla.com/login-api/jwt/auth-by-username-and-password">Login API Reference</a>
     * @param username user's username
     * @param password user's email
     * @param callback status callback
     */
    public static void login(String username, String password, XLoginCallback<AuthResponse> callback) {
        AuthUserBody authUserBody = new AuthUserBody(username, password);
        getInstance().loginApi.login(getInstance().projectId, authUserBody).enqueue(callback);
    }

    /**
     * Authenticate via social network
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     * @param socialNetwork social network to authenticate with
     * @param callback status callback
     */
    public static void loginSocial(SocialNetwork socialNetwork, XLoginSocialCallback<SocialAuthResponse> callback) {
        getInstance().loginApi.getLinkForSocialAuth(socialNetwork.providerName, getInstance().projectId).enqueue(callback);
    }

    /**
     * Reset user's password
     * @see <a href="https://developers.xsolla.com/login-api/general/reset-password">Login API Reference</a>
     * @param username user's username
     * @param callback status callback
     */
    public static void resetPassword(String username, XLoginCallback<Void> callback) {
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(username);
        getInstance().loginApi.resetPassword(getInstance().projectId, resetPasswordBody).enqueue(callback);
    }

    /**
     * Clear authentication data
     */
    public static void logout() {
        getInstance().tokenUtils.clearToken();
    }

    public static boolean isTokenExpired() {
        JWT jwt = getInstance().tokenUtils.getJwt();
        if (jwt == null) return true;
        return jwt.isExpired(0);
    }

    /**
     * Get current user's token metadata
     * @return parsed JWT content
     */
    public static JWT getJwt() {
        return getInstance().tokenUtils.getJwt();
    }

    public static String getCallbackUrl() {
        return getInstance().callbackUrl;
    }

}
