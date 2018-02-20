package klim.developer.com.httptest;

import android.graphics.Bitmap;

/**
 * one item contains info about one launch
 */

public class Item {
    private String mrocketName,
                   mlaunchDate,
                   mdetails,
                   mimage,
                   marticleLink;
    private Bitmap mbimtmap;

    public String getRocketName(){
        return mrocketName;
    }

    public String getLaunchDate(){
        return  mlaunchDate;
    }

    public String getDetails(){
        return mdetails;
    }

    public String getImage() {
        return mimage;
    }

    public String getArticleLink(){
        return marticleLink;
    }

    public Bitmap getBitmap(){
        return mbimtmap;
    }


    public void setRocketName(String rocket_name){
        mrocketName = rocket_name;
    }

    public void setLaunchDate(String launch_date){
        mlaunchDate = launch_date;
    }

    public void setDetails(String details){
        mdetails = details;
    }

    public void setImage(String image) {
        mimage = image;
    }

    public void setArticleLink(String articleLink){
        marticleLink = articleLink;
    }

    public void setBitmap(Bitmap bitmap){
        mbimtmap = bitmap;
    }

    public Item(){}
}
