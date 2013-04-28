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


package andlabs.lounge.lobby;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;

import andlabs.lounge.LoungeServiceCallback;
import andlabs.lounge.LoungeServiceController;
import andlabs.lounge.lobby.util.Id;
import andlabs.lounge.model.Game;
import andlabs.lounge.util.Ln;
import android.content.Context;
import android.os.Bundle;

public class LoungeLobbyController {

    private String mUserName;
    private LoungeLobbyCallback mLoungeLobbyCallback;
    private LoungeLobbyCallback mIdleLoungeLobbyCallback = new LoungeLobbyCallback();
    private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
    private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

        @Override
        public String toString() {
            return "LoungeServiceCallback (Lobby)";
        };


        @Override
        public void theAnswerIs42() {
            Ln.v("LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
        }


        @Override
        public void onStart() {
            Ln.v("LoungeServiceCallback.onStart():");
            mLoungeServiceController.login(mUserName);
        }


        @Override
        public void onConnect() {
            Ln.v("LoungeServiceCallback.onConnect():");
            // TODO Auto-generated method stub
        }


        @Override
        public void onDisconnect() {
            Ln.v("LoungeServiceCallback.onDisconnect():");
            // TODO Auto-generated method stub
        }


        @Override
        public void onOpenGamesUpdate(Map<String, Game> pGames) {
            try {
                Ln.v("LoungeServiceCallback.onOpenGamesUpdate(): pGames = %s", pGames);
            } catch (ConcurrentModificationException e) {
                Ln.v("ConcurrentModificationException", e.getMessage());
            }
            mLoungeLobbyCallback.onOpenGamesUpdate(new ArrayList<Game>(pGames.values()));
        }


        @Override
        public void onRunningGamesUpdate(Map<String, Game> pGames) {
            try {
                Ln.v("LoungeServiceCallback.onRunningGamesUpdate(): pGames = %s", pGames);
            } catch (ConcurrentModificationException e) {
                Ln.v("ConcurrentModificationException", e.getMessage());
            }
            mLoungeLobbyCallback.onRunningGamesUpdate(new ArrayList<Game>(pGames.values()));
        }


        @Override
        public void onError(String message) {
            Ln.e("LoungeServiceCallback.onError(): %s", message);
        }


        @Override
        public void onGameMessage(String pMatchID, Bundle pData) {
            // TODO Auto-generated method stub

        }

    };


    public void bindServiceTo(Context pContext) {
        Ln.v("bindServiceTo()");
        mUserName = Id.getName(pContext);
        mLoungeServiceController.registerCallback(mLoungeServiceCallback);
        mLoungeServiceController.bindServiceTo(pContext);
    }


    public void registerCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
        Ln.v("registerCallback(): pLoungeLobbyCallback = %s", pLoungeLobbyCallback);
        mLoungeLobbyCallback = pLoungeLobbyCallback;
    }


    public void unregisterCallback(LoungeLobbyCallback pLoungeLobbyCallback) {
        Ln.v("unregisterCallback(): pLoungeLobbyCallback = %s", pLoungeLobbyCallback);
        mLoungeLobbyCallback = mIdleLoungeLobbyCallback;;
    }


    public void unbindServiceFrom(Context pContext) {
        Ln.v("unbindServiceFrom()");
        mLoungeServiceController.unbindServiceFrom(pContext);
        mLoungeServiceController.unregisterCallback(mLoungeServiceCallback);
    }


    public void openMatch(String pPackageId, String pDisplayName) {
        Ln.v("openMatch(): pPackageId = %s, pDisplayName = %s", pPackageId, pDisplayName);
        mLoungeServiceController.openMatch(pPackageId, pDisplayName);
    }


    public void joinMatch(String pPackageId, String pMatchId) {
        Ln.v("joinGame(): pPackageId = %s, pMatchId = %s", pPackageId, pMatchId);
        mLoungeServiceController.joinMatch(pPackageId, pMatchId);
    }

}
