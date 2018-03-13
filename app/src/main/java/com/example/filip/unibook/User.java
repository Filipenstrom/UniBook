package com.example.filip.unibook;

/**
 * Created by Ludvig on 2018-03-13.
 */

public class User {
    private String id;
    private String name;
    private String surname;
    private String mail;
    private byte[] pic;

    public User(){}

    public String getId()
    {
        return this.id;
    }
    public void setId(String value)
    {
        this.id = value;
    }

    public String getName()
    {
        return this.name;
    }
    public void setName(String value)
    {
        this.name = value;
    }

    public String getSurname()
    {
        return this.surname;
    }
    public void setSurname(String value)
    {
        this.surname = value;
    }

    public String getMail()
    {
        return this.mail;
    }
    public void setMail(String value)
    {
        this.mail = value;
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
