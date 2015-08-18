package com.inase.android.gocci.aws;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;
import com.inase.android.gocci.common.Const;

/**
 * Created by kinagafuji on 15/07/06.
 */
public class CustomProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    private String mIdentityId;
    private String mToken;

    public CustomProvider(String identityId, String token) {
        super(null, Const.IDENTITY_POOL_ID, Regions.US_EAST_1);

        mIdentityId = identityId;
        mToken = token;
    }

    @Override
    public String refresh() {
        // Override the existing token
        setToken(null);

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.

        update(mIdentityId, mToken);
        return mToken;
    }

    @Override
    public String getProviderName() {
        return Const.ENDPOINT_INASE;
    }

    @Override
    public String getIdentityId() {
        return mIdentityId;
    }
}
