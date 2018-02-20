package klim.developer.com.httptest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<Item> mItems;

    public Adapter(Activity activity,List<Item> items){
        mActivity = activity;
        mItems = items;
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_layout,null);
        }
        TextView rocketNameView = (TextView) convertView.findViewById(R.id.tvRocketName);
        TextView launchDateView = (TextView) convertView.findViewById(R.id.tvLaunchDate);
        TextView detailsView = (TextView) convertView.findViewById(R.id.tvDetails);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);


        // get data from items list
        Item item = mItems.get(position);
        // imageLink
        String imageLink = item.getImage();
        // rocketName
        rocketNameView.setText(item.getRocketName());
        // launchDate
        launchDateView.setText(item.getLaunchDate());
        // details
        detailsView.setText(item.getDetails());
        // set Bitmap to imageView
        imageView.setImageBitmap(item.getBitmap());

        return convertView;
    }
}
