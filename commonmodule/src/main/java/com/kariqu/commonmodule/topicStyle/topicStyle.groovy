import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def map = new HashMap()
    def productService = context.get('productService')

    def productList = []

    String topicBodyProductId = params.get("topicStyleProductId")
    if (StringUtils.isNotBlank(topicBodyProductId)) {
        String[] array = topicBodyProductId.split(",")
        for (id in array) {
            def pid = NumberUtils.toInt(id.trim())
            if (pid > 0)
                productList << productService.buildBasicProduct(pid)
        }
        map.put("productList", productList)
    }

    def topicNumber = NumberUtils.toInt(params.get("topicStyleNumber"))
    if (topicNumber < 1) topicNumber = 4
    map.put("topicStyleNumber", topicNumber)

    def topicStyleDescNum = NumberUtils.toInt(params.get("topicStyleDescNum"))
    if (topicStyleDescNum < 1) topicStyleDescNum = 20
    map.put("topicStyleDescNum", topicStyleDescNum)
    map
}

def executeForm(context, params) {
	new HashMap()
}
