package com.example.filip.unibook;

/**
 * Created by filip on 2018-03-13.
 */

public class Course {

    private String id;
    private String name;
    private String description;
    private String code;

    public Course()
    {

    }

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

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getCode()
    {
        return this.code;
    }

    public void setCode(String value)
    {
        this.code = value;
    }
}
