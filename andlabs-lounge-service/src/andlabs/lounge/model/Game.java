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

package andlabs.lounge.model;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String gameID;
    public String gameName;
    boolean isActive;

    // Not in game but in match public int totalSpots;
    // Not in game but in match public String status;
    public ConcurrentHashMap<String, Match> matches = new ConcurrentHashMap<String, Match>();


    @Override
    public String toString() {
        return String.format("{gameID: '%s', gameName: '%s', matches: %s}", gameID, gameName, matches.values());
    }

}
