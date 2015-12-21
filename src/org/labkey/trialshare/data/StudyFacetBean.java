package org.labkey.trialshare.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by susanh on 12/8/15.
 */
public class StudyFacetBean
{
    private String name;
    private String pluralName;
    private String hierarchyName;
    private String levelName;
    private String allMemberName;
    private FacetFilter.Type defaultFilterType;
    private List<FacetFilter> filterOptions;
    private Integer ordinal;

    public StudyFacetBean() {}

    public StudyFacetBean(String name, String pluralName, String hierarchyName, String levelName, String allMembersName, FacetFilter.Type defaultFilterType, Integer ordinal) {
        this.name = name;
        this.pluralName = pluralName;
        this.hierarchyName = hierarchyName;
        this.levelName = levelName;
        this.allMemberName = allMembersName;
        this.defaultFilterType = defaultFilterType;
        this.ordinal = ordinal;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAllMemberName()
    {
        return allMemberName;
    }

    public void setAllMemberName(String allMemberName)
    {
        this.allMemberName = allMemberName;
    }

    public List<FacetFilter> getFilterOptions()
    {
        return filterOptions;
    }

    public void setFilterOptions(List<FacetFilter> filterOptions)
    {
        this.filterOptions = filterOptions;
    }

    public FacetFilter.Type getDefaultFilterType()
    {
        return defaultFilterType;
    }

    public void setDefaultFilterType(FacetFilter.Type defaultFilterType)
    {
        this.defaultFilterType = defaultFilterType;
    }

    public String getHierarchyName()
    {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName)
    {
        this.hierarchyName = hierarchyName;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public String getPluralName()
    {
        return pluralName;
    }

    public void setPluralName(String pluralName)
    {
        this.pluralName = pluralName;
    }

    public Integer getOrdinal()
    {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal)
    {
        this.ordinal = ordinal;
    }
}
