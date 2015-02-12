package com.kariqu.productmanager.web;

import com.google.common.collect.Lists;
import com.kariqu.common.JsonResult;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.PictureDesc;
import com.kariqu.productcenter.domain.ProductPicture;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.UploadImageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * sku图片
 * User: Alec
 * Date: 13-8-20
 * Time: 下午6:10
 */
@Controller
public class SkuPictureController {
    private final Log logger = LogFactory.getLog(SkuPictureController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("uploadImage")
    private UploadImageService uploadImage;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    /**
     * 查询出商品的所有图片对象
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/skuImg/{productId}/{skuId}")
    public void productImageList(@PathVariable("productId") int productId, @PathVariable("skuId") long skuId, HttpServletResponse response) throws IOException {
        PictureDesc pictureDesc = productService.getPictureDesc(productId);
        new JsonResult(true).addData("pictureList", getSkuPictures(pictureDesc.getPictures(), skuId)).toJson(response);
    }

    private List<Map<String,Object>> getSkuPictures(List<ProductPicture> pictures, long skuId) {
        List<Map<String,Object>> skuPictures = new ArrayList();
        List<Map<String,Object>> ckecked = Lists.newArrayList();
        List<Map<String,Object>> noChecked = Lists.newArrayList();
        for (ProductPicture picture : pictures) {
            Map<String, Object> map = new HashMap();
            map.put("id", picture.getId());
            map.put("skuId", picture.getSkuId());
            map.put("name", picture.getName());
            map.put("pictureUrl", ProductPictureResolver.getMinSizeImgUrl(picture.getPictureUrl()));
            map.put("number", picture.getNumber());

            if(picture.getSkuId().contains(String.valueOf(skuId))){
                map.put("checkde", true);
                ckecked.add(map);
            }else{
                map.put("checkde", false);
                noChecked.add(map);
            }
        }
        skuPictures.addAll(noChecked);
        skuPictures.addAll(ckecked);
        return skuPictures;
    }

    /**
     * sku绑定图片
     *
     * @param ids
     * @param skuId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/skuImg/updateSkuId")
    public void updateProductImageSkuId(int[] ids, long skuId, String updateFlag, HttpServletResponse response) throws IOException {
        if("add".equals(updateFlag)){
            productService.addProductPictureForSku(ids, skuId);
        }else if("remove".equals(updateFlag)){
            productService.removeProductPictureForSku(ids, skuId);
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改sku图片显示顺序
     *
     * @param id
     * @param num
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/skuImg/updateNum", method = RequestMethod.POST)
    public void updateProductImageNumber(int id, int num, HttpServletResponse response) throws IOException {
        productService.updatePictureNumber(id, num);
        new JsonResult(true).toJson(response);
    }

}
