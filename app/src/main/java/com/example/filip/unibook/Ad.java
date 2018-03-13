package com.example.filip.unibook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marku on 2018-03-13.
 */

public class Ad {
    private String title;
    private String price;
    private byte[] pic;

    public Ad(){

    }

    public String getTitle()
    {
        return this.title;
    }
    public void setTitle(String value)
    {
        this.title = value;
    }

    public String getPrice()
    {
        return this.price;
    }
    public void setPrice(String value)
    {
        this.price = value;
    }

    public byte[] getPic()
    {
        return this.pic;
    }
    public void setPic(byte[] value)
    {
        this.pic = value;
    }
}
