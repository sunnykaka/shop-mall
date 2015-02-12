package com.kariqu.om;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.SystemLog;
import com.kariqu.om.repository.SystemLogRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-4
 *        Time: 下午2:12
 */
@ContextConfiguration(locations = {"/omCenter.xml"})
public class SystemLogRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SystemLogRepository cmsRepository;

    @Test
    @Rollback(false)
    public void testSystemLogRepository() {
        SystemLog systemLog = new SystemLog();
        systemLog.setId(10);
        systemLog.setTitle("bingo");
        systemLog.setOperator("添加商家");
        systemLog.setContent("AAAAAAA");
        systemLog.setIp("127.0.0.1");
        systemLog.setRoleName("运营");
        cmsRepository.createSystemLog(systemLog);
    }

    @Test
    public void testDeleteSystemLog() {
        SystemLog systemLog = new SystemLog();
        systemLog.setId(10);
        systemLog.setTitle("bingo");
        systemLog.setRoleName("admin");
        systemLog.setOperator("添加商家");
        systemLog.setContent("AAAAAAA");
        systemLog.setIp("127.0.0.1");
        cmsRepository.createSystemLog(systemLog);

        cmsRepository.deleteSystemLog(systemLog.getId());
    }


    @Test
    @Rollback(false)
    public void testQuerySystemLog() {
        SystemLog systemLog = new SystemLog();
        systemLog.setId(10);
        systemLog.setRoleName("admin");
        systemLog.setTitle("aaaaaaa");
        systemLog.setOperator("添加商家");
        systemLog.setContent("AAAAAAA");
        systemLog.setIp("127.0.0.1");
        systemLog.setDate(new Date());

        cmsRepository.createSystemLog(systemLog);
        Page<SystemLog> page = cmsRepository.querySystemLogByPage(1, 10, "a", "127");
        assertEquals("AAAAAAA", (page.getResult().get(0)).getContent());
    }

}
