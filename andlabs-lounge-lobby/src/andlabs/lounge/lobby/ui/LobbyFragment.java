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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import andlabs.lounge.lobby.LoungeLobbyCallback;
import andlabs.lounge.lobby.LoungeLobbyController;
import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.mock.TestData;
import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.util.Utils;
import andlabs.lounge.lobby.util.parser.PlayParser;
import andlabs.lounge.lobby.util.parser.PlayParser.PlayListener;
import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.util.Ln;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private ExpandableListView mLobbyList;
    private LobbyListAdapter mLobbyAdapter;
    private ListView mHostList;
    private HostGameAdapter mHostAdapter;
    private View mStaticBeacon;
    private View mPulseBeacon;

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
                        mLobbyAdapter.setJoinedGames(pGames);
                        mLobbyAdapter.notifyDataSetChanged();
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
                        mLobbyAdapter.setOpenGames(pGames);
                        mLobbyAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

    };


    @Override
    public View onCreateView(final LayoutInflater pLayoutInflater, ViewGroup pViewGroup, Bundle pBundle) {
        View view = pLayoutInflater.inflate(R.layout.fragment_lobby, pViewGroup, false);
        mLobbyList = (ExpandableListView) view.findViewById(R.id.list);
        mLobbyList.setDividerHeight(0);
        mLobbyList.setDivider(null);

        mLobbyAdapter = new LobbyListAdapter(getActivity());
        mLobbyList.setAdapter(mLobbyAdapter);
        mLobbyAdapter.setLoungeController(mLoungeLobbyController);

        mLobbyAdapter.setJoinedGames(TestData.getJoinedGames());
        mLobbyAdapter.setOpenGames(TestData.getOpenGames());
        mLobbyList.setOnChildClickListener(this); // TODO make consistent with host drawer

        // final SlidingDrawer drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer);
        //
        // mStaticBeacon = view.findViewById(R.id.ic_lobby_host_static_pulse);
        // mPulseBeacon = view.findViewById(R.id.ic_lobby_host_pulse);
        // startAnimatingHostMode();

        // drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
        //
        // @Override
        // public void onDrawerClosed() {
        // stopAnimatingHostMode();
        // }
        // });


        mHostList = (ListView) view.findViewById(R.id.installed_games);
        mHostAdapter = new HostGameAdapter(getActivity());
        mHostList.setAdapter(mHostAdapter);
        mHostList.setOnItemClickListener(mHostAdapter); // TODO make consistent with lobby list

        view.findViewById(R.id.btn_host).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mLoungeLobbyController.openMatch(mHostAdapter.getSelectedItemPackage(), mHostAdapter.getSelectedItemName());

            }
        });

        return view;
    }


    private void queryPlay(List<Game> games) {
        Ln.v("games = %s", games);
        final PlayParser parser = PlayParser.getInstance(getActivity());

        for (int index = 0; index < games.size(); index++) {
            final Game element = games.get(index);
            parser.queryPlay(getPackageNameFromGameId(element.gameID));
        }

        parser.addListener(new PlayListener() {

            @Override
            public void onPlayResult(Map<String, Drawable> pResults) {
                mLobbyAdapter.notifyDataSetChanged();
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
        Ln.v(" groupPosition = %d, childPosition = %d", groupPosition, childPosition);
        Game game = (Game) mLobbyAdapter.getGroup(groupPosition);
        Match match = (Match) mLobbyAdapter.getChild(groupPosition, childPosition);
        Ln.d(" game = %s, match = %s", game, match);

        // If the game is open...
        if ((Integer) v.getTag() == LobbyListAdapter.TYPE_OPENGAME) {
            final String gameID = game.gameID;
            if (Utils.isGameInstalled(getActivity(), gameID)) { // ...and installed, join it...
                mLoungeLobbyController.joinMatch(gameID, match.matchID);
            } else { // ...otherwise, open the play store
                Utils.openPlay(getActivity(), gameID);
            }
        } else {
            if ("running".equalsIgnoreCase(match.status)) {
                // If the game is already running, and you are involved
                Intent intent = Utils.launchGameApp(getActivity(), game.gameID, match);
                if (intent != null) {
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            } else {
                Toast.makeText(getActivity(), "Game not started yet", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
}
