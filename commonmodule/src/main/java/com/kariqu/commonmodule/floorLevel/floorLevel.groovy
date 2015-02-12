package com.kariqu.commonmodule.floorLevel

import com.kariqu.designcenter.domain.open.module.Product
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    int floorNumber = NumberUtils.toInt(params.get("floorNumber"))
    if (floorNumber < 1) floorNumber = 5

    // 楼层名(楼层之间以 ~! 隔开)
    def nameList = params.get("floorNames")
    if (StringUtils.isBlank(nameList) || nameList.split("~!").length != floorNumber) {
        throw new RuntimeException("楼层名设置有误!")
    }
    // 楼层主推类目, 与楼层名一样
    def floorMasterPushCategory = params.get("floorMasterPushCategory")
    if (StringUtils.isBlank(floorMasterPushCategory) || floorMasterPushCategory.split("~!").length != floorNumber) {
        log.error("fmpc: " + floorMasterPushCategory)
        throw new RuntimeException("楼层主推类目设置有误!")
    }
    // 楼层主推的大图(楼层之间以 ~! 隔开)
    def floorMasterPushImg = params.get("floorMasterPushImg")
    if (StringUtils.isBlank(floorMasterPushImg) || floorMasterPushImg.split("~!").length != floorNumber) {
        throw new RuntimeException("楼层主推的大图设置有误!")
    }
    // 楼层主推品牌(楼层之间以 ~! 隔开)
    def floorMasterPushBrand = params.get("floorMasterPushBrand")
    if (StringUtils.isBlank(floorMasterPushBrand) || floorMasterPushBrand.split("~!").length != floorNumber) {
        throw new RuntimeException("楼层主推品牌设置有误!")
    }
    // 楼层主推商品(楼层之间以 ~! 隔开):
    def floorMasterPushProduct = params.get("floorMasterPushProduct")
    if (StringUtils.isBlank(floorMasterPushProduct) || floorMasterPushProduct.split("~!").length != floorNumber) {
        throw new RuntimeException("楼层主推商品设置有误!")
    }

    String[] floorNames = nameList.split("~!")
    String[] fmpCategory = floorMasterPushCategory.split("~!")
    String[] fmpImg = floorMasterPushImg.split("~!")
    String[] fmpBrand = floorMasterPushBrand.split("~!")
    String[] fmpProduct = floorMasterPushProduct.split("~!")

    def floorList = []
    for (int i = 0; i < floorNumber; i++) {
        def floor = new Floor()

        floor.setNameList(floorName(floorNames[i]))
        floor.setCategoryList(floorCategory(fmpCategory[i]))
        floor.setImage(floorImg(fmpImg[i]))
        floor.setBrandList(floorBrand(fmpBrand[i]))

        String fpProduct = fmpProduct[i].trim()
        boolean allProId = fpProduct.split("\n").length == 1 && fpProduct.split(",").length > 1
        floor.setAllProId(allProId)
        floor.setProductList(floorProduct(allProId, fpProduct, context.get('productService')))

        floorList << floor
    }
    ["floorList" : floorList]
}

def executeForm(context, params) {
    new HashMap()
}

def floorName(String floorNames) {
    // 楼层名(每楼之间的类目以 换行 隔开, 可以有空行, 每一行类目:名字,链接, 若不需要链接可以不写)
    List<FloorName> floorNameList = []
    for (String fName : floorNames.split("\n")) {
        if (StringUtils.isBlank(fName)) continue

        FloorName frName = new FloorName()
        String[] floorName = fName.split(",")
        if (floorName.length > 0) frName.setName(floorName[0].trim())
        if (floorName.length > 1) frName.setLink(floorName[1].trim())

        floorNameList.add(frName)
    }
    floorNameList
}

def floorCategory(String floorMasterPushCategory) {
    // 楼层类目, 与楼层名一样
    List<FloorCategory> floorCategoryList = []
    for (String fmpCategory : floorMasterPushCategory.split("\n")) {
        if (StringUtils.isBlank(fmpCategory)) continue

        FloorCategory frCategory = new FloorCategory()
        String[] floorBrand = fmpCategory.split(",")
        if (floorBrand.length > 0) frCategory.setName(floorBrand[0].trim())
        if (floorBrand.length > 1) frCategory.setLink(floorBrand[1].trim())

        floorCategoryList.add(frCategory)
    }
    floorCategoryList
}

