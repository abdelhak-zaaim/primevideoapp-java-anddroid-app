package com.cinefilmz.tv.Interface;

public interface OnItemClick {
    void longClick(String itemID, String clickType, int position);

    void itemClick(String itemID, String clickType, int position);
}
