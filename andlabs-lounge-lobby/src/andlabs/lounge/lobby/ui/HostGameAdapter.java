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

public class HostGameAdapter extends BaseAdapter implements OnItemClickListener {

    private List<ResolveInfo> mContent;
    private PackageManager mPackageManager;

    private View mLastMarkedView;
    private int mSelectedItem = -1;
    private Context mContext;


    public HostGameAdapter(Context context) {
        mContext = context;
        mContent = Utils.getInstalledLoungeGames(context);
        mPackageManager = context.getPackageManager();
    }


    @Override
    public int getCount() {

        return this.mContent.size();
    }


    @Override
    public Object getItem(int position) {
        return this.mContent.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            final LayoutInflater inflater = LayoutInflater.from(this.mContext);
            view = inflater.inflate(R.layout.lobby_drawer_list_entry, null);
        }

        final ResolveInfo info = mContent.get(position);

        final ImageView icon = (ImageView) view.findViewById(R.id.appIcon);
        icon.setImageDrawable(info.loadIcon(this.mPackageManager));

        final TextView name = (TextView) view.findViewById(R.id.appName);
        name.setText(info.loadLabel(this.mPackageManager));

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long smthn) {
        if (this.mLastMarkedView != null) {
            this.mLastMarkedView.setBackgroundColor(Color.TRANSPARENT);
        }
        view.setBackgroundColor(Color.argb(80, 73, 206, 255));
        this.mLastMarkedView = view;
        this.mSelectedItem = position;
    }


    public String getSelectedItemName() {
        if (this.mSelectedItem == -1) {
            return null;
        }

        final ResolveInfo info = this.mContent.get(this.mSelectedItem);
        return info.loadLabel(this.mPackageManager).toString();
    }


    public ResolveInfo getSelectedItemResolveInfo() {
        return mSelectedItem == -1 ? null : mContent.get(mSelectedItem);
    }


    public String getSelectedItemActivity() {
        final ResolveInfo info = getSelectedItemResolveInfo();
        return info == null ? null : info.activityInfo.packageName + "/" + info.activityInfo.name;
    }

}
