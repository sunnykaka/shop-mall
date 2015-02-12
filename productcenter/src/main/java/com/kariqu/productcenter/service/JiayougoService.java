package com.kariqu.productcenter.service;

/**
 * 打通家有购平台
 * User: Alec
 * Date: 13-10-15
 * Time: 下午5:22
 */
public interface JiayougoService {

    /**
     * 向家有购添加商品. 转换为家有购的商品格式
     * title	        String	必须	片断套头连帽格子卫衣	商品名称。
     * maker_location	String	必须	北京	原产地 (国产：省名，进口：国家名称)。
     * category	        String	必须	服装;女装;外套;	商品类别（大分类;中分类;小分类）。
     * maker	        String	必须	北京服装有限公司	制造商。
     * spec	            String	必须	M:白色:20;L:红色:70;XL:黑色:30;XXL:绿色:60;	款式库存(如果只有一个样式，共同:共同:20)。
     * supplier_price	Price	必须	200	成本(供应商提供给家有的价格)。
     * price	        Price	必须	299	售价（家有的售价）。
     * brand_name	    String	必须	三星电子	品牌。
     * maker_country	String	必须	国产	国产/进口（单选）。
     * desc	            String	必须	<h1>商品说明</h1> <img src="http://www.jiayougo.com/product/1122_5.jpg">	产品介绍（支持html格式）。
     * image	        Url	    必须	http://www.jiayougo.com/product/1122.jpg	商品基本图片(url)。
     * product_images	Url[]	必须	http://www.jiayougo.com/product/1122_1.jpg;http://www.jiayougo.com/product/1122_2.jpg	商品的大图片（url;多个url用分号区分）。
     * outer_id	        Integer	必须	123456	商品外部编码（供应商的商品编码）。
     *
     * @param productId
     * @return
     */
    boolean addProductToJYG(int productId) throws Exception;

}
