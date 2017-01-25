package com.bionic.andr.mvp.login;

import com.bionic.andr.api.data.Weather;

import rx.Observable;

/**  */
public interface EnterCityView {

    Observable<CharSequence> cityChange();
    Observable<Void> submit();

    void onValidationCheck(boolean valid);
    void showProgress(boolean show);
    void onWeatherLoaded(Weather weather);
    void onError(int status);
}
