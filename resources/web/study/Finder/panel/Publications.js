Ext4.define("LABKEY.study.panel.Publications", {
    extend: 'Ext.panel.Panel',

    layout: {
        type: 'vbox',
        align: 'stretch',
        pack: 'start'
    },

    border: false,
    alias : 'widget.labkey-publications-panel',
    cls: 'labkey-publications-panel',

    padding: "5 0 0 0",

    objectName: 'Publication',

    autoScroll: true,

    initComponent : function() {
        this.items = [
            this.getCardPanelHeader(),
            this.getCardsContainer()
        ];
        this.callParent();

        //this.getCards().store.addListener('filterChange',this.onFilterSelectionChanged, this);
        this.on(
                {'subsetChanged': this.onSubsetChanged,
                 'searchTermsChanged': this.onSearchTermsChanged
                 //'filterSelectionChanged': this.onFilterSelectionChanged
                }
        );
    },

    //onFilterSelectionChanged: function() {
    //    console.log('filterChange happened!')
    //},

    onSubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getCardPanelHeader().getSubsetMenu().getValue();
        this.getCards().store.updateFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(value) {
        console.log("search terms changed to " + value)
    },

    // TODO move to base class
    getCardPanelHeader : function() {
        if (!this.cardPanelHeader) {
            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
                dataModuleName: this.dataModuleName,
                showSearch : this.showSearch,
                objectName: this.objectName
            });
        }
        return this.cardPanelHeader;
    },

    getCardsContainer : function() {
        if (!this.facetsContainer) {
            this.facetsContainer = {
                xtype: 'container',
                itemId: 'publicationCardsContainer',
                flex: 10,
                autoScroll: true,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    pack: 'start'
                },
                items: [
                    this.getCards()
                ]
            };
        }
        return this.facetsContainer;
    },

    getCards : function() {
        if (!this.cards) {
            this.cards = Ext4.create("LABKEY.study.panel.PublicationCards", {
                dataModuleName: this.dataModuleName
            });
        }
        return this.cards;
    }
});