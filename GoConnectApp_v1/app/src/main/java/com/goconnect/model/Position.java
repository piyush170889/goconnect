package com.goconnect.model;

import java.io.Serializable;

public class Position implements Serializable
{
    private String organisationName;

    private String jobTitle;

    public String getOrganisationName ()
    {
        return organisationName;
    }

    public void setOrganisationName (String organisationName)
    {
        this.organisationName = organisationName;
    }

    public String getJobTitle ()
    {
        return jobTitle;
    }

    public void setJobTitle (String jobTitle)
    {
        this.jobTitle = jobTitle;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [organisationName = "+organisationName+", jobTitle = "+jobTitle+"]";
    }
}
			
			