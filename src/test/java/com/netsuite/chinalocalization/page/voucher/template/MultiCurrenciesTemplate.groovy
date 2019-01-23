package com.netsuite.chinalocalization.page.voucher.template

import com.netsuite.chinalocalization.cashflow.CONSTANTS.VoucherEnum

/**
 * mia.wang@oracle.com
 * Mar/9/2018
 */
class MultiCurrenciesTemplate extends VoucherTemplate {
    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportCreatorXpath(int reportIndex) {
        String XPATH_RPT_CREATOR = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/th[1]";
        return XPATH_RPT_CREATOR;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportApproverXpath(int reportIndex) {
        String XPATH_RPT_APPROVER = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/th[2]";
        return XPATH_RPT_APPROVER;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportPosterXpath(int reportIndex) {
        String XPATH_RPT_POSTER = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/th[3]";
        return XPATH_RPT_POSTER;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportAttachmentXpath(int reportIndex) {
        String XPATH_RPT_ATTACHMENT = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/th[4]";
        return XPATH_RPT_ATTACHMENT;
    }

    /**
     *
     * @param headerTitle
     * @param headerSubTitle: Used for Transaction Currency/Functional Currency column
     * @param reportIndex: starts from 1
     * @param lineIndex: starts from 1
     * @return {String} xpath
     */
    @Override
    String getReportItemsOnLineXpath(String headerTitle, String headerSubTitle, int reportIndex, int lineIndex) {
        String xpath = "";
        switch (headerSubTitle) {
            case ["Credit"]:
                switch (headerTitle) {
                    case ["Transaction Currency"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[7]";
                        break;
                    case ["Functional Currency"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[9]";
                        break;
                    default:
                        break;
                }
                break;
            case ["Debit"]:
                switch (headerTitle) {
                    case ["Transaction Currency"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[6]";
                        break;
                    case ["Functional Currency"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[8]";
                        break;
                    default:
                        break;
                }
                break;
            default:
                switch (headerTitle) {
                    case ["Line No."]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[1]";
                        break;
                    case ["Line Description"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[2]";
                        break;
                    case ["Account and Description"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[3]";
                        break;
                    case ["Transaction Currency"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[4]";
                        break;
                    case ["Rate"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[5]";
                        break;
                    default:
                        break;
                }
        }

        return xpath;
    }
}
