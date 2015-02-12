def execute(context, params) {
    ["productList" : context.get('productService').getProductMap(params.get("guessProductIds"))]
}

def executeForm(context, params) {
	new HashMap()
}