def floorImg(String floorMasterPushImg) {
    // 楼层主推大图(每楼:图片地址,链接,title)
    FloorImg image = new FloorImg()
    String[] fmpi = floorMasterPushImg.split(",")
    if (fmpi.length > 0) image.setImg(fmpi[0])
    if (fmpi.length > 1) image.setLink(fmpi[1])
    if (fmpi.length > 2) image.setTitle(fmpi[2])
    image
}

def floorBrand(String floorMasterPushBrand) {
    // 楼层主推品牌(每楼之间的品牌以 换行 隔开, 可以空行, 每一行品牌:品牌名~品牌图地址~链接~文字1,*要标红的文字2*,文字3), 若当前品牌占两个位置, 则前后加等号(=)
    List<FloorBrand> floorBrandList = []
    for (String fmpBrand : floorMasterPushBrand.split("\n")) {
        if (StringUtils.isBlank(fmpBrand)) continue
        fmpBrand = fmpBrand.trim()

        FloorBrand frBrand = new FloorBrand()
        String[] floorBrand = fmpBrand.split("~")
        if (floorBrand.length > 0) frBrand.setName(floorBrand[0])
        if (floorBrand.length > 1) frBrand.setImg(floorBrand[1])
        if (floorBrand.length > 2) frBrand.setLink(floorBrand[2])
        if (floorBrand.length > 3) {
            List<FloorBrandDesc> floorBrandDescs = []
            for (String desc : floorBrand[3].split(",")) {
                if (StringUtils.isBlank(desc)) continue
                desc = desc.trim()

                FloorBrandDesc floorBrandDesc = new FloorBrandDesc()
                def red = desc.startsWith("*") && desc.endsWith("*")
                floorBrandDesc.setRed(red)
                floorBrandDesc.setDesc(red ? desc.substring(1, desc.length() - 1) : desc)
                floorBrandDescs.add(floorBrandDesc)
            }
            frBrand.setDescList(floorBrandDescs)
        }
        floorBrandList.add(frBrand)
    }
    floorBrandList
}

def floorProduct(boolean allProId, String floorMasterPushProduct, def productService) {
    List<FloorProduct> floorProductList = []
    if (allProId) {
        // 每一楼的商品若以 英文逗号(,) 隔开则只需要写 商品id 即可
        def array = floorMasterPushProduct.trim().split(",")
        for (String id in array) {
            def pid = NumberUtils.toInt(id)
            if (pid <= 0) continue

            Product product = productService.buildOpenProduct(pid)
            if (product == null) throw new RuntimeException("没有此商品(" + pid + ")")

            FloorProduct frProduct = new FloorProduct()
            frProduct.setImg(product.getImageUrl())
            frProduct.setLink(id)
            frProduct.setName(product.getBrandName() + product.getName())
            frProduct.setDesc(product.getDescription())
            frProduct.setPrice(product.getSellPrice())
            floorProductList.add(frProduct)
        }
    } else {
        // 每一楼的商品若以 换行 隔开则每一行是一个设定, 商品:图片地址~链接~名称~推荐理由~价格, 如果该商品需要占用两个位置, 则前后加星号(*)
        for (String fmpProduct : floorMasterPushProduct.split("\n")) {
            if (StringUtils.isBlank(fmpProduct)) continue
            fmpProduct = fmpProduct.trim()

            FloorProduct frProduct = new FloorProduct()
            def big = fmpProduct.startsWith("*") && fmpProduct.endsWith("*")
            frProduct.setDoublePoint(big)
            String[] floorProduct = (big ? fmpProduct.substring(1, fmpProduct.length() - 1) : fmpProduct).split("~")
            if (floorProduct.length > 0) frProduct.setImg(floorProduct[0])
            if (floorProduct.length > 1) frProduct.setLink(floorProduct[1])
            if (floorProduct.length > 2) frProduct.setName(floorProduct[2])
            if (floorProduct.length > 3) frProduct.setDesc(floorProduct[3])
            if (floorProduct.length > 4) frProduct.setPrice(floorProduct[4])
            floorProductList.add(frProduct)
        }
    }
    floorProductList
}

class Floor {
    List<FloorName> nameList
    List<FloorCategory> categoryList
    FloorImg image
    List<FloorBrand> brandList
    boolean allProId // 是否全是商品id
    List<FloorProduct> productList
}

class FloorName {
    String name
    String link
}

class FloorCategory {
    String name
    String link
}

class FloorImg {
    String img
    String link
    String title
}

class FloorBrand {
    String name
    String img
    String link
    List<FloorBrandDesc> descList
}

class FloorBrandDesc {
    String desc
    boolean red // 是否标红
}

class FloorProduct {
    String img
    String link
    String name
    String desc
    String price
    boolean doublePoint // 是否占两个位置
}
