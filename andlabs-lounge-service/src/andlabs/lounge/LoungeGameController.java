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

package andlabs.lounge;

import java.util.Map;

import andlabs.lounge.model.Game;
import andlabs.lounge.util.Ln;
import android.content.Context;
import android.os.Bundle;


public class LoungeGameController {

    private String mPackageId;
    private LoungeGameCallback mLoungeGameCallback;
    private LoungeServiceController mLoungeServiceController = new LoungeServiceController();
    private LoungeServiceCallback mLoungeServiceCallback = new LoungeServiceCallback() {

        @Override
        public String toString() {
            return "LoungeServiceCallback (Game)";
        };


        @Override
        public void theAnswerIs42() {
            Ln.v("LoungeServiceCallback.theAnswerIs42(): Universal Answer ;-)");
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
        public void onStart() {
            Ln.v("LoungeServiceCallback.onStart():");
            // TODO Auto-generated method stub

        }


        @Override
        public void onOpenGamesUpdate(Map<String, Game> pGames) {
            Ln.v("LoungeServiceCallback.onOpenGamesUpdate(): pGames = %s", pGames);
        }


        @Override
        public void onRunningGamesUpdate(Map<String, Game> pGames) {
            Ln.v("LoungeServiceCallback.onRunningGamesUpdate(): pGames = %s", pGames);
        }


        @Override
        public void onError(String message) {
            Ln.e("LoungeServiceCallback.onError(): %s", message);
        }


        @Override
        public void onGameMessage(String pMatchID, Bundle pMsg) {
            if(pMatchID.equals(mCheckinMatchId) && mLoungeGameCallback != null) {  // TODO: move this condition to messageprocessor

                mLoungeGameCallback.onGameMessage(pMsg);
            }
        }

    };
    private String mCheckinMatchId;


    public void bindServiceTo(Context pContext) {
        Ln.v("bindServiceTo()");
        mPackageId = pContext.getApplicationInfo().packageName;
        mLoungeServiceController.registerCallback(mLoungeServiceCallback);
        mLoungeServiceController.bindServiceTo(pContext);
    }


    public void registerCallback(LoungeGameCallback pLoungeGameCallback) {
        Ln.v("registerCallback(): pLoungeGameCallback = %s", pLoungeGameCallback);
        mLoungeGameCallback = pLoungeGameCallback;
    }


    public void unregisterCallback(LoungeGameCallback pLoungeGameCallback) {
        Ln.v("unregisterCallback(): pLoungeGameCallback = %s", pLoungeGameCallback);
        mLoungeGameCallback = null;
    }


    public void unbindServiceFrom(Context pContext) {
        Ln.v("unbindServiceFrom()");
        mLoungeServiceController.unbindServiceFrom(pContext);
        mLoungeServiceController.unregisterCallback(mLoungeServiceCallback);
    }


    public void checkin(String pMatchId) {
        Ln.v("checkin(): pMatchId = %s", pMatchId);
        mCheckinMatchId = pMatchId;  // TODO: move this to messageprocessor
        mLoungeServiceController.checkin(mPackageId, pMatchId);
    }


    public void checkout(String pMatchId) {
        mCheckinMatchId = null;
        Ln.v("checkout(): pMatchId = %s", pMatchId);
        mLoungeServiceController.checkout(mPackageId,pMatchId);
    }


    public void sendGameMove(String pMatchId, Bundle pMoveBundle) {
        Ln.v("sendGameMove(): pMatchId = %s, pMoveBundle = %s", pMatchId, pMoveBundle);
        mLoungeServiceController.sendGameMove(mPackageId, pMatchId, pMoveBundle);
    }

    public void streamGameMessage(String pMatchId, Bundle pMoveBundle) {
        //Remove this? Game doesnt know if it is stream or move, but the server does
        Ln.v("streamGameMessage(): pMatchId = %s, pMoveBundle = %s", pMatchId, pMoveBundle);
//        mLoungeServiceController.streamGameMessage(mPackageId, pMatchId, pMoveBundle);
    }
    public void closeMatch(String pMatchId) {
        Ln.v("closeMatch(): pMatchId = %s, ",pMatchId);
        mLoungeServiceController.closeMatch(mPackageId,pMatchId);
    }

}
