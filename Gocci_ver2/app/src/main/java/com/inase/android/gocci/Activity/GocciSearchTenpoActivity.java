package com.inase.android.gocci.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Session;
import com.hatenablog.shoma2da.eventdaterecorderlib.EventDateRecorder;
import com.inase.android.gocci.Application.Application_Gocci;
import com.inase.android.gocci.Event.BusHolder;
import com.inase.android.gocci.Event.DrawerHeaderRefreshEvent;
import com.inase.android.gocci.Event.SearchKeywordPostEvent;
import com.inase.android.gocci.R;
import com.inase.android.gocci.View.DrawerProfHeader;
import com.inase.android.gocci.common.Const;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;

import org.apache.http.Header;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class GocciSearchTenpoActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private CardView search_card;

    private SearchView mSearchView;

    private double mLat = 0.0;
    private double mLon = 0.0;

    private Drawer.Result result;

    private Application_Gocci gocci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gocci_search_tenpo);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.inflateMenu(R.menu.search);

        mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("お店を検索する");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchWord) {
                if (mLat != 0.0) {
                    BusHolder.get().post(new SearchKeywordPostEvent(searchWord, mLat, mLon));
                    mSearchView.clearFocus();
                } else {
                    Toast.makeText(GocciSearchTenpoActivity.this, "現在地が読み込めず、検索に失敗しました。", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmartLocation.with(GocciSearchTenpoActivity.this).location().oneFix().start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        if (location != null) {
                            mLat = location.getLatitude();
                            mLon = location.getLongitude();
                            Log.e("とったどー", "検索に使うよ");
                        } else {
                            Log.e("からでしたー", "locationupdated");
                        }
                    }
                });
            }
        });

        search_card = (CardView) findViewById(R.id.search_card);
        search_card.setCardElevation(4);
        search_card.setRadius(4);

        gocci = (Application_Gocci)getApplication();
        View header = new DrawerProfHeader(this, gocci.getMyName(), gocci.getMypicture(), gocci.getMyBackground(), gocci.getMyFollower(), gocci.getMyFollowee(), gocci.getMyCheer());

        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("タイムライン").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withCheckable(false),
                        new PrimaryDrawerItem().withName("ライフログ").withIcon(GoogleMaterial.Icon.gmd_event).withIdentifier(2).withCheckable(false),
                        new PrimaryDrawerItem().withName("お店を検索する").withIcon(GoogleMaterial.Icon.gmd_explore).withIdentifier(3).withCheckable(false),
                        new PrimaryDrawerItem().withName("マイプロフィール").withIcon(GoogleMaterial.Icon.gmd_person).withIdentifier(4).withCheckable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("アプリに関する要望を送る").withCheckable(false).withIdentifier(5),
                        new SecondaryDrawerItem().withName("利用規約とポリシー").withCheckable(false).withIdentifier(6),
                        new SecondaryDrawerItem().withName("ログアウト").withCheckable(false).withIdentifier(7)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Handler handler = new Handler();
                                handler.postDelayed(new timelineClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Handler handler = new Handler();
                                handler.postDelayed(new lifelogClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 4) {
                                Handler handler = new Handler();
                                handler.postDelayed(new myprofClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 5) {
                                Handler handler = new Handler();
                                handler.postDelayed(new adviceClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 6) {
                                Handler handler = new Handler();
                                handler.postDelayed(new policyClickHandler(), 500);
                            } else if (drawerItem.getIdentifier() == 7) {
                                Handler handler = new Handler();
                                handler.postDelayed(new logoutClickHandler(), 500);
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(2)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Subscriberとして登録する
        BusHolder.get().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Subscriberの登録を解除する
        BusHolder.get().unregister(this);
    }

    @Subscribe
    public void subscribe(DrawerHeaderRefreshEvent event) {
        View refreshHeader = new DrawerProfHeader(this, event.refreshName, event.refreshPicture, event.refreshBackground,
                event.refreshFollower, event.refreshFollowee, event.refreshCheer);
        result.setHeader(refreshHeader);
        result.setSelectionByIdentifier(3);
    }

    class timelineClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciTimelineActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class lifelogClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciLifelogActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class myprofClickHandler implements Runnable {
        public void run() {
            Intent intent = new Intent(GocciSearchTenpoActivity.this, GocciMyprofActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class adviceClickHandler implements Runnable {
        public void run() {
            new MaterialDialog.Builder(GocciSearchTenpoActivity.this)
                    .title("アドバイスを送る")
                    .content("以下から当てはまるもの１つを選択してください。")
                    .items(R.array.single_choice_array)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            return true;
                        }
                    })
                    .positiveText("次へ進む")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);

                            if (dialog.getSelectedIndex() != -1) {
                                switch (dialog.getSelectedIndex()) {
                                    case 0:
                                        dialog.cancel();
                                        setNextDialog("ご要望");
                                        break;
                                    case 1:
                                        dialog.cancel();
                                        setNextDialog("苦情");
                                        break;
                                    case 2:
                                        dialog.cancel();
                                        setNextDialog("ご意見");
                                        break;
                                }
                            } else {
                                dialog.show();
                                Toast.makeText(GocciSearchTenpoActivity.this, "一つを選択してください", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .show();
        }

        private void setNextDialog(final String string) {
            new MaterialDialog.Builder(GocciSearchTenpoActivity.this)
                    .title("アドバイスを送る")
                    .content("コメントを入力してください")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(string, null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        }
                    })
                    .positiveText("送信する")
                    .positiveColorRes(R.color.gocci_header)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            String message = dialog.getInputEditText().getText().toString();

                            if (!message.isEmpty()) {
                                postSignupAsync(GocciSearchTenpoActivity.this, string, message);
                            } else {
                                Toast.makeText(GocciSearchTenpoActivity.this, "文字を入力してください", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).show();
        }

        private void postSignupAsync(final Context context, final String category, final String message) {
            final AsyncHttpClient httpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("user_name", gocci.getLoginName());
            params.put("picture", gocci.getLoginPicture());
            httpClient.post(context, Const.URL_SIGNUP_API, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e("サインアップ成功", "status=" + statusCode);
                    postAsync(context, httpClient, category, message);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "サインアップに失敗しました", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void postAsync(final Context context, AsyncHttpClient client, String category, String message) {
            RequestParams sendParams = new RequestParams();
            sendParams.put("select_support", category);
            sendParams.put("content", message);
            client.post(context, Const.URL_ADVICE_API, sendParams, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "ご協力ありがとうございました！", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "送信に失敗しました", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    class policyClickHandler implements Runnable {
        public void run() {
            Uri uri = Uri.parse("http://inase-inc.jp/rules/");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    class logoutClickHandler implements Runnable {
        public void run() {
            new MaterialDialog.Builder(GocciSearchTenpoActivity.this)
                    .title("確認")
                    .content("本当にログアウトしますか？")
                    .positiveText("はい")
                    .negativeText("いいえ")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);

                            SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                            String judge = pref.getString("judge", "no judge");

                            switch (judge) {
                                case "facebook":
                                    Session session = Session.getActiveSession();
                                    if (session != null) {
                                        if (!session.isClosed()) {
                                            session.closeAndClearTokenInformation();
                                            //clear your preferences if saved
                                        }
                                    } else {
                                        session = new Session(GocciSearchTenpoActivity.this);
                                        Session.setActiveSession(session);

                                        session.closeAndClearTokenInformation();
                                        //clear your preferences if saved
                                    }
                                    break;
                                case "twitter":
                                    Twitter.logOut();
                                    break;
                                case "auth":
                                    break;
                                default:
                                    break;
                            }

                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();

                            EventDateRecorder recorder = EventDateRecorder.load(GocciSearchTenpoActivity.this, "use_first_gocci_android");
                            recorder.clear();

                            Intent intent = new Intent(GocciSearchTenpoActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    }).show();
        }
    }
}
