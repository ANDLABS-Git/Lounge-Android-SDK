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

import android.os.Bundle;


public class LoungeGameCallback  {

    /**
     * is called every time one of the other players is ready to play
     */
    public void onCheckIn(String player) {
        
    }


    /**
     * is called when all other players are ready to play
     */
    public void onAllPlayerCheckedIn() {
        
    }


    /**
     * is called when a custom game message is received
     */
    public void onGameMessage(Bundle msg) {
        
    }


    /**
     * is called when another player pauses playing
     */
    public void onCheckOut(String player) {
        
    }

}
