package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.AttentionInfo;
import com.kariqu.productcenter.repository.AttentionInfoRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: wendy
 * Date: 11-6-27
 * Time: 上午10:39
 */
public class AttentionInfoRepositoryImpl extends SqlMapClientDaoSupport implements AttentionInfoRepository {

    @Override
    public List<AttentionInfo> queryAttentionInfo(int productId,String type) {
         Map map=new HashMap();
         map.put("productId",productId);
         map.put("type",type);
         return getSqlMapClientTemplate().queryForList("queryAttentionInfo",map);
    }

    @Override
    public void deleteAttentionById(int id) {
        getSqlMapClientTemplate().delete("deleteAttentionById",id);
    }

    @Override
    public void deleteAttentionByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteAttentionByProductId",productId);
    }

    @Override
    public void updateAttention(AttentionInfo attentionInfo) {
        getSqlMapClientTemplate().update("updateAttention",attentionInfo);
    }

    @Override
    public void createAttention(AttentionInfo attentionInfo) {
          getSqlMapClientTemplate().insert("createAttention",attentionInfo);
    }
}
