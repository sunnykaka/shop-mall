package com.kariqu.suppliersystem.supplierManager.web;

import com.kariqu.securitysystem.domain.UrlPermission;
import com.kariqu.securitysystem.service.SecurityService;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.suppliersystem.supplierManager.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限Vo控制器
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-28
 * Time: 上午10:58
 */
public class PermissionVoController {

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected SupplierService supplierService;

    protected List<PermissionVo> genePermissionVoList() {
        List<PermissionVo> permissionVoList = new ArrayList<PermissionVo>();
        List<UrlPermission> urlPermissions = securityService.queryALlPermission();
        for (UrlPermission urlPermission : urlPermissions) {
            if (permissionVoList.size() == 0) {
                PermissionVo permissionVo = new PermissionVo();
                permissionVo.setCategory(urlPermission.getCategory());
                List<UrlPermission> urlPermissionList = new ArrayList<UrlPermission>();
                urlPermissionList.add(urlPermission);
                permissionVo.setUrlPermissionList(urlPermissionList);
                permissionVoList.add(permissionVo);
            } else {
                boolean has = false;
                for (int j = 0; j < permissionVoList.size(); j++) {
                    if (permissionVoList.get(j).getCategory().equals(urlPermission.getCategory())) {
                        has = true;
                        List<UrlPermission> urlPermissionList = permissionVoList.get(j).getUrlPermissionList();
                        boolean isPermissionName = false;
                        for (UrlPermission urlPermissionVo : urlPermissionList) {
                            if (urlPermissionVo.getPermissionName().equals(urlPermission.getPermissionName())) {
                                isPermissionName = true;
                            }
                        }
                        if (!isPermissionName) {
                            urlPermissionList.add(urlPermission);
                            PermissionVo permissionVo = new PermissionVo();
                            permissionVo.setCategory(urlPermission.getCategory());
                            permissionVo.setUrlPermissionList(urlPermissionList);
                            permissionVoList.remove(j);
                            permissionVoList.add(permissionVo);
                        }
                    }
                }
                if (!has) {
                    PermissionVo permissionVo = new PermissionVo();
                    permissionVo.setCategory(urlPermission.getCategory());
                    List<UrlPermission> urlPermissionList = new ArrayList<UrlPermission>();
                    urlPermissionList.add(urlPermission);
                    permissionVo.setUrlPermissionList(urlPermissionList);
                    permissionVoList.add(permissionVo);
                }
            }
        }
        return permissionVoList;
    }
}
