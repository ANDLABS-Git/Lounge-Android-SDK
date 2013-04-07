package andlabs.lounge.lobby.ui;

import java.util.List;

import andlabs.lounge.lobby.R;
import android.content.Context;
import android.content.Intent;
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


    public HostGameAdapter(List<ResolveInfo> installedGames, Context context, PackageManager packageManager) {
        this.mContext = context;
        mContent = installedGames;
        this.mPackageManager = packageManager;
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


    public String getSelectedItemPackage() {
        if (this.mSelectedItem == -1) {
            return null;
        }

        final ResolveInfo info = this.mContent.get(this.mSelectedItem);
        return info.activityInfo.packageName;  // + "/" + info.loadLabel(this.mPackageManager);
    }
}
