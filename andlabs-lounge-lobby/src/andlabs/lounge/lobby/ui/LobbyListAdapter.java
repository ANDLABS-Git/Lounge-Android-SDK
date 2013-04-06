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

import roboguice.util.Ln;

import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.util.ColorAnimatorTask;
import andlabs.lounge.lobby.util.parser.PlayParser;
import andlabs.lounge.model.Game;
import andlabs.lounge.model.Match;
import andlabs.lounge.model.Player;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class LobbyListAdapter extends BaseExpandableListAdapter {

    private Object mSeparator = new Object() {

    };

    public static final int TYPE_JOINEDGAME = 0;
    public static final int TYPE_SEPARATOR = 1;
    public static final int TYPE_OPENGAME = 2;

    // private List<LobbyListElement> content;
    private List<Game> mJoinedGames = new ArrayList<Game>();
    private List<Game> mOpenGames = new ArrayList<Game>();
    private LayoutInflater mInflater;
    private Handler handler = new Handler();

    private PlayParser parser;

    private ColorAnimatorTask anmimatorTask;

    private List<ResolveInfo> installedGames;

    private PackageManager mPackageManager;

    private boolean mSeparatorFlag;

    public LobbyListAdapter(Context pContext) {
        mInflater = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.anmimatorTask = new ColorAnimatorTask(pContext, 0, 1, 1000);

        this.parser = PlayParser.getInstance(pContext);
    }

    public void setJoinedGames(List<Game> pRunningGames) {
        mJoinedGames = pRunningGames;
        setSeparatorFlag();
    }

    public void setOpenGames(List<Game> pOpenGames) {
        mOpenGames = pOpenGames;
        setSeparatorFlag();
    }

    private void setSeparatorFlag() {
        mSeparatorFlag = mJoinedGames.size() > 0 && mOpenGames.size() > 0;

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Ln.v("getChild(): groupPosition = %d, childPosition = %d", groupPosition, childPosition);
        Game game = (Game) getGroup(groupPosition);
        return game.matches.values().toArray()[childPosition];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Ln.v("getChildId(): groupPosition = %d, childPosition = %d", groupPosition, childPosition);
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Ln.v("getChildView(): groupPosition = %d, childPosition = %d, isLastChild = %b", groupPosition, childPosition,
                isLastChild);

        int type = getChildType(groupPosition, childPosition);
        if (convertView == null || convertView.getId() != type) {
            // When it is a new view or not recycleable because its a different
            // view type

            if (convertView == null || (Integer) convertView.getTag() != type) {
                // When it is a new view or not recycleable because its a
                // different
                // view type

                switch (type) {
                case TYPE_JOINEDGAME:
                    convertView = mInflater.inflate(R.layout.lobby_match_list_entry_2players, parent, false);
                    fillJoinedGameView(groupPosition, childPosition, convertView);

                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.lobby_seperator, null);
                    break;

                case TYPE_OPENGAME:
                    convertView = mInflater.inflate(R.layout.lobby_open_game_entry, null);
                    break;
                }

            }

            switch (type) {
            case TYPE_JOINEDGAME:
                fillJoinedGameView(groupPosition, childPosition, convertView);
                break;

            case TYPE_SEPARATOR:
                break;

            case TYPE_OPENGAME:
                fillOpenGameView(groupPosition, childPosition, convertView);
                break;
            }

        }

        convertView.setTag(type);

        return convertView;
    }

    private void fillOpenGameView(final int groupPosition, final int childPosition, final View convertView) {
        TextView hostname = (TextView) convertView.findViewById(R.id.hostname);
        TextView playercount = (TextView) convertView.findViewById(R.id.playercount);
        final Match match = (Match) getChild(groupPosition, childPosition);
        final Game game = (Game) getGroup(groupPosition);
        hostname.setText(match.players.get(0).playerID);
        playercount.setText(match.matchID.substring(10, match.matchID.length()));
        final LinearLayout joinBtn = (LinearLayout) convertView.findViewById(R.id.joinBtn);
        if (isGameInstalled(game.gameID)) {
            convertView.findViewById(R.id.playstoreIcon).setVisibility(View.GONE);
        } else {
            joinBtn.setVisibility(View.GONE);
            convertView.findViewById(R.id.playstoreIcon).setVisibility(View.VISIBLE);
        }

        joinBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Ln.v("OnClickListener.onClick(groupPosition = %d, childPosition = %d", groupPosition, childPosition);
                joinBtn.setVisibility(View.GONE);
                convertView.findViewById(R.id.joinInProgress).setVisibility(View.VISIBLE);

            }
        });
    }

    private void fillJoinedGameView(int groupPosition, int childPosition, final View convertView) {
        final TextView player1Label = (TextView) convertView.findViewById(R.id.playerLbl1);
        TextView player2Label = (TextView) convertView.findViewById(R.id.playerLbl2);

        final View player1Beacon = convertView.findViewById(R.id.playerBeacon1);
        View player2Beacon = convertView.findViewById(R.id.playerBeacon2);

        final Match match = (Match) getChild(groupPosition, childPosition);

        player1Label.setText(match.players.get(0).playerID);
        player1Beacon.setBackgroundColor(Color.GREEN);
        if (match.players.size() == 2) {
            Player player2 = match.players.get(1);

            player2Label.setText(player2.playerID);
            player2Beacon.setBackgroundColor(Color.GREEN);
        } else {
            player2Beacon.setBackgroundColor(Color.WHITE);
            player2Label.setText("Open");
        }

        if ("running".equalsIgnoreCase(match.status)) {
            TableRow.LayoutParams params2 = (LayoutParams) player2Beacon.getLayoutParams();
            TableRow.LayoutParams params1 = (LayoutParams) player1Beacon.getLayoutParams();
            params2.leftMargin = 0;
            // params2.rightMargin=20;
            player2Beacon.setLayoutParams(params2);

            // params1.leftMargin=20;
            params1.rightMargin = 0;
            player1Beacon.setLayoutParams(params1);
        } else {
            TableRow.LayoutParams params2 = (LayoutParams) player2Beacon.getLayoutParams();
            TableRow.LayoutParams params1 = (LayoutParams) player1Beacon.getLayoutParams();
            params2.leftMargin = 15;
            // params2.rightMargin=20;
            player2Beacon.setLayoutParams(params2);

            // params1.leftMargin=20;
            params1.rightMargin = 15;
            player1Beacon.setLayoutParams(params1);
        }

        // if (match.isLocalPlayerOnTurn()) { // TODO: Adopt
        //
        // if (!this.anmimatorTask.isRunning()) {
        // this.anmimatorTask.execute(new ViewColorAnimationHolder(
        // convertView, R.color.blue, R.color.yellow));
        // }
        // this.anmimatorTask.add(player1Beacon, R.color.whiteAlpha,
        // R.color.green);
        // }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Ln.v("getChildrenCount(): groupPosition = %d", groupPosition);
        Game game = null;
        if (groupPosition < mJoinedGames.size()) {
            game = mJoinedGames.get(groupPosition);
        } else if (mSeparatorFlag && groupPosition == mJoinedGames.size()) {
        } else {
            int index = groupPosition - mJoinedGames.size() - (mSeparatorFlag ? 1 : 0);
            game = mOpenGames.get(index);
        }
        return (game != null) ? game.matches.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        Ln.v("getChildrenCount(): groupPosition = %d", groupPosition);
        Game game = null;
        if (groupPosition < mJoinedGames.size()) {
            game = mJoinedGames.get(groupPosition);
        } else if (mSeparatorFlag && groupPosition == mJoinedGames.size()) {
        } else {
            int index = groupPosition - mJoinedGames.size() - (mSeparatorFlag ? 1 : 0);
            game = mOpenGames.get(index);
        }
        return (game != null) ? game : mSeparator;
    }

    @Override
    public int getGroupCount() {
        int groupCount = mJoinedGames.size() + mOpenGames.size();
        if (mJoinedGames.size() > 0 && mOpenGames.size() > 0) {
            groupCount += 1;
        }
        return groupCount;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Ln.v("getGroupView(): groupPosition = %d, isExpanded = %b", groupPosition, isExpanded);

        Object group = getGroup(groupPosition);
        int type = getGroupType(groupPosition);
        if (convertView == null || (Integer) convertView.getTag() != type) { // When
                                                                             // it
                                                                             // is
            // a new view or not recycleable because its a different view type

            switch (type) {
            case TYPE_JOINEDGAME:
                convertView = mInflater.inflate(R.layout.lobby_gamelist_entry, parent, false);

                break;
            case TYPE_SEPARATOR:
                convertView = mInflater.inflate(R.layout.lobby_seperator, null);
                break;

            case TYPE_OPENGAME:
                convertView = mInflater.inflate(R.layout.lobby_gamelist_entry, null);
                break;
            }

        }
        switch (type) {
        case TYPE_JOINEDGAME:
            createGameView((Game) group, convertView, true);
            break;

        case TYPE_SEPARATOR:
            break;

        case TYPE_OPENGAME:
            createGameView((Game) group, convertView, false);
            break;
        }
        convertView.setTag(type);
        return convertView;

    }

    private void createGameView(Game game, View convertView, boolean isInvolved) {
        ((TextView) convertView.findViewById(R.id.gamename)).setText(game.gameName);
        ((TextView) convertView.findViewById(R.id.count)).setText(Integer.toString(game.matches.size()));

        if (isInvolved) {
            convertView.findViewById(R.id.involvedGameIndicator).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.involvedGameIndicator).setVisibility(View.GONE);

        }

        final Drawable promo = this.parser.getResult(game.gameID);
        if (promo != null) {
            convertView.findViewById(R.id.promo).setBackgroundDrawable(promo);
        }

        for (ResolveInfo info : installedGames) {
            Log.i("installed adapter",game.gameID + " = "+info.activityInfo.packageName);
            if (game.gameID.equalsIgnoreCase(info.activityInfo.packageName)) {
                final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                icon.setImageDrawable(info.loadIcon(this.mPackageManager));
            }

        }

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return getGroupType(groupPosition);
    }

    @Override
    public int getGroupType(int groupPosition) {
        int type;
        if (groupPosition < mJoinedGames.size()) {
            type = TYPE_JOINEDGAME;
        } else if (groupPosition == mJoinedGames.size() && mJoinedGames.size() > 0) {
            type = TYPE_SEPARATOR;
        } else {
            type = TYPE_OPENGAME;
        }
        return type;
    }

    @Override
    public int getChildTypeCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    public void setInstalledGames(List<ResolveInfo> installedGames, PackageManager packageManager) {
        this.installedGames = installedGames;
        this.mPackageManager = packageManager;

    }

    private boolean isGameInstalled(String gameID) {
        for (ResolveInfo info : installedGames) {
            if (gameID.equalsIgnoreCase(info.activityInfo.packageName)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public int getGroupTypeCount() {
        // TODO Auto-generated method stub
        return 3;
    }

}
