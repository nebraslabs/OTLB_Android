package com.nebrasapps.otlb.pojo;

/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */



public class Services {
    private String typeName;
    private int image;
    private int selectedImg;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected=false;
    public Services(String typeName, int selectedImg, int image) {
        this.typeName = typeName;
        this.image = image;
        this.selectedImg = selectedImg;
        this.selected = false;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getSelectedImg() {
        return selectedImg;
    }

    public void setSelectedImg(int selectedImg) {
        this.selectedImg = selectedImg;
    }
}
