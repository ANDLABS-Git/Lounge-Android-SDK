/*
 * Copyright (C) 2012, 2013 ANDLABS GmbH. All rights reserved.
 *
 * www.lounge.andlabs.com
 * lounge@andlabs.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package andlabs.lounge.lobby.ui;

import andlabs.lounge.lobby.R;
import andlabs.lounge.util.Ln;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LoungeActivity extends FragmentActivity implements OnPageChangeListener {

    private static final int ALPHA_OFF = (int) (255 * 0.3f);
    private static final int LOBBY = 0;
    private static final int CHAT = 1;
    private static final int STATS = 2;
    private static final int ABOUT = 3;

    private TextView mSectionLabel;
    private ViewPager mViewPager;
    private ImageView mLobbyIcon;
    private ImageView mStatsIcon;
    private ImageView mAboutIcon;
    private ImageView mChatIcon;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity);

        mSectionLabel = (TextView) findViewById(R.id.lobbySection);
        mAboutIcon = (ImageView) findViewById(R.id.ic_tab_about);
        mLobbyIcon = (ImageView) findViewById(R.id.ic_tab_lobby);
        mStatsIcon = (ImageView) findViewById(R.id.ic_tab_stat);
        mChatIcon = (ImageView) findViewById(R.id.ic_tab_chat);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 4;
            }


            @Override
            public Fragment getItem(int position) {
                Ln.d("ITEM " + position);
                switch (position) {
                    case LOBBY:
                        return new LobbyFragment();
                    case CHAT:
                        return new ChatFragment();
                    case STATS:
                        return new StatsFragment();
                    case ABOUT:
                        return new AboutFragment();
                }
                return null;
            }
        });
        onPageSelected(LOBBY);
    }


    public void onNavigationClicked(View v) {
        if (v.getId() == R.id.ic_tab_lobby) {
            mViewPager.setCurrentItem(LOBBY, true);
        } else if (v.getId() == R.id.ic_tab_chat) {
            mViewPager.setCurrentItem(CHAT, true);
        } else if (v.getId() == R.id.ic_tab_stat) {
            mViewPager.setCurrentItem(STATS, true);
        } else if (v.getId() == R.id.ic_tab_about) {
            mViewPager.setCurrentItem(ABOUT, true);
        }
    }


    @Override
    public void onPageSelected(int position) {
        Ln.d("page selected");
        switch (position) {
            case LOBBY:
                mSectionLabel.setText("Lobby");
                mLobbyIcon.setAlpha(255);
                mChatIcon.setAlpha(ALPHA_OFF);
                mStatsIcon.setAlpha(ALPHA_OFF);
                mAboutIcon.setAlpha(ALPHA_OFF);
                break;

            case CHAT:
                mSectionLabel.setText("Chat");
                mLobbyIcon.setAlpha(ALPHA_OFF);
                mChatIcon.setAlpha(255);
                mStatsIcon.setAlpha(ALPHA_OFF);
                mAboutIcon.setAlpha(ALPHA_OFF);
                break;

            case STATS:
                mSectionLabel.setText("Statistics");
                mLobbyIcon.setAlpha(ALPHA_OFF);
                mChatIcon.setAlpha(ALPHA_OFF);
                mStatsIcon.setAlpha(255);
                mAboutIcon.setAlpha(ALPHA_OFF);
                break;

            case ABOUT:
                mSectionLabel.setText("About");
                mLobbyIcon.setAlpha(ALPHA_OFF);
                mChatIcon.setAlpha(ALPHA_OFF);
                mStatsIcon.setAlpha(ALPHA_OFF);
                mAboutIcon.setAlpha(255);
                break;
            default:
                break;
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }


    @Override
    public void onPageScrolled(int position, float foo, int arg2) {
        // switch (position) {
        // case LOBBY:
        // mHeader.setBackgroundColor(Utils.ipc(this, R.color.orange,
        // R.color.green, foo));
        // break;
        // case CHAT:
        // mHeader.setBackgroundColor(Utils.ipc(this, R.color.green,
        // R.color.blue, foo));
        // break;
        // case STATS:
        // mHeader.setBackgroundColor(Utils.ipc(this, R.color.blue,
        // R.color.yellow, foo));
        // break;
        // }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
