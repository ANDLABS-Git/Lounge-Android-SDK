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

import java.util.List;

import andlabs.lounge.lobby.R;
import andlabs.lounge.lobby.util.Utils;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The adapter for the list content of the host game sliding drawer
 * 
 */
public class HostGameAdapter extends BaseAdapter implements OnItemClickListener {

    private List<ResolveInfo> mContent;
    private PackageManager mPackageManager;

    private View mLastMarkedView;
    private int mSelectedItem = -1;
    private LayoutInflater mInflater;

    public HostGameAdapter(Context context) {
        // Set the content to be all apps that use Lounge on this device
        mContent = Utils.getInstalledLoungeGames(context);
        mPackageManager = context.getPackageManager();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return mContent.size();
    }

    @Override
    public Object getItem(final int pPosition) {
        return mContent.get(pPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int pPosition, final View pConvertView, final ViewGroup pParent) {
        // We don't modify our parameters, we create copies
        View view = pConvertView;

        if (view == null) {
            // If the view hasn't been created yet, do so
            view = mInflater.inflate(R.layout.lobby_drawer_list_entry, null);
        }

        // Fill view with content
        final ResolveInfo info = mContent.get(pPosition);

        // Set the item's icon
        final ImageView icon = (ImageView) view.findViewById(R.id.appIcon);
        icon.setImageDrawable(info.loadIcon(this.mPackageManager));

        // Set the item's name
        final TextView name = (TextView) view.findViewById(R.id.appName);
        name.setText(info.loadLabel(this.mPackageManager));

        return view;
    }

    @Override
    public void onItemClick(final AdapterView<?> pAdapter, final View pView, final int pPosition, final long pId) {
        // If a view has been marked before, reset it's background to transparent
        if (mLastMarkedView != null) {
            mLastMarkedView.setBackgroundColor(Color.TRANSPARENT);
        }
        // Set the background color of the selected view to some fancy blue value
        pView.setBackgroundColor(Color.argb(80, 73, 206, 255));
        // Set this view to be the last marked view so that it's color can be reset
        mLastMarkedView = pView;
        // Set the selected item's position to be the one of this view
        mSelectedItem = pPosition;
    }

    /**
     * Get the name of the list item that is highlighted in the host game sliding drawer
     * @return
     */
    public String getSelectedItemName() {
        if (mSelectedItem == -1) {
            return null;
        }

        final ResolveInfo info = mContent.get(mSelectedItem);
        return info.loadLabel(mPackageManager).toString();
    }

    /**
     * Get the package name of the list item that is highlighted in the host game sliding drawer
     * @return
     */
    public String getSelectedItemPackage() {
        if (mSelectedItem == -1) {
            return null;
        }

        final ResolveInfo info = mContent.get(mSelectedItem);
        return info.activityInfo.packageName;
    }
}
