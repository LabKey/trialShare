Ext4.define('LABKEY.study.store.Subsets', {
    extend: "Ext.data.Store",
    autoLoad: true,
    model: 'LABKEY.study.data.Subset',
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
                for (var i = 0; i < records.length; i++)
                {
                    if (records[i].data.isDefault)
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
        this.proxy.url = LABKEY.ActionURL.buildURL(config.dataModuleName, "subsets.api", LABKEY.containerPath, {objectName: config.objectName});
        this.callParent(config);
    }
});