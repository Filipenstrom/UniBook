package com.example.filip.unibook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marku on 2018-03-13.
 */

public class Ad {
    private String title;
    private String price;
    private String ISDN;
    private String info;
    private String program;
    private String course;
    private String id;
    private byte[] pic;

    public Ad(){

    }

    public String getId()
    {
        return this.id;
    }
    public void setId(String value)
    {
        this.id = value;
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

    public String getISDN()
    {
        return this.ISDN;
    }
    public void setISDN(String value)
    {
        this.ISDN = value;
    }

    public String getInfo()
    {
        return this.info;
    }
    public void setinfo(String value)
    {
        this.info = value;
    }

    public String getProgram()
    {
        return this.program;
    }
    public void setProgram(String value)
    {
        this.program = value;
    }

    public String getCourse()
    {
        return this.course;
    }
    public void setCourse(String value)
    {
        this.course = value;
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
