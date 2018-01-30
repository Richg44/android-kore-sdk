package kore.botssdk.models;

import java.util.ArrayList;

/**
 * Created by Pradeep Mahato on 13/7/17.
 * Copyright (c) 2014 Kore Inc. All rights reserved.
 */
public class BotCarouselModel {

    String title = "";

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setDefault_action(BotListDefaultModel default_action) {
        this.default_action = default_action;
    }

    public void setButtons(ArrayList<BotCaourselButtonModel> buttons) {
        this.buttons = buttons;
    }

    String image_url = "";
    String subtitle;
    BotListDefaultModel default_action;
    ArrayList<BotCaourselButtonModel> buttons;

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public BotListDefaultModel getDefault_action() {
        return default_action;
    }

    public ArrayList<BotCaourselButtonModel> getButtons() {
        return buttons;
    }
}
