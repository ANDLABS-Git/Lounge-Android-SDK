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
import android.os.Bundle;


public interface LoungeServiceCallback {

    public void theAnswerIs42();


    public void onStart();


    public void onConnect();


    public void onDisconnect();


    public void onOpenGamesUpdate(Map<String, Game> pGames);


    public void onRunningGamesUpdate(Map<String, Game> pGames);


    public void onError(String message);
    
    public void onGameMessage(String pMatchID, Bundle pData);
    
}
