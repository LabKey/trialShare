/*
 * Copyright (c) 2016-2017 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.util.CubeObjectHelper', {
    singleton: true,

    getModelProxy: function(queryName, cubeContainerPath, schemaName)
    {
        var modelProxy = {
            type : 'ajax',
            url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api', cubeContainerPath),
            extraParams : {
                schemaName  : schemaName ? schemaName : 'lists',
                queryName   : queryName,
                limit       : -1
            },
            reader : {
                type : 'json',
                root : 'rows'
            }
        };
        return modelProxy;
    },

    naturalSortOnFieldFn: function(o1, o2, fieldName)
    {
        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return -1;
        else if (o2 == null)
            return 1;
        return LABKEY.study.util.CubeObjectHelper.naturalSortFn(o1.get(fieldName), o2.get(fieldName));
    },

    naturalSortFn: function (aObj, bObj)
    {
        // http://stackoverflow.com/questions/19247495/alphanumeric-sorting-an-array-in-javascript
        var a, b, a1, b1, i = 0, n, L,
                rx = /(\.\d+)|(\d+(\.\d+)?)|([^\d.]+)|(\.\D+)|(\.$)/g;

        if (aObj === bObj) return 0;
        a = aObj.toLowerCase().match(rx);
        b = bObj.toLowerCase().match(rx);

        L = a.length;
        while (i < L)
        {
            if (!b[i]) return 1;
            a1 = a[i];
            b1 = b[i++];
            if (a1 !== b1)
            {
                n = a1 - b1;
                if (!isNaN(n)) return n;
                return a1 > b1 ? 1 : -1;
            }
        }
        return b[i] ? -1 : 0;
    },

    studyShortNameNaturalSortFn: function(o1, o2)
    {
        return LABKEY.study.util.CubeObjectHelper.naturalSortOnFieldFn(o1, o2, 'shortName');
    },

    containerPathNaturalSortFn: function(o1, o2)
    {
        return LABKEY.study.util.CubeObjectHelper.naturalSortOnFieldFn(o1, o2, 'Path');
    }

});