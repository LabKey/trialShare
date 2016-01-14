Ext4.define("LABKEY.study.panel.PublicationCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-publicationCards-panel',

    cls: 'labkey-publication-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-publication-card',

    autoScroll: true,

    store : Ext4.create('LABKEY.study.store.Publications', {
        dataModuleName: this.dataModuleName
    }),

    tpl: new Ext4.XTemplate(
            '<div id="publicationpanel">',
            '   <tpl for=".">',
            '   <tpl if="status==&quot;In Progress&quot;">',
            '   <div class="labkey-publication-card labkey-publication-highlight">',
            '   <tpl else>',
            '   <div class="labkey-publication-card">',
            '   </tpl>',
            '       <div>',
            '           <tpl if="url">',
            '           <a class="labkey-text-link labkey-publication-goto" href="{url}">view document</a>',
            '           </tpl>',
            '           <tpl if="dataUrl">',
            '           <a class="labkey-text-link labkey-study-card-goto" href="{dataUrl}">view data</a>',
            '           </tpl>',
            '       </div>',
            '       <div class="labkey-publication-title">{title}</div>',
            '       <div class="labkey-publication-author">{authorAbbrev}</div>',
            '       <div class="labkey-publication-citation">{citation}</div>',
            '       <a class="labkey-text-link labkey-study-card-summary">more details</a>',
            '   </div>',
            '   </tpl>',
            '</div>'
    ),

    listeners: {
        itemClick: function(view, record, item, index, event, eOpts) {
            if (event.target.className.includes("labkey-study-card-summary"))
            {
                this.showDetailPopup(record.get("id"));
            }
        }
    },

    showDetailPopup : function(studyId)
    {
        this.hidePopup(this.detailShowing);

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
                url: this.dataModuleName + '-publicationDetail.view?_frame=none&detailType=publication&id=' + studyId
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
        this.getStore().proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "publications.api", LABKEY.containerPath);
        this.getStore().load();
        this.callParent();
    },

    constructor: function(config)
    {
        this.dataModuleName = config.dataModuleName;
        this.callParent(config);
    }
});