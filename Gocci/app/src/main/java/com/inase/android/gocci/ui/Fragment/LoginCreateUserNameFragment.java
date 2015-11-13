package com.inase.android.gocci.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.inase.android.gocci.Application_Gocci;
import com.inase.android.gocci.R;
import com.inase.android.gocci.consts.Const;
import com.inase.android.gocci.datasource.repository.API3;
import com.inase.android.gocci.datasource.repository.CheckRegIdRepository;
import com.inase.android.gocci.datasource.repository.CheckRegIdRepositoryImpl;
import com.inase.android.gocci.datasource.repository.LoginRepository;
import com.inase.android.gocci.datasource.repository.LoginRepositoryImpl;
import com.inase.android.gocci.domain.executor.UIThread;
import com.inase.android.gocci.domain.usecase.CheckRegIdUseCase;
import com.inase.android.gocci.domain.usecase.CheckRegIdUseCaseImpl;
import com.inase.android.gocci.domain.usecase.UserLoginUseCase;
import com.inase.android.gocci.domain.usecase.UserLoginUseCaseImpl;
import com.inase.android.gocci.presenter.ShowUserLoginPresenter;
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
            API3.Util.AuthSignupLocalCode localCode = API3.Impl.getRepository().auth_signup_parameter_regex(mUsernameTextInput.getEditText().getText().toString(), "android", SavedData.getVersionName(getActivity()), Build.MODEL, SavedData.getRegId(getActivity()));
            if (localCode == null) {
                mPresenter.loginUser(Const.APICategory.AUTH_SIGNUP,
                        API3.Util.getAuthSignupAPI(mUsernameTextInput.getEditText().getText().toString(), Build.VERSION.RELEASE, SavedData.getVersionName(getActivity()), Build.MODEL, SavedData.getRegId(getActivity())));
            } else {
            Toast.makeText(getActivity(), API3.Util.authSignupLocalErrorMessageTable(localCode), Toast.LENGTH_SHORT).show();
            }
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
        API3 api3Impl = API3.Impl.getRepository();
        LoginRepository loginRepositoryImpl = LoginRepositoryImpl.getRepository(api3Impl);
        CheckRegIdRepository checkRegIdRepositoryImpl = CheckRegIdRepositoryImpl.getRepository(api3Impl);
        UserLoginUseCase userLoginUseCaseImpl = UserLoginUseCaseImpl.getUseCase(loginRepositoryImpl, UIThread.getInstance());
        CheckRegIdUseCase checkRegIdUseCaseImpl = CheckRegIdUseCaseImpl.getUseCase(checkRegIdRepositoryImpl, UIThread.getInstance());
        mPresenter = new ShowUserLoginPresenter(userLoginUseCaseImpl, checkRegIdUseCaseImpl);
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
                enableLocationAndStorage();
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
    public void onCheckSuccess() {

    }

    @Override
    public void onCheckFailureCausedByLocalError(String id, String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckFailureCausedByGlobalError(API3.Util.GlobalCode globalCode) {
        Application_Gocci.resolveOrHandleGlobalError(Const.APICategory.AUTH_CHECK, globalCode);
    }

    @Override
    public void showResult(Const.APICategory api) {
        mFabProgressCircle.beginFinalAnimation();
    }

    @Override
    public void showNoResultCausedByGlobalError(Const.APICategory api, API3.Util.GlobalCode globalCode) {
        mFab.setClickable(true);
        mFabProgressCircle.hide();
        mUsernameTextInput.setError(API3.Util.globalErrorMessageTable(globalCode));
        Application_Gocci.resolveOrHandleGlobalError(api, globalCode);
    }

    @Override
    public void showNoResultCausedByLocalError(Const.APICategory api, String errorMessage) {
        mFab.setClickable(true);
        mFabProgressCircle.hide();
        mUsernameTextInput.setError(errorMessage);
    }

    private void enableLocationAndStorage() {
        if (PermissionChecker.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE}, 45);
        } else {
            TutorialActivity activity = (TutorialActivity) getActivity();
            activity.mPager.setCurrentItem(4, true);
        }
    }
}
