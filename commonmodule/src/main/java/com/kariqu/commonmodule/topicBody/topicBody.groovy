import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def map = new HashMap()
    def productService = context.get('productService')

    def productList = []

    String topicBodyProductId = params.get("topicBodyProductId")
    if (StringUtils.isNotBlank(topicBodyProductId)) {
        String[] array = topicBodyProductId.split(",")
        for (id in array) {
            def pid = NumberUtils.toInt(id.trim())
            if (pid > 0)
                productList << productService.buildBasicProduct(pid)
        }
        map.put("productList", productList)
    }

    def topicBodyDescNum = NumberUtils.toInt(params.get("topicBodyDescNum"))
    if (topicBodyDescNum < 1) topicBodyDescNum = 20
    map.put("topicBodyDescNum", topicBodyDescNum)
    map
}

def executeForm(context, params) {
	new HashMap()
}
