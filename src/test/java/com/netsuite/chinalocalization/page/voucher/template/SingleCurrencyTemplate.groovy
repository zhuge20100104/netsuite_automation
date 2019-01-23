package com.netsuite.chinalocalization.page.voucher.template
/**
 * mia.wang@oracle.com
 * Mar/9/2018
 */
class SingleCurrencyTemplate extends VoucherTemplate {
    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    /**
     * the difference between mutli-ccy template vs single-ccy template is the last element in xpath
     * for multi-ccy is tr[1]/th[1], but for single-ccy is tr[1]/td[1]
     * that's why adding getReport{Creator/Approver/Poster/Attachment}Xpath to abstract method
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportCreatorXpath(int reportIndex) {
        String XPATH_RPT_CREATOR = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/td[1]";
        return XPATH_RPT_CREATOR;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportApproverXpath(int reportIndex) {
        String XPATH_RPT_APPROVER = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/td[2]";
        return XPATH_RPT_APPROVER;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportPosterXpath(int reportIndex) {
        String XPATH_RPT_POSTER = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/td[3]";
        return XPATH_RPT_POSTER;
    }

    /**
     *
     * @param reportIndex: starts from 1
     * @return
     */
    @Override
    String getReportAttachmentXpath(int reportIndex) {
        String XPATH_RPT_ATTACHMENT = "//span[@id='inlinehtml_fs']/span/pdf/table[@class='persontable'][" + reportIndex + "]/tbody/tr[1]/td[4]";
        return XPATH_RPT_ATTACHMENT;
    }

    /**
     *
     * @param headerTitle
     * @param headerSubTitle: Not used in Single Currency Template
     * @param reportIndex: starts from 1
     * @param lineIndex: starts from 1
     * @return {String} xpath
     */
    @Override
    String getReportItemsOnLineXpath(String headerTitle, String headerSubTitle, int reportIndex, int lineIndex) {
        String xpath = "";
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
            case ["Functional Currency"]:
                switch (headerSubTitle) {
                    case ["Debit"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[4]";
                        break;
                    case ["Credit"]:
                        xpath = ".//*[@id='inlinehtml_val']/pdf/table[@class='itemtable'][" + reportIndex + "]/tbody/tr[" + lineIndex + "]/td[5]";
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return xpath;
    }
}
