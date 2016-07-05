package org.labkey.test.pages.trialshare;

import org.openqa.selenium.WebDriver;

/**
 * Created by susanh on 6/30/16.
 */
public class StudyEditPage extends CubeObjectEditPage
{
    public static final String SHORT_NAME = "shortName";
    public static final String STUDY_ID = "studyId";
    public static final String TITLE = "title";
    public static final String PARTICIPANT_COUNT = "participantCount";
    public static final String STUDY_TYPE ="studyType";
    public static final String ICON_URL = "iconUrl";
    public static final String EXTERNAL_URL = "externalURL";
    public static final String EXTERNAL_URL_DESCRIPTION = "externalUrlDescription";
    public static final String DESCRIPTION = "description";
    public static final String INVESTIGATOR = "investigator";
    public static final String AGE_GROUPS = "ageGroups";
    public static final String PHASES = "phases";
    public static final String CONDITIONS = "conditions";
    public static final String THERAPEUTIC_AREAS = "therapeuticAreas";

    static
    {
        DROPDOWN_FIELD_NAMES.put(STUDY_TYPE, "Study Type:");
    }

    static
    {
        MULTI_SELECT_FIELD_NAMES.put(AGE_GROUPS, "Age Groups:");
        MULTI_SELECT_FIELD_NAMES.put(PHASES, "Phases:");
        MULTI_SELECT_FIELD_NAMES.put(CONDITIONS, "Conditions:");
        MULTI_SELECT_FIELD_NAMES.put(THERAPEUTIC_AREAS, "Therapeutic Areas:");
    }

    static
    {
        FIELD_NAMES.add(SHORT_NAME);
        FIELD_NAMES.add(STUDY_ID);
        FIELD_NAMES.add(TITLE);
        FIELD_NAMES.add(PARTICIPANT_COUNT);
        FIELD_NAMES.add(STUDY_TYPE);
        FIELD_NAMES.add(ICON_URL);
        FIELD_NAMES.add(EXTERNAL_URL);
        FIELD_NAMES.add(EXTERNAL_URL_DESCRIPTION);
        FIELD_NAMES.add(DESCRIPTION);
        FIELD_NAMES.add(INVESTIGATOR);
        FIELD_NAMES.add(AGE_GROUPS);
        FIELD_NAMES.add(PHASES);
        FIELD_NAMES.add(CONDITIONS);
        FIELD_NAMES.add(THERAPEUTIC_AREAS);
    }

    public StudyEditPage(WebDriver driver)
    {
        super(driver);
    }
}