package com.bionic.andr.mvp.enter_city;

import com.bionic.andr.api.OpenWeatherApi;
import com.bionic.andr.dagger.AppComponent;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**  */
public class EnterCityPresenter {
    private EnterCityView view;

    @Inject
    OpenWeatherApi api;

    public EnterCityPresenter(AppComponent component) {
        component.inject(this);
    }

    public void attach(EnterCityView view) {
        this.view = view;
        Observable<CharSequence> validation = view.cityChange().doOnNext(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence cityName) {
                validate(cityName);
            }
        });

        view.submit()
                .withLatestFrom(validation,
                        (click, cityName) -> cityName)
                .subscribe(city -> {
                    loadWeather(city);
                });
    }

    public void detach() {
        this.view = null;
    }

    private void loadWeather(CharSequence cityName) {
        view.showProgress(true);
        api.getWeatherByCity(cityName.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weather -> {
                    view.showProgress(false);
                    view.onWeatherLoaded(weather);
                },
                t -> {
                    view.showProgress(false);
                    HttpException ex = (HttpException) t;
                    view.onError(ex.code());
                });
    }

    private void validate(CharSequence cityName) {
        Observable.just(cityName)
                .map(city -> isCityValid(city))
                .subscribe(valid -> {
                    view.onValidationCheck(valid);
                });
    }

    private boolean isCityValid(CharSequence cityName) {
        return (cityName.length() > 1);
    }

}
