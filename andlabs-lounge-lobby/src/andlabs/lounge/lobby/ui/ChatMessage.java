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

/**
 * Data object representing a chate message
 * 
 */
// TODO: Move into service
public class ChatMessage {

    // The player's name
    private String mPlayerName;
    // The message
    private String mMessage;
    // TODO: Add timestamp


    public String getPlayerName() {
        return mPlayerName;
    }


    public void setPlayerName(String pPlayerName) {
        this.mPlayerName = pPlayerName;
    }


    public String getMessage() {
        return mMessage;
    }


    public void setMessage(String message) {
        this.mMessage = message;
    }

}
