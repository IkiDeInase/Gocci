package com.inase.android.gocci.common;

import java.util.Locale;

/**
 * Created by kinagafuji on 15/07/02.
 */
public abstract class LocaleFormatHelper {
    /*
    * 他言語対応のため、国別で継承して行う処理を分けていく
    * 値段・文字・日付・マイナス表記・バリデーション関連
     */
    private static final String location = Locale.getDefault().getCountry().toLowerCase();

    public abstract String formatPrice(String price);

    public abstract String formatData(int data);

    public abstract String formatValidation(int validation);

    public static final LocaleFormatHelper getInstance() {
        LocaleFormatHelper lfh;
        if (location.equals(Locale.JAPAN.getCountry().toLowerCase())) {
            lfh = new LocaleFormatHelperJP();
        } else {
            lfh = new LocaleFormatHelperUS();
        }
        return lfh;
    }

}
