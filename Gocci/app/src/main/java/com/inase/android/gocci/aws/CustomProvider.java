package com.inase.android.gocci.aws;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;

/**
 * Created by kinagafuji on 15/07/06.
 */
public class CustomProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    private static final String poolId = "us-east-1:a8cc1fdb-92b1-4586-ba97-9e6994a43195";

    private String mIdentityId;
    private String mToken;

    public CustomProvider(String identityId, String token) {
        super(null, poolId, Regions.US_EAST_1);

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
        return "test.login.gocci";
    }

    @Override
    public String getIdentityId() {
        return mIdentityId;
    }
}
