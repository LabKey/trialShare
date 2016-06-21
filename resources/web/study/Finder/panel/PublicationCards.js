/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.PublicationCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-publicationCards-panel',

    cls: 'labkey-publication-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-publication-card',

    loadMask: false,

    bubbleEvents: ["detailsChange"],

    store:  Ext4.create('LABKEY.study.store.CubeObjects', {
            storeId: 'Publication',
            model: 'LABKEY.study.data.Publication',
            autoLoad: false,
            facetSelectedMembers : {}, // initially we indicate that none of the members is selected by facets
            searchSelectedMembers : null, // initially we have no search terms so everything is selected
            selectedSubset : null,
            sorters: [
                {
                    property: 'year',
                    direction: 'DESC'
                },
                {
                    property: 'title',
                    direction: 'ASC'
                }
            ]
        }),

    tpl: new Ext4.XTemplate(
        '<div id="publicationpanel">',
        '   <tpl for=".">',
        '   {[this.startCardDiv(values)]}',
        '       <div class="labkey-publication">',
        '           <span>',
        '               <span id="morePublicationDetails"><i class="fa fa-plus-square"></i></span>',
        '               <span id="lessPublicationDetails"><i class="fa fa-minus-square"></i></span>',
        '           </span>',
        '       <div>',
        '           <div class="labkey-publication-title">{title:htmlEncode}</div>',
        '           <div id="abbreviatedAuthorList" class="labkey-publication-author">{authorAbbrev:htmlEncode}</div>',
        '           <div id="fullAuthorList" class="labkey-publication-author">{author:htmlEncode}</div>',
        '           <div class="labkey-publication-citation">{citation:htmlEncode}</div>',
        '           <div>',
        '           <tpl if="url">',
        '               <div class="labkey-publication-goto">',
        '               <a class="labkey-text-link labkey-goto-link-text" target="_blank" href="{url}">view document</a>',
        '               <a class="labkey-goto-link-icon" target="_blank" href="{url}"><div class="labkey-goto-link-icon"></div></a>',
        '               </div>',
        '           </tpl>',
        '           <tpl if="dataUrl">',
        '                <div class="labkey-publication-data-link">',
        '                <a class="labkey-text-link labkey-data-link-text" href="{dataUrl:htmlEncode}" target="_blank"><span class="labkey-data-link-text">Clinical and Assay Data</span></a>',
        '                <a class="labkey-data-link-icon" href="{dataUrl:htmlEncode}" target="_blank"><div class="labkey-data-link-icon"></div></a>',
        '                </div>',
        '           </tpl>',
        '           </div>',
        '           <tpl if="viewState == &quot;expanded&quot;">',
        '           <div id="publicationDetails_{id}" class="labkey-publication-detail {viewState}">',
        '               <div id="labkey-publication-details-content">',
        '               <tpl if="urls"/>',
        '                   <div class="labkey-publication-links">',
        '                   <tpl for="urls">',
        '                       <tpl if="link && linkText">',
        '                       <div><a class="labkey-text-link" href="{link:htmlEncode}" target="_blank">{linkText:htmlEncode}</a></div>',
        '                       </tpl>',
        '                   </tpl>',
        '                   </div>',
        '               </tpl>',
        '                   <div class="labkey-publication-identifiers">',
        '                   <tpl if="pmid">',
        '                       <span class="labkey-publication-identifier"><a href="http://www.ncbi.nlm.nih.gov/pubmed/?term={pmid:htmlEncode}" class="labkey-text-link" target="_blank">PMID {pmid:htmlEncode}</a></span>',
        '                   </tpl>',
        '                   <tpl if="pmcid">',
        '                       <span class="labkey-publication-identifier"><a href="http://www.ncbi.nlm.nih.gov/pmc/articles/{pmcid:htmlEncode}" class="labkey-text-link" target="_blank">PMCID {pmcid:htmlEncode}</a></span>',
        '                   </tpl>',
        '                   <tpl if="doi">',
        '                       <span class="labkey-publication-identifier"><a href="http://dx.doi.org/{doi:htmlEncode}" class="labkey-text-link" target="_blank">DOI {doi:htmlEncode}</a></span>',
        '                   </tpl>',
        '                   </div>',
        '                   <tpl if="studies">',
        '                   <div class="labkey-publication-studies">',
        '                       <span class="labkey-publication-detail-label">Studies</span>',
        '                       <tpl for="studies">',
        '                       <tpl if="url">',
        '                           <span class="labkey-study-short-name"><a href="{url:htmlEncode}">{shortName:htmlEncode}</a></span>',
        '                       <tpl else>',
        '                           <span class="labkey-study-short-name">{shortName:htmlEncode}</span>',
        '                       </tpl>',
        '                       </tpl>',
        '                   </div>',
        '                   </tpl>',
        '                   <tpl if="keywords">',
        '                       <div class="labkey-publication-keywords">',
        '                           <span class="labkey-publication-detail-label">Keywords</span>',
        '                           {keywords:htmlEncode}',
        '                       </div>',
        '                   </tpl>',
        '                   <tpl if="abstractText">',
        '                       <div class="labkey-publication-abstract">',
        '                           <span class="labkey-publication-detail-label">Abstract</span>',
        '                           {abstractText}',
        '                       </div>',
        '                   </tpl>',
        '                   <tpl if="thumbnails">',
        '                       <ul class="labkey-figures-list">',
        '                       <tpl for="thumbnails">',
        '                           <li class="labkey-figure"><a href="{link}" title="{title}" target="_blank"><img src="{linkText}"></a></li>',
        '                       </tpl>',
        '                       </ul>',
        '                   </tpl>',
        '               </div>',
        '           </div>',
        '           </div>',
        '           </tpl>',
        '       </div>',
        '   </div>',
        '   </div>',
        '   </tpl>',
        '</div>',
            {
                startCardDiv: function(values)
                {
                    var html = '<div class="labkey-publication-card ';
                    html += values.viewState;
                    if (values.status)
                            if (values.status.toLowerCase() == "in progress")
                                html += " labkey-publication-highlight1";
                            else
                                html += " labkey-publication-highlight2";
                    if (values.publicationType)
                        if (values.publicationType.toLowerCase() == "manuscript")
                            html += " labkey-publication-highlight3";
                        else
                            html += " labkey-publication-highlight4";
                    html += '">';
                    return html;
                }
            }
    ),

    listeners: {
        itemClick: function(view, record, item, index, event, eOpts)
        {
            if (event.target.className.indexOf("fa-plus-square") >= 0)
            {
                this.toggleDetails(record.get("id"), item, true);
            }
            else if (event.target.className.indexOf("fa-minus-square") >= 0)
            {
                this.toggleDetails(record.get("id"), item, false);
            }
        }
    },

    toggleDetails : function(publicationId, item, expand)
    {

        var publication = this.store.getById(publicationId);
        
        if (expand)
        {
            var url = LABKEY.ActionURL.buildURL(this.dataModuleName, "publicationDetails.api", null, {
                "id": publicationId
            });
            Ext4.Ajax.request({
                url: url,
                success: function (response)
                {
                    var o = Ext4.decode(response.responseText);
                    if (o.success)
                    {
                        item.className = item.className.replace("collapsed", "expanded"); // change the +/- icon
                        publication.set(o.data);
                        publication.set("viewState", "expanded");
                    }

                    this.fireEvent('detailsChange');
                    LABKEY.Utils.signalWebDriverTest('publicationDetailsLoaded');
                },
                scope: this
            });
        }
        else
        {
            publication.set("viewState", "collapsed");
            item.className = item.className.replace("expanded", "collapsed");
        }

    },

    showDetailPopup : function(objectId)
    {
        this.hidePopup(this.detailShowing);

        var url = LABKEY.ActionURL.buildURL(this.dataModuleName, 'publicationDetail.view', this.cubeContainerPath, {
            _frame: 'none',
            detailType: 'publication',
            id : objectId
        });
        var detailWindow = Ext4.create('Ext.window.Window', {
            width: 800,
            maxHeight: 600,
            resizable: true,
            layout: 'fit',
            border: false,
            cls: 'labkey-publication-detail',
            autoScroll: true,
            loader: {
                autoLoad: true,
                url: url
            }
        });
        var viewScroll = Ext4.getBody().getScroll();
        var viewSize = Ext4.getBody().getViewSize();
        var region = [viewScroll.left, viewScroll.top, viewScroll.left + viewSize.width, viewScroll.top + viewSize.height];
        var proposedXY = [region[0] + viewSize.width / 2 - 400, region[1] + viewSize.height / 2 - 300];
        proposedXY[1] = Math.max(region[1], Math.min(region[3] - 400, proposedXY[1]));
        detailWindow.setPosition(proposedXY);
        this.detailShowing = detailWindow;
        this.detailShowing.show();
    },


    hidePopup: function(popup)
    {
        if (popup)
        {
            popup.hide();
            popup.destroy();
            popup = null;
        }
    },

    initComponent: function(config)
    {
        this.getStore().proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "publications.api", this.cubeContainerPath);
        this.getStore().load();

        this.callParent();
    }

});