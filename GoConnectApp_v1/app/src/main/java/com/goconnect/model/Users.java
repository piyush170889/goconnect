package com.goconnect.model;

import java.io.Serializable;

public class Users implements Serializable
{
    private Distl1[] distl1;

    private MoreData[] moreData;

    private Distl2[] distl2;

    private Distl3[] distl3;

    public Distl1[] getDistl1 ()
    {
        return distl1;
    }

    public void setDistl1 (Distl1[] distl1)
    {
        this.distl1 = distl1;
    }

    public MoreData[] getMoreData ()
    {
        return moreData;
    }

    public void setMoreData (MoreData[] moreData)
    {
        this.moreData = moreData;
    }

    public Distl2[] getDistl2 ()
    {
        return distl2;
    }

    public void setDistl2 (Distl2[] distl2)
    {
        this.distl2 = distl2;
    }

    public Distl3[] getDistl3 ()
    {
        return distl3;
    }

    public void setDistl3 (Distl3[] distl3)
    {
        this.distl3 = distl3;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [distl1 = "+distl1+", moreData = "+moreData+", distl2 = "+distl2+", distl3 = "+distl3+"]";
    }
}