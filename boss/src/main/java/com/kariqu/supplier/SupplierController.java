package com.kariqu.supplier;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.*;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家控制器
 * User: Asion
 * Date: 12-6-20
 * Time: 下午3:03
 */
@Controller
public class SupplierController {

    private final Log logger = LogFactory.getLog(SupplierController.class);

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private MessageTaskService messageTaskService;


    /**
     * 添加商家
     *
     * @param customer
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customer/add", method = RequestMethod.POST)
    public void createCustomer(Supplier customer, HttpServletResponse response) throws IOException {
        try {
            Supplier dbCustomer = supplierService.queryCustomerByName(customer.getName());
            if (null != dbCustomer) {
                new JsonResult(false, "此商家已存在").toJson(response);
            } else {
                supplierService.createCustomer(customer);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("添加商家失败：" + e);
            new JsonResult(false, "添加商家失败").toJson(response);
        }

    }

    /**
     * 删除商家
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("删除商家")
    @RequestMapping(value = "/crm/customer/delete/batch", method = RequestMethod.POST)
    public void deleteCustomer(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                supplierService.deleteCustomerById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除商家错误：" + e);
            new JsonResult(false, "删除商家失败").toJson(response);
        }
    }

    /**
     * 修改商家
     *
     * @param customer
     * @param response
     * @throws java.io.IOException
     */
    @Permission("修改商家")
    @RequestMapping(value = "/crm/customer/update", method = RequestMethod.POST)
    public void updateCustomer(Supplier customer, HttpServletResponse response) throws IOException {
        try {
            Supplier currentCustomer = supplierService.queryCustomerById(customer.getId());
            currentCustomer.setDefaultLogistics(customer.getDefaultLogistics());
            supplierService.updateCustomer(currentCustomer);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改商家失败：" + e);
            new JsonResult(false, "编辑失败").toJson(response);
        }

    }

