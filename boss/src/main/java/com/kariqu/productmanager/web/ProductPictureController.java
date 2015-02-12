package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.file.PictureValidateUtil;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.PictureDesc;
import com.kariqu.productcenter.domain.ProductPicture;
import com.kariqu.productcenter.service.PictureUploadResult;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.UploadImageService;
import com.kariqu.productmanager.helper.PictureUploadItem;
import magick.MagickException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品图片管理
 * 商品有一个主图和一些附图，根据这些主图和附图我们可以生成缩略图
 * 关于商品图片，可以选择云存储，直接通过ftp管理，而不是这个控制器
 * User: Asion
 * Date: 11-9-7
 * Time: 下午3:28
 */
@Controller
public class ProductPictureController {

    private final Log logger = LogFactory.getLog(ProductPictureController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("uploadImage")
    private UploadImageService uploadImage;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    /**
     * 根据商品ID对本地服务器的文件夹个数进行模运算
     */
    private static int PICTURE_STORAGE_DIR_MODULUS = 100;


    /**
     * 商品图片最大高度
     */
    private int productPictureMaxHeight;

    /**
     * 商品图片最大宽度
     */
    private int productPictureMaxWidth;

    /**
     * 查询出商品的所有图片对象
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/images/{productId}")
    public void productImageList(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        PictureDesc pictureDesc = productService.getPictureDesc(productId);
        for (ProductPicture productPicture : pictureDesc.getPictures()) {
            productPicture.setPictureUrl(ProductPictureResolver.getMinSizeImgUrl(productPicture.getPictureUrl()));
        }
        new JsonResult(true).addData("pictureList", pictureDesc.getPictures()).toJson(response);
    }


    /**
     * 上传图片
     *
     * @param pictureUploadItem
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/product/images/upload", method = RequestMethod.POST)
    public void createProductPicture(PictureUploadItem pictureUploadItem, HttpServletResponse response) throws IOException, MagickException {
        response.setContentType("text/html");
        //判断宽高、高度
        InputStream inputStream = null;
        try {
            String fileName = pictureUploadItem.getUploadFile().getOriginalFilename();
            if (!PictureValidateUtil.isPicture(fileName)) {
                new JsonResult(false, "不是图片文件").addData("failure", true).toJson(response);
                return;
            }

            //判断宽高、高度
            inputStream = pictureUploadItem.getUploadFile().getInputStream();
            BufferedImage bi = ImageIO.read(inputStream);


            if (bi.getHeight() != productPictureMaxHeight || bi.getWidth() != productPictureMaxWidth) {
                new JsonResult(false, "图片的高度或者宽度不符合" + productPictureMaxWidth + "x" + productPictureMaxHeight).addData("failure", true).toJson(response);
                return;
            }

            int productId = pictureUploadItem.getProductId();

            if (pictureUploadItem.isMainPic() && productService.existMainPicture(productId)) {
                new JsonResult(false, "主图已经存在").addData("failure", true).toJson(response); //文件上传不管失败成功都会调用客户端的ajax成功方法，所以失败了就用一个标志
                return;
            }
            List<ProductPicture> pictures = productService.getPictureDesc(productId).getPictures();

            String newPictureName = productId + "_main_" + (getMaxDbPictureIndex(pictures) + 1) + PictureValidateUtil.getFileType(fileName);
            String newFileName = Integer.toString(productId % PICTURE_STORAGE_DIR_MODULUS) + "/" + newPictureName;

            PictureUploadResult uploadResult = uploadImage.uploadPicture(pictureUploadItem.getUploadFile().getInputStream(), newFileName);

            if (uploadResult.isCdnSuccess() || uploadResult.isLocalSuccess()) {
                ProductPicture productPicture = new ProductPicture();
                productPicture.setProductId(productId);
                productPicture.setOriginalName(fileName);
                productPicture.setName(newPictureName);
                productPicture.setMainPic(pictureUploadItem.isMainPic());
                String cdnUrl = uploadResult.getCdnUrl();
                String pictureLocalUrl = urlBrokerFactory.getUrl("ImageUpload").addQueryData("fileName", newFileName).toString();
                productPicture.setPictureUrl(StringUtils.isEmpty(cdnUrl) ? pictureLocalUrl : cdnUrl);
                productPicture.setPictureLocalUrl(pictureLocalUrl);
                productService.createProductPicture(productPicture);
                new JsonResult(true).toJson(response);
                return;
            }

            new JsonResult(false, uploadResult.getErrorMsg()).addData("failure", true).toJson(response);
        } catch (Exception e) {
            logger.error("为商品上传图片时异常：" + e);
            new JsonResult(false, "上传图片出错").addData("failure", true).toJson(response);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * 删除图片
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/images/delete")
    public void deleteImages(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            List<ProductPicture> willDeletePicture = new ArrayList();

            for (int id : ids) {
                if (isMainPicture(id)) {
                    new JsonResult(false, "主图不能删除").toJson(response);
                    return;
                }
                willDeletePicture.add(productService.getProductPictureById(id));
                productService.deleteProductPictureById(id);
            }

            for (ProductPicture picture : willDeletePicture) {
                uploadImage.deletePicture(picture.getPictureLocalUrl());
                if (!picture.getPictureLocalUrl().equals(picture.getPictureUrl())) {
                    uploadImage.deletePicture(picture.getPictureUrl());
                }
            }
        } catch (Exception e) {
            logger.error("商品管理的删除图片异常" + e);
            new JsonResult(false, "删除图片出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 检查是否为主图
     *
     * @param id
     * @return
     */
    private boolean isMainPicture(int id) {
        ProductPicture productPicture = productService.getProductPictureById(id);
        if (null == productPicture) {
            return false;
        }
        return productPicture.isMainPic();
    }

    /**
     * 设置图片为主图
     *
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/images/setMainPic")
    public void setMainPicture(int id, HttpServletResponse response) throws IOException {
        try {
            ProductPicture productPicture = productService.getProductPictureById(id);
            productService.setMainProductPicture(id);
            productService.notifyProductUpdate(productPicture.getProductId());
        } catch (Exception e) {
            logger.error("设置商品主图出错" + e);
            new JsonResult(false, "设置商品主图出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 设置图片为副图
     *
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/images/setMinorPic")
    public void setMinorPicture(int id, HttpServletResponse response) throws IOException {
        try {
            ProductPicture productPicture = productService.getProductPictureById(id);
            productService.setMinorProductPicture(id);
            productService.notifyProductUpdate(productPicture.getProductId());
        } catch (Exception e) {
            logger.error("设置商品副图出错" + e);
            new JsonResult(false, "设置商品副图出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 找出数据库中的最大图片索引ID，注意不能用图片个数，这样会导致删除部分之后产生重名文件
     *
     * @param productPictures
     * @return
     */
    private int getMaxDbPictureIndex(List<ProductPicture> productPictures) {
        int index = 0;
        for (ProductPicture picture : productPictures) {
            String pictureLocalUrl = picture.getPictureLocalUrl();
            if (null != pictureLocalUrl) {
                String dbIndexString = pictureLocalUrl.substring(pictureLocalUrl.lastIndexOf("_") + 1, pictureLocalUrl.lastIndexOf("."));
                int dbIndex = Integer.parseInt(dbIndexString);
                if (dbIndex > index) {
                    index = dbIndex;
                }
            }
        }
        return index;
    }


    public int getProductPictureMaxHeight() {
        return productPictureMaxHeight;
    }

    public void setProductPictureMaxHeight(int productPictureMaxHeight) {
        this.productPictureMaxHeight = productPictureMaxHeight;
    }

    public int getProductPictureMaxWidth() {
        return productPictureMaxWidth;
    }

    public void setProductPictureMaxWidth(int productPictureMaxWidth) {
        this.productPictureMaxWidth = productPictureMaxWidth;
    }
}
