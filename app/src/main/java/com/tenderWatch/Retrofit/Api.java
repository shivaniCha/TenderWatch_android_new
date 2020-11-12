package com.tenderWatch.Retrofit;

/**
 * Created by lcom48 on 25/11/17.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tenderWatch.Category;
import com.tenderWatch.ClientDrawer.TenderList;
import com.tenderWatch.Models.AllContractorTender;
import com.tenderWatch.Models.GetCategory;
import com.tenderWatch.Models.GetCountry;
import com.tenderWatch.Models.IntrestedContractor;
import com.tenderWatch.Models.LoginPost;
import com.tenderWatch.Models.Message;
import com.tenderWatch.Models.PesapalPaymentRESP;
import com.tenderWatch.Models.Register;
import com.tenderWatch.Models.ResponseNotifications;
import com.tenderWatch.Models.ResponseRating;
import com.tenderWatch.Models.Sender;
import com.tenderWatch.Models.SubScriptionResponse;
import com.tenderWatch.Models.SubscriptionCategoryResponse;
import com.tenderWatch.Models.SubscriptionList;
import com.tenderWatch.Models.Success;
import com.tenderWatch.Models.Tender;
import com.tenderWatch.Models.UpdateTender;
import com.tenderWatch.Models.UploadTender;
import com.tenderWatch.Models.User;
import com.tenderWatch.PesapalListActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @POST("auth/glogin")
    @FormUrlEncoded
    Call<Register> savePostGoogle(@Field("token") String idToken,
                                  @Field("role") String role,
                                  @Field("androidDeviceId") String deviceId);

    @POST("auth/facelogin")
    @FormUrlEncoded
    Call<Register> savePostFB(@Field("token") String idToken,
                              @Field("role") String role,
                              @Field("androidDeviceId") String deviceId);

    @POST("auth/login")
    @FormUrlEncoded
    Call<Register> savePost(@Field("email") String email,
                            @Field("password") String password,
                            @Field("role") String role,
                            @Field("androidDeviceId") String deviceId);

    @POST("auth/forgot")
    @FormUrlEncoded
    Call<LoginPost> forgotPassword(@Field("email") String email,
                                   @Field("role") String role);

    @POST("auth/checkEmail")
    @FormUrlEncoded
    Call<Message> checkEmailExit(@Field("email") String email,
                                 @Field("role") String role);

    @GET("auth/country")
    Call<ArrayList<GetCountry>> getCountryData();

    @GET("auth/category")
    Call<ArrayList<GetCategory>> getCategoryData();

    @Multipart
    @POST("auth/register")
    Call<Register> uploadImage(@Part MultipartBody.Part email,
                               @Part MultipartBody.Part password,
                               @Part MultipartBody.Part country,
                               @Part MultipartBody.Part contactNo,
                               @Part MultipartBody.Part occupation,
                               @Part MultipartBody.Part aboutMe,
                               @Part MultipartBody.Part role,
                               @Part MultipartBody.Part androidDeviceId,
                               @Part MultipartBody.Part image,
                               @Part MultipartBody.Part firstName,
                               @Part MultipartBody.Part lastName);

    @Multipart
    @POST("auth/register")
    Call<Register> uploadContractor(@Part MultipartBody.Part email,
                                    @Part MultipartBody.Part password,
                                    @Part MultipartBody.Part country,
                                    @Part MultipartBody.Part contactNo,
                                    @Part MultipartBody.Part occupation,
                                    @Part MultipartBody.Part aboutMe,
                                    @Part MultipartBody.Part role,
                                    @Part MultipartBody.Part androidDeviceId,
                                    @Part MultipartBody.Part image,
                                    @Part MultipartBody.Part selections,
                                    @Part MultipartBody.Part subscribe,
                                    @Part MultipartBody.Part firstName,
                                    @Part MultipartBody.Part lastName,
                                    @Part MultipartBody.Part loginToken);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "users", hasBody = true)
    Call<ResponseBody> logout(@Header("Authorization") String token,
                              @Field("androidDeviceId") String deviceId,
                              @Field("role") String role);

    /*get all Interested Contractors without any tender id */
    @GET("tender/interestedContractor/{userId}")
    Call<ArrayList<AllContractorTender>> GetAllContractorsList(@Header("Authorization") String token,@Path("userId") String userId
    );

    @Multipart
    @POST("users/{userId}")
    Call<User> UpdateUser(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Part MultipartBody.Part country,
            @Part MultipartBody.Part contactNo,
            @Part MultipartBody.Part occupation,
            @Part MultipartBody.Part aboutMe,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part firstName,
            @Part MultipartBody.Part lastName);

    @GET("users/{userId}")
    Call<User> getUserDetail(@Header("Authorization") String token, @Path("userId") String userId);

    @POST("users/changePassword/{userId}")
    @FormUrlEncoded
    Call<Success> ChangePassword(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword);

    @POST("tender/getTenders")
    Call<ArrayList<Tender>> getAllTender(
            @Header("Authorization") String token);

    @POST("tender/getTenders")
    Call<ArrayList<AllContractorTender>> getAllContractorTender(
            @Header("Authorization") String token);

    @Multipart
    @POST("tender")
    Call<UploadTender> uploadTender(
            @Header("Authorization") String token,
            @Part MultipartBody.Part email,
            @Part MultipartBody.Part tenderName,
            @Part MultipartBody.Part city,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part descriptionLink,
            @Part MultipartBody.Part contactNo,
            @Part MultipartBody.Part landlineNo,
            @Part MultipartBody.Part address,
            @Part MultipartBody.Part country,
            @Part MultipartBody.Part category,
            @Part MultipartBody.Part isFollowTender,
            @Part MultipartBody.Part isFollowTenderLink,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part targetViewers);

    @Multipart
    @PUT("tender/{tenderDetailId}")
        Call<ResponseBody> updateTender(
            @Header("Authorization") String token,
            @Path("tenderDetailId") String id,
            @Part MultipartBody.Part email,
            @Part MultipartBody.Part tenderName,
            @Part MultipartBody.Part city,
            @Part MultipartBody.Part description,
            @Part MultipartBody.Part descriptionLink,
            @Part MultipartBody.Part contactNo,
            @Part MultipartBody.Part landlineNo,
            @Part MultipartBody.Part address,
            @Part MultipartBody.Part country,
            @Part MultipartBody.Part category,
            @Part MultipartBody.Part isFollowTender,
            @Part MultipartBody.Part isFollowTenderLink,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part targetViewers);

    @DELETE("tender/{tenderDetailId}")
    Call<ResponseBody> removeTender(
            @Header("Authorization") String token,
            @Path("tenderDetailId") String id);

    @GET("service/userServices")
    Call<SubScriptionResponse> getSubscriptionDetails(
            @Header("Authorization") String token);

    @GET("notification")
    Call<ArrayList<ResponseNotifications>> getNotifications(
            @Header("Authorization") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "notification/delete", hasBody = true)
    Call<ResponseBody> deleteNotification(@Header("Authorization") String token,
                                          @Field("notification") ArrayList<String> notification);

    @POST("review")
    @FormUrlEncoded
    Call<ResponseRating> giveRating(
            @Header("Authorization") String token,
            @Field("user") String clientId,
            @Field("rating") String Rating);

    @GET("tender/{tenderDetailId}")
    Call<UpdateTender> getTender(
            @Header("Authorization") String token,
            @Path("tenderDetailId") String id);

    @POST("tender/{tenderDetailId}")
    Call<List<Sender>> getAllInterestedContractor(@Header("Authorization") String token,
                                                  @Path("tenderDetailId") String tender);

    @PUT("tender/interested/{tenderId}")
    Call<ResponseBody> callInterested(
            @Header("Authorization") String token,
            @Path("tenderId") String id);

    @PUT("tender/favorite/{tenderId}")
    Call<ResponseBody> addFavorite(
            @Header("Authorization") String token,
            @Path("tenderId") String id);

    @GET("tender/getTenders")
    Call<ArrayList<AllContractorTender>> getAllFavoriteTender(
            @Header("Authorization") String token);

    @PUT("notification/{notificationId}")
    Call<ResponseBody> readNotification(
            @Header("Authorization") String token, @Path("notificationId") String id);

    @DELETE("tender/favorite/{favoriteId}")
    Call<ResponseBody> removeFavorite(
            @Header("Authorization") String token,
            @Path("favoriteId") String id);

    @DELETE("tender/interested/{tenderId}")
    Call<ResponseBody> removeInterested(
            @Header("Authorization") String token,
            @Path("tenderId") String id);

    /*get Interested Contractor when select any tender*/
    @GET("tender/interestedContractorForTender/{tenderId}")
    Call<ArrayList<AllContractorTender>> getTenderContractors(
            @Header("Authorization") String token,
            @Path("tenderId") String tenderId);



    @FormUrlEncoded
    @POST("payments/pesapal")
    Call<ResponseBody> getPesaPalURL(@Header("Authorization") String token,
                                     @Field("desc") String desc,
                                     @Field("amount") double amount,
                                     @Field("email") String email,
                                     @Field("pesapalInfo") JSONObject pesapalInfo);


    @FormUrlEncoded
    @POST("payments/paypal")
    Call<ResponseBody> getPayPalURL(@Header("Authorization") String token,
                                     @Field("desc") String desc,
                                     @Field("amount") double amount,
                                     @Field("email") String email,
                                     @Field("paypalInfo") JSONObject pesapalInfo);

    @FormUrlEncoded
    @PUT("payments/paypal")
    Call<ResponseBody> executePaypalPayment(@Header("Authorization") String token,
                                    @Field("paymentId") String paymentId,
                                    @Field("token") String tokenId,
                                    @Field("payerID") String payerID);

    @FormUrlEncoded
    @POST("payments/pesapal/details")
    Call<PesapalPaymentRESP> getPesaPalPaymentDetails(@Header("Authorization") String token,
                                                      @Field("pesapal_merchant_reference") String pesapal_merchant_reference);

    @PUT("service/userServices")
    Call<ResponseBody> updatePesaPalPaymentDetails(@Header("Authorization") String token,
                                                   @Body PesapalListActivity.UpdatePaymentDetails paymentDetails);

    @POST("service/userServices")
    Call<ResponseBody> getPesaPalPaymentDetails(@Header("Authorization") String token);

    @GET("service/Subscription/history")
    Call<List<SubscriptionList>> getSubcriptionList(@Header("Authorization") String token);

    @DELETE("users/{userId}")
    Call<ResponseBody> removeAccount(@Header("Authorization") String token, @Path("userId") String userId);

    @GET("auth/getSubRoles")
    Call<ResponseBody> getContractorRole();

    @GET("auth/getCommonValues")
    Call<ResponseBody> getSubscriptionValue();

    @GET("auth/getSubRoles")
    Call<JsonArray> getSubRole();

    @GET("auth/getCommonValues")
    Call<List<SubscriptionCategoryResponse>> getAllSubscriptionCategory();


    @POST("auth/discontinue")
    @FormUrlEncoded
    Call<Boolean> isTrialVisible(@Field("email")String email,@Field("contactNo")String contactNo);

    @POST("service/checkservice")
    @FormUrlEncoded
    Call<Category.CheckServiceModel> checkSubscription(@Header("Authorization") String token,
                                                       @Field("countryId") String countryId,
                                                       @Field("category") String categoryId);
    @POST("tender/SendDetail")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBody> sendEmail(
            @Header("Authorization") String token,
            @Body JsonObject jsonBody);

    /*Send Email when client remove their profile*/
    @POST("tender/SendDetailToContractor")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBody> sendListContractorEmail(
            @Header("Authorization") String token,
            @Body JsonObject jsonBody);

    @PUT("service/RemoveSubscription")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBody> removeSubscription(@Header("Authorization") String token, @Body JsonObject jsonBody);
}

