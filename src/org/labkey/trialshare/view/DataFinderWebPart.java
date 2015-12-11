package org.labkey.trialshare.view;

import org.labkey.api.data.Container;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.JspView;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;

public class DataFinderWebPart extends JspView
{
    boolean isAutoResize = false;

    public boolean isAutoResize()
    {
        return isAutoResize;
    }

    public void setIsAutoResize(boolean isAutoResize)
    {
        this.isAutoResize = isAutoResize;
    }

    public DataFinderWebPart(Container c)
    {
        super("/org/labkey/trialshare/view/dataFinder.jsp");
        setTitle("Data Finder");
        setTitleHref(new ActionURL(TrialShareController.DataFinderAction.class, c));
    }
    public DataFinderWebPart(ViewContext v)
    {
        this(v.getContainer());
    }

    @Override
    public void setIsOnlyWebPartOnPage(boolean b)
    {
        setIsAutoResize(b);
    }
}
