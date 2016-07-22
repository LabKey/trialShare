/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.labkey.test.tests.trialshare;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.Locator;
import org.labkey.test.ModulePropertyValue;
import org.labkey.test.TestTimeoutException;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.components.trialshare.PublicationPanel;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.PermissionsEditor;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.pages.trialshare.PublicationsListHelper;
import org.labkey.test.pages.trialshare.StudiesListHelper;
import org.labkey.test.util.APIContainerHelper;
import org.labkey.test.util.AbstractContainerHelper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.PortalHelper;
import org.labkey.test.util.ReadOnlyTest;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Category({Git.class})
public class TrialShareDataFinderTest extends DataFinderTestBase implements ReadOnlyTest
{
    private static final String RELOCATED_DATA_FINDER_PROJECT = "RelocatedDataFinder";

    @Override
    protected void doCleanup(boolean afterTest) throws TestTimeoutException
    {
        super.doCleanup(afterTest);
        _containerHelper.deleteProject(RELOCATED_DATA_FINDER_PROJECT, afterTest);
    }

    @BeforeClass
    public static void initTest()
    {
        TrialShareDataFinderTest init = (TrialShareDataFinderTest)getCurrentTest();

        if (init.needsSetup())
            init.setUpProject();
    }

    @Override
    protected String getProjectName()
    {
        return "TrialShareDataFinderTest Project";
    }

    @Override
    public boolean needsSetup()
    {

        try
        {
            return HttpStatus.SC_NOT_FOUND == WebTestHelper.getHttpGetResponse(WebTestHelper.buildURL("project", getProjectName(), "begin"));
        }
        catch (IOException fail)
        {
            return true;
        }
    }

    @Override
    protected void createStudies()
    {
        log("Creating a study container for each study");
        for (String subset : studySubsets.keySet())
        {
            for (String accession : studySubsets.get(subset))
            {
                String name = "DataFinderTest" + subset + accession;
                createStudy(name, subset.equalsIgnoreCase("operational"));
            }
        }
        createStudy(PUBLIC_STUDY_NAME);
        createStudy(OPERATIONAL_STUDY_NAME);
        goToProjectHome();
        StudiesListHelper queryUpdatePage = new StudiesListHelper(this);
        queryUpdatePage.setStudyContainers();
        goToProjectHome();
        PublicationsListHelper pubUpdatePage = new PublicationsListHelper(this);
        pubUpdatePage.setPermissionsContainers("/" + getProjectName() + "/" + PUBLIC_STUDY_NAME, "/" + getProjectName() + "/" + OPERATIONAL_STUDY_NAME);
    }

    protected void createUsers()
    {
        log("Creating users and setting permissions");
        goToProjectHome();

        _userHelper.createUser(PUBLIC_READER);
        _userHelper.createUser(CASALE_READER);
        _userHelper.createUser(WISPR_READER);

        PermissionsEditor permissionsEditor = new PermissionsEditor(this);

        goToProjectHome();
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setSiteGroupPermissions("All Site Users", "Reader");

        for (String subset : studySubsets.keySet())
        {
            for (String accession : studySubsets.get(subset))
            {
                String name = "DataFinderTest" + subset + accession;
                doAndWaitForPageToLoad(() -> permissionsEditor.selectFolder(name));
                sleep(500); // HACK, but waitForPageLoad doesn't do the trick here.  Perhaps waitForElement would work...
                if (subset.equalsIgnoreCase("public"))
                {
                    permissionsEditor.setUserPermissions(PUBLIC_READER, "Reader");
                    permissionsEditor.setUserPermissions(WISPR_READER, "Reader");
                }
                if (accession.equalsIgnoreCase("Casale"))
                {
                    permissionsEditor.setUserPermissions(CASALE_READER, "Reader");
                }
                else if (accession.equalsIgnoreCase("WISP-R"))
                {
                    permissionsEditor.setUserPermissions(WISPR_READER, "Reader");
                }
            }
        }
        permissionsEditor.selectFolder(PUBLIC_STUDY_NAME);
        permissionsEditor.setUserPermissions(PUBLIC_READER, "Reader");
        permissionsEditor.setUserPermissions(WISPR_READER, "Reader");
    }

