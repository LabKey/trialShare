package org.labkey.test.components.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.components.Component;
import org.labkey.test.util.LogMethod;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PublicationPanel extends Component
{
    protected BaseWebDriverTest _test;
    protected WebElement _panel;

    public PublicationPanel(BaseWebDriverTest test)
    {
        _test = test;
    }

    @Override
    public WebElement getComponentElement()
    {
        return _panel;
    }

    public String getAuthor()
    {
        return getTextOfElement(Locators.allAuthors);
    }

    public String getTitle()
    {
        return getTextOfElement(Locators.title);
    }

    public String getCitation()
    {
        return getTextOfElement(Locators.citation);
    }

    public boolean isPMIDDisplayed()
    {
        return _test.isElementVisible(Locators.PMID);
    }

    public int getPMIDCount()
    {
        return Locators.PMID.findElements(_test.getDriver()).size();
    }

    public String getPMID()
    {
        return getTextOfElement(Locators.PMID);
    }
    public void clickPMID() { _test.click(Locators.PMID); }

    public boolean isPMCIDDisplayed()
    {
        return _test.isElementVisible(Locators.PMCID);
    }
    public String getPMCID()
    {
        return getTextOfElement(Locators.PMCID);
    }
    public void clickPMCID() { _test.click(Locators.PMCID); }

    public String getDOI()
    {
        return getTextOfElement(Locators.DOI);
    }
    public void clickDOI() { _test.click(Locators.DOI); }

    public String getStudyShortName()
    {
        return getTextOfElement(Locators.studyShortName);
    }

    @LogMethod(quiet = true)
    private String getTextOfElement(Locator el)
    {
        try
        {
            return _test.getText(el);
        }
        catch (org.openqa.selenium.NoSuchElementException nse)
        {
            _test.log("Element not found returning an empty string.");
            return "";
        }
    }


    private static class Locators
    {
        private static Locator.CssLocator publicationCard = Locator.css(".labkey-publication-card");
        private static Locator.CssLocator allAuthors = publicationCard.append(Locator.css("#fullAuthorList"));
        private static Locator.CssLocator title = publicationCard.append(Locator.css(".labkey-publication-title"));
        private static Locator.CssLocator citation = publicationCard.append(Locator.css(".labkey-publication-citation"));
        private static Locator.XPathLocator PMID = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')]//a[contains(text(), 'PMID')]");
        private static Locator.XPathLocator PMCID = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')]//a[contains(text(), 'PMCID')]");
        private static Locator.XPathLocator DOI = Locator.xpath("//div[contains(@class, 'labkey-publication-detail')]//span[contains(@class, 'labkey-publication-identifier')]//a[contains(text(), 'DOI')]");
        private static Locator.CssLocator studyShortName = Locator.css(".labkey-study-short-name");
        private static Locator.CssLocator closeButton = Locator.css(".x4-tool-close");
    }

}
