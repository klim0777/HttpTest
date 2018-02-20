package klim.developer.com.httptest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
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
            inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = inflater.inflate(R.layout.custom_layout,null);
        }
        TextView rocketName= (TextView) convertView.findViewById(R.id.tvRocketName);
        TextView launchDate= (TextView) convertView.findViewById(R.id.tvLaunchDate);
        TextView details= (TextView) convertView.findViewById(R.id.tvDetails);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);


        // get data from items list
        Item item=items.get(position);
        // imageLink
        String imageLink = item.getImage();
        // rocketName
        rocketName.setText(item.getRocketName());
        // launchDate
        launchDate.setText(item.getLaunchDate());
        // details
        details.setText(item.getDetails());
        // set Bitmap to imageView
        image.setImageBitmap(item.getBimtmap());
        return convertView;
    }
}
