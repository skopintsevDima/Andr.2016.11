package com.bionic.andr.mvp;

import com.bionic.andr.AndrApp;
import com.bionic.andr.R;
import com.bionic.andr.api.data.Weather;
import com.bionic.andr.mvp.login.EnterCityPresenter;
import com.bionic.andr.mvp.login.EnterCityView;
import com.bionic.andr.ui.ProgressDialogFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**  */
public class EnterCityActivity extends AppCompatActivity implements EnterCityView {
    private static final String TAG = EnterCityActivity.class.getSimpleName();

    private static final String PROGRESS_DIALOG_TAG = "dialog:progress";

    @BindView(R.id.city)
    EditText city;
    @BindView(R.id.submit)
    View submit;

    EnterCityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_city);
        ButterKnife.bind(this);

        presenter = new EnterCityPresenter(AndrApp.getAppComponent());
        presenter.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    @Override
    public Observable<CharSequence> cityChange() {
        return RxTextView.textChanges(city);
    }

    @Override
    public Observable<Void> submit() {
        return RxView.clicks(submit);
    }

    @Override
    public void onValidationCheck(boolean valid) {
        submit.setEnabled(valid);
    }

    @Override
    public void showProgress(boolean show) {
        FragmentManager fm = getSupportFragmentManager();
        ProgressDialogFragment f = (ProgressDialogFragment) fm.findFragmentByTag(PROGRESS_DIALOG_TAG);
        if (show) {
            if (f == null) {
                ProgressDialogFragment.newInstance("Loading")
                        .show(getSupportFragmentManager(), PROGRESS_DIALOG_TAG);
            }
        } else if (f != null) {
            f.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onWeatherLoaded(Weather weather) {
        Toast.makeText(getApplicationContext(), weather.getName() + " / " + weather.getTemp(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int status) {
        Toast.makeText(getApplicationContext(), "Error: " + status, Toast.LENGTH_SHORT).show();
    }
}
