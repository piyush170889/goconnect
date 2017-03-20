package com.goconnect.model;

import java.io.Serializable;

public class Affiliation implements Serializable{
	private String mainAffiliation;

    private String endYear;

    private String college;

    private String uwcContact;

    public String getMainAffiliation ()
    {
        return mainAffiliation;
    }

    public void setMainAffiliation (String mainAffiliation)
    {
        this.mainAffiliation = mainAffiliation;
    }

    public String getEndYear ()
    {
        return endYear;
    }

    public void setEndYear (String endYear)
    {
        this.endYear = endYear;
    }

    public String getCollege ()
    {
        return college;
    }

    public void setCollege (String college)
    {
        this.college = college;
    }

    public String getUwcContact ()
    {
        return uwcContact;
    }

    public void setUwcContact (String uwcContact)
    {
        this.uwcContact = uwcContact;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [mainAffiliation = "+mainAffiliation+", endYear = "+endYear+", college = "+college+", uwcContact = "+uwcContact+"]";
    }
}
