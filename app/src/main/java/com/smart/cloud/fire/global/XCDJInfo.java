package com.smart.cloud.fire.global;

import com.smart.cloud.fire.view.TakePhoto.Photo;

import java.io.Serializable;
import java.util.ArrayList;

public class XCDJInfo implements Serializable {

    String title;
    String desc;
    ArrayList<Photo> mlist=new ArrayList<>();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<Photo> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<Photo> mlist) {
        this.mlist = mlist;
    }


}
