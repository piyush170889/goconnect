package com.goconnect.model;

import java.io.Serializable;

public class Facebook implements Serializable
{

private String id;

private String first_name;

private String email;

private String name;

private String last_name;

public String getId ()
{
    return id;
}

public void setId (String id)
{
    this.id = id;
}

public String getFirst_name ()
{
    return first_name;
}

public void setFirst_name (String first_name)
{
    this.first_name = first_name;
}

public String getEmail ()
{
    return email;
}

public void setEmail (String email)
{
    this.email = email;
}

public String getName ()
{
    return name;
}

public void setName (String name)
{
    this.name = name;
}

public String getLast_name ()
{
    return last_name;
}

public void setLast_name (String last_name)
{
    this.last_name = last_name;
}

@Override
public String toString()
{
    return "ClassPojo [id = "+id+", first_name = "+first_name+", email = "+email+", name = "+name+", last_name = "+last_name+"]";
}}