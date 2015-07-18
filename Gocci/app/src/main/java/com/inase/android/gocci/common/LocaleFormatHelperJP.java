package com.inase.android.gocci.common;

/**
 * Created by kinagafuji on 15/07/02.
 */
public class LocaleFormatHelperJP extends LocaleFormatHelper {
    @Override
    public String formatPrice(String price) {
        return price + "円";
    }

    @Override
    public String formatData(int data) {
        return null;
    }

    @Override
    public String formatValidation(int validation) {
        return null;
    }
}
