package com.smart.cloud.fire.activity.AllSmoke;

import com.smart.cloud.fire.base.presenter.BasePresenter;
import com.smart.cloud.fire.base.ui.BaseFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.global.SmokeSummary;
import com.smart.cloud.fire.mvp.Alarm.AlarmView;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpAreaResult;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.AllDevFragment.AllDevFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.OffLineDevFragment.OffLineDevFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragment;
import com.smart.cloud.fire.mvp.fragment.ShopInfoFragment.ShopInfoFragmentView;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;



/**
 * Created by Rain on 2017/7/15.
 */
public class AllSmokePresenter extends BasePresenter<AllSmokeView> {


    public AllSmokePresenter(AllSmokeView view){
        attachView(view);
    }
    public void getSmokeSummary(String userId, String privilege, String parentId, String areaId, String placeTypeId, String devType){
        Observable mObservable = apiStoresFire.getDevSummary(userId,privilege,parentId,areaId,placeTypeId,devType);
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<SmokeSummary>() {
            @Override
            public void onSuccess(SmokeSummary model) {
                int resultCode = model.getErrorCode();
                if(resultCode==0){
                    mvpView.getSmokeSummary(model);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
            }

            @Override
            public void onCompleted() {
            }
        }));
    }

    public void getAllSmoke(String userId, String privilege, String page,String devtype, final List<Smoke> list, final int type, boolean refresh){
        if(!refresh){
            mvpView.showLoading();
        }
        Observable mObservable;
//        if(privilege.equals("1")){
//            mObservable = apiStores1.getAllSmoke(userId,privilege,page);//@@12.20
//        }else{
            mObservable = apiStores1.getNeedDev(userId,privilege,"",page,"","1");
//        }

        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                int result=model.getErrorCode();
                if(result==0){
                    List<Smoke> smokeList = model.getSmoke();
                    if(type==1){
                        if(list==null||list.size()==0){
                            mvpView.getDataSuccess(smokeList,false);
                        }else if(list!=null&&list.size()>=20){
                            mvpView.onLoadingMore(smokeList);
                        }
                    }
                }else{
                    List<Smoke> mSmokeList = new ArrayList<>();
                    mvpView.getDataSuccess(mSmokeList,false);
                    mvpView.getDataFail("?????????");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if(type!=1){
                    List<Smoke> mSmokeList = new ArrayList<>();
                    mvpView.getDataSuccess(mSmokeList,false);
                }
                mvpView.getDataFail("????????????");
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }

    public void getAllDev(String userId, String privilege, String page,String devtype,String state,String areaid,final List<Smoke> list, final int type, boolean refresh){
        if(!refresh){
            mvpView.showLoading();
        }
        Observable mObservable;
//        if(privilege.equals("1")){
//            mObservable = apiStores1.getAllSmoke(userId,privilege,page);//@@12.20
//        }else{
        mObservable = apiStoresFire.getNeedDev2(userId,privilege,areaid,page,state,devtype);
//        }

        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                int result=model.getErrorCode();
                if(result==0){
                    List<Smoke> smokeList = model.getSmoke();
                    if(type==1){
                        if(page.equals("1")){
                            mvpView.getDataSuccess(smokeList,refresh);
                        }else if(list!=null&&list.size()>=20){
                            mvpView.onLoadingMore(smokeList);
                        }
                    }
                }else{
                    List<Smoke> mSmokeList = new ArrayList<>();
                    mvpView.getDataSuccess(mSmokeList,refresh);
                    mvpView.getDataFail("?????????");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if(type!=1){
                    List<Smoke> mSmokeList = new ArrayList<>();
                    mvpView.getDataSuccess(mSmokeList,false);
                }
                mvpView.getDataFail("????????????");
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }



    public void unSubscribe(String type){
        mvpView.hideLoading();
        onUnsubscribe();
        mvpView.unSubscribe(type);
    }
    private CompositeSubscription mCompositeSubscription;//??????subseription
    //RXjava????????????????????????????????????
    public void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription=null;
        }
    }
    /**
     * ??????????????????????????????
     * type:1???????????????????????????2????????????????????????
     * @param userId
     * @param privilege
     * @param type
     */
    public void getPlaceTypeId(String userId, String privilege, final int type){
        Observable mObservable = null;
        if(type==1){
            mObservable= apiStores1.getPlaceTypeId(userId,privilege,"").map(new Func1<HttpError,ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(HttpError o) {
                    return o.getPlaceType();
                }
            });
        }else{
            mObservable= apiStores1.getAreaId(userId,privilege,"").map(new Func1<HttpAreaResult,ArrayList<Object>>() {
                @Override
                public ArrayList<Object> call(HttpAreaResult o) {
                    return o.getSmoke();
                }
            });
        }
        //?????????shopinfofragment??????????????????
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<ArrayList<Object>>() {
            @Override
            public void onSuccess(ArrayList<Object> model) {
                if(model!=null&&model.size()>0){
                    mvpView.getAreaType(model,type);
                }else{
                    mvpView.getAreaTypeFail("?????????",type);
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                mvpView.getAreaTypeFail("????????????",type);
            }
            @Override
            public void onCompleted() {
            }
        }));
    }
    public void getNeedLossSmoke(String userId, String privilege,String parentId, String areaId, String placeTypeId, final String page,String devType, boolean refresh, final int type, final List<Smoke> list, final OffLineDevFragment offLineDevFragment){
        if(!refresh){
            mvpView.showLoading();
        }
        Observable mObservable = apiStores1.getNeedLossDev(userId,privilege,parentId,areaId,page,placeTypeId,devType);
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                int result=model.getErrorCode();
                if(result==0){
                    List<Smoke> smokeList = model.getSmoke();
                    if(type==1){
                        if(list==null||list.size()==0){
                            offLineDevFragment.getDataSuccess(smokeList,false);
                        }else if(list!=null&&list.size()>=20){
                            offLineDevFragment.onLoadingMore(smokeList);
                        }
                    }else{
                        if(list==null||list.size()==0){
                            offLineDevFragment.getDataSuccess(smokeList,true);
                        }else if(list!=null&&list.size()>=20){
                            offLineDevFragment.onLoadingMore(smokeList);
                        }
//                        offLineDevFragment.getDataSuccess(smokeList,true);
                    }
                }else{
//                    List<Smoke> mSmokeList = new ArrayList<>();
//                    offLineDevFragment.getDataSuccess(mSmokeList,false);
                    offLineDevFragment.getDataFail("???????????????");
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                offLineDevFragment.getDataFail("????????????");
            }

            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }

    public void getNeedSmoke(String userId, String privilege, final String page, String parentId, String areaId, String placeTypeId, String devType){
        mvpView.showLoading();
        Observable mObservable = apiStores1.getNeedDev2(userId,privilege,parentId,areaId,page,placeTypeId,devType);
        addSubscription(mObservable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                if(model!=null){
                    int errorCode = model.getErrorCode();
                    if(errorCode==0){
                        List<Smoke> smokes = model.getSmoke();
                        if(page.equals("1")){
                            mvpView.getDataSuccess(smokes,true);
                        }else{
                            mvpView.onLoadingMore(smokes);
                            mvpView.showLoading();
                        }
//                        allDevFragment.getDataSuccess(smokes,true);
                    }else {
                        mvpView.getDataFail("?????????");
                        List<Smoke> smokes = new ArrayList<Smoke>();//@@4.27
                        mvpView.getDataSuccess(smokes,true);//@@4.27
                    }
                }else{
                    mvpView.getDataFail("?????????");
                    List<Smoke> smokes = new ArrayList<Smoke>();//@@4.27
                    mvpView.getDataSuccess(smokes,true);//@@4.27
                }
            }
            @Override
            public void onFailure(int code, String msg) {
                mvpView.getDataFail("????????????");
            }
            @Override
            public void onCompleted() {
                mvpView.hideLoading();
            }
        }));
    }
    @Override
    public void getShop(ShopType shopType) {
        super.getShop(shopType);
        mvpView.getChoiceShop(shopType);
    }

    @Override
    public void getArea(Area area) {
        super.getArea(area);
        mvpView.getChoiceArea(area);
    }
}
