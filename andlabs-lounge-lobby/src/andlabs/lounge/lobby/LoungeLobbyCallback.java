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
import java.util.List;

import andlabs.lounge.lobby.model.ChatMessage;
import andlabs.lounge.model.Game;

public class LoungeLobbyCallback {

    public void onChatDataUpdated(List<ChatMessage> data) {
        
    }


    /**
     * Sends all cached Chatmessages to the Listner. This happens either when the user logs in or the Activity need to be
     * reinstatiated after being closed.
     * 
     * @param chatLog
     */
    public void onNewChatLog(List<ChatMessage> chatLog) {
        
    }


    /**
     * For every new Chat Message that came in after the the User got into the LobbyActivity. Complementary Method to
     * onNewChatLog, so that method does not need to send the whole Array everytime.
     * 
     * @param chatMsg
     */
    public void onNewChatMessage(ChatMessage chatMsg) {
        
    }


    /**
     * This is the dataset that the LobbyList creates its running games representation from. For further Details look into
     * {@link Game}
     * 
     * @param pGames
     */
    public void onRunningGamesUpdate(ArrayList<Game> pGames) {
        
    }


    /**
     * This is the dataset that the LobbyList creates its open games representation from. For further Details look into
     * {@link Game}
     * 
     * @param pGames
     */
    public void onOpenGamesUpdate(ArrayList<Game> pGames) {
        
    }

}
