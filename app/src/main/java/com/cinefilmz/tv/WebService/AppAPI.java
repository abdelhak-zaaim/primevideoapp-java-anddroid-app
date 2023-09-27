package com.cinefilmz.tv.WebService;

import com.cinefilmz.tv.Model.LangaugeModel.LangaugeModel;
import com.cinefilmz.tv.Model.AvatarModel.AvatarModel;
import com.cinefilmz.tv.Model.CastDetailModel.CastDetailModel;
import com.cinefilmz.tv.Model.GeneralSettings.GeneralSettings;
import com.cinefilmz.tv.Model.GenresModel.GenresModel;
import com.cinefilmz.tv.Model.LoginRegiModel.LoginRegiModel;
import com.cinefilmz.tv.Model.PagesModel.PagesModel;
import com.cinefilmz.tv.Model.PayTmModel.PayTmModel;
import com.cinefilmz.tv.Model.PaymentOptionModel.PaymentOptionModel;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.Model.RentProductModel.RentProductModel;
import com.cinefilmz.tv.Model.SearchModel.SearchModel;
import com.cinefilmz.tv.Model.SectionBannerModel.SectionBannerModel;
import com.cinefilmz.tv.Model.SectionChannelModel.SectionChannelModel;
import com.cinefilmz.tv.Model.SectionDetailModel.SectionDetailModel;
import com.cinefilmz.tv.Model.SectionListModel.SectionListModel;
import com.cinefilmz.tv.Model.SectionTypeModel.SectionTypeModel;
import com.cinefilmz.tv.Model.SubscriptionModel.SubscriptionModel;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.Model.VideoModel.VideoModel;
import com.cinefilmz.tv.Model.VideoSeasonModel.VideoSeasonModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppAPI {

    @POST("general_setting")
    Call<GeneralSettings> general_setting();

    @POST("get_pages")
    Call<PagesModel> get_pages();

    @FormUrlEncoded
    @POST("change_password")
    Call<SuccessModel> change_password(@Field("user_id") String user_id,
                                       @Field("password") String password);

    /* type => 1-Facebook, 2-Google, 3-OTP */
    @FormUrlEncoded
    @POST("login")
    Call<LoginRegiModel> login(@Field("type") String type,
                               @Field("email") String email,
                               @Field("name") String name);

    @FormUrlEncoded
    @POST("login")
    Call<LoginRegiModel> loginWithOTP(@Field("type") String type,
                                      @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("get_profile")
    Call<ProfileModel> get_profile(@Field("id") String id);

    @FormUrlEncoded
    @POST("update_profile")
    Call<ProfileModel> update_profile(@Field("id") String id,
                                      @Field("name") String name);

    @FormUrlEncoded
    @POST("update_profile")
    Call<ProfileModel> updateMissingData(@Field("id") String id,
                                         @Field("name") String name,
                                         @Field("mobile") String mobile,
                                         @Field("email") String email);

    @POST("get_avatar")
    Call<AvatarModel> get_avatar();

    @Multipart()
    @POST("image_upload")
    Call<SuccessModel> image_upload(@Part("id") RequestBody id,
                                    @Part MultipartBody.Part image);


    @POST("get_type")
    Call<SectionTypeModel> get_type();

    @FormUrlEncoded
    @POST("section_list")
    Call<SectionListModel> section_list(@Field("user_id") String user_id,
                                        @Field("type_id") String type_id,
                                        @Field("is_home_page") String is_home_page);

    /* type => 1-movies, 2-news, 3-sport, 4-tv show */
    @FormUrlEncoded
    @POST("get_banner")
    Call<SectionBannerModel> get_banner(@Field("user_id") String user_id,
                                        @Field("type_id") String type_id,
                                        @Field("is_home_page") String is_home_page);

    @FormUrlEncoded
    @POST("section_detail")
    Call<SectionDetailModel> section_detail(@Field("type_id") String type_id,
                                            @Field("video_type") String video_type,
                                            @Field("upcoming_type") String upcoming_type,
                                            @Field("user_id") String user_id,
                                            @Field("video_id") String video_id);

    @FormUrlEncoded
    @POST("get_video_by_session_id")
    Call<VideoSeasonModel> get_video_by_session_id(@Field("user_id") String user_id,
                                                   @Field("session_id") String session_id,
                                                   @Field("show_id") String show_id);

    @FormUrlEncoded
    @POST("cast_detail")
    Call<CastDetailModel> cast_detail(@Field("cast_id") String cast_id);

    @POST("get_category")
    Call<GenresModel> get_category();

    @POST("get_language")
    Call<LangaugeModel> get_language();

    @FormUrlEncoded
    @POST("add_continue_watching")
    Call<SuccessModel> add_continue_watching(@Field("user_id") String user_id,
                                             @Field("video_id") String video_id,
                                             @Field("video_type") String video_type,
                                             @Field("stop_time") String stop_time);

    @FormUrlEncoded
    @POST("remove_continue_watching")
    Call<SuccessModel> remove_continue_watching(@Field("user_id") String user_id,
                                                @Field("video_id") String video_id,
                                                @Field("video_type") String video_type);
    /* user_id, video_id, video_type
     * Show :=> ("video_id" = Episode's ID)  AND  ("video_type" = "2")
     * Video :=> ("video_id" = Video's ID) */

    @FormUrlEncoded
    @POST("add_remove_bookmark")
    Call<SuccessModel> add_remove_bookmark(@Field("user_id") String user_id,
                                           @Field("video_id") String video_id,
                                           @Field("video_type") String video_type,
                                           @Field("type_id") String type_id);

    @FormUrlEncoded
    @POST("get_bookmark_video")
    Call<VideoModel> get_bookmark_video(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("add_remove_download")
    Call<SuccessModel> add_remove_download(@Field("user_id") String user_id,
                                           @Field("video_id") String video_id,
                                           @Field("video_type") String video_type,
                                           @Field("type_id") String type_id,
                                           @Field("other_id") String other_id);
    /* user_id, video_id, video_type, type_id, other_id
     * Show :=> ("video_id" = Session's ID)  AND  ("other_id" = Show's ID)
     * Video :=> ("other_id" = "0") */

    @FormUrlEncoded
    @POST("video_by_category")
    Call<VideoModel> video_by_category(@Field("user_id") String user_id,
                                       @Field("type_id") String type_id,
                                       @Field("category_id") String category_id);

    @FormUrlEncoded
    @POST("video_by_language")
    Call<VideoModel> video_by_language(@Field("user_id") String user_id,
                                       @Field("type_id") String type_id,
                                       @Field("language_id") String language_id);

    @FormUrlEncoded
    @POST("rent_video_list")
    Call<RentProductModel> rent_video_list(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("user_rent_video_list")
    Call<RentProductModel> user_rent_video_list(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("channel_section_list")
    Call<SectionChannelModel> channel_section_list(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_package")
    Call<SubscriptionModel> get_package(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("search_video")
    Call<SearchModel> search_video(@Field("user_id") String user_id,
                                   @Field("name") String name);

    @POST("get_payment_option")
    Call<PaymentOptionModel> get_paymentoption();

    @FormUrlEncoded
    @POST("get_payment_token")
    Call<PayTmModel> getPaymentToken(@Field("MID") String MID,
                                     @Field("order_id") String order_id,
                                     @Field("CUST_ID") String CUST_ID,
                                     @Field("CHANNEL_ID") String CHANNEL_ID,
                                     @Field("TXN_AMOUNT") String TXN_AMOUNT,
                                     @Field("WEBSITE") String WEBSITE,
                                     @Field("CALLBACK_URL") String CALLBACK_URL,
                                     @Field("INDUSTRY_TYPE_ID") String INDUSTRY_TYPE_ID);

    @FormUrlEncoded
    @POST("add_transaction")
    Call<SuccessModel> add_transaction(@Field("user_id") String user_id,
                                       @Field("package_id") String package_id,
                                       @Field("description") String description,
                                       @Field("amount") String amount,
                                       @Field("payment_id") String payment_id,
                                       @Field("currency_code") String currency_code);

    @FormUrlEncoded
    @POST("add_rent_transaction")
    Call<SuccessModel> add_rent_transaction(@Field("user_id") String user_id,
                                            @Field("video_id") String video_id,
                                            @Field("price") String price,
                                            @Field("type_id") String type_id,
                                            @Field("video_type") String video_type);

}