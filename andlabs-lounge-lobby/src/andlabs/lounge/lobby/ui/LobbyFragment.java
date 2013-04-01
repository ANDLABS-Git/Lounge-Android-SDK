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

import java.util.List;

import andlabs.lounge.lobby.LoungeLobbyCallback;
import andlabs.lounge.lobby.LoungeLobbyController;
import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.lobby.model.GameMatch;
import andlabs.lounge.lobby.model.LobbyListElement;
import andlabs.lounge.lobby.util.Utils;
import andlabs.lounge.lobby.util.parser.PlayParser;
import andlabs.lounge.lobby.util.parser.PlayParser.PlayListener;
import andlabs.lounge.lobby.util.parser.PlayResult;
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
		public void onLobbyDataUpdated(final List<LobbyListElement> data) {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mAdapter.setContent(data);
					mAdapter.notifyDataSetChanged();
				}
			});
		}

	};

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
				lobbyList.setIndicatorBounds(lobbyList.getRight() - 40,
						lobbyList.getWidth());
			}
		});
		mAdapter = new LobbyListAdapter(getActivity());

		final List<LobbyListElement> data = TestData.getMockData();
		final PlayParser parser = PlayParser.getInstance(getActivity());

		for (LobbyListElement element : data) {
			parser.queryPlay(element.getPgkName());
		}

		parser.addListener(new PlayListener() {

			@Override
			public void onPlayResult(PlayResult pResult) {
				mAdapter.notifyDataSetChanged();
			}
		});

		mAdapter.setContent(data);
		lobbyList.setAdapter(mAdapter);
		lobbyList.setOnChildClickListener(this);

		this.mHostList = (ListView) view.findViewById(R.id.installed_games);
		this.hostAdapter = new HostGameAdapter(getActivity());
		this.mHostList.setAdapter(hostAdapter);
		this.mHostList.setOnItemClickListener(hostAdapter);

		view.findViewById(R.id.btn_host).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				 mLoungeLobbyController.openMatch(hostAdapter.getSelectedItemPackage(),hostAdapter.getSelectedItemName());

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
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		LobbyListElement game = (LobbyListElement) mAdapter
				.getGroup(groupPosition);
		GameMatch match = (GameMatch) mAdapter.getChild(groupPosition,
				childPosition);

		 if (match.isInvolved()) { //TODO Adopt here
			 mLoungeLobbyController.joinMatch(match.getMatchId(),game.getPgkName());
		 // Lounge.join(match.getMatchId(),game.getPgkName()); // join Game
		 } else {
		 // open joined game
		 if (match.isRunning()) {
		 Utils.launchGameApp(getActivity(), game.getPgkName(), match);
		 } else {
		 Toast.makeText(getActivity(), "Game not started yet",
		 Toast.LENGTH_LONG).show();
		 }
		 }
		return false;
	}

}