    /**
     * 查询商家列表,带分页
     *
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customer/list")
    public void customerList(@RequestParam("start") int start, @RequestParam("limit") int limit, HttpServletResponse response) throws IOException {
        Page<Supplier> customerPage = supplierService.queryCustomerByPage(new Page<Supplier>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", customerPage.getTotalCount()).addData("result", customerPage.getResult()).toJson(response);
    }

    /**
     * 添加仓库
     *
     * @param productStorage
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/product/storage/add", method = RequestMethod.POST)
    public void createProductStorage(ProductStorage productStorage, HttpServletResponse response) throws IOException {
        try {
            ProductStorage dbProductStorage = supplierService.queryProductStorageByNameAndCustomerId(productStorage.getName(), productStorage.getCustomerId());
            if (null != dbProductStorage) {
                new JsonResult(false, "此库位已存在").toJson(response);
            } else {
                supplierService.createProductStorage(productStorage);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("添加仓库失败：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }
    }

    /**
     * 批量删除商家仓库
     * <p/>
     * 如果仓库下有商品是不能删除的
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("删除商家仓库")
    @RequestMapping(value = "/crm/product/storage/delete/batch", method = RequestMethod.POST)
    public void deleteProductStorage(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            int sum = 0;  // 存放仓库下的商品数
            for (int id : ids) {
                sum += supplierService.querySkuCountByProductStorageId(id);
            }
            // 判断仓库下是否有商品
            if (sum > 0) {
                new JsonResult(false, "false").toJson(response);
            } else {
                for (int id : ids) {
                    supplierService.deleteProductStorageById(id);
                }
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("批量删除商家仓库失败：" + e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }
    }

    /**
     * 修改商家仓库
     *
     * @param productStorage
     * @param response
     * @throws java.io.IOException
     */
    @Permission("修改商家仓库")
    @RequestMapping(value = "/crm/product/storage/update", method = RequestMethod.POST)
    public void updateProductStorage(ProductStorage productStorage, HttpServletResponse response) throws IOException {
        try {
            ProductStorage dbProductStorage = supplierService.queryProductStorageByNameAndCustomerId(productStorage.getName(), productStorage.getCustomerId());

            // 数据库已经存在修改名字后的库位
            if (null != dbProductStorage && !productStorage.getName().equals(dbProductStorage.getName())) {
                new JsonResult(false, "此库位已存在").toJson(response);
            } else {
                ProductStorage currentProductStorage = supplierService.queryProductStorageById(productStorage.getId());
                currentProductStorage.setId(productStorage.getId());
                currentProductStorage.setName(productStorage.getName());
                currentProductStorage.setCustomerId(productStorage.getCustomerId());
                currentProductStorage.setAddress(productStorage.getAddress());
                currentProductStorage.setConsignor(productStorage.getConsignor());
                currentProductStorage.setRemarks(productStorage.getRemarks());
                currentProductStorage.setCompany(productStorage.getCompany());
                currentProductStorage.setTelephone(productStorage.getTelephone());
                currentProductStorage.setLocation("");
                supplierService.updateProductStorage(currentProductStorage);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("修改商家仓库失败：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
    }

    /**
     * 添加商家经营的品牌
     *
     * @param brand
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/brand/add", method = RequestMethod.POST)
    public void createCustomerBrand(Brand brand, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Brand dbBrand = supplierService.queryBrandByName(brand.getName());
            if (dbBrand != null) {
                new JsonResult(false, "该品牌已存在").toJson(response);
                return;
            }

            supplierService.createBrand(brand);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("添加商家品牌失败：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }
    }

    /**
     * 删除商家的某个品牌
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("删除商家品牌")
    @RequestMapping(value = "/crm/brand/delete/batch", method = RequestMethod.POST)
    public void deleteCustomerBrand(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                supplierService.deleteBrandById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除商家品牌失败：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }

    }

    /**
     * 修改商家的某个品牌
     *
     * @param brand
     * @param response
     * @throws java.io.IOException
     */
    @Permission("修改商家品牌")
    @RequestMapping(value = "/crm/brand/update", method = RequestMethod.POST)
    public void updateCustomerBrand(Brand brand, HttpServletResponse response) throws IOException {
        try {
            Brand brandInDb = supplierService.queryBrandById(brand.getId());
            brandInDb.setStory(brand.getStory());
            brandInDb.setDesc(brand.getDesc());
            brandInDb.setPicture(brand.getPicture());
            // brandInDb.setName(brand.getName());
            supplierService.updateBrand(brandInDb);

            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改商家品牌失败：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
    }

    /**
     * 某个商家的品牌列表
     *
     * @param response
     * @param customerId
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customer/brand/list/{customerId}")
    public void brandList(@RequestParam("start") int start,
                          @RequestParam("limit") int limit,
                          @PathVariable("customerId") int customerId,
                          HttpServletResponse response) throws IOException {
        Page<Brand> customerPage = supplierService.queryBrandByPage(customerId, new Page<Brand>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", customerPage.getTotalCount()).addData("result", customerPage.getResult()).toJson(response);
    }

    /**
     * 查询某个商家的所有库位
     *
     * @param response
     * @param customerId
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customer/store/list/{customerId}")
    public void productStoreList(@RequestParam("start") int start,
                                 @RequestParam("limit") int limit,
                                 @PathVariable("customerId") int customerId,
                                 HttpServletResponse response) throws IOException {
        Page<ProductStorage> productStoragePage = supplierService.queryProductStorageByPage(customerId, new Page<ProductStorage>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", productStoragePage.getTotalCount()).addData("result", productStoragePage.getResult()).toJson(response);
    }

    /**
     * 给商家分配帐号
     *
     * @param supplierAccount
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customerAccount/add")
    public void addCustomerAccount(final SupplierAccount supplierAccount, HttpServletResponse response) throws IOException {
        try {
            SupplierAccount account = supplierService.querySupplierAccountByName(supplierAccount.getAccountName(), supplierAccount.getCustomerId());
            if (account != null) {
                new JsonResult(false, "该帐号已存在").toJson(response);
            } else {
                supplierAccount.setMainAccount(true);
                supplierAccount.setPassword(BCryptUtil.encryptPassword("666666"));  // 加密，密码默认为"666666"
                supplierService.createSupplierAccount(supplierAccount);

                // 给用户发送邮件
                final Map mailParams = new HashMap();
                mailParams.put("mailKey", "message");
                mailParams.put("accountName", supplierAccount.getAccountName()); // 用于在邮件显示用户名
                mailParams.put("password", "666666"); // 用于在邮件显示密码，默认为"666666"
                sendMail(mailParams, supplierAccount.getEmail());
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("添加商家帐号错误：" + e);
            new JsonResult(false, "添加失败").toJson(response);
        }
    }

    /**
     * 发送HTML邮件
     *
     * @param mailParams
     * @param mail
     * @return
     */
    private boolean sendMail(Map mailParams, String mail) {
        // mailFrom 与 JavaMailSenderImpl 中的 userName 值一致, 避免多个地方维护, 这个值移到 JavaMailSenderImpl 发送邮件之前去设置.
        MailHeader mailHeader = new MailHeader();
        mailHeader.setMailSubject("易居尚邮件通知");
        mailHeader.setMailTo(mail);
        mailHeader.setParams(mailParams);
        try {
            messageTaskService.sendHtmlMail(mailHeader, MessageTemplateName.ACCOUNT_INFORM);
        } catch (Exception e) {
            logger.error("邮件发送失败：" + e);
            return false;
        }
        return true;
    }

    /**
     * 查询某个商家的帐号
     *
     * @param response
     * @param customerId
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customerAccount/list/{customerId}")
    public void SupplierAccountList(@RequestParam("start") int start,
                                    @RequestParam("limit") int limit,
                                    @PathVariable("customerId") int customerId,
                                    HttpServletResponse response) throws IOException {
        Page<SupplierAccount> supplierAccountPage = supplierService.querySupplierAccountByPage(customerId, new Page<SupplierAccount>(start / limit + 1, limit));
        new JsonResult(true).addData("totalCount", supplierAccountPage.getTotalCount()).addData("result", supplierAccountPage.getResult()).toJson(response);
    }

    /**
     * 修改商家的帐号
     *
     * @param customerAccount
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/crm/customerAccount/update", method = RequestMethod.POST)
    public void updateSupplierAccount(final SupplierAccount customerAccount,
                                      HttpServletResponse response) throws IOException {
        try {
            String newPassword = customerAccount.getPassword(); // 新密码，防止加密 让其发送到商家邮箱
            customerAccount.setPassword(BCryptUtil.encryptPassword(customerAccount.getPassword()));  // 加密
            supplierService.updateSupplierAccount(customerAccount);

            // 给用户发送邮件
            final Map mailParams = new HashMap();
            mailParams.put("mailKey", "message");
            mailParams.put("accountName", customerAccount.getAccountName()); // 用于在邮件显示用户名
            mailParams.put("password", newPassword); // 用于在邮件显示密码
            sendMail(mailParams, customerAccount.getEmail());
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改商家帐号失败：" + e);
            new JsonResult(false, "修改失败").toJson(response);
        }
    }

    /**
     * 批量删除商家帐号
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("删除商家账号")
    @RequestMapping(value = "/crm/customerAccount/delete/batch", method = RequestMethod.POST)
    public void deleteSupplierAccount(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                supplierService.deleteSupplierAccountById(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除商家帐号失败：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }
    }

}
