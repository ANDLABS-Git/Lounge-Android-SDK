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
package andlabs.lounge.lobby;

public interface LoungeConstants {

    /**
     * Indicates whether this game was hosted by this device
     */
    public static final String EXTRA_IS_HOST = "isHost";
    /**
     * The name of the hosting player
     */
    public static final String EXTRA_HOST_NAME = "hostName";
    /**
     * The ID of this match, needed for sending messages
     */
    public static final String EXTRA_MATCH_ID = "matchID";
    /**
     * All the players playing in this match
     */
    public static final String EXTRA_PLAYER_NAMES = "playerNames";
}
