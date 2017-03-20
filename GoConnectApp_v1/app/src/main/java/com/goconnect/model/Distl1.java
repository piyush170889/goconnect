package com.goconnect.model;

import java.io.Serializable;

public class Distl1 implements Serializable{
	
	private Position position;

    private String __v;

    private String provider;

    private String avatar;

    private String mode;

    private String topicToConnect;

    private String distance;

    private String _id;

    private String email;

    private Facebook facebook;

    private CurrLocation currLocation;

    private String role;

    private String dateCreated;

    private Affiliation affiliation;

    private String isNewUser;

    public Position getPosition ()
    {
        return position;
    }

    public void setPosition (Position position)
    {
        this.position = position;
    }

    public String get__v ()
    {
        return __v;
    }

    public void set__v (String __v)
    {
        this.__v = __v;
    }

    public String getProvider ()
    {
        return provider;
    }

    public void setProvider (String provider)
    {
        this.provider = provider;
    }

    public String getAvatar ()
    {
        return avatar;
    }

    public void setAvatar (String avatar)
    {
        this.avatar = avatar;
    }

    public String getMode ()
    {
        return mode;
    }

    public void setMode (String mode)
    {
        this.mode = mode;
    }

    public String getTopicToConnect ()
    {
        return topicToConnect;
    }

    public void setTopicToConnect (String topicToConnect)
    {
        this.topicToConnect = topicToConnect;
    }

    public String getDistance ()
    {
        return distance;
    }

    public void setDistance (String distance)
    {
        this.distance = distance;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public Facebook getFacebook ()
    {
        return facebook;
    }

    public void setFacebook (Facebook facebook)
    {
        this.facebook = facebook;
    }

    public CurrLocation getCurrLocation ()
    {
        return currLocation;
    }

    public void setCurrLocation (CurrLocation currLocation)
    {
        this.currLocation = currLocation;
    }

    public String getRole ()
    {
        return role;
    }

    public void setRole (String role)
    {
        this.role = role;
    }

    public String getDateCreated ()
    {
        return dateCreated;
    }

    public void setDateCreated (String dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public Affiliation getAffiliation ()
    {
        return affiliation;
    }

    public void setAffiliation (Affiliation affiliation)
    {
        this.affiliation = affiliation;
    }

    public String getIsNewUser ()
    {
        return isNewUser;
    }

    public void setIsNewUser (String isNewUser)
    {
        this.isNewUser = isNewUser;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [position = "+position+", __v = "+__v+", provider = "+provider+", avatar = "+avatar+", mode = "+mode+", topicToConnect = "+topicToConnect+", distance = "+distance+", _id = "+_id+", email = "+email+", facebook = "+facebook+", currLocation = "+currLocation+", role = "+role+", dateCreated = "+dateCreated+", affiliation = "+affiliation+", isNewUser = "+isNewUser+"]";
    }

}
