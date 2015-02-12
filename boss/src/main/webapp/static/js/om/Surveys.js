(function () {

    Ext.chart.Chart.CHART_URL = '/extjs/resources/charts.swf';

    Desktop.SurveysWindow = Ext.extend(Ext.app.Module, {
        id: 'Surveys-win',
        title: '问卷调查',
        createSurveysGrid: function () {
            var store = createJsonStore('surveys/list', [
                {
                    name: 'id',
                    type: 'int'
                },
                {
                    name: 'surveyName',
                    type: 'string'
                },
                {
                    name: 'surveyExplain',
                    type: 'string'
                }
            ]);

            store.load();

            var grid = new Ext.grid.GridPanel({
                id: 'surveysGird',
                store: store,
                region: 'north',
                height: 200,
                loadMask: true,
                tbar: [
                    {
                        text: '新建问卷',
                        iconCls: 'add',
                        handler: function () {

                            var form = buildForm('surveys/add', [
                                {
                                    text: '提交',
                                    handler: function () {
                                        commitForm(form, function () {
                                            form.ownerCt.close();
                                            grid.getStore().reload();
                                        })
                                    }
                                }
                            ], [
                                {
                                    fieldLabel: '问卷名',
                                    name: 'surveyName',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '问题描述',
                                    name: 'surveyExplain',
                                    xtype: 'textarea',
                                    allowBlank: true
                                }
                            ]);

                            buildWin('新建问卷', 400, form).show(this.id)

                        }
                    },
                    {
                        text: '刷新',
                        iconCls: 'refresh',
                        handler: function () {
                            Ext.getCmp('surveysGird').getStore().reload();
                        }
                    },
                    new Ext.ux.CopyButton({
                        text: '复制链接',
                        iconCls: 'config',
                        getValue: function () {
                            var sm = grid.getSelectionModel();
                            if (!sm.hasSelection()) {
                                Ext.Msg.alert('错误', '请选择问卷');
                                return;
                            } else if (sm.getSelections().length > 1) {
                                Ext.Msg.alert('错误', '请只选择一个问卷');
                                return;
                            }

                            var record = sm.getSelected();

                            return window.BuyHome + '/survey/' + record.get('id');
                        }
                    }),
                    {
                        text: '删除',
                        iconCls: 'delete',
                        handler: function () {
                            doGridRowDelete(Ext.getCmp('surveysGird'), 'surveys/delete', function () {
                                Ext.getCmp('surveysGird').getStore().reload()
                            })
                        }
                    },
                    {
                        text: '新建问题',
                        iconCls: 'add',
                        handler: function () {

                            var sm = grid.getSelectionModel();
                            if (!sm.hasSelection()) {
                                Ext.Msg.alert('错误', '请选择问卷');
                                return;
                            } else if (sm.getSelections().length > 1) {
                                Ext.Msg.alert('错误', '请只选择一个问卷');
                                return;
                            }

                            var record = sm.getSelected();

                            var form = buildForm('surveys/question/add', [
                                {
                                    text: '提交',
                                    handler: function () {
                                        commitForm(form, function () {
                                            form.ownerCt.close();
                                            grid.getStore().reload();
                                        })
                                    }
                                }
                            ], [
                                {
                                    xtype: 'hidden',
                                    name: 'surveyId',
                                    value: record.get('id')
                                },
                                {
                                    xtype: 'combo',
                                    hiddenName: 'answerType',
                                    fieldLabel: '多选还是单选',
                                    mode: 'local',
                                    editable: false,
                                    triggerAction: 'all',
                                    emptyText: '请选择',
                                    store: new Ext.data.ArrayStore({
                                        fields: [ 'type', 'value' ],
                                        data: [
                                            [ '单选', 'radio' ],
                                            [ '多选', 'checkbox' ]
                                        ]
                                    }),
                                    displayField: 'type',
                                    valueField: 'value',
                                    value: 'radio',
                                    allowBlank: false
                                },
                                {
                                    xtype: 'combo',
                                    hiddenName: 'mustReply',
                                    fieldLabel: '必须回答吗',
                                    mode: 'local',
                                    editable: false,
                                    triggerAction: 'all',
                                    store: new Ext.data.ArrayStore({
                                        fields: [ 'type', 'value' ],
                                        data: [
                                            [ 'Yes', 'true' ],
                                            [ 'No', 'false' ]
                                        ]
                                    }),
                                    displayField: 'type',
                                    valueField: 'value',
                                    value: 'Yes',
                                    allowBlank: false
                                },
                                {
                                    fieldLabel: '问题名',
                                    name: 'questionDetail',
                                    allowBlank: false
                                }
                            ]);

                            buildWin('新建问题', 400, form).show(this.id)

                        }
                    }
                ],
                viewConfig: {
                    forceFit: true
                },
                columns: [
                    {
                        header: 'ID',
                        dataIndex: 'id'
                    },
                    {
                        header: 'name',
                        dataIndex: 'surveyName'
                    }
                ]
            });


            grid.on('rowclick', function (grid, rowIndex, event) {

                var record = grid.getStore().getAt(rowIndex);

                var report = Ext.getCmp('surveysReport');
                report.removeAll();

                doAjax('surveys/answer/report/' + record.get('id'), function (obj) {
                    Ext.each(obj.data.reportData, function (data) {
                        var panel= new Ext.Panel({
                            title:data[0].questionDetail,
                            items: {
                                store: new Ext.data.JsonStore({
                                    fields: ['answer', 'total'],
                                    data: data
                                   }),
                                xtype: 'piechart',
                                dataField: 'total',
                                categoryField: 'answer',
                                extraStyle: {
                                    legend: {
                                        display: 'bottom',
                                        padding: 5,
                                        font: {
                                            family: 'Tahoma',
                                            size: 13
                                        }
                                    }
                                }
                            }
                        });
                        report.add(panel);
                    })
                    report.activate(0);

                });

                var loader = Ext.getCmp('surveysTree').getLoader();
                loader.dataUrl = 'surveys/answer/tree/' + record.get('id');
                Ext.getCmp('surveysTree').root.reload();
                Ext.getCmp('surveysTree').show();
                report.show();
                Ext.getCmp('Surveys-win').setHeight(document.body.clientHeight*0.85);
                Ext.getCmp('Surveys-win').doLayout();
            });

            grid.on('rowdblclick', function (grid, rowIndex, event) {
                var record = grid.getStore().getAt(rowIndex);
                var form = buildForm('surveys/update', [
                    {
                        text: '提交',
                        handler: function () {
                            commitForm(form, function () {
                                form.ownerCt.close();
                                grid.getStore().reload();
                            })
                        }
                    }
                ], [
                    {
                        xtype: 'hidden',
                        name: 'id',
                        value: record.get('id')
                    },
                    {
                        fieldLabel: '问卷名',
                        name: 'surveyName',
                        value: record.get('surveyName'),
                        allowBlank: false
                    },
                    {
                        fieldLabel: '问题描述',
                        name: 'surveyExplain',
                        xtype: 'textarea',
                        value: record.get('surveyExplain'),
                        allowBlank: false
                    }
                ]);

                buildWin('修改问卷', 400, form).show(this.id)

            });

            return grid;
        },

        createTree: function () {
            var tree = new Ext.tree.TreePanel({
                id: 'surveysTree',
                rootVisible: false,
                lines: true,
                region: 'south',
                hidden:true,
                height: 200,
                loader: new Ext.tree.TreeLoader({dataUrl: 'surveys/answer/tree/-1'}),
                autoScroll: true,
                root: {
                    id: -1,
                    nodeType: 'async',
                    expanded: true
                }
            }) ;

            tree.on("contextmenu", function (node, e) {
                var treeMenu = new Ext.menu.Menu();

                treeMenu.add({
                    text: '删除',
                    handler: function () {
                        doAjax(node.leaf ? 'surveys/answer/delete' : 'surveys/question/delete', function () {
                            Ext.getCmp('surveysTree').root.reload();
                        }, {
                            id: node.id
                        });

                    }
                });

                treeMenu.add({
                    text: '修改',
                    handler: function () {
                        doAjax(node.leaf ? 'surveys/answer' : '/surveys/question', function (response) {
                            var form;
                            if (node.leaf) {
                                var answer = response.data.answer;

                                form = new Ext.FormPanel({
                                    baseCls:'x-plain',
                                    labelWidth:120,
                                    url:'surveys/answer/update',
                                    buttonAlign:'center',
                                    autoHeight:true,
                                    defaults:{
                                        anchor:'100%',
                                        xtype:'textfield'
                                    },
                                    buttons:[
                                        {
                                            text: '提交',
                                            handler: function () {
                                                commitForm(form, function () {
                                                    form.ownerCt.close();
                                                    Ext.getCmp('surveysTree').root.reload();
                                                })
                                            }
                                        }
                                    ],
                                    items:[
                                        {
                                            xtype: 'hidden',
                                            name: 'id',
                                            //value: node.id
                                            value: answer.id
                                        },
                                        {
                                            xtype: 'combo',
                                            hiddenName: 'hasWrite',
                                            fieldLabel: '需要用户手动输入吗',
                                            mode: 'local',
                                            editable: false,
                                            triggerAction: 'all',
                                            store: new Ext.data.ArrayStore({
                                                fields: [ 'type', 'value' ],
                                                data: [
                                                    [ 'No', false ],
                                                    [ 'Yes', true ]
                                                ]
                                            }),
                                            displayField: 'type',
                                            valueField: 'value',
                                            value: answer.hasWrite,
                                            allowBlank: false
                                        },
                                        {
                                            fieldLabel: '答案名',
                                            name: 'value',
                                            value: answer.answerDetail,
                                            allowBlank: false
                                        }
                                    ]
                                });
                            } else {
                                var question = response.data.question;

                                form = buildForm('surveys/question/update', [
                                    {
                                        text: '提交',
                                        handler: function () {
                                            commitForm(form, function () {
                                                form.ownerCt.close();
                                                Ext.getCmp('surveysTree').root.reload();
                                            })
                                        }
                                    }
                                ], [
                                    {
                                        xtype: 'hidden',
                                        name: 'id',
                                        // value: node.id
                                        value: question.id
                                    },
                                    {
                                        xtype: 'combo',
                                        hiddenName: 'answerType',
                                        fieldLabel: '多选还是单选',
                                        mode: 'local',
                                        editable: false,
                                        triggerAction: 'all',
                                        store: new Ext.data.ArrayStore({
                                            fields: [ 'type', 'value' ],
                                            data: [
                                                [ '单选', 'radio' ],
                                                [ '多选', 'checkbox' ]
                                            ]
                                        }),
                                        displayField: 'type',
                                        valueField: 'value',
                                        value: question.answerType,
                                        allowBlank: false
                                    },
                                    {
                                        xtype: 'combo',
                                        hiddenName: 'mustReply',
                                        fieldLabel: '必须要回答吗',
                                        mode: 'local',
                                        editable: false,
                                        triggerAction: 'all',
                                        store: new Ext.data.ArrayStore({
                                            fields: [ 'type', 'value' ],
                                            data: [
                                                [ 'Yes', true ],
                                                [ 'No', false ]
                                            ]
                                        }),
                                        displayField: 'type',
                                        valueField: 'value',
                                        value: question.mustReply,
                                        allowBlank: false
                                    },
                                    {
                                        fieldLabel: '问题名',
                                        name: 'value',
                                        value: node.text,
                                        // value: question.questionDetail,
                                        allowBlank: false
                                    }
                                ]);
                            }

                            buildWin('修改', 400, form).show(this.id);
                        }, {
                            id: node.id
                        });
                    }
                });

                if (!node.leaf) {
                    treeMenu.add({
                        text: '添加答案',
                        handler: function () {
                            var answerName = new Ext.form.TextField({
                                fieldLabel: '答案名',
                                name: 'answerDetail',
                                allowBlank: false
                            });

                            var form = new Ext.FormPanel({
                                baseCls:'x-plain',
                                labelWidth:120,
                                url:'surveys/answer/add',
                                buttonAlign:'center',
                                autoHeight:true,
                                defaults:{
                                    anchor:'100%',
                                    xtype:'textfield'
                                },
                                buttons:[
                                    {
                                        text: '提交',
                                        handler: function () {
                                            commitForm(form, function () {
                                                form.ownerCt.close();
                                                Ext.getCmp('surveysTree').root.reload();
                                            })
                                        }
                                    }
                                ],
                                items:[
                                    {
                                        xtype: 'hidden',
                                        name: 'questionId',
                                        value: node.id
                                    },
                                    {
                                        xtype: 'combo',
                                        hiddenName: 'hasWrite',
                                        fieldLabel: '需要用户手动输入吗',
                                        mode: 'local',
                                        editable: false,
                                        triggerAction: 'all',
                                        store: new Ext.data.ArrayStore({
                                            fields: [ 'type', 'value' ],
                                            data: [
                                                [ 'No', 'false' ],
                                                [ 'Yes', 'true' ]
                                            ]
                                        }),
                                        displayField: 'type',
                                        valueField: 'value',
                                        value: 'false',
                                        allowBlank: false,
                                        listeners: {
                                            select: function(combo) {
                                                answerName.allowBlank = (combo.getValue() == 'true');
                                                form.doLayout();
                                            }
                                        }
                                    },
                                    answerName
                                ]});

                            buildWin('添加答案', 400, form).show(this.id)
                        }
                    });
                }

                treeMenu.showAt(e.getXY());

            });

            return tree;
        },

        createReport: function () {
            return new Ext.TabPanel({
                id:"surveysReport",
                border: false,
                enableTabScroll:true,
                hidden:true,
                region:"center"
            });

        },


        createWindow: function () {
            return this.app.getDesktop().createWindow({
                id: this.id,
                title: this.title,
                pageX:document.body.clientWidth*0.3,
                pageY:document.body.clientHeight*0.1,
                height: document.body.clientHeight * 0.24,
                width: 750,
                layout: 'border',
                items: [this.createSurveysGrid(), this.createTree(), this.createReport()]
            });
        }
    });

}).call(this);
