package kore.botssdk.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import kore.botssdk.R;
import kore.botssdk.models.BotCarouselModel;
import kore.botssdk.view.viewUtils.CarouselItemViewHelper;

/**
 * Created by Pradeep Mahato on 14-July-17.
 * Copyright (c) 2014 Kore Inc. All rights reserved.
 */
public class BotCarouselAdapter extends PagerAdapter {

    ArrayList<BotCarouselModel> botCarouselModels = new ArrayList<>();
    Activity activityContext;
    LayoutInflater ownLayoutInflater;
    float pageWidth = 1.0f;

    public BotCarouselAdapter(FragmentManager fm, Activity activityContext) {
        super();
        this.activityContext = activityContext;
        ownLayoutInflater = activityContext.getLayoutInflater();

        TypedValue typedValue = new TypedValue();
        activityContext.getResources().getValue(R.dimen.carousel_item_width_factor, typedValue, true);
        pageWidth = typedValue.getFloat();
    }

    @Override
    public int getCount() {
        return botCarouselModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View carouselItemLayout = ownLayoutInflater.inflate(R.layout.carousel_item_layout, container, false);

        CarouselItemViewHelper.initializeViewHolder(carouselItemLayout);
        CarouselItemViewHelper.populateStuffs((CarouselItemViewHelper.CarouselViewHolder) carouselItemLayout.getTag(), botCarouselModels.get(position), activityContext);

        container.addView(carouselItemLayout);

        return carouselItemLayout;

    }

    public void setBotCarouselModels(ArrayList<BotCarouselModel> botCarouselModels) {
        this.botCarouselModels = botCarouselModels;
    }

    @Override
    public float getPageWidth(int position) {
        if (getCount() == 0) {
            return super.getPageWidth(position);
        } else {
            return pageWidth;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
