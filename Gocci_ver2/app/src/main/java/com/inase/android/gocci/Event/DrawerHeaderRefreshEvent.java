package com.inase.android.gocci.Event;

/**
 * Created by kinagafuji on 15/05/04.
 */
public class DrawerHeaderRefreshEvent {

    public String refreshName;
    public String refreshPicture;
    public String refreshBackground;
    public int refreshFollower;
    public int refreshFollowee;
    public int refreshCheer;

    public DrawerHeaderRefreshEvent(String name, String picture, String background, int follower, int followee, int cheer) {
        super();
        this.refreshName = name;
        this.refreshPicture = picture;
        this.refreshBackground = background;
        this.refreshFollower = follower;
        this.refreshFollowee = followee;
        this.refreshCheer = cheer;
    }
}
