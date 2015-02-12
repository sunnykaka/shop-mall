package com.kariqu.om.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.SystemLog;
import com.kariqu.om.repository.SystemLogRepository;
import com.kariqu.om.service.OMService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-4
 *        Time: 下午2:10
 */
public class OMServiceImpl implements OMService {

    @Autowired
    private SystemLogRepository cmsRepository;

    @Override
    public void createSystemLog(SystemLog systemLog) {
        cmsRepository.createSystemLog(systemLog);
    }

    @Override
    public void deleteSystemLog(int id) {
        cmsRepository.deleteSystemLog(id);
    }

    @Override
    public Page<SystemLog> querySystemLogByPage(int pageNo, int pageSize, String title, String ip) {
        return cmsRepository.querySystemLogByPage(pageNo, pageSize, title, ip);
    }
}
