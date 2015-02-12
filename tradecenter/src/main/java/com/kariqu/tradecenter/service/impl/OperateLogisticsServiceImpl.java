package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.BCConvert;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.tradecenter.domain.BackMsg;
import com.kariqu.tradecenter.domain.LogisticsInfo;
import com.kariqu.tradecenter.domain.PackageLogistics;
import com.kariqu.tradecenter.repository.OperaLogisticsRepository;
import com.kariqu.tradecenter.service.OperateLogisticsService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作物流信息的服务类
 *
 * @author Athens(刘杰)
 * @Time 2012-09-25 12:55
 * @since 1.0.0
 */
public class OperateLogisticsServiceImpl implements OperateLogisticsService {

    private static final Log LOGGER = LogFactory.getLog("LogisticsLog");

    @Autowired
    private OperaLogisticsRepository operaLogisticsRepository;

    @Autowired
    private PackageLogistics packageLogistics;

    @Override
    public void receiveThirdLogisticsInfo(String msg) {
        if (StringUtils.isBlank(msg))
            return;

        // 将返回的数据转换为 对象.
        BackMsg backMsg = JsonUtil.json2Object(msg, BackMsg.class);

        LogisticsInfo log = new LogisticsInfo();
        log.setExpressNo(BCConvert.qj2bj(backMsg.getLastResult().getNu()));
        log.setExpressValue(msg);
        log.setStatus(backMsg.getLastResult().getIscheck());

        updateLogistics(log);
    }

    @Override
    public void handleThirdLogisticsInfo(String company, String expressNumber, String from, String to) {
        expressNumber = BCConvert.qj2bj(expressNumber);
        // 若不为空则表示有向三方物流发送过查询请求.
        if (queryLogistics(expressNumber) != null) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("快递单(" + expressNumber + "/" + company + ")已经向第三方发送过请求, 不需要再发送.");
            return;
        }

        // 发送请求若返回成功, 则写入数据库并返回请求成功的标识
        if (packageLogistics.requestThirdLogistics(company, expressNumber, from, to)) {
            LogisticsInfo logisticsInfo = new LogisticsInfo();
            logisticsInfo.setExpressNo(expressNumber);
            logisticsInfo.setExpressValue("");
            logisticsInfo.setStatus(0);

            insertLogistics(logisticsInfo);
        }
    }

    @Override
    public LogisticsInfo queryLogistics(String expressNumber) {
        return operaLogisticsRepository.select(expressNumber);
    }

    @Override
    @Transactional
    public void insertLogistics(LogisticsInfo info) {
        operaLogisticsRepository.insert(info);
    }

    @Override
    @Transactional
    public void updateLogistics(LogisticsInfo info) {
        operaLogisticsRepository.update(info);
    }

}
