import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def categoryTree = categoryCenterContainer.getCategoryTree()
    def wordNumber = NumberUtils.toInt(params.get("categoryWordNumber"))

    ["categoryTree" : categoryTree.loadNavCategoryTree(), "wordNumber" : (wordNumber > 0 ? wordNumber : 8)]
}

def executeForm(context, params) {
    new HashMap()
}
