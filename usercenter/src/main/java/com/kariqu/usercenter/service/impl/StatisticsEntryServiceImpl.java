package com.kariqu.usercenter.service.impl;

import com.kariqu.usercenter.domain.StatisticsEntry;
import com.kariqu.usercenter.domain.UserEntryInfo;
import com.kariqu.usercenter.repository.StatisticsEntryRepository;
import com.kariqu.usercenter.service.StatisticsEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-06 11:54
 * @since 1.0.0
 */
public class StatisticsEntryServiceImpl implements StatisticsEntryService {

    @Autowired
    private StatisticsEntryRepository statisticsEntryRepository;

    @Transactional
    public long createEntry(StatisticsEntry entry) {
        return statisticsEntryRepository.createEntry(entry);
    }

    public int queryCountEntry(String userName) {
        return statisticsEntryRepository.queryCountEntry(userName);
    }

    public int deleteEntry(String userName) {
        return statisticsEntryRepository.deleteEntry(userName);
    }

    public int physicsDeleteEntry(String userName) {
        return statisticsEntryRepository.physicsDeleteEntry(userName);
    }

    public List<StatisticsEntry> queryEntryByName(String userName, int start, int limit) {
        return statisticsEntryRepository.queryEntryByName(userName, start, limit);
    }

    public List<StatisticsEntry> queryEntryWithNewCount(String userName, int count) {
        return statisticsEntryRepository.queryEntryWithNewCount(userName, count);
    }

    public List<UserEntryInfo> queryActiveEntry(int start, int limit) {
        return statisticsEntryRepository.queryActiveEntry(start, limit);
    }
    
}
