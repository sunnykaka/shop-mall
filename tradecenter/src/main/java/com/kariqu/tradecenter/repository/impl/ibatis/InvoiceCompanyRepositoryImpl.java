package com.kariqu.tradecenter.repository.impl.ibatis;
import com.kariqu.tradecenter.domain.InvoiceCompany;
import com.kariqu.tradecenter.repository.InvoiceCompanyRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * User: Asion
 * Date: 12-5-30
 * Time: 下午5:50
 */
public class InvoiceCompanyRepositoryImpl extends SqlMapClientDaoSupport implements InvoiceCompanyRepository{


    @Override
    public void createInvoiceCompany(InvoiceCompany invoiceCompany) {
        getSqlMapClientTemplate().insert("insertInvoiceCompany",invoiceCompany);
    }

    @Override
    public List<InvoiceCompany> queryUserInvoiceCompany(int userId) {
        return getSqlMapClientTemplate().queryForList("selectInvoiceCompanyByUserId",userId);
    }

    @Override
    public void updateInvoiceCompany(InvoiceCompany invoiceCompany) {
        getSqlMapClientTemplate().update("updateInvoiceCompany",invoiceCompany);
    }

    @Override
    public void deleteInvoiceCompany(int id) {
        getSqlMapClientTemplate().update("deleteInvoiceCompany",id);
    }
}
