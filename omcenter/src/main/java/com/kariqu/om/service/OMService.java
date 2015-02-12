package com.kariqu.om.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.SystemLog;

/**
 * 运维服务
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-4
 *        Time: 上午10:44
 */
public interface OMService {

    void createSystemLog(SystemLog systemLog);

    void deleteSystemLog(int id);

    Page<SystemLog> querySystemLogByPage(int pageNo,int pageSize, String title, String ip);
}
