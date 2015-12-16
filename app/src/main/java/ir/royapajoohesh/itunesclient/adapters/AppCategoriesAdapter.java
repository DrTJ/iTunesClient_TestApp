package ir.royapajoohesh.itunesclient.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.royapajoohesh.itunesclient.R;
import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.utils.ActivityUtils;
import ir.royapajoohesh.utils.Padding;

public class AppCategoriesAdapter extends BaseAdapter {

    private static final String TAG = "AppCategoriesAdapter";
    ArrayList<AppCategories> items;
    private Context Context;
    private int itemsLayout;
    private SharedPreferences prefs;
    private long selectedItemID = -1;



    public AppCategoriesAdapter(Context contex, ArrayList<AppCategories> itemsList, int itemsLayout) {
        this.Context = contex;
        this.items = itemsList;
        this.itemsLayout = itemsLayout;

        this.prefs = PreferenceManager.getDefaultSharedPreferences(contex);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AppCategories getItem(int position) {
        return (position < items.size() && position >= 0) ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        if (position < items.size() && position >= 0)
            return items.get(position).id;
        else
            return -1;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.Context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemsLayout, parent, false);

            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppCategories tmpItem = getItem(position);
        holder.titleTextView.setText(tmpItem.label);


        if (itemsLayout == ViewType.Drawer) {
            if (selectedItemID != -1 && tmpItem.id == selectedItemID) {
                Padding pd = Padding.getPadding(convertView);

                if (android.os.Build.VERSION.SDK_INT >= 22) {
                    convertView.setBackground(Context.getResources().getDrawable(R.drawable.drawer_selected_item, Context.getTheme()));
                    holder.titleTextView.setTextColor(Context.getResources().getColor(R.color.drawer_selected_item_text_color, Context.getTheme()));
                } else {
                    convertView.setBackground(Context.getResources().getDrawable(R.drawable.drawer_selected_item));
                    holder.titleTextView.setTextColor(Context.getResources().getColor(R.color.drawer_selected_item_text_color));
                }

                pd.applyTo(convertView);
            } else {
                Padding pd = Padding.getPadding(convertView);

                convertView.setBackgroundColor(Color.TRANSPARENT);
                holder.titleTextView.setTextColor(Context.getResources().getColor(R.color.drawer_list_foreground_color));

                pd.applyTo(convertView);
            }
        }

        if (this.itemsLayout == ViewType.Grid) {
            if (holder.subTitleTextView == null)
                holder.subTitleTextView = (TextView) convertView.findViewById(R.id.subTitleTextView);

            String txt = tmpItem.AppsList.size() + " apps";
            holder.subTitleTextView.setText(txt);
        }

        holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(Context,
                this.prefs.getInt(tmpItem.label, R.mipmap.icon)));

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        ImageView iconImageView;

        public ViewHolder() {
            titleTextView = null;
            subTitleTextView = null;
            iconImageView = null;
        }
    }

    public void setSelectedByID(long id) {
        this.selectedItemID = id;
    }


}