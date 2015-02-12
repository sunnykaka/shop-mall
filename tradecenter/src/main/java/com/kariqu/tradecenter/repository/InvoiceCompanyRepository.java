package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.InvoiceCompany;

import java.util.List;

/**
 * 管理购物车和购物项的仓库
 * User: Asion
 * Date: 11-10-11
 * Time: 上午11:28
 */
public interface InvoiceCompanyRepository {

    void createInvoiceCompany(InvoiceCompany invoiceCompany);

    List<InvoiceCompany> queryUserInvoiceCompany(int userId);

    void updateInvoiceCompany(InvoiceCompany invoiceCompany);

    void deleteInvoiceCompany(int id);

}
