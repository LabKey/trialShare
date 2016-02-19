/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.data.Publication', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields : [
        {name: 'id', type:'int'},
        {name: 'studyId'},
        {name: 'pmid'}, // PubMed Id
        {name: 'pmcid'},    // PubMed Central reference number
        {name: 'doi'},
        {name: 'author'},
        {name: 'authorAbbrev'},
        {name: 'issue'},
        {name: 'journal'},
        {name: 'pages'},
        {name: 'title'},
        {name: 'year'},
        {name: 'citation'},
        {name: 'status'},
        {name: 'url'},
        {name: 'dataUrl'},
        {name: 'publicationType'},
        {name: 'isSelected', type: 'boolean', defaultValue: true},
        {name: 'isHighlighted', type: 'boolean', defaultValue: false}
    ]

});