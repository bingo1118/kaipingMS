package com.smart.cloud.fire.retrofit;

import com.smart.cloud.fire.activity.AssetManage.Tag.TagListEntity;
import com.smart.cloud.fire.global.AllAssetListEntity;
import com.smart.cloud.fire.global.AssetListEntity;
import com.smart.cloud.fire.global.AssetManager;
import com.smart.cloud.fire.global.CheckListEntity;
import com.smart.cloud.fire.global.ChuangAnValue;
import com.smart.cloud.fire.global.Electric;
import com.smart.cloud.fire.global.ElectricInfo;
import com.smart.cloud.fire.global.ElectricValue;
import com.smart.cloud.fire.global.ProofGasHistoryEntity;
import com.smart.cloud.fire.global.SafeScore;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.global.TagAlarmListEntity;
import com.smart.cloud.fire.global.TemperatureTime;
import com.smart.cloud.fire.mvp.fragment.ConfireFireFragment.ConfireFireModel;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.mvp.login.model.LoginModel;
import com.smart.cloud.fire.mvp.register.model.RegisterModel;
import com.smart.cloud.fire.order.OrderInfoDetail.HttpOrderInfoEntity;
import com.smart.cloud.fire.order.OrderList.HttpOrderListEntity;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiStores {
    //登录技威服务器
    @FormUrlEncoded
    @POST("Users/LoginCheck.ashx")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<LoginModel>
    loginYooSee(@Field("User") String User, @Field("Pwd") String Pwd,
                                       @Field("VersionFlag") String VersionFlag, @Field("AppOS") String AppOS,
                                       @Field("AppVersion") String AppVersion);
    //登录本地服务器
    @GET("login")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<LoginModel> login(@Query("userId") String userId);

    //登录本地服务器2，登陆新接口2017.5.16
    @GET("login")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<LoginModel> login2(@Query("userId") String userId,@Query("pwd") String pwd
            ,@Query("cid") String cid,@Query("appId") String appId,@Query("ifregister") String ifregister);

    //获取短信验证码
    @FormUrlEncoded
    @POST("Users/PhoneCheckCode.ashx")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<RegisterModel> getMesageCode(@Field("CountryCode") String countryCode, @Field("PhoneNO") String phoneNO
            , @Field("AppVersion") String appVersion);

    //检查短信验证码
    @FormUrlEncoded
    @POST("Users/PhoneVerifyCodeCheck.ashx")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<RegisterModel> verifyPhoneCode(@Field("CountryCode") String countryCode,@Field("PhoneNO") String phoneNO
            ,@Field("VerifyCode") String verifyCode);

    //注册
    @FormUrlEncoded
    @POST("Users/RegisterCheck.ashx")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<RegisterModel> register(@Field("VersionFlag") String versionFlag,@Field("Email") String email
            ,@Field("CountryCode") String countryCode,@Field("PhoneNO") String phoneNO
            ,@Field("Pwd") String pwd,@Field("RePwd") String rePwd
            ,@Field("VerifyCode") String verifyCode,@Field("IgnoreSafeWarning") String ignoreSafeWarning);

    //获取用户所有的烟感
    @GET("getAllSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllSmoke(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //获取用户所有故障烟感
    @GET("getAllDetailSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllDetailSmoke(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page,@Query("type") String type);

    //获取用户所有的有线终端@@6.29
    @GET("getAllFaultinfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllFaultinfo(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //获取用户所有的有线终端下的烟感@@6.29
    @GET("getEquipmentOfOneRepeater")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getEquipmentOfOneRepeater(@Query("userId") String userId, @Query("repeater") String repeater,@Query("page") String page);

    //获取用户某个烟感的历史报警记录@@7.3
//    @GET("getAlarmOfRepeater")
//    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
//    Observable<HttpError> getAlarmOfRepeater(@Query("userId") String userId, @Query("repeater") String repeater
//            ,@Query("smokeMac") String smokeMac,@Query("startTime") String startTime
//            ,@Query("endTime") String endTime,@Query("page") String page,@Query("faultDesc") String faultDesc);
    //获取用户某个烟感的历史报警记录@@7.3
    @FormUrlEncoded
    @POST("getAlarmOfRepeater")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAlarmOfRepeater(@Field("userId") String userId, @Field("repeater") String repeater
            ,@Field("smokeMac") String smokeMac,@Field("startTime") String startTime
            ,@Field("endTime") String endTime,@Field("page") String page,@Field("faultDesc") String faultDesc);

    //获取用户所有的设备
    @GET("getAllDevice")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllDevice(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

//    @FormUrlEncoded
//    @POST("getAllSmoke")
//    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
//    Observable<HttpError> getAllSmoke(@Field("userId") String userId, @Field("privilege") String privilege,@Field("page") String page);

    //获取用户所有的摄像头
    @GET("getAllCamera")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllCamera(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //获取所有的店铺类型
    @GET("getPlaceTypeId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getPlaceTypeId(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //获取所有的NFC设备类型
    @GET("getNFCDeviceTypeId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNFCDeviceTypeId();

    //获取所有的巡检设备类型
    @GET("getNFCType")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNFCType();

    //获取所有的巡检点类型
    @GET("getPoints")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getPoints(@Query("userId") String userId, @Query("areaId") String areaId);

    //获取区域下的管理员
    @GET("getManagersByAreaId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getManagersByAreaId(@Query("areaId") String areaId);

    //获取查找的巡检点类型
    @GET("getItemsByName")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItemsByName(@Query("userId") String userId,@Query("pid") String pid, @Query("deviceName") String areaId);

    //获取查找任务中的巡检点类型
    @GET("getItemsByName2")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItemsByName2(@Query("pid") String pid, @Query("deviceName") String areaId);

    //获取所有的巡检任务
    @GET("getTasks")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getTasks(@Query("userId") String userId,@Query("tlevel") String tlevel
                                    ,@Query("state") String state,@Query("startDate") String startDate
                                    ,@Query("endDate") String endDate);

    //获取所有的巡检任务
    @GET("getTaskCount")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getTaskCount(@Query("userId") String userId);

    //获取所有的巡检点下的巡检项目
    @GET("getItemsByPid")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItemsByPid(@Query("pid") String pid);

    //获取所有的巡检项目的巡检历史
    @GET("getRecordList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getRecordList(@Query("uid") String uid);

    @GET("getNoticeByUserId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNoticeByUserId(@Query("userId") String userId);

    //获取用户名下的巡检项目
    @GET("getAllItems")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllItems(@Query("userId") String userid,@Query("pid") String pid);

    //获取用户名下的巡检项目
    @GET("getItemsList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItemsList(@Query("userId") String userid,@Query("status") String status,@Query("taskType") String taskType);

    //获取用户名下的巡检项目
    @GET("getItemInfoByAreaId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItemInfoByAreaId(@Query("userId") String userid,@Query("areaId") String areaId);

    //获取所有的巡检点下的巡检项目
    @GET("getItems")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getItems(@Query("tid") String pid,@Query("state") String state);

    //获取所有的区域类型
    @GET("getAreaId")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpAreaResult> getAreaId(@Query("userId") String userId, @Query("privilege") String privilege, @Query("page") String page);

    //根据条件查询用户烟感
    @GET("getNeedSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedSmoke(@Query("userId") String userId, @Query("privilege") String privilege,
                                       @Query("areaId") String areaId,@Query("page") String page,
                                       @Query("placeTypeId") String placeTypeId);

    //根据条件查询用户设备
    @GET("getNeedDev")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedDev(@Query("userId") String userId, @Query("privilege") String privilege,
                                     @Query("areaId") String areaId,@Query("page") String page,
                                     @Query("placeTypeId") String placeTypeId,@Query("devType") String devType);

    //根据条件查询资产盘点列表数据
    @GET("getACheckList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<CheckListEntity> getACheckList(@Query("userId") String userId,
                                              @Query("privilege") String privilege, @Query("stateName") String page);

    //根据条件查询资产盘点清单
    @GET("getAssetByCkey")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<AssetListEntity> getAssetByCkey(@Query("ckey") String ckey,
                                               @Query("atPid") String atPid, @Query("ifFinish") String ifFinish,
                                               @Query("startTime") String startTime, @Query("endTime") String endTime);

    //根据条件查询标签清单
    @GET("getTagList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TagListEntity> getTagList(@Query("userId") String userId,
                                         @Query("privilege") String privilege, @Query("areaId") String areaId,
                                         @Query("mac") String mac, @Query("name") String name,
                                         @Query("netstate") String netstate, @Query("page") String page);

    //根据条件查询所有资产列表
    @GET("getAssetList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<AllAssetListEntity> getAssetList(@Query("userId") String userId,
                                                @Query("privilege") String privilege,
                                                @Query("page") String page,
                                                @Query("akey") String akey,
                                                @Query("areaId") String areaId,
                                                @Query("atId") String atId,
                                                @Query("named") String named,
                                                @Query("state") String state);

    //根据底座报警列表
    @GET("getTabAlarmList")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TagAlarmListEntity> getTabAlarmList(@Query("userId") String userId,
                                                   @Query("privilege") String privilege,
                                                   @Query("page") String page,
                                                   @Query("ifDeal") String ifDeal);

    //根据条件查询用户设备（巡检）
    @GET("getNeedDev")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedDev2(@Query("userId") String userId, @Query("privilege") String privilege,
                                     @Query("areaId") String areaId,@Query("page") String page,
                                     @Query("state") String placeTypeId,@Query("devType") String devType);

    //查询用户工单列表
    @GET("getAllOrder")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpOrderListEntity> getAllOrder(@Query("userId") String userId, @Query("privilege") String privilege
            , @Query("state") String state);

    //查询用户工单详情
    @GET("getOrderDetail")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpOrderInfoEntity> getOrderDetail(@Query("jkey") String jkey);

    //根据条件查询用户设备@@9.1 添加区域分级查询
    @GET("getNeedDev")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedDev2(@Query("userId") String userId, @Query("privilege") String privilege,@Query("parentId") String parentId,
                                     @Query("areaId") String areaId,@Query("page") String page,
                                     @Query("placeTypeId") String placeTypeId,@Query("devType") String devType);

    //根据条件查询用户设备
    @GET("getNeedLossDev")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedLossDev(@Query("userId") String userId, @Query("privilege") String privilege,
                                         @Query("parentId") String parentId,@Query("areaId") String areaId,@Query("page") String page,
                                     @Query("placeTypeId") String placeTypeId,@Query("devType") String devType);

    //根据条件查询用户所有设备（设备类型<11）
    @GET("getNeedDevice")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedDevice(@Query("userId") String userId, @Query("privilege") String privilege,
                                       @Query("areaId") String areaId,@Query("page") String page,
                                       @Query("placeTypeId") String placeTypeId);

    //根据查询内容查询用户烟感
    @GET("getSmokeBySearch")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getSearchSmoke(@Query("userId") String userId, @Query("privilege") String privilege,
                                       @Query("search") String search);
    //处理报警消息
    @GET("dealAlarm")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> dealAlarm(@Query("userId") String userId, @Query("smokeMac") String smokeMac);

    //处理报警消息详情
    @GET("dealAlarmDetail")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> dealAlarmDetail(@Query("userId") String userId, @Query("smokeMac") String smokeMac,
            @Query("dealPeople") String dealPeople, @Query("alarmTruth") String alarmTruth,
            @Query("dealDetail") String dealDetail, @Query("image_path") String image_path, @Query("video_path") String video_path);

    //获取单个烟感信息
    @GET("getOneSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> getOneSmoke(@Query("userId") String userId, @Query("smokeMac") String smokeMac,
                                             @Query("privilege") String privilege);

    //获取燃气历史数据
    @GET("getGasHistoryInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ProofGasHistoryEntity> getGasHistoryInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                        @Query("smokeMac") String smokeMac, @Query("page") String page);

    //添加烟感
    @FormUrlEncoded
    @POST("addSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> addSmoke(@Field("userId") String userId, @Field("smokeName") String smokeName,
                                          @Field("privilege") String privilege, @Field("smokeMac") String smokeMac,
                                          @Field("address") String address, @Field("longitude") String longitude,
                                          @Field("latitude") String latitude, @Field("placeAddress") String placeAddress,
                                          @Field("placeTypeId") String placeTypeId, @Field("principal1") String principal1,
                                          @Field("principal1Phone") String principal1Phone, @Field("principal2") String principal2,
                                          @Field("principal2Phone") String principal2Phone, @Field("areaId") String areaId,
                                          @Field("repeater") String repeater,@Field("camera") String camera,@Field("deviceType") String deviceType,
                                          @Field("electrState") String electrState,@Field("image") String image);

    //添加烟感
    @FormUrlEncoded
    @POST("addHeiMenSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> addHeiMenSmoke(@Field("userId") String userId, @Field("smokeName") String smokeName,
                                          @Field("privilege") String privilege, @Field("smokeMac") String smokeMac,
                                          @Field("address") String address, @Field("longitude") String longitude,
                                          @Field("latitude") String latitude, @Field("placeAddress") String placeAddress,
                                          @Field("placeTypeId") String placeTypeId, @Field("principal1") String principal1,
                                          @Field("principal1Phone") String principal1Phone, @Field("principal2") String principal2,
                                          @Field("principal2Phone") String principal2Phone, @Field("areaId") String areaId,
                                          @Field("repeater") String repeater,@Field("camera") String camera,@Field("deviceType") String deviceType,
                                          @Field("electrState") String electrState);

    //添加烟感
//    @FormUrlEncoded
//    @GET("addSmoke")
//    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
//    Observable<ConfireFireModel> addSmoke(@Query("userId") String userId, @Query("smokeName") String smokeName,
//                                          @Query("privilege") String privilege, @Query("smokeMac") String smokeMac,
//                                          @Query("address") String address, @Query("longitude") String longitude,
//                                          @Query("latitude") String latitude, @Query("placeAddress") String placeAddress,
//                                          @Query("placeTypeId") String placeTypeId, @Query("principal1") String principal1,
//                                          @Query("principal1Phone") String principal1Phone, @Query("principal2") String principal2,
//                                          @Query("principal2Phone") String principal2Phone, @Query("areaId") String areaId,
//                                          @Query("repeater") String repeater,@Query("camera") String camera,@Query("deviceType") String deviceType,
//                                          @Query("electrState") String electrState);

//    @FormUrlEncoded
//    @POST("addSmoke")
//    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
//    Observable<ConfireFireModel> addSmoke(@Field("userId") String userId, @Field("smokeName") String smokeName,
//                                          @Field("privilege") String privilege, @Field("smokeMac") String smokeMac,
//                                          @Field("address") String address, @Field("longitude") String longitude,
//                                          @Field("latitude") String latitude, @Field("placeAddress") String placeAddress,
//                                          @Field("placeTypeId") String placeTypeId, @Field("principal1") String principal1,
//                                          @Field("principal1Phone") String principal1Phone, @Field("principal2") String principal2,
//                                          @Field("principal2Phone") String principal2Phone, @Field("areaId") String areaId,
//                                          @Field("repeater") String repeater,@Field("camera") String camera,@Field("deviceType") String deviceType);

    //获取用户报警消息
    @GET("getAllAlarm")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getAllAlarm(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //条件查询获取用户报警消息
    @GET("getNeedAlarm")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedAlarm(@Query("userId") String userId, @Query("privilege") String privilege
            ,@Query("startTime") String startTime,@Query("endTime") String endTime
            ,@Query("areaId") String areaId,@Query("placeTypeId") String placeTypeId
            ,@Query("page") String page,@Query("parentId") String parentId);

    //条件查询获取用户报警任务
    @GET("getNeedAlarmMessage")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedAlarmMsg(@Query("userId") String userId, @Query("privilege") String privilege
            ,@Query("startTime") String startTime,@Query("endTime") String endTime
            ,@Query("areaId") String areaId,@Query("placeTypeId") String placeTypeId
            ,@Query("page") String page,@Query("parentId") String parentId,@Query("grade") String grade
            ,@Query("distance") String distance,@Query("progress") String progress);

    //条件查询获取用户报警任务2.0
    @GET("getNeedOrderMsg")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedOrderMsg(@Query("userId") String userId, @Query("privilege") String privilege
            ,@Query("page") String page,@Query("grade") String grade
            ,@Query("progress") String progress);

    //添加摄像头
    @GET("addCamera")
    Observable<HttpError> addCamera(@Query("cameraId") String cameraId, @Query("cameraName") String cameraName,
                                          @Query("cameraPwd") String cameraPwd, @Query("cameraAddress") String cameraAddress,
                                          @Query("longitude") String longitude, @Query("latitude") String latitude,
                                          @Query("principal1") String principal1, @Query("principal1Phone") String principal1Phone,
                                          @Query("principal2") String principal2, @Query("principal2Phone") String principal2Phone,
                                          @Query("areaId") String areaId, @Query("placeTypeId") String placeTypeId);

    //绑定烟感与摄像头
    @GET("bindCameraSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> bindCameraSmoke(@Query("cameraId") String cameraId, @Query("smoke") String smoke);


    @GET("getCid")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> bindAlias(@Query("alias") String alias, @Query("cid") String cid,@Query("projectName") String projectName);

    //一键报警
    @GET("textAlarm")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> textAlarm(@Query("userId") String userId, @Query("privilege") String privilege,
                                    @Query("smokeMac") String smokeMac,@Query("info") String info);

    //一键报警确认回复
    @GET("textAlarmAck")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> textAlarmAck(@Query("userId") String userId, @Query("alarmSerialNumber") String alarmSerialNumber);


    @GET("getNeedLossSmoke")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedLossSmoke(@Query("userId") String userId, @Query("privilege") String privilege,
                                           @Query("areaId") String areaId,@Query("page") String page,
                                           @Query("placeTypeId") String placeTypeId);

    @GET("getSmokeSummary")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<SmokeSummary> getSmokeSummary(@Query("userId") String userId, @Query("privilege") String privilege,
                                             @Query("areaId") String areaId);

    @GET("getDevSummary")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<SmokeSummary> getDevSummary(@Query("userId") String userId, @Query("privilege") String privilege,
                                           @Query("parentId") String parentId,@Query("areaId") String areaId,@Query("placeTypeId") String placeTypeId
                                                ,@Query("devType") String devType);

    @GET("getSafeScore")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<SafeScore> getSafeScore(@Query("userId") String userId, @Query("privilege") String privilege);

    @GET("getNFCSummary")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<SmokeSummary> getNFCSummary(@Query("userId") String userId, @Query("privilege") String privilege,
                                           @Query("areaId") String areaId,@Query("period") String period,
                                           @Query("devicetype") String devicetype);

    @GET("getAllElectricInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ElectricInfo<Electric>> getAllElectricInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                          @Query("page") String page);
//    getOneElectricInfo?userId=13428282520&privilege=2&smokeMac=32110533
    @GET("getOneElectricInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ElectricInfo<ElectricValue>> getOneElectricInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                               @Query("smokeMac") String smokeMac,@Query("devType") String devType);
    @GET("getOneChuangAnInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ElectricInfo<ChuangAnValue.ChuangAnValueBean>> getOneChuangAnInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                                                 @Query("smokeMac") String smokeMac);

//    getElectricTypeInfo?userId=13428282520&privilege=2&smokeMac=32110533&electricType=6&electricNum=1&page=post
    @GET("getWaterHistoryInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TemperatureTime> getWaterHistoryInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                    @Query("smokeMac") String smokeMac, @Query("page") String page);

    @GET("getTHDevInfoHistoryInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TemperatureTime> getTHDevInfoHistoryInfo(@Query("mac") String smokeMac, @Query("page") String page, @Query("type") String tepe);

    @GET("getElectricTypeInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TemperatureTime> getElectricTypeInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                    @Query("smokeMac") String smokeMac, @Query("electricType") String electricType,
                                                    @Query("electricNum") String electricNum, @Query("page") String page, @Query("devType") int devType);

    @GET("getChuanganHistoryInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<TemperatureTime> getChuanganHistoryInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                    @Query("smokeMac") String smokeMac,
                                                    @Query("electricNum") String electricNum, @Query("page") String page);

//    getNeedElectricInfo?userId=13622215085&privilege=2&areaId=14&placeTypeId=2&page
    @GET("getNeedElectricInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ElectricInfo<Electric>> getNeedElectricInfo(@Query("userId") String userId, @Query("privilege") String privilege,
                                                           @Query("parentId") String parentId,@Query("areaId") String areaId, @Query("placeTypeId") String placeTypeId,
                                                    @Query("page") String page);

    @FormUrlEncoded
    @POST("changeCameraPwd")
    Observable<HttpError> changeCameraPwd(@Field("cameraId") String cameraId, @Field("cameraPwd") String cameraPwd);

    //@@7.19获取用户安防设备列表
    @GET("getSecurityInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getSecurityInfo(@Query("userId") String userId, @Query("privilege") String privilege,@Query("page") String page);

    //@@7.19根据条件查询用户安防设备列表
    @GET("getNeedSecurity")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNeedSecurity(@Query("userId") String userId, @Query("privilege") String privilege,
                                          @Query("page") String page,@Query("areaId") String areaId,
                                          @Query("placeTypeId") String placeTypeId);

    //添加烟感
    @FormUrlEncoded
    @POST("addNFC")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> addNFC(@Field("userId") String userId, @Field("privilege") String privilege,
                                        @Field("smokeName") String smokeName, @Field("uid") String uid,
                                          @Field("address") String address, @Field("longitude") String longitude,
                                          @Field("latitude") String latitude,
                                          @Field("deviceType") String deviceType,@Field("areaId") String areaId,
                                        @Field("producer") String producer,
                                        @Field("makeTime") String makeTime,@Field("workerPhone") String workerPhone,
                                        @Field("makeAddress") String makeAddress);

    //巡检隐患上报
    @FormUrlEncoded
    @POST("uploadHiddenDanger")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> uploadHiddenDanger(@Field("title") String title, @Field("address") String address,
                                        @Field("managers") String managers, @Field("workerId") String workerId,
                                        @Field("areaId") String areaId, @Field("desc") String desc,
                                        @Field("imgs") String imgs);

    //添加巡检项目
    @FormUrlEncoded
    @POST("addNFCInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> addNFCInfo(@Field("userId") String userId, @Field("privilege") String privilege,
                                        @Field("smokeName") String smokeName, @Field("uid") String uid,
                                        @Field("address") String address, @Field("longitude") String longitude,
                                        @Field("latitude") String latitude,
                                        @Field("deviceType") String deviceType,@Field("areaId") String areaId,
                                        @Field("producer") String producer,
                                        @Field("makeTime") String makeTime,@Field("memo") String memo,
                                        @Field("makeAddress") String makeAddress,@Field("pid") String pid,
                                        @Field("photo1") String photo1);

    //修改巡检项目
    @FormUrlEncoded
    @POST("updateItemInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<ConfireFireModel> updateItemInfo(@Field("userId") String userId,
                                            @Field("smokeName") String smokeName, @Field("uid") String uid,
                                            @Field("address") String address,
                                            @Field("deviceType") String deviceType,@Field("memo") String memo);

    //获取NFC
    @GET("getNFCInfo")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getNFCInfo(@Query("userId") String userId, @Query("areaId") String areaId,
                                     @Query("page") String page,@Query("period") String period,
                                     @Query("devicetype") String devicetype,@Query("devicestate") String devicestate);

    //获取电气设备切换设备
    @GET("getEleNeedHis")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> getEleNeedHis(@Query("smokeMac") String smokeMac,@Query("page") String page);

    //确认报警上报
    @GET("makeSureAlarm")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> makeSureAlarm(@Query("userId") String userId,@Query("smokeMac") String smokeMac,@Query("alarmType") String alarmType);

    //处理报警提交
    @GET("submitOrder")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> submitOrder(@Query("userId") String userId,@Query("smokeMac") String smokeMac
            ,@Query("alarmTruth") String alarmTruth,@Query("dealDetail") String dealDetail
            ,@Query("imagePath") String imagePath,@Query("videoPath") String videoPath);

    //设备消音
    @GET("ackNB_IOT_Control")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<HttpError> ackNB_IOT_Control(@Query("userId") String userId,@Query("smokeMac") String smokeMac,@Query("eleState") String eleState);

    @Multipart
    @POST("UploadFileAction")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Observable<Result<String>> uploadImege(@Part("partList") List<MultipartBody.Part> partList);

    @GET("nanjing_jiade_cancel")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Call<HttpError> nanjing_jiade_cancel(@Query("imeiValue") String imeiValue,@Query("deviceType") String deviceType,@Query("cancelState") String cancelState);//cancelState 1 单次消音 2 连续消音

    @GET("ackNB_IOT_Control")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Call<HttpError> NB_IOT_Control(@Query("userId") String userId,@Query("smokeMac") String smokeMac,@Query("eleState") String eleState);

    @GET("EasyIot_erasure_control")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Call<HttpError> EasyIot_erasure_control(@Query("userId") String userId,@Query("devSerial") String devSerial,@Query("appId") String appId);

    @GET("cancelSound")
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    Call<HttpError> cancelSound(@Query("repeaterMac") String repeaterMac);
}

