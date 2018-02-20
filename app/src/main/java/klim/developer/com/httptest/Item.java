package klim.developer.com.httptest;

import android.graphics.Bitmap;

/**
 * one item contains info about one launch
 */

public class Item {
    private String mRocketName,
                   mLaunchDate,
                   mDetails,
                   mImage,
                   mArticleLink;
    private Bitmap mBimtmap;

    public String getRocketName(){
        return mRocketName;
    }

    public String getLaunchDate(){
        return  mLaunchDate;
    }

    public String getDetails(){
        return mDetails;
    }

    public String getImage() {
        return mImage;
    }

    public String getArticleLink(){
        return mArticleLink;
    }

    public Bitmap getBitmap(){
        return mBimtmap;
    }


    public void setRocketName(String rocketName){
        mRocketName = rocketName;
    }

    public void setLaunchDate(String launchDate){
        mLaunchDate = launchDate;
    }

    public void setDetails(String details){
        mDetails = details;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public void setArticleLink(String articleLink){
        mArticleLink = articleLink;
    }

    public void setBitmap(Bitmap bitmap){
        mBimtmap = bitmap;
    }

}
