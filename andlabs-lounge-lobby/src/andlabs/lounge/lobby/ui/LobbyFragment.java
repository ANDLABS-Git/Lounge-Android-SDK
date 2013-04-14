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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.Toast;

/**
 * The fragment of the main lobby
 * 
 */
public class LobbyFragment extends Fragment implements OnChildClickListener {

    // The lobby's expandable list view
    private ExpandableListView mLobbyList;
    // The lobby's list view's adapter
    private LobbyListAdapter mLobbyAdapter;
    // The list view in the host game sliding drawer
    private ListView mHostList;
    // The host game sliding drawer's adapter
    private HostGameAdapter mHostAdapter;
    // The static handle-ImageView of the host sliding drawer
    private ImageView mStaticHandle;
    // The animated handle-ImageView of the host sliding drawer
    private ImageView mPulseHandle;
    // The lobby controller, needed for sending messages to the server
    LoungeLobbyController mLoungeLobbyController = new LoungeLobbyController();
    // The lobby callback, needed for processing messages by the server
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

        /**
         * Called when the games the user is involved in are updated
         */
        @Override
        public void onRunningGamesUpdate(final ArrayList<Game> pGames) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (pGames != null) {
                        // Query the play store for promo graphics
                        queryPlay(pGames);
                        // Set the joined games
                        mLobbyAdapter.setJoinedGames(pGames);
                        // Redraw the view
                        mLobbyAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

        /**
         * Called when the games the user is not involved in are updated
         */
        @Override
        public void onOpenGamesUpdate(final ArrayList<Game> pGames) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (pGames != null) {
                        // Query the play store for promo graphics
                        queryPlay(pGames);
                        // Set the joined games
                        mLobbyAdapter.setOpenGames(pGames);
                        // Redraw the view
                        mLobbyAdapter.notifyDataSetChanged();
                    }
                }

            });
        }

    };

    @Override
    public View onCreateView(final LayoutInflater pLayoutInflater, final ViewGroup pViewGroup, final Bundle pBundle) {
        final View view = pLayoutInflater.inflate(R.layout.fragment_lobby, pViewGroup, false);
        // Find the lobby list
        mLobbyList = (ExpandableListView) view.findViewById(R.id.list);
        // Remove the lobby list's divider
        // TODO: Check whether this can be set in XML
        mLobbyList.setDividerHeight(0);
        mLobbyList.setDivider(null);

        // Set the lobby list's adapter
        mLobbyAdapter = new LobbyListAdapter(getActivity());
        mLobbyList.setAdapter(mLobbyAdapter);

        // Set some test data.
        // TODO: Remove
        mLobbyAdapter.setJoinedGames(TestData.getJoinedGames());
        mLobbyAdapter.setOpenGames(TestData.getOpenGames());

        // Set the click listener for
        mLobbyList.setOnChildClickListener(this); // TODO make consistent with
                                                  // host drawer - either put it
                                                  // into the adapter or put the
                                                  // host drawers listener into
                                                  // this class.

        // Animate the host drawer for some seconds to indicate it can be
        // slided.
        // TODO: Make it work
        final SlidingDrawer drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer);

        mStaticHandle = (ImageView) view.findViewById(R.id.ic_lobby_host_static_pulse);
        mPulseHandle = (ImageView) view.findViewById(R.id.ic_lobby_host_pulse);
        startAnimatingHostMode();

        // End the animation when the drawer is closed
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed() {
                stopAnimatingHostMode();
            }
        });

        // Find the host drawer's game view
        mHostList = (ListView) view.findViewById(R.id.installed_games);
        // Set the list adapter of the host drawer
        mHostAdapter = new HostGameAdapter(getActivity());
        mHostList.setAdapter(mHostAdapter);
        // Set the click listener for the host game drawer
        mHostList.setOnItemClickListener(mHostAdapter); // TODO make consistent
                                                        // with lobby list-
                                                        // either put this into
                                                        // the HostGameAdapter
                                                        // or put the
                                                        // lobbyListAdapter's
                                                        // click listener into
                                                        // this class.

        // Set the host game-onClickListener
        view.findViewById(R.id.btn_host).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Host a the selected game
                mLoungeLobbyController.openMatch(mHostAdapter.getSelectedItemPackage(), mHostAdapter.getSelectedItemName());
            }
        });

        return view;
    }

    /**
     * Calls the {@link PlayParser} for promo graphics of the passed
     * {@link Game}s
     * 
     * @param games
     */
    private void queryPlay(List<Game> games) {
        Ln.v("games = %s", games);
        // Get an instance of the {@link PlayParser}
        final PlayParser parser = PlayParser.getInstance(getActivity());

        // Query the {@link PlayParser} for promo graphics, one by one
        for (int index = 0; index < games.size(); index++) {
            final Game element = games.get(index);
            parser.queryPlay(element.gameID);
        }

        // Add a new listener to get notified if a new promo graphic arrives
        parser.addListener(new PlayListener() {

            @Override
            public void onPlayResult(Map<String, Drawable> pResults) {
                mLobbyAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Start the animation of the host game handle
     */
    private void startAnimatingHostMode() {
        // Make the to-be-animated handle visible, the other one invisible
        mPulseHandle.setVisibility(View.VISIBLE);
        mStaticHandle.setVisibility(View.INVISIBLE);

        // Load and start the animation
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        mPulseHandle.startAnimation(animation);
    }

    /**
     * End the animation of the host game handle
     */
    private void stopAnimatingHostMode() {
        // Make the to-be-animated handle invisible, the other one visible
        mPulseHandle.setVisibility(View.INVISIBLE);
        mStaticHandle.setVisibility(View.VISIBLE);
    }

    /*
     * Order of calls to the LoungeLobbyController:
     * 
     * 1. bindServiceTo() 2. registerCallback() 3. unregisterCallback() 4.
     * unbindServiceFrom()
     */
    @Override
    public void onStart() {
        super.onStart();
        // Bind the lobby service
        mLoungeLobbyController.bindServiceTo(getActivity());
    }

    @Override
    public void onResume() {
        // Register the lobby callback
        mLoungeLobbyController.registerCallback(mLoungeLobbyCallback);
        super.onResume();
    }

    @Override
    public void onPause() {
        // Unregister the lobby callback
        mLoungeLobbyController.unregisterCallback(mLoungeLobbyCallback);
        super.onPause();
    }

    @Override
    public void onStop() {
        // Unbind the service
        mLoungeLobbyController.unbindServiceFrom(getActivity());
        super.onStop();
    }

    /**
     * React to clicks on child elements of the lobby list view, meaning
     * {@link Match}es
     */
    @Override
    public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition,
            final int childPosition, final long id) {
        Ln.v(" groupPosition = %d, childPosition = %d", groupPosition, childPosition);
        // The selected game the clicked match is of
        final Game game = (Game) mLobbyAdapter.getGroup(groupPosition);
        // The selected match
        final Match match = (Match) mLobbyAdapter.getChild(groupPosition, childPosition);
        Ln.v(" game = %s, match = %s", game, match);

        // If the game is open...
        if ((Integer) v.getTag() == LobbyListAdapter.TYPE_OPENGAME) {
            final String gameID = game.gameID;
            if (Utils.isGameInstalled(getActivity(), gameID)) { // ...and
                                                                // installed,
                                                                // join it...
                mLoungeLobbyController.joinMatch(gameID, match.matchID);
            } else { // ...otherwise, open the play store
                Utils.openPlay(getActivity(), gameID);
            }
        } else {
            if ("running".equalsIgnoreCase(match.status)) {
                // If the game is already running, and you are involved
                Utils.launchGameApp(getActivity(), game.gameID, match);
            } else {
                // If the game is one the user is involved in but has not been
                // started because it is waiting for players, show a simple
                // toast
                Toast.makeText(getActivity(), "Game not started yet", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
}
