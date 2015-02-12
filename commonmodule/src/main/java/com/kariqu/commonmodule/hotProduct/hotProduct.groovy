import org.apache.commons.lang.math.NumberUtils

/**
 * 首页热点商品
 * User: Alec
 * Date: 12-7-25
 * Time: 上午11:41
 */
def execute(context, params) {
    def productService = context.get('productService')
    def array = params.get('hotProductIds').split(",")
    def productList = []
    for (id in array) {
        def pid = NumberUtils.toInt(id)
        if (pid > 0) {
            productList << productService.buildOpenProduct(pid)
        }
    }
    ["hotProductList" : productList]
}

def executeForm(context, params) {
    new HashMap()
}
