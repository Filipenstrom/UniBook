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
    private String adress;
    private String phone;
    private String school;

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

    public String getAdress()
    {
        return this.adress;
    }
    public void setAdress(String value)
    {
        this.adress = value;
    }

    public String getPhone()
    {
        return this.phone;
    }
    public void setPhone(String value)
    {
        this.phone = value;
    }

    public String getSchool()
    {
        return this.school;
    }
    public void setSchool(String value)
    {
        this.school = value;
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
