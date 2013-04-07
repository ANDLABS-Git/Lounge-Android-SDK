/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
 *  lounge.andlabs.com
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import andlabs.lounge.lobby.LoungeLobbyCallback;
import andlabs.lounge.lobby.LoungeLobbyController;
import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.util.Utils;
import andlabs.lounge.lobby.util.parser.PlayParser;
import andlabs.lounge.lobby.util.parser.PlayParser.PlayListener;
import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class LobbyFragment extends Fragment implements OnChildClickListener {

    public static final String CATEGORY = "eu.andlabs.lounge";
    private SparseIntArray listPositions = new SparseIntArray();
    private ExpandableListView lobbyList;
    private LobbyListAdapter mAdapter;
    private ListView mHostList;
    private HostGameAdapter hostAdapter;
    private static final String TAG = "Lounge";
    private static final int GAMES = 0;

    LoungeLobbyController mLoungeLobbyController = new LoungeLobbyController();

    LoungeLobbyCallback mLoungeLobbyCallback = new LoungeLobbyCallback() {

        @Override
        public void onNewChatMessage(ChatMessage chatMsg) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onNewChatLog(List<ChatMessage> chatLog) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onChatDataUpdated(List<ChatMessage> data) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRunningGamesUpdate(final ArrayList<Game> pGames) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (pGames != null) {
                        queryPlay(pGames);
                        mAdapter.setJoinedGames(pGames);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

        @Override
        public void onOpenGamesUpdate(final ArrayList<Game> pGames) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (pGames != null) {
                        queryPlay(pGames);
                        mAdapter.setOpenGames(pGames);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

    };
    private PackageManager mPackageManager;
    private List<ResolveInfo> installedGames;
    private View mStaticBeacon;
    private View mPulseBeacon;

    @Override
    public View onCreateView(final LayoutInflater pLayoutInflater, ViewGroup pViewGroup, Bundle pBundle) {
        View view = pLayoutInflater.inflate(R.layout.fragment_lobby, pViewGroup, false);
        lobbyList = (ExpandableListView) view.findViewById(R.id.list);
        lobbyList.setDividerHeight(0);
        lobbyList.setDivider(null);

        mAdapter = new LobbyListAdapter(getActivity());

        // mAdapter.setJoinedGames(joinedGames);
        // mAdapter.setOpenGames(openGames);
        lobbyList.setAdapter(mAdapter);
        lobbyList.setOnChildClickListener(this);

//        final SlidingDrawer drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer);
//
//        mStaticBeacon = view.findViewById(R.id.ic_lobby_host_static_pulse);
//        mPulseBeacon = view.findViewById(R.id.ic_lobby_host_pulse);
//        startAnimatingHostMode();

        // drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
        //
        // @Override
        // public void onDrawerClosed() {
        // stopAnimatingHostMode();
        // }
        // });

        this.mHostList = (ListView) view.findViewById(R.id.installed_games);

        this.mPackageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(CATEGORY);
        this.installedGames = this.mPackageManager.queryIntentActivities(intent, 0);
        mAdapter.setInstalledGames(installedGames, mPackageManager);

        this.mHostList = (ListView) view.findViewById(R.id.installed_games);
        this.hostAdapter = new HostGameAdapter(installedGames, getActivity(), mPackageManager);
        this.mHostList.setAdapter(hostAdapter);
        this.mHostList.setOnItemClickListener(hostAdapter);

        view.findViewById(R.id.btn_host).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mLoungeLobbyController.openMatch(hostAdapter.getSelectedItemPackage(), hostAdapter.getSelectedItemName());

            }
        });

        return view;
    }

    private void queryPlay(List<Game> games) {
        final PlayParser parser = PlayParser.getInstance(getActivity());

        for (int index = 0; index < games.size(); index++) {
            final Game element = games.get(index);
            Log.d("game", element.toString());
            parser.queryPlay(getPackageNameFromGameId(element.gameID));
        }

        parser.addListener(new PlayListener() {

            @Override
            public void onPlayResult(Map<String, Drawable> pResults) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void startAnimatingHostMode() {
        mPulseBeacon.setVisibility(View.VISIBLE);
        mStaticBeacon.setVisibility(View.INVISIBLE);

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        mPulseBeacon.startAnimation(animation);
    }

    private void stopAnimatingHostMode() {
        mPulseBeacon.setVisibility(View.INVISIBLE);
        mStaticBeacon.setVisibility(View.VISIBLE);
    }

    private String getPackageNameFromGameId(String gameID) {

        if (gameID.contains("/")) {
            return gameID.split("/")[0];
        }

        return gameID;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLoungeLobbyController.bindServiceTo(getActivity());
    }

    @Override
    public void onResume() {
        mLoungeLobbyController.registerCallback(mLoungeLobbyCallback);
        super.onResume();
    }

    @Override
    public void onPause() {
        mLoungeLobbyController.unregisterCallback(mLoungeLobbyCallback);
        super.onPause();
    }

    @Override
    public void onStop() {
        mLoungeLobbyController.unbindServiceFrom(getActivity());
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Game game = (Game) mAdapter.getGroup(groupPosition);
        Match match = (Match) mAdapter.getChild(groupPosition, childPosition);

        if ((Integer) v.getTag() == LobbyListAdapter.TYPE_OPENGAME) { // TODO
                                                                      // Adopt
                                                                      // here
            mLoungeLobbyController.joinMatch(game.gameID, match.matchID);
            // Lounge.join(match.getMatchId(),game.getPgkName()); // join Game
        } else {
            // open joined game
            if (match.status.equalsIgnoreCase("running")) {
                Utils.launchGameApp(getActivity(), game.gameID, match);
            } else {
                Toast.makeText(getActivity(), "Game not started yet", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

}
