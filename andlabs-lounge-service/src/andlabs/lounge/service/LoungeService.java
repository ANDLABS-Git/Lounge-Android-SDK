/*
 *  Copyright (C) 2012,2013 ANDLABS. All rights reserved. 
 *  Lounge@andlabs.com
 *  lounge.andlabs.com
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

package andlabs.lounge.service;

import andlabs.lounge.util.Ln;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class LoungeService extends Service {

    private LoungeServiceImpl mLoungeService;

    @Override
    public IBinder onBind(Intent intent) {
        Ln.v("onBind(): arg0 = %s", intent);
        mLoungeService = new LoungeServiceImpl(intent);
        return mLoungeService;
    }


    @Override
    public void onCreate() {
        Ln.v("onCreate():");
        super.onCreate();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Ln.v("onStart():");
        super.onStart(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.v("onStartCommand():");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Ln.v("onDestroy():");
        try {
            if (mLoungeService != null) {
                mLoungeService.disconnect();
            }
        } catch (RemoteException e) {
            Ln.e(e, "unbindServiceFrom(): caught exception while disconnecting");
        }
        super.onDestroy();
    }

}
