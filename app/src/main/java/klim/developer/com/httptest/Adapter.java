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

    private LayoutInflater inflater;
    private Activity activity;
    private List<Item> items;

    public Adapter(Activity activity,List<Item> items){
        this.activity = activity;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = inflater.inflate(R.layout.custom_layout,null);
        }
        TextView rocketNameView = (TextView) convertView.findViewById(R.id.tvRocketName);
        TextView launchDateView = (TextView) convertView.findViewById(R.id.tvLaunchDate);
        TextView detailsView = (TextView) convertView.findViewById(R.id.tvDetails);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);


        // get data from items list
        Item item = items.get(position);
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
