Ext4.define('LABKEY.study.store.StudySubsets', {
    extend: "Ext.data.Store",
    autoLoad: true,
    id: 'StudySubsetStore',
    model: 'LABKEY.study.data.StudySubset',
    isLoaded: false,
    defaultValue: null,
    proxy: {
        type: 'ajax',
        //url: set in constructor below
        reader: {
            type: 'json',
            root: 'data'
        }
    },
    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.isLoaded = true;
                for (var i = 0; i < records.length; i++)
                {
                    if (records[i].data.default)
                    {
                        store.defaultValue = records[i];
                        break;
                    }
                }
            },
            scope: this
        }
    },

    constructor: function(config) {
        this.proxy.url = LABKEY.ActionURL.buildURL(config.dataModuleName, "studySubsets.api", LABKEY.containerPath);
        this.callParent(config);
    }
});