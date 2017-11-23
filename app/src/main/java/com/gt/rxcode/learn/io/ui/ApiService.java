package com.gt.rxcode.learn.io.ui;

import com.gt.rxcode.learn.io.ui.responses.WSAppResponse;

import java.util.Map;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface ApiService {

    @POST(ApiConstants.URL_APP)
    Observable<WSAppResponse> wsApp(@QueryMap Map<String, Object> map);

}
