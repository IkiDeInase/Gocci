package com.inase.android.gocci.Base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Property;
import android.view.View;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringListener;
/**
 * Created by kinagafuji on 15/09/07.
 */
public class Performer implements SpringListener {

    /**
     * The view to modify.
     */
    @Nullable
    protected View mTarget;
    /**
     * The property of the view to modify.
     */
    @NonNull
    protected Property<View, Float> mProperty;

    /**
     * Constructor. Note that a {@link View} must be specified by {@link #setTarget(View)}.
     *
     * @param property
     * 		the view property to modify.
     */
    public Performer(@NonNull Property<View, Float> property) {
        this(null, property);
    }

    /**
     * Constructor.
     *
     * @param target
     * 		the view to modify.
     * @param property
     * 		the view property to modify.
     */
    public Performer(@Nullable View target, @NonNull Property<View, Float> property) {
        this.mTarget = target;
        this.mProperty = property;
    }

    @Nullable
    public View getTarget() {
        return mTarget;
    }

    public void setTarget(@Nullable View target) {
        this.mTarget = target;
    }

    @NonNull
    public Property getProperty() {
        return mProperty;
    }

    public void setProperty(@NonNull Property<View, Float> property) {
        this.mProperty = property;
    }

    @Override
    public void onSpringUpdate(@NonNull Spring spring) {
        if (mProperty != null && mTarget != null) {
            mProperty.set(mTarget, (float) spring.getCurrentValue());
        }
    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }
}
