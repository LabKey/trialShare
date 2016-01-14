package org.labkey.test.pages.trialshare;

import com.google.common.base.Predicate;
import org.junit.Assert;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.Component;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataFinderPage extends LabKeyPage
{
    private static final String CONTROLLER = "trialshare";
    private static final String ACTION = "dataFinder";
    private static final String COUNT_SIGNAL = "dataFinderCountsUpdated";
    private static final String GROUP_UPDATED_SIGNAL = "participantGroupUpdated";

    public DataFinderPage(BaseWebDriverTest test)
    {
        super(test);
    }

    @Override
    protected void waitForPage()
    {
        _test.waitForElement(LabKeyPage.Locators.pageSignal(COUNT_SIGNAL));
    }

    protected void waitForGroupUpdate()
    {
        _test.waitForElement(LabKeyPage.Locators.pageSignal(GROUP_UPDATED_SIGNAL));
    }

    public static DataFinderPage goDirectlyToPage(BaseWebDriverTest test, String containerPath)
    {
        test.beginAt(WebTestHelper.buildURL(CONTROLLER, containerPath, ACTION));
        return new DataFinderPage(test);
    }

    public void selectStudySubset(String text)
    {
        // FIXME isn't there a general method for asking "is this combo list item already selected"?
        _ext4Helper.openComboList(Locators.studySubsetCombo);
        if (!_test.isElementPresent(Ext4Helper.Locators.comboListItemSelected().withText(text)))
        {
            _test.doAndWaitForPageSignal(() ->_ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT), COUNT_SIGNAL);

        }
        else // FIXME you should be able to just close the combo box at this point, but the close method assumes you've chosen something from teh lis
        {
            _ext4Helper.selectItemFromOpenComboList(text, Ext4Helper.TextMatchTechnique.EXACT);
        }
    }

    @LogMethod
    public void studySearch(@LoggedParam final String search)
    {
        _test.doAndWaitForPageSignal(() -> _test.setFormElement(Locators.studySearchInput, search), COUNT_SIGNAL);
    }

    @LogMethod(quiet = true)
    public void clearSearch()
    {
        if (_test.isElementPresent(Locators.studySearchInput) && !_test.getFormElement(Locators.studySearchInput).isEmpty())
            studySearch(" ");
    }

    public void saveGroup(String name)
    {
        _test.setFormElement(Locators.groupLabelInput, name);
        _test.clickButtonContainingText("Save", BaseWebDriverTest.WAIT_FOR_EXT_MASK_TO_DISSAPEAR);
        waitForGroupUpdate();
    }

    public String getGroupLabel()
    {
        return Locators.groupLabel.findElement(_test.getDriver()).getText().trim();
    }

    public GroupMenu getMenu(Locator locator)
    {
        return new GroupMenu(locator.findElement(_test.getDriver()));
    }

    public boolean menuIsDisabled(Locator.CssLocator locator)
    {
        return _test.isElementPresent(locator.append(" .labkey-disabled-text-link"));
    }

    public void openMenu(Locator locator)
    {
        locator.findElement(_test.getDriver()).click();
    }

    public Map<Dimension, Integer> getSummaryCounts()
    {
        WebElement summaryElement = Locators.summaryArea.findElement(_test.getDriver());
        SummaryPanel summary = new SummaryPanel(summaryElement);

        Map<Dimension, Integer> countMap = new HashMap<>();
        for (String value : summary.getValues())
        {
            String[] parts = value.split("\n");
            Dimension dimension = Dimension.fromString(parts[0]);
            Integer count = Integer.parseInt(parts[1].trim());
            countMap.put(dimension, count);
        }
        return countMap;
    }

    public List<StudyCard> getStudyCards()
    {
        List<WebElement> studyCardEls = Locators.studyCard.findElements(_test.getDriver());
        List<StudyCard> studyCards = new ArrayList<>();

        for (WebElement el : studyCardEls)
        {
            studyCards.add(new StudyCard(el));
        }

        return studyCards;
    }

    public FacetGrid getFacetsGrid()
    {
        return new FacetGrid(Locators.facetPanel.findElement(_test.getDriver()));
    }


    public void clearAllFilters()
    {
        if (_test.isElementPresent(Locators.activeClearAll))
        {
            final WebElement clearAll = Locators.activeClearAll.findElement(_test.getDriver());
            if (clearAll.isDisplayed())
            {
                _test.doAndWaitForPageSignal(clearAll::click, COUNT_SIGNAL);
            }
        }
    }

    public void dismissTour()
    {
        _test.shortWait().until(new Predicate<WebDriver>()
        {
            @Override
            public boolean apply(WebDriver webDriver)
            {
                try
                {
                    return (Boolean) _test.executeScript("" +
                            "if (window.hopscotch)" +
                            "  return !hopscotch.endTour().isActive;" +
                            "else" +
                            "  return true;");
                }
                catch (Exception recheck)
                {
                    return false;
                }
            }

            @Override
            public String toString()
            {
                return "tour to be dismissed.";
            }
        });
    }
    
    public static class Locators
    {
        public static final Locator.CssLocator studyFinder = Locator.css(".labkey-data-finder-outer");
        public static final Locator.CssLocator studySearchInput = studyFinder.append(Locator.css("#searchTerms"));
        public static final Locator.XPathLocator studySubsetCombo = Ext4Helper.Locators.formItemWithInputNamed("studySubsetSelect");
        public static final Locator.CssLocator studyCard = studyFinder.append(Locator.css(".labkey-study-card"));
        public static final Locator.CssLocator selectionPanel = studyFinder.append(Locator.css(".labkey-facet-selection-panel"));
        public static final Locator.CssLocator facetPanel = selectionPanel.append(Locator.css(" .labkey-study-facets"));
        public static final Locator.CssLocator summaryArea = selectionPanel.append(Locator.css("#summaryArea"));
        public static final Locator.CssLocator activeClearAll = Locator.css(".labkey-clear-all.active");
        public static final Locator.CssLocator groupLabel = Locator.css(".labkey-group-label");
        public static final Locator.NameLocator groupLabelInput = Locator.name("groupLabel");
        public static final Locator.CssLocator saveMenu = Locator.css("#saveMenu");
        public static final Locator.CssLocator loadMenu = Locator.css("#loadMenu");
        public static final Locator.IdLocator manageMenu = Locator.id("manageMenu");

    }

    public enum Dimension
    {
        STUDIES("studies", null),
        SUBJECTS("subjects", null),
        THERAPEUTIC_AREA("therapeutic area", "Study.Therapeutic Area"),
        STUDY_TYPE("study type", "Study.Study Type"),
        ASSAY("assay", "Study.Assay"),
        AGE_GROUP("age group", "Study.AgeGroup"),
        PHASE("phase", "Study.Phase"),
        CONDITION("condition", "Study.Condition");

        private String caption;
        private String hierarchyName;

        Dimension(String caption, String hierarchyName)
        {
            this.caption = caption;
            this.hierarchyName = hierarchyName;
        }

        public String getCaption()
        {
            return caption;
        }

        public String getHierarchyName()
        {
            return hierarchyName;
        }

        public static Dimension fromString(String value)
        {
            for (Dimension dimension : values())
            {
                if (value.toLowerCase().equals(dimension.getCaption()))
                    return dimension;
            }

            throw new IllegalArgumentException("No such dimension: " + value);
        }
    }


    public class GroupMenu extends Component
    {

        private final WebElement menu;
        private final Elements elements;

        private GroupMenu(WebElement menu)
        {
            this.menu = menu;
            elements = new Elements();
        }

        public void toggleMenu()
        {
            this.menu.click();
        }

        @Override
        public WebElement getComponentElement()
        {
            return menu;
        }

        public List<String> getActiveOptions()
        {
            return getOptions(elements.activeOption);
        }

        public List<String> getInactiveOptions()
        {
            return getOptions(elements.inactiveOption);
        }

        public void chooseOption(String optionText, boolean waitForUpdate)
        {
            _test.log("Choosing menu option " + optionText);
            List<WebElement> activeOptions = findElements(elements.activeOption);
            for (WebElement option : activeOptions)
            {
                if (optionText.equals(option.getText().trim()))
                {
                    option.click();
                    if (waitForUpdate)
                        waitForGroupUpdate();
                    return;
                }
            }
        }

        private List<String> getOptions(Locator locator)
        {
            List<WebElement> options = findElements(locator);
            List<String> optionStrings = new ArrayList<String>();
            for (WebElement option : options)
            {
                optionStrings.add(option.getText().trim());
            }
            return optionStrings;
        }

        private class Elements
        {
            public Locator.CssLocator activeOption = Locator.css(".menu-item-link:not(.inactive)");
            public Locator.CssLocator inactiveOption = Locator.css(".menu-item-link.inactive");
        }
    }

    public class FacetGrid extends Component
    {

        private WebElement grid;
        private Locators locators;

        public FacetGrid(WebElement grid)
        {
            this.grid = grid;
            this.locators = new Locators();
        }

        @Override
        public WebElement getComponentElement()
        {
            return this.grid;
        }

        public void toggleFacet(Dimension dimension, String name)
        {
            Locator.XPathLocator rowLocator = locators.facetMember(dimension, name);
            _test.scrollIntoView(rowLocator);
            WebElement row = rowLocator.findElement(_test.getDriver());

            _test.doAndWaitForPageSignal(() -> row.click(), COUNT_SIGNAL);
        }

        public void clearFilter(Dimension dimension)
        {
            WebElement clearEl = locators.facetClear(dimension).findElement(_test.getDriver());
            Assert.assertFalse("Attempting to clear filter that is not active", clearEl.getAttribute("class").contains("inactive"));
            clearEl.click();
        }

        public Map<Dimension, List<String>> getSelectedMembers()
        {
            Map<Dimension, List<String>> selections = new HashMap<>();
            for (Dimension dimension : Dimension.values())
            {
                if (dimension.getHierarchyName() != null)
                {
                    selections.put(dimension, getSelectedMembers(dimension));
                }
            }
            return selections;

        }

        public List<String> getSelectedMembers(Dimension dimension)
        {
            List<WebElement> selectedElements =  locators.facetMemberSelected(dimension).findElements(_test.getDriver());
            List<String> selectedNames = new ArrayList<>();
            for (WebElement element: selectedElements)
            {
                selectedNames.add(locators.memberName.findElement(element).getText());
            }
            return selectedNames;
        }

        public Map<String, Integer> getMemberCounts(Dimension dimension)
        {
            List<WebElement> memberElements = locators.facetMembers(dimension).findElements(_test.getDriver());
            Map<String, Integer> countMap = new HashMap<>();
            for (WebElement member : memberElements)
            {
                String name = locators.memberName.findElement(member).getText();
                Integer count = Integer.valueOf(locators.memberCount.findElement(member).getText());
                countMap.put(name, count);
            }
            return countMap;

        }

        private class Locators
        {
            public Locator.XPathLocator facetMemberRow(Dimension dimension, String name)
            {
                return Locator.tagWithAttribute("tr", "data-recordid", "[" + dimension.getHierarchyName() + "].[" + name + "]");
            }

            public Locator.XPathLocator facetMember(Dimension dimension, String name)
            {
                return facetMemberRow(dimension, name).append("//span[contains(@class, 'labkey-facet-member')]");
            }

            public Locator.XPathLocator facetGroup(Dimension dimension)
            {
                return new Locator.XPathLocator("//tr[contains(@data-recordid,'" + dimension.getHierarchyName() + "')]");
            }

            public Locator.XPathLocator facetGroupTitle(Dimension dimension)
            {
                //tr[contains(@data-recordid,"Study.Therapeutic Area")]//div[@class="x4-grid-group-title"]
                return facetGroup(dimension).append("//div[@class='x4-grid-group-title']");
            }

            public Locator.XPathLocator facetClear(Dimension dimension)
            {
                return new Locator.XPathLocator("//tr[contains(@data-recordid,'" + dimension.getHierarchyName() + "')]//span[contains(@class,'labkey-clear-filter')]");
            }

            public Locator.XPathLocator facetMemberSelected(Dimension dimension)
            {
                //tr[contains(@data-recordid, 'Therapeutic Area')][contains(@class, 'x4-grid-row-selected')]
                return facetGroup(dimension).append("[contains(@class, 'x4-grid-row-selected')]");
            }

            public Locator.XPathLocator facetMembers(Dimension dimension)
            {

                return facetGroup(dimension).append("//span[contains(concat(' ', normalize-space(@class), ' '), ' labkey-facet-member ')]");
            }

            public Locator.CssLocator memberCount = Locator.css(".labkey-facet-member-count");
            public Locator.CssLocator memberName = Locator.css(".labkey-facet-member-name");
            public Locator.CssLocator emptyMemberName = Locator.css(".labkey-empty-member .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyMemberName = Locator.css(".labkey-facet-member:not(.labkey-empty-member) .labkey-facet-member-name");
            public Locator.CssLocator selectedMemberName = Locator.css(".x4-grid-row-selected .labkey-facet-member-name");
            public Locator.CssLocator nonEmptyNonSelectedMemberName = Locator.css(".x4-grid-row:not(.x4-grid-row-selected) .labkey-facet-member:not(.labkey-empty-member) .labkey-member-name");
        }
    }

    public class SummaryPanel extends Component
    {
        private WebElement panel;
        private Elements elements;
        private Dimension dimension;

        private SummaryPanel(WebElement panel)
        {
            this.panel = panel;
            elements = new Elements();
        }

        @Override
        public WebElement getComponentElement()
        {
            return panel;
        }

        public List<String> getValues()
        {
            return _test.getTexts(findElements(elements.member));
        }

        private class Elements
        {
            public Locator.CssLocator member = Locator.css(".labkey-facet-member");
        }
    }

    public class StudyCard
    {
        WebElement card;
        Elements elements;
        String title;
        String accession;
        String pi;

        private StudyCard(WebElement card)
        {
            this.card = card;
            elements = new Elements();
        }

        public WebElement getCardElement()
        {
            return card;
        }

        public StudySummaryWindow viewSummary()
        {
            elements.viewStudyLink.findElement(card).click();
            return new StudySummaryWindow(_test);
        }

        public void clickGoToStudy()
        {
            _test.clickAndWait(elements.goToStudyLink.findElement(card));
        }

        public String getAccession()
        {
            return elements.accession.findElement(card).getText();
        }

        public String getShortName()
        {
            return elements.shortName.findElement(card).getText();
        }

        public String getPI()
        {
            return elements.PI.findElement(card).getText();
        }

        public String getTitle()
        {
            return elements.title.findElement(card).getText();
        }

        private class Elements
        {
            public Locator viewStudyLink = Locator.linkWithText("view summary");
            public Locator goToStudyLink = Locator.linkWithText("go to study");
            public Locator accession = Locator.css(".labkey-study-card-accession");
            public Locator shortName = Locator.css(".labkey-study-card-short-name");
            public Locator PI = Locator.css(".labkey-study-card-pi");
            public Locator title = Locator.css(".labkey-study-card-title");
        }
    }
}