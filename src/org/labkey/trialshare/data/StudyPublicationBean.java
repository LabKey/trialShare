/*
 * Copyright (c) 2015 LabKey Corporation
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
package org.labkey.trialshare.data;

import org.labkey.api.util.Pair;

public class StudyPublicationBean
{
    // common fields
    private String studyId;
    private String pubmedId;
    private String author;
    private String issue;
    private String journal;
    private String pages;
    private String title;
    private String year;
    // the first item in the pair is the link; the second is the description (link text)
    private Pair<String, String>[] urls = new Pair[5];

    // TrialShare fields
    private String citation;

    public String getStudyId()
    {
        return studyId;
    }

    public void setStudyId(String studyId)
    {
        this.studyId = studyId;
    }

    public String getPubmedId()
    {
        return pubmedId;
    }

    public void setPubmedId(String pubmedId)
    {
        this.pubmedId = pubmedId;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getIssue()
    {
        return issue;
    }

    public void setIssue(String issue)
    {
        this.issue = issue;
    }

    public String getJournal()
    {
        return journal;
    }

    public void setJournal(String journal)
    {
        this.journal = journal;
    }

    public String getPages()
    {
        return pages;
    }

    public void setPages(String pages)
    {
        this.pages = pages;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    public String getCitation()
    {
        return citation;
    }

    public void setCitation(String citation)
    {
        this.citation = citation;
    }

    public void setDescription1(String description1)
    {
        setUrlText(0, description1);
    }

    public Pair<String, String>[] getUrls()
    {
        return urls;
    }

    public void setUrls(Pair<String, String>[] urls)
    {
        this.urls = urls;
    }

    private void setUrlText(int index, String description)
    {
        if (description.equals("&nbsp;"))
            description = "";
        Pair<String, String> urlData = urls[index];
        if (urlData == null)
        {
            urlData = new Pair<>(null, description);
            urls[index] = urlData;
        }
        else
        {
            urlData.second = description;
        }
    }

    private void setUrlLink(int index, String link)
    {
        Pair<String, String> urlData = urls[index];
        if (urlData == null)
        {
            urlData = new Pair<>(link, null);
            urls[index] = urlData;
        }
        else
        {
            urlData.first = link;
        }
    }

    public void setDescription2(String description2)
    {
        setUrlText(1, description2);
    }

    public void setDescription3(String description3)
    {
        setUrlText(2, description3);
    }


    public void setLink1(String link1)
    {
        setUrlLink(0, link1);
    }


    public void setLink2(String link2)
    {
        setUrlLink(1, link2);
    }


    public void setLink3(String link3)
    {
        setUrlLink(2, link3);
    }

    public boolean hasPubmedLink()
    {
        return getPubmedLink() != null;
    }

    public String getPubmedLink()
    {
        for (Pair<String, String> urlData : urls) {
            if (urlData != null && urlData.first != null && urlData.first.contains("pubmed"))
                return urlData.first;
        }
        return null;
    }
}