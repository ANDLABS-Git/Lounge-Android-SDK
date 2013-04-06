/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
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

import andlabs.lounge.lobby.LoungeLobbyCallback;
import andlabs.lounge.lobby.LoungeLobbyController;
import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.mock.TestData;
import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.util.Utils;
import andlabs.lounge.lobby.util.parser.PlayParser;
import andlabs.lounge.lobby.util.parser.PlayParser.PlayListener;
import andlabs.lounge.lobby.util.parser.PlayResult;
import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
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
                        mAdapter.setOpenGames(pGames);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

    };
    private PackageManager mPackageManager;
    private List<ResolveInfo> installedGames;


    @Override
    public View onCreateView(final LayoutInflater pLayoutInflater, ViewGroup pViewGroup, Bundle pBundle) {
        View view = pLayoutInflater.inflate(R.layout.fragment_lobby, pViewGroup, false);
        lobbyList = (ExpandableListView) view.findViewById(R.id.list);
        lobbyList.setDividerHeight(0);
        lobbyList.setDivider(null);

        ViewTreeObserver vto = lobbyList.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                lobbyList.setIndicatorBounds(lobbyList.getRight() - 40, lobbyList.getWidth());
            }
        });
        mAdapter = new LobbyListAdapter(getActivity());

        List<Game> joinedGames = TestData.getJoinedGames();
        List<Game> openGames = TestData.getOpenGames();

        final PlayParser parser = PlayParser.getInstance(getActivity());

        for (Game element : joinedGames) {
            parser.queryPlay(element.gameID);
        }

        parser.addListener(new PlayListener() {

            @Override
            public void onPlayResult(PlayResult pResult) {
                mAdapter.notifyDataSetChanged();
            }
        });

        // mAdapter.setJoinedGames(joinedGames);
        // mAdapter.setOpenGames(openGames);
        lobbyList.setAdapter(mAdapter);
        lobbyList.setOnChildClickListener(this);

        this.mHostList = (ListView) view.findViewById(R.id.installed_games);

        this.mPackageManager = getActivity().getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(CATEGORY); // TODO: What was it?
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

        if ((Integer) v.getTag() == LobbyListAdapter.TYPE_OPENGAME) { // TODO Adopt here
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
