package com.goconnect.model;

import java.io.Serializable;

public class NewNearUsers implements Serializable
{
    private Users users;

    private String status;

    public Users getUsers ()
    {
        return users;
    }

    public void setUsers (Users users)
    {
        this.users = users;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [users = "+users+", status = "+status+"]";
    }
}