    @Test
    public void testCounts()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        assertCountsSynced(finder, DataFinderPage.Dimension.STUDIES);

        Map<DataFinderPage.Dimension, Integer> studyCounts = finder.getSummaryCounts();

        for (Map.Entry<DataFinderPage.Dimension, Integer> count : studyCounts.entrySet())
        {
            if (count.getKey().getCaption() != null)
                assertNotEquals("No " + count.getKey().getCaption(), 0, count.getValue().intValue());
        }
    }

    @Test
    public void testStudyCards()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);

        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();

        studyCards.get(0).viewStudySummary();
    }

    @Test
    public void testStudySubset()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        Set<String> linkedStudyNames = new HashSet<>();
        for (String subset : studySubsets.keySet())
        {
            log("Toggle facet: " + subset);
            facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, subset);
            List<DataFinderPage.DataCard> studyCards = finder.getDataCards();
            Set<String> studies = new HashSet<>();
            for (DataFinderPage.DataCard studyCard : studyCards)
            {
                studies.add(studyCard.getStudyShortName());
            }
            assertEquals("Wrong study cards for studies", studySubsets.get(subset), studies);
            facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, subset);
            linkedStudyNames.addAll(getTexts(Locator.tagWithClass("div", "labkey-study-card").withPredicate(Locator.linkWithText("go to study"))
                    .append(Locator.tagWithClass("span", "labkey-study-card-short-name")).findElements(getDriver())));
        }
    }

    @Test
    public void testPublicAccess()
    {
        goToProjectHome();
        impersonate(PUBLIC_READER);
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        Assert.assertFalse("Public user should not see the visibility facet", facetGrid.facetIsPresent(DataFinderPage.Dimension.VISIBILITY));
        List<DataFinderPage.DataCard> cards = finder.getDataCards();
        Assert.assertEquals("Number of studies not as expected", studySubsets.get("Public").size(), cards.size());
        stopImpersonating();
        doAndWaitForPageSignal(() -> goToProjectHome(), finder.getCountSignal());

        sleep(1000);
        Assert.assertTrue("Admin user should see visibility facet", facetGrid.facetIsPresent(DataFinderPage.Dimension.VISIBILITY));

        doAndWaitForPageSignal(() -> impersonate(CASALE_READER), finder.getCountSignal());
        Assert.assertFalse("User with access to only  Casale study should not see the visibility facet", facetGrid.facetIsPresent(DataFinderPage.Dimension.VISIBILITY));
        cards = finder.getDataCards();
        Assert.assertEquals("User with access to only Casale study should see only that study", 1, cards.size());
        stopImpersonating();
    }

    @Test
    public void testOperationalAccess()
    {
        goToProjectHome();
        impersonate(WISPR_READER);
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        Assert.assertTrue("Operational user should see visibility facet", facetGrid.facetIsPresent(DataFinderPage.Dimension.VISIBILITY));
        facetGrid.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Operational");
        List<DataFinderPage.DataCard> cards = finder.getDataCards();
        Assert.assertEquals("User with access to only WISP-R study should see only that study", 1, cards.size());
        stopImpersonating();
    }


    @Test
    public void testDataFinderRelocation()
    {
        log("Test that we can put the data finder in a project other than the one with the cube definition and lists");
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);
        containerHelper.deleteProject(RELOCATED_DATA_FINDER_PROJECT, false);
        containerHelper.createProject(RELOCATED_DATA_FINDER_PROJECT, "Custom");
        containerHelper.addCreatedProject(RELOCATED_DATA_FINDER_PROJECT);
        containerHelper.enableModule(MODULE_NAME);
        List<ModulePropertyValue> propList = new ArrayList<>();
        propList.add(new ModulePropertyValue("TrialShare", "/" + RELOCATED_DATA_FINDER_PROJECT, "DataFinderCubeContainer", getProjectName()));
        setModuleProperties(propList);

        goToProjectHome(RELOCATED_DATA_FINDER_PROJECT);
        new PortalHelper(this).addWebPart(WEB_PART_NAME);
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        Assert.assertTrue("Should see the visibility facet", facetGrid.facetIsPresent(DataFinderPage.Dimension.VISIBILITY));
        facetGrid.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Public");
        Assert.assertEquals("Should see all the public data cards", 4, finder.getDataCards().size());
        goToProjectHome();
    }

    @Test
    public void testSelection()
    {
        DataFinderPage finder = new DataFinderPage(this, true);

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Public");
        facets.toggleFacet(DataFinderPage.Dimension.AGE_GROUP, "Adult");
        facets.toggleFacet(DataFinderPage.Dimension.PHASE, "Phase 1");

        assertCountsSynced(finder, DataFinderPage.Dimension.STUDIES);

        facets.clearFilter(DataFinderPage.Dimension.PHASE);

        assertEquals("Clearing Phase filters did not remove selection",  Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        // re-select
        facets.toggleFacet(DataFinderPage.Dimension.PHASE, "Phase 1");
        // deselect
        facets.toggleFacet(DataFinderPage.Dimension.AGE_GROUP, "Adult");
        assertEquals("Clearing selection did not remove selection", Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.AGE_GROUP));
        assertEquals("Clearing selection removed other filter", Collections.singletonList("Phase 1"), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        finder.clearAllFilters();
        assertEquals("Clearing all filters didn't clear selection", Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        assertCountsSynced(finder, DataFinderPage.Dimension.STUDIES);
    }

    @Test
    public void testSelectingEmptyMeasure()
    {
        Map<DataFinderPage.Dimension, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put(DataFinderPage.Dimension.STUDIES, 0);
        expectedCounts.put(DataFinderPage.Dimension.SUBJECTS, 0);

        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);
        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Operational");
        facets.toggleFacet(DataFinderPage.Dimension.CONDITION, "Allergy");

        facets = finder.getFacetsGrid();
        List<DataFinderPage.DataCard> filteredStudyCards = finder.getDataCards();
        assertEquals("Study cards visible after selection", 0, filteredStudyCards.size());

        Map<DataFinderPage.Dimension, Integer> filteredSummaryCounts = finder.getSummaryCounts();
        assertEquals("Wrong counts after selecting empty measure", expectedCounts, filteredSummaryCounts);

        for (DataFinderPage.Dimension dimension : DataFinderPage.Dimension.values())
        {
            if (dimension.getHierarchyName() != null)
            {
                Map<String, String> memberCounts = facets.getMemberCounts(dimension);
                for (Map.Entry<String,String> memberCount : memberCounts.entrySet())
                {
                    assertTrue("Wrong counts for member " + memberCount.getKey() + " of dimension " + dimension + " after selecting empty measure: " + memberCount.getValue(), memberCount.getValue().matches("0 / \\d+"));
                }
            }
        }
    }

    @Test
    public void testStudySearch()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Operational");


        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();
        String searchString = studyCards.get(0).getStudyAccession();

        testSearchTerm(finder, DataFinderPage.Dimension.STUDIES, "Study Accession", searchString, 1);
        finder.clearSearch();
        assertTrue("Clear all should still be active after clearing search with facet selected", finder.clearAllActive());
        finder.clearAllFilters();
        assertTrue("Clear All should no longer be active", !finder.clearAllActive());
        testSearchTerm(finder, DataFinderPage.Dimension.STUDIES, "Study Short Name and Investigator", "Casale", 1);
        testSearchTerm(finder, DataFinderPage.Dimension.STUDIES, "Empty", "", 8);
        testSearchTerm(finder, DataFinderPage.Dimension.STUDIES, "Title", "System", 2);
        testSearchTerm(finder, DataFinderPage.Dimension.STUDIES, "Multiple Terms", "Tolerant Kidney Transplant", 7);
    }


    @Test
    public void testStudySearchPermissions()
    {
        impersonate(PUBLIC_READER);
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);

        finder.search("transplant");

        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();

        assertEquals("Wrong number of studies after search", 2, studyCards.size());
        assertEquals("Wrong study card available", "Shapiro", studyCards.get(0).getStudyShortName());

        assertCountsSynced(finder, DataFinderPage.Dimension.STUDIES);
        stopImpersonating();
    }

    @Test
    public void testPublicationSearch()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        finder.clearAllFilters();

        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Author", "Asare", 7);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "PMID", "21953143", 1);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Keyword", "Alemtuzier", 1);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Abstract", "Lorem Ipsum", 1);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Citation", "New Eng",  2);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Study, facet value, regular word", "FACTOR", 3);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Journal facet",  "\"New England Journal\"", 1);
        testSearchTerm(finder, DataFinderPage.Dimension.PUBLICATIONS, "Empty", "", 16);

        finder.clearSearch();
        assertTrue("Clear All should no longer be active", !finder.clearAllActive());
    }

    private void testSearchTerm(DataFinderPage finder, DataFinderPage.Dimension dimension, String field, String term, int expectedCount)
    {
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();
        finder.search(term);
        if (expectedCount > 0)
            shortWait().until(ExpectedConditions.stalenessOf(dataCards.get(0).getCardElement()));
        if (!StringUtils.isEmpty(term))
        {
            assertTrue("Clear All should be active after entering search term", finder.clearAllActive());
        }
        dataCards = finder.getDataCards();
        assertEquals("Wrong number of cards after search for '" + term + "' (" + field + ")", expectedCount, dataCards.size());
        assertCountsSynced(finder, dimension);
    }


    @Test
    public void testPublicationSearchPermissions()
    {
        impersonate(PUBLIC_READER);
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        finder.search("Progress");
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();

        assertEquals("Wrong number of cards after search", 0, dataCards.size());

        assertCountsSynced(finder, DataFinderPage.Dimension.PUBLICATIONS);
        stopImpersonating();
    }

    @Test
    public void testStudySummaryWindow()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);

        DataFinderPage.DataCard studyCard = finder.getDataCards().get(0);

        StudySummaryWindow summaryWindow = studyCard.viewStudySummary();

        assertEquals("Study card does not match summary (Accession)", studyCard.getStudyAccession().toLowerCase(), summaryWindow.getAccession().toLowerCase());
        assertEquals("Study card does not match summary (Short Name)", studyCard.getStudyShortName().toLowerCase(), summaryWindow.getShortName().toLowerCase());
        assertEquals("Study card does not match summary (Title)", studyCard.getTitle().toUpperCase(), summaryWindow.getTitle().toUpperCase());

        summaryWindow.closeWindow();
    }

    @Test
    public void testGoToStudyMenu()
    {
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);
        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        log("Filtering to show DIAMOND card with two study containers");
        facets.toggleFacet(DataFinderPage.Dimension.VISIBILITY, "Public");
        doAndWaitForPageSignal(() -> facets.toggleFacet(DataFinderPage.Dimension.CONDITION, "Lupus Nephritis"), finder.getCountSignal());
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();
        Assert.assertEquals("Should have a single data card at this point", 1, dataCards.size());
        DataFinderPage.DataCard card = dataCards.get(0);
        Assert.assertEquals("DIAMOND", card.getStudyShortName());
        log("Go to operational study");
        card.clickGoToStudy("/" + getProjectName() + "/DataFinderTestOperationalDIAMOND");
    }

    @Test
    public void testGoToStudyNoMenuForPublicReader()
    {
        log("Impersonating public reader who should see only one go to study link");
        impersonate(PUBLIC_READER);
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), true);
        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        log("Filtering to show DIAMOND card");
        doAndWaitForPageSignal(() -> facets.toggleFacet(DataFinderPage.Dimension.CONDITION, "Lupus Nephritis"), finder.getCountSignal());
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();
        Assert.assertEquals("Should have a single data card at this point", 1, dataCards.size());
        DataFinderPage.DataCard card = dataCards.get(0);
        Assert.assertEquals("DIAMOND", card.getStudyShortName());
        card.clickGoToStudy();
        stopImpersonating();
    }

    @Test
    public void testSwitchBetweenStudyAndPublication()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        log("Start at home.");
        doAndWaitForPageSignal(() -> goToProjectHome(), finder.getCountSignal());
        waitForElement(DataFinderPage.Locators.studyFinder);
        log("Click the 'Publications' tab");
        finder.navigateToPublications();
        log("Go back by clicking the 'Studies' tab");
        finder.navigateToStudies();
    }

    @Test
    public void testFilterOnStatus()
    {
        String cardTitle = "Circulating markers of vascular injury";
        String cardAuthors = "Monach PA, Tomasson G, Specks U, et al.";
        String cardText;
        Map<String, String> counts;

        log("Go to publications and verify the default filtered of 'In Progress' on Status.");
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        DataFinderPage.FacetGrid fg = finder.getFacetsGrid();
        assertEquals("Finder is not filtered by In Progress by default for internal user", Collections.singletonList(DataFinderPage.Dimension.IN_PROGRESS.getHierarchyName()), fg.getSelectedMembers(DataFinderPage.Dimension.STATUS));

        log("Validate that the number, content and style of the cards is as expected.");
        counts = fg.getMemberCounts(DataFinderPage.Dimension.IN_PROGRESS);
        assertEquals("Expected count after filtering for 'In Progress' was not as expected.", "1 / 1", counts.get("In Progress"));

        // I have no idea why assertTextPresent returned false for these strings. The below tests appear to be more reliable.
        scrollIntoView(DataFinderPage.Locators.pubCardBorderHighlight);
        cardText = getText(DataFinderPage.Locators.pubCardBorderHighlight);
        assertTrue("Could not find '" + cardTitle + "' on card.", cardText.contains(cardTitle));
        assertTrue("Could not find '" + cardAuthors + "' on card.", cardText.contains(cardAuthors));

        log("Validate that there is only one publication card present and has the correct style.");
        assertElementPresent(DataFinderPage.Locators.pubCard, 1);
        assertElementVisible(DataFinderPage.Locators.pubCardBorderHighlight);
        assertElementVisible(DataFinderPage.Locators.pubCardBackgroundHighlight);
        assertElementPresent(DataFinderPage.Locators.pubCardBorderHighlight, 1);
        assertElementPresent(DataFinderPage.Locators.pubCardBackgroundHighlight, 1);

        log("Remove the 'In Progress' filter, and apply the 'Complete' filter.");
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "In Progress");
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "Complete");

        log("Validate counts for 'Complete' publications.");
        counts = fg.getMemberCounts(DataFinderPage.Dimension.COMPLETE);
        // one is "in progress" and one is set to not show
        assertEquals("Expected count after filtering for 'Complete' was not as expected.", "15 / 15", counts.get("Complete"));

        log("Validate that there are no 'In Progress' cards visible.");
        assertElementNotPresent("There is a card with the 'In Progress' style, there should not be.", DataFinderPage.Locators.pubCardBorderHighlight);

    }

    @Test
    public void testPublicationStatusForReader()
    {
        impersonate(PUBLIC_READER);
        log("Go to publications and clear any filters that may have been set.");
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        finder.clearAllFilters();
        Map<DataFinderPage.Dimension, Integer> summaryCount = finder.getSummaryCounts();
        DataFinderPage.FacetGrid fg = finder.getFacetsGrid();
        Assert.assertFalse("Status facet should not be present for someone with only read permission", fg.facetIsPresent(DataFinderPage.Dimension.STATUS));

        // one publication has "show on dash" set to false; one publication is "in progress" and thus not visible to the public
        Assert.assertEquals("Publication count should not count incomplete publications", (Integer) 15, summaryCount.get(DataFinderPage.Dimension.PUBLICATIONS));
        stopImpersonating();
    }

    @Test
    public void testPublicationDetail()
    {
        DataFinderPage.FacetGrid fg;
        Map<DataFinderPage.Dimension, Integer> summaryCount;


        log("Go to publications and clear any filters that may have been set.");
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);

        finder.clearAllFilters();

        log("Filter for a publication that has DOI, PMID and PMCID values.");
        fg = finder.getFacetsGrid();
        doAndWaitForPageSignal(() -> fg.toggleFacet(DataFinderPage.Dimension.STATUS, "In Progress"), finder.getCountSignal());


        summaryCount = finder.getSummaryCounts();
        assertTrue("Number of publication cards returned does not match dimension count. Number of cards: " + finder.getDataCards().size() + " Count in dimension: " + summaryCount.get(DataFinderPage.Dimension.PUBLICATIONS), summaryCount.get(DataFinderPage.Dimension.PUBLICATIONS) == finder.getDataCards().size());

        log("Click the 'More Details' and validate that the detail content is as expected.");
        DataFinderPage.DataCard card = finder.getDataCards().get(0);
        PublicationPanel publicationPanel = card.viewDetail();

        assertTrue("Author value not as expected on detail page: " + publicationPanel.getAuthor(), publicationPanel.getAuthor().contains("Monach PA, Tomasson G, Specks U, Stone JH, Cuthbertson D"));
        assertTrue("Title value not as expected on detail page:" + publicationPanel.getTitle(), publicationPanel.getTitle().contains("Circulating markers of vascular injury and angiogenesis in Antineutrophil Cytoplasmic Antibody-Associated Vasculitis."));
        assertTrue("Citation value not as expected on detail page:" + publicationPanel.getCitation(), publicationPanel.getCitation().contains("Arthritis Rheum 63: 3988-3997, 2011"));
        assertTrue("PMID value not as expected on detail page:" + publicationPanel.getPMID(), publicationPanel.getPMID().contains("21953143"));
        assertTrue("PMCID value not as expected on detail page:" + publicationPanel.getPMCID(), publicationPanel.getPMCID().contains("PMC3227746"));
        assertTrue("DOI value not as expected on detail page:" + publicationPanel.getDOI(), publicationPanel.getDOI().contains("10.1002/ART.30615"));
        assertTrue("Studies value not as expected on detail page:" + publicationPanel.getStudyShortName(), publicationPanel.getStudyShortName().contains("RAVE"));

        card = finder.getDataCards().get(0);
        card.hideDetail();
        publicationPanel = new PublicationPanel(this);
        Assert.assertFalse("Author value not as expected in collapsed view", publicationPanel.getAuthor().contains("Cuthbertson"));
        Assert.assertFalse("PMID should not be displayed in collapsed view", publicationPanel.isPMIDDisplayed());
        Assert.assertFalse("PMCID should not be displayed in collapsed view", publicationPanel.isPMCIDDisplayed());

        // open it up again and make sure we have only one copy of the fields
        card = finder.getDataCards().get(0);
        card.viewDetail();
        Assert.assertEquals("Should still have just one PMID", 1, publicationPanel.getPMIDCount());


        log("Go to another publication that doesn't have the same type of detail.");
        finder.clearAllFilters();
        fg.toggleFacet(DataFinderPage.Dimension.PUB_STUDY, "RAVE");
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "Complete");
        fg.toggleFacet(DataFinderPage.Dimension.PUB_THERAPEUTIC_AREA, "Autoimmune");
        fg.toggleFacet(DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript");

        summaryCount = finder.getSummaryCounts();
        assertEquals("Number of studies count not as expected.", 2, summaryCount.get(DataFinderPage.Dimension.STUDIES).intValue());

        log("Show details, this time validate that the missing values are rendered as expected.");
        card = finder.getDataCards().get(0);
        publicationPanel = card.viewDetail();

        assertTrue("Author value not as expected on detail page:" + publicationPanel.getAuthor(), publicationPanel.getAuthor().contains("Ytterberg SR, Mueller M, Sejismundo LP, Mieras K, Stone JH."));
        assertTrue("Title value not as expected on detail page.", publicationPanel.getTitle().contains("Efficacy of Remission-Induction Regimens for ANCA-Associated Vasculitis"));
        assertTrue("Citation value not as expected on detail page.", publicationPanel.getCitation().contains("New Eng J Med 2013; 369:417-427"));
        assertTrue("PMID value not as expected on detail page.", publicationPanel.getPMID().contains("23902481"));
        assertTrue("PMCID value not as expected on detail page.", publicationPanel.getPMCID().contains(""));
        assertTrue("DOI value not as expected on detail page.", publicationPanel.getDOI().contains(""));
        assertTrue("Studies value not as expected on detail page.", publicationPanel.getStudyShortName().contains("RAVE"));

        card = finder.getDataCards().get(0);
        card.hideDetail();
    }


    @LogMethod(quiet = true)
    private void assertCountsSynced(DataFinderPage finder, DataFinderPage.Dimension dimension)
    {
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();
        Map<DataFinderPage.Dimension, Integer> summaryCounts = finder.getSummaryCounts();

        assertEquals("Summary count mismatch", dataCards.size(), summaryCounts.get(dimension).intValue());
    }
}