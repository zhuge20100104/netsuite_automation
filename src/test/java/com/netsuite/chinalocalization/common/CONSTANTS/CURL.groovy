package com.netsuite.chinalocalization.common.CONSTANTS

class CURL {
    public static String HOME_CURL = "/app/center/card.nl";
    public static String MY_ROLES="/app/center/myroles.nl";

    // Journal Entry
    public static String JOURNAL_ENTRY_CURL = "/app/accounting/transactions/journal.nl";

    // AP transaction urls
    public static String EXPENSE_REPORT_CURL = "/app/accounting/transactions/exprept.nl";
    public static String PURCHASE_ORDER_CRUL= "/app/accounting/transactions/purchord.nl";
    public static String VENDOR_BILL_CURL = "/app/accounting/transactions/vendbill.nl";
    public static String VENDOR_CREDIT_CURL = "/app/accounting/transactions/vendcred.nl";
    public static String VENDOR_RETURN_AUTHORISATION_CURL = "/app/accounting/transactions/vendauth.nl";
    public static String CHECK_CURL = "/app/accounting/transactions/check.nl";

    // AR transaction urls
    public static String SALES_ORDER_CURL = "/app/accounting/transactions/salesord.nl";
    public static String INVOICE_CURL = "/app/accounting/transactions/custinvc.nl";
    public static String CREDIT_MEMO_CURL = "/app/accounting/transactions/custcred.nl";
    public static String CASH_SALE_CURL = "/app/accounting/transactions/cashsale.nl";
    public static String CASH_REFUND_CURL = "/app/accounting/transactions/cashrfnd.nl";
    public static String RETURN_AUTHORISATION_CURL = "/app/accounting/transactions/rtnauth.nl";
    public static String CUSTOMER_PAYMENT_CURL = "/app/accounting/transactions/custpymt.nl";
    public static String BANK_DEPOSIT_CURL = "/app/accounting/transactions/deposit.nl";
    public static String CUSTOMER_DEPOSIT_CURL = "/app/accounting/transactions/custdep.nl";
    public static String CUSTOMER_REFUND_CURL = "/app/accounting/transactions/custrfnd.nl";
    public static String CUSTOMER_CHARGE_CURL = "/app/accounting/transactions/custchrg.nl";
    public static String VENDOR_PAYMENT_CURL = "/app/accounting/transactions/vendpymt.nl";
    public static String DEPOSIT_APPLICATION_CURL = "/app/accounting/transactions/depappl.nl"
    public static String ADVANCED_INTERCOMPANY_JOURNAL_ENTRY_CURL = "/app/accounting/transactions/icjournal.nl"
    public static String INTERCOMPANY_JOURNAL_ENTRY_CURL = "/app/accounting/transactions/journal.nl?icj=T"

    // Invoice Sales Orders
    public static String INVOICE_SALES_ORDERS_CURL = "/app/accounting/transactions/salesordermanager.nl?type=proc&whence="
    // Bulk Process Error
    public static String BULKPROCESSERROR = "/app/accounting/bulkprocessing/bulkprocessingerrors.nl?bulkProcessingSubmission="
    // Processed Records
    public static String PROCESSEDRECORDS = "/app/accounting/bulkprocessing/bulkprocessingresults.nl"
}
