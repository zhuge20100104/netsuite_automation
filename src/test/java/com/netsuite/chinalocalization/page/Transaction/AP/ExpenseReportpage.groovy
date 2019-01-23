package com.netsuite.chinalocalization.page

import com.netsuite.chinalocalization.common.CONSTANTS.CURL

class ExpenseReportpage extends TransactionBasePage {

    public static String TITLE="Expense Report";
    public static String URL=CURL.EXPENSE_REPORT_CURL

    public ExpenseReportpage(){
        super(TITLE,URL);
    }

    public fillExpenseReport(){
        createDefaultTransaction()
    }
}
