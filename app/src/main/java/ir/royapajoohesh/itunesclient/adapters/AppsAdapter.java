package ir.royapajoohesh.itunesclient.adapters;

import ir.royapajoohesh.itunesclient.R;
import ir.royapajoohesh.itunesclient.data.AppIconsDataSource;
import ir.royapajoohesh.itunesclient.data.Apps;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AppsAdapter extends BaseAdapter {

	ArrayList<Apps> items;
	private Context context;
	private boolean IsTablet;

	public AppsAdapter(Context contex, ArrayList<Apps> itemsList, boolean isTablet) {
		this.context = contex;
		this.items = itemsList;
		this.IsTablet = isTablet;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Apps getItem(int position) {
		if (position < items.size() && position >= 0)
			return items.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		if (position < items.size() && position >= 0)
			return items.get(position).id;
		else
			return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			int layoutID = this.IsTablet ? R.layout.apps_grid_layout : R.layout.app_categories_list_layout;

			LayoutInflater infalter = (LayoutInflater) this.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalter.inflate(layoutID, parent, false);

			holder = new ViewHolder();
			holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		Apps tmpApp = getItem(position);

		holder.titleTextView.setText(tmpApp.name);

		if (this.IsTablet) {
			if (holder.subTitleTextView == null)
				holder.subTitleTextView = (TextView) convertView.findViewById(R.id.subTitleTextView);

			holder.subTitleTextView.setText(tmpApp.GetPrice(context));
		}

		if (tmpApp.IconsList.size() > 0) {
			Bitmap tmpBitmap = AppIconsDataSource.GetLargestIcon(tmpApp.id, context, tmpApp.IconsList);
			holder.iconImageView.setImageBitmap(tmpBitmap);
		}

		return convertView;
	}

	public static void SetViewData(View v, Apps app, boolean isGrid, Context context) {
		TextView title = (TextView) v.findViewById(R.id.titleTextView);
		ImageView icon = (ImageView) v.findViewById(R.id.iconImageView);

		title.setText(app.name);

		if (isGrid) {
			TextView subTitle = (TextView) v.findViewById(R.id.subTitleTextView);
			subTitle.setText(app.GetPrice(context));
		}

		if (app.IconsList.size() > 0) {
			Bitmap tmpBitmap = AppIconsDataSource.GetLargestIcon(app.id, context, app.IconsList);
			icon.setImageBitmap(tmpBitmap);
		}
	}

	class ViewHolder {
		TextView titleTextView = null;
		TextView subTitleTextView = null;
		ImageView iconImageView = null;
	}

}