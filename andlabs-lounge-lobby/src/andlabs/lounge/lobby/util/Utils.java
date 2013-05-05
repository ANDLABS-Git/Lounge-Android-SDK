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

package andlabs.lounge.lobby.util;

import java.util.List;

import andlabs.lounge.lobby.LoungeConstants;
import andlabs.lounge.model.Match;
import andlabs.lounge.util.Ln;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Utils implements LoungeConstants {

    public static final String CATEGORY = "eu.andlabs.lounge";

    public static List<ResolveInfo> getInstalledLoungeGames(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(CATEGORY);
        return ctx.getPackageManager().queryIntentActivities(intent, 0);
    }

    public static void launchGameApp(Context context, String packageName, Match match) {
        final ResolveInfo info = getInstalledGameInfo(context, packageName);
        if (info != null) {
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
            intent.putExtra(EXTRA_IS_HOST, Id.getName(context));
            intent.putExtra(EXTRA_HOST_NAME, match.players.get(0).playerID);
            intent.putExtra(EXTRA_MATCH_ID, match.matchID);

            final String[] players = new String[match.players.size()];
            for (int i = 0; i < match.players.size(); i++) {
                players[i] = match.players.get(i).playerID;
            }
            intent.putExtra(EXTRA_PLAYER_NAMES, players);

            context.startActivity(intent);
        } else {
            Ln.w("launchGameApp(): unable to start the game %s", packageName);
        }
    }

    private static ResolveInfo getInstalledGameInfo(Context context, String packageName) {
        for (ResolveInfo info : getInstalledLoungeGames(context)) {
            Ln.d("getInstalledGameInfo(): packageName = %s (%s)", packageName, info.activityInfo.packageName);
            if (packageName.equalsIgnoreCase(info.activityInfo.packageName)) {
                return info;
            }
        }
        return null;
    }

    public static Drawable getGameIcon(Context context, String packageName) {
        ResolveInfo info = getInstalledGameInfo(context, packageName);
        if (info == null) {
            return null;
        } else {
            return info.loadIcon(context.getPackageManager());
        }
    }

    public static void openPlay(Context context, String packageName) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));

        context.startActivity(intent);
    }

    public static boolean isGameInstalled(Context pContext, String pGameID) {
        List<ResolveInfo> installedGames = Utils.getInstalledLoungeGames(pContext);

        for (ResolveInfo info : installedGames) {
            if (pGameID.equalsIgnoreCase(info.activityInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    public static int ipc(Context context, int colorA, int colorB, float proportion) {
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        Color.colorToHSV(colorA, hsva);
        Color.colorToHSV(colorB, hsvb);
        for (int i = 0; i < 3; i++) {
            hsvb[i] = Utils.interpolate(hsva[i], hsvb[i], proportion);
        }
        return Color.HSVToColor(hsvb);
    }

    public static float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }
}
