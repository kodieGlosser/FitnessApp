package com.main.toledo.gymtrackr;

/**
 * Created by Adam on 4/9/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NextAnimation extends Animation {

    public final static int COLLAPSE = 1;
    public final static int EXPAND = 0;

    private View mView;
    private int mEndHeight;
    private int mType;
    private LinearLayout.LayoutParams mLayoutParams;

    private FrameLayout listLayout;
    private FrameLayout editLayout;
    private ImageView listImage;

    private Context mContext;
    public NextAnimation(LinearLayout view, int duration, int listId, int editId, Context c) {
        mContext = c;
        LinearLayout mainFragmentLayout = (LinearLayout)view.getChildAt(1);
        listLayout = (FrameLayout)mainFragmentLayout.findViewById(listId);
        editLayout = (FrameLayout)mainFragmentLayout.findViewById(editId);

        initializeListImage();
        setDuration(duration);
        mView = view;
        mEndHeight = mView.getHeight();
        mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());

        view.setVisibility(View.VISIBLE);
    }

    public int getHeight(){
        return mView.getHeight();
    }

    public void setHeight(int height){
        mEndHeight = height;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                mLayoutParams.height =  (int)(mEndHeight * interpolatedTime);
            } else {
                mLayoutParams.height = (int) (mEndHeight * (1 - interpolatedTime));
            }
            mView.requestLayout();
        } else {
            if(mType == EXPAND) {
                mLayoutParams.height = LayoutParams.WRAP_CONTENT;
                mView.requestLayout();
            }else{
                mView.setVisibility(View.GONE);
            }
        }
    }

    private void initializeListImage(){
        listLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(listLayout.getDrawingCache());

        int position = (int)listLayout.getY();
        //int width = listLayout.getWidth();
        //int height = listLayout.getHeight();

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = position;

        listImage = new ImageView(mContext);
        listImage.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(listImage, mWindowParams);


    }
}