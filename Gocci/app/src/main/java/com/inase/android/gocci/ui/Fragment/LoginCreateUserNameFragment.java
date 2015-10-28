package com.inase.android.gocci.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.datasource.repository.LoginRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.model.User;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCaseImpl;
import com.inase.android.gocci.presenter.ShowUserLoginPresenter;
import com.inase.android.gocci.ui.activity.CameraActivity;
import com.inase.android.gocci.ui.activity.CameraPreviewAlreadyExistActivity;
import com.inase.android.gocci.ui.activity.TutorialActivity;
import com.inase.android.gocci.ui.activity.WebViewActivity;
import com.inase.android.gocci.utils.SavedData;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kinagafuji on 15/08/05.
 */
public class LoginCreateUserNameFragment extends Fragment implements FABProgressListener, ShowUserLoginPresenter.ShowUserLogin {

    private ShowUserLoginPresenter mPresenter;

    @Bind(R.id.username_textInput)
    TextInputLayout mUsernameTextInput;
    @Bind(R.id.created_username)
    TextView mCreatedUsername;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.fab_progress_circle)
    FABProgressCircle mFabProgressCircle;

    @OnClick(R.id.fab)
    public void fab() {
        if (mUsernameTextInput.getEditText().getText().length() != 0) {
            mUsernameTextInput.setError("");
            mPresenter.loginUser(Const.LOGIN_SIGNUP,
                    Const.getAuthSignupAPI(mUsernameTextInput.getEditText().getText().toString(), Build.VERSION.RELEASE, Build.MODEL, SavedData.getRegId(getActivity())));
        } else {
            Toast.makeText(getActivity(), getString(R.string.please_input_username), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rule)
    public void rule() {
        WebViewActivity.startWebViewActivity(0, getActivity());
    }

    @OnClick(R.id.policy)
    public void policy() {
        WebViewActivity.startWebViewActivity(1, getActivity());
    }

    public static LoginCreateUserNameFragment newInstance() {
        LoginCreateUserNameFragment pane = new LoginCreateUserNameFragment();
        return pane;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository();
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl);
        mPresenter.setShowUserLoginView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.view_tutorial4, container, false);
        ButterKnife.bind(this, rootView);

        mUsernameTextInput.setErrorEnabled(true);
        mUsernameTextInput.getEditText().setHintTextColor(getResources().getColor(R.color.namegrey));
        mUsernameTextInput.getEditText().setHighlightColor(getResources().getColor(R.color.namegrey));
        mUsernameTextInput.getEditText().setTextColor(getResources().getColor(R.color.namegrey));

        mCreatedUsername.setAlpha(0);

        mFabProgressCircle.attachListener(this);

        return rootView;
    }

    @Override
    public void onFABProgressAnimationEnd() {
        mCreatedUsername.setText(SavedData.getServerName(getActivity()) + "さん");
        mFabProgressCircle.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFabProgressCircle.setVisibility(View.INVISIBLE);
            }
        }).setStartDelay(200);
        mUsernameTextInput.animate().alpha(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mUsernameTextInput.setVisibility(View.INVISIBLE);
                startVisibleUsername();
            }
        }).setStartDelay(200);
    }

    private void startVisibleUsername() {
        mCreatedUsername.animate().alphaBy(100).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                TutorialActivity activity = (TutorialActivity) getActivity();
                activity.mPager.setCurrentItem(4, true);
            }
        }).setStartDelay(200);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showLoading() {
        mFabProgressCircle.show();
        mFab.setClickable(false);
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showResult(int api, User user) {
        if (api == Const.LOGIN_SIGNUP) {
            if (user.getCode() == 200) {
                SavedData.setWelcome(getActivity(), user.getUserName(), user.getProfileImg(), String.valueOf(user.getUserId()), user.getIdentityId(), user.getBadgeNum());
                Application_Gocci.GuestInit(getActivity(), user.getIdentityId(), user.getToken(), String.valueOf(user.getUserId()));
                SavedData.setFlag(getActivity(), 0);
                mFabProgressCircle.beginFinalAnimation();
            } else {
                Toast.makeText(getActivity(), user.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showNoResult(int api) {
        if (api == Const.LOGIN_SIGNUP) {
            mFab.setClickable(true);
            mFabProgressCircle.hide();
            mUsernameTextInput.setError(getString(R.string.multiple_username));
        }
    }

    @Override
    public void showError() {
        mFab.setClickable(true);
        mFabProgressCircle.hide();
        Toast.makeText(getActivity(), getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
    }

    private void enableLocationAndStorage() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission_group.LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission_group.STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission_group.LOCATION)) {

                Toast.makeText(getActivity(), "権限よこせや", Toast.LENGTH_SHORT).show();

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission_group.LOCATION, Manifest.permission_group.STORAGE},
                        45);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 45: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getActivity(), "なんでくれないのよ.....", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
