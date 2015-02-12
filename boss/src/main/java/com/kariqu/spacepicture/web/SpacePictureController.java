package com.kariqu.spacepicture.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.file.PictureValidateUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.domain.SpaceProperty;
import com.kariqu.productcenter.service.PictureUploadResult;
import com.kariqu.productcenter.service.SpacePictureService;
import com.kariqu.productcenter.service.UploadImageService;
import com.kariqu.spacepicture.helper.ImageInfo;
import com.kariqu.spacepicture.helper.PictureSpaceTreeJson;
import com.kariqu.spacepicture.helper.SpacePictureUploadItem;
import magick.MagickException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 空间图片管理
 * User: ennoch
 * Date: 12-7-13
 * Time: 下午1:12
 */
@Controller
public class SpacePictureController {

    private final Log logger = LogFactory.getLog(SpacePictureController.class);

    @Autowired
    @Qualifier("uploadImageService")
    private UploadImageService uploadImageService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private SpacePictureService spacePictureService;

    private static int PICTURE_STORAGE_DIR_MODULUS = 10;

    @RequestMapping(value = "/spacePicture/queryAllSpace")
    public
    @ResponseBody
    List<PictureSpaceTreeJson> tree() {
        List<PictureSpaceTreeJson> pictureSpaceTree = new ArrayList<PictureSpaceTreeJson>();
        List<SpaceProperty> spaces = spacePictureService.queryAllSpace();
        for (SpaceProperty spaceProperty : spaces) {
            PictureSpaceTreeJson pictureSpaceTreeJson = new PictureSpaceTreeJson();
            pictureSpaceTreeJson.setId(spaceProperty.getId());
            pictureSpaceTreeJson.setText(spaceProperty.getSpaceName());
            pictureSpaceTreeJson.setLeaf(true);
            pictureSpaceTree.add(pictureSpaceTreeJson);
        }
        return pictureSpaceTree;
    }

    @RequestMapping(value = "/spacePicture/queryPictureBySpaceId")
    public void queryPictureBySpaceId(@RequestParam("start") int start,
                                      @RequestParam("limit") int limit,
                                      @RequestParam("spaceId") int spaceId,
                                      HttpServletResponse response) throws IOException {
        Page<SpacePicture> spacePictures = spacePictureService.queryAllPictureOfSpace(spaceId, new Page<SpacePicture>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", spacePictures.getTotalCount()).addData("result", spacePictures.getResult()).toJson(response);
    }

    /**
     * 图片空间处添加图片
     *
     * @param spaceId
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/spacePicture/createSpacePicture/{id}", method = RequestMethod.POST)
    public void createSpacePicture(@PathVariable("id") int spaceId, SpacePictureUploadItem spacePictureUploadItem,
                                   HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        try {
            String fileName = spacePictureUploadItem.getUploadFile().getOriginalFilename();
            if (!PictureValidateUtil.isPicture(fileName)) {
                new JsonResult(false, "不是图片文件，上传该图片失败").toJson(response);
                return;
            }
            if (fileName.length() > 128) {
                new JsonResult(false, "文件名太长! 请不要超过 128 个字").toJson(response);
                return;
            }
            // 空间编号
            // 从商品管理的描述管理处保存图片
            if (spaceId == 0) {
                spaceId = spacePictureService.queryDefaultSpacePropertyId();
            }
            String picUrl = imageUpload(spacePictureUploadItem, fileName, spaceId);
            if (StringUtils.isEmpty(picUrl)) {
                new JsonResult(false, "上传该图片失败").toJson(response);
                return;
            }

            new JsonResult(true).addData("picUrl", picUrl).toJson(response);
        } catch (Exception e) {
            logger.error("图片空间管理的上传图片异常：" + e);
            new JsonResult(false, "上传图片出错").toJson(response);
        }

    }


    /**
     * kindeditor功能里面的上传图片
     *
     * @param spacePictureUploadItem
     * @param response
     * @throws IOException
     * @throws MagickException
     */
    @RequestMapping(value = "/spacePicture/createImageUpload", method = RequestMethod.POST)
    public void createImageUpload(SpacePictureUploadItem spacePictureUploadItem,
                                  HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            String fileName = spacePictureUploadItem.getUploadFile().getOriginalFilename();
            if (!PictureValidateUtil.isPicture(fileName)) {
                out.println(alertMsg("不是图片文件。"));
                return;
            }
            if (fileName.length() > 128) {
                out.println("文件名太长! 请不要超过 128 个字");
                return;
            }
            int spaceId = spacePictureService.queryDefaultSpacePropertyId();

            String picUrl = imageUpload(spacePictureUploadItem, fileName, spaceId);
            if (StringUtils.isBlank(picUrl)) {
                out.println(alertMsg("上传图片失败。"));
                return;
            }

            out.println(urlJson(picUrl));

        } catch (Exception e) {
            logger.error("图片空间管理的上传图片异常：" + e);
            out.println(alertMsg("上传图片出错。"));
        }

    }

