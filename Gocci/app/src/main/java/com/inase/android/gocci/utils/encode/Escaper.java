package com.inase.android.gocci.utils.encode;

/**
 * Created by kinagafuji on 15/11/22.
 */
public interface Escaper {
    public String escape(String string);

    public Appendable escape(Appendable out);
}
