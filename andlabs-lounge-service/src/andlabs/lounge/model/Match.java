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
import java.util.ArrayList;

public class Match implements Serializable {

    public String matchID;
    public ArrayList<Player> players = new ArrayList<Player>();

    // New stuff
    public String playerOnTurn;
    public int totalSpots;
    public String status;


    @Override
    public String toString() {
        return String.format("{matchID: '%s', players: %s, playerOnTurn: '%s', totalSpots: %d, status: '%s'}", matchID,
                players, playerOnTurn, totalSpots, status);
    }
}