    //图片上传
    private String imageUpload(SpacePictureUploadItem spacePictureUploadItem, String fileName, int spaceId) {
        String picUrl = "";
        try {
            String imageType = PictureValidateUtil.getFileType(fileName);
            String realImageName = UUID.randomUUID().toString().replace("-", "").toUpperCase() + imageType;
            PictureUploadResult uploadResult = uploadImageService.uploadPicture(spacePictureUploadItem.getUploadFile().getInputStream(),
                    spaceId % PICTURE_STORAGE_DIR_MODULUS + "/" + realImageName);
            if (uploadResult.isCdnSuccess() || uploadResult.isLocalSuccess()) {
                SpacePicture spacePicture = new SpacePicture();
                spacePicture.setOriginalName(fileName);
                String cdnUrl = uploadResult.getCdnUrl();
                String pictureLocalUrl = urlBrokerFactory.getUrl("SpaceImageUpload")
                        .addQueryData("fileName", spaceId % PICTURE_STORAGE_DIR_MODULUS + "/" + realImageName).toString();
                spacePicture.setPictureUrl(StringUtils.isEmpty(cdnUrl) ? pictureLocalUrl : cdnUrl);
                spacePicture.setPictureLocalUrl(pictureLocalUrl);
                spacePicture.setPictureName(realImageName);
                spacePicture.setSpaceId(spaceId);
                spacePictureService.createPicture(spacePicture);
                picUrl = spacePicture.getPictureUrl();
            }
        } catch (Exception e) {
            logger.error("图片空间管理的上传图片异常：" + e);
        }

        return picUrl;
    }

    /**
     * 删除空间里面的图片
     *
     * @param ids
     * @param response
     * @throws IOException
     */
    @Permission("删除图片")
    @RequestMapping(value = "/spacePicture/deleteSpacePicture")
    public void deleteImages(@RequestParam("ids") String[] ids, HttpServletResponse response) throws IOException {
        try {
            List<SpacePicture> pictures = new ArrayList();
            for (String id : ids) {
                pictures.add(spacePictureService.querySpacePictureById(new Integer(id).intValue()));
            }
            for (SpacePicture picture : pictures) {
                uploadImageService.deletePicture(picture.getPictureLocalUrl());
                if (!picture.getPictureUrl().equals(picture.getPictureLocalUrl())) {
                    uploadImageService.deletePicture(picture.getPictureUrl());
                }
                spacePictureService.deletePicture(picture.getId());
            }
        } catch (Exception e) {
            logger.error("图片空间的图片删除异常：" + e);
            new JsonResult(false, "删除图片空间出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @Permission("删除图片空间")
    @RequestMapping(value = "/spacePicture/deleteSpace")
    public void deleteSpace(@RequestParam("spaceName") String spaceName, HttpServletResponse response) throws IOException {
        try {
            SpaceProperty space = spacePictureService.querySpaceByName(spaceName);
            int totalPicture = spacePictureService.queryCountPictureOfSpace(space.getId());
            if (totalPicture > 0) {
                new JsonResult(false, "该空间有图片，不能删除").toJson(response);
            } else {
                spacePictureService.deleteSpacePicture(space.getId());
                spacePictureService.deleteSpace(spaceName);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("图片空间管理的删除空间异常：" + e);
            new JsonResult(false, "删除空间出错").toJson(response);
        }

    }

    /**
     * 添加空间与修改空间功能
     *
     * @param spaceProperty
     * @param oldValue
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/spacePicture/updateSpace", method = RequestMethod.POST)
    public void updateSpace(SpaceProperty spaceProperty, String oldValue, HttpServletResponse response) throws IOException {
        try {
            if ((spaceProperty.getSpaceName()).trim().equals("")) {
                new JsonResult(false, "名称不能允许只有空格").toJson(response);
                return;
            }
            List<SpaceProperty> list;
            SpaceProperty currentSpaceProperty = spacePictureService.querySpaceByName(oldValue);
            boolean has = false;

            if (currentSpaceProperty != null) {        //更新空间名字
                if (oldValue.equals(spaceProperty.getSpaceName())) {
                    new JsonResult(false, "该名称已存在或名称没有修改").toJson(response);
                    return;
                }
                list = spacePictureService.queryAllSpace();
                for (SpaceProperty spaceProperty2 : list) {
                    if (spaceProperty2.getSpaceName().equals(spaceProperty.getSpaceName())) {
                        has = true;
                    }
                }
                if (has) {
                    new JsonResult(false, "该名称在空间中已存在").toJson(response);
                    return;
                } else {
                    currentSpaceProperty.setSpaceName(spaceProperty.getSpaceName());
                    spacePictureService.updateSpaceProperty(currentSpaceProperty);
                }

            } else {                      //创建空间名字
                list = spacePictureService.queryAllSpace();
                for (SpaceProperty spaceProperty2 : list) {
                    if (spaceProperty2.getSpaceName().equals(spaceProperty.getSpaceName())) {
                        has = true;
                    }
                }
                if (has) {
                    new JsonResult(false, "该名称在空间中已存在").toJson(response);
                    return;
                } else {
                    spacePictureService.createSpaceProperty(spaceProperty);
                }
            }
        } catch (Exception e) {
            logger.error("图片空间管理的保存空间异常：" + e);
            new JsonResult(false, "保存空间失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    private String alertMsg(String message) {
        StringBuffer stringBuffer = new StringBuffer("{\"error\":1,\"message\":\"");
        stringBuffer.append(message).append("\"}");
        return stringBuffer.toString();
    }

    private String urlJson(String url) {
        StringBuffer stringBuffer = new StringBuffer("{\"error\":0,\"url\":\"");
        stringBuffer.append(url).append("\"}");
        return stringBuffer.toString();
    }

}
