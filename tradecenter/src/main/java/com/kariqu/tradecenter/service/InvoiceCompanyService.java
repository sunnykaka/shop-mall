package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.InvoiceCompany;

import java.util.List;

/**
 * User: Asion
 * Date: 12-6-4
 * Time: 下午4:10
 */
public interface InvoiceCompanyService {

    void createInvoiceCompany(InvoiceCompany invoiceCompany);

    List<InvoiceCompany> queryUserInvoiceCompany(int userId);

    void updateInvoiceCompany(InvoiceCompany invoiceCompany);

    void deleteInvoiceCompany(int id);
}
