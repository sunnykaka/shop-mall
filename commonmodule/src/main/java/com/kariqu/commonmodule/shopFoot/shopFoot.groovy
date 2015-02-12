def execute(context, params) {
    def contentMap = new LinkedHashMap()
    // 查询"帮助中心"类目
    def rootCategory = cmsService.queryCategoryByName(params.get("helpCategory"))

    if (rootCategory != null) {
        // 查询帮助中心的所有子类目
        categoryList = cmsService.querySubCategory(rootCategory.getId())

        for (category in categoryList) {
            // 查询子类目的所有内容
            contentList = cmsService.queryContentByCategoryId(category.getId())
            contentMap.put(category.getName(), contentList)
        }
    }

    ["categoryMap": contentMap]
}

def executeForm(context, params) {
    new HashMap()
}
