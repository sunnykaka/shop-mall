CmsEditWindow = function (config) {

    this.formItems = [];

    var categoryStore = new Ext.data.Store({
        reader: new Ext.data.JsonReader({
            fields: [ 'id', 'name' ],
            root: 'data.categoryList'
        }),
        proxy: new Ext.data.HttpProxy({
            url: 'cms/category/comboBoxList',
            method: 'GET'
        })
    });
    categoryStore.load();
    var categoryComboBox = new Ext.form.ComboBox({
        anchor: '80%',
        hiddenName: 'parent',
        fieldLabel: '父分类',
        mode: 'local',
        editable: false,
        store: categoryStore,
        triggerAction: 'all',
        emptyText: '请选择',
        displayField: 'name',
        valueField: 'id',
        allowBlank: false
    });

    this.formItems.push({
        fieldLabel: '分类名称',
        name: 'name',
        xtype: 'textfield',
        maxLength: 100
    });
    this.formItems.push({
        fieldLabel: '目录名称',
        name: 'directory',
        xtype: 'textfield',
        maxLength: 100
    });

    if (config.mode == "add") {
        this.formItems.push(new Ext.form.Hidden({
            name: 'parent',
            value: config.parent
        }));
        this.title = "添加栏目分类";
        this.url = 'cms/category/add';
    } else if (config.mode = "edit") {

        if (config.parent != 'xnode-20') {
            this.formItems.push(categoryComboBox);
        }
        this.formItems.push(new Ext.form.Hidden({
            name: 'id',
            xtype: 'hidden',
            value: config.id
        }));
        this.formItems.push(new Ext.form.Hidden({
            xtype: 'hidden',
            name: 'parent',
            value: -1
        }));
        this.title = "修改栏目分类";
        this.url = 'cms/category/update';
    }

    this.form = new Ext.FormPanel({
        items: this.formItems,
        border: false,
        bodyStyle: 'background:transparent;padding:10px;',
        defaults: {
            anchor: '100%'
        }
    });

    if (config.mode == "edit") {
        this.form.load({
            url: 'cms/category/category/' + config.id,
            method: "GET",
            success: function (form, action) {
                this.form.getForm().setValues(action.result.data.object);
            },
            scope: this
        });
    }

    CategoryAddWindow.superclass.constructor.call(this, {
        autoHeight: true,
        width: 500,
        resizable: false,
        plain: true,
        modal: true,
        autoScroll: true,
        buttonAlign: 'center',
        closeAction: 'hide',
        title: this.title,

        buttons: [
            {
                text: '提交',
                handler: function (button) {
                    button.setDisabled(true);

                    commitForm(this.form, function () {
                        button.ownerCt.ownerCt.close();
                        var tree = Ext.getCmp('cmsExt-tree');
                        tree.root.reload();
                    }, this.url);
                },
                scope: this
            },
            {
                text: '取消',
                handler: this.close.createDelegate(this, [])
            }
        ],

        items: this.form
    });
};

Ext.extend(CmsEditWindow, Ext.Window);
