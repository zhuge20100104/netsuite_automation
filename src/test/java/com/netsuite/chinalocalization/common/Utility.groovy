package com.netsuite.chinalocalization.common

import com.google.inject.Inject
import com.netsuite.chinalocalization.lib.NetSuiteAppCN
import com.netsuite.testautomation.aut.NetSuiteApp
import com.netsuite.testautomation.aut.system.NSCredentials

public class Utility {
    @Inject
    static NetSuiteApp aut

    static String getEnvValue(String key) {
        return System.getenv(key) == null ? aut.getContext().getProperty(key) : System.getenv(key)
    }

    public static boolean login(NetSuiteApp aut) {
        return loginAsRole(aut, null);
    }

    public static boolean loginAsRole(NetSuiteApp aut, String role) {
        String userName = aut.getContext().getProperty("testautomation.nsapp.default.user");
        String pwd = aut.getContext().getProperty("testautomation.nsapp.default.password");
        String company = aut.getContext().getProperty("testautomation.nsapp.default.account");
        NSCredentials userLogin = new NSCredentials([user: userName, password: pwd]);
        boolean loginSuccess = ((NetSuiteAppCN) aut).logInViaForm(userLogin);
        if (!loginSuccess)
            return loginSuccess;
        loginSuccess = aut.answerSecurityQuestion(["What was your childhood nickname?"                    : "nickname",
                                                   "In what city or town did your mother and father meet?": "town",
                                                   "In what city or town was your first job?"             : "city"]);
        if (!loginSuccess)
            return loginSuccess;
        if (role != null) {
            loginSuccess = aut.selectRole("role": role, "company": company);
        }
        // Go to Home
        if (!aut.getPageTitle().startsWith("Home")) {
            aut.navigateTo("/app/center/card.nl?sc=-29");
        }
        return loginSuccess;
    }

    static def switchToRole(NetSuiteApp aut, String role) {
        def company = aut.getContext().getProperty("testautomation.nsapp.default.account")

        def loginSuccess = aut.isLoginSuccessful()
        if (loginSuccess) {
            if (role) {
                loginSuccess = aut.selectRole("role": role, "company": company)
            } else {
                loginSuccess = false
            }
        }
        return loginSuccess
    }

    public static String getSuiteletURL(NetSuiteApp aut, String scriptId, String deploymentId) {
        return aut.executeScript("return (nlapiResolveURL('SUITELET', '" + scriptId + "', '" + deploymentId + "'));");
    }

    // customer/vendor, customerName
    public static String getEntityId(NetSuiteApp aut, String type, String name) {
        String script = "var x = nlapiSearchRecord('" + type + "', null, new nlobjSearchFilter('entityId', null, 'contains', '" + name + "'));" +
                "return((x != null) ? x[0].getId() : null);";
        return (aut.executeScript(script));
    }

    public static String getNonCashflowSubsidiaryId(NetSuiteApp aut, String name) {
        String script = "var x = nlapiSearchRecord('subsidiary', null, [new nlobjSearchFilter('name', null, 'contains', ' " + name + "'), new nlobjSearchFilter('name', null, 'doesnotcontain', 'Cash')]);" +
                "return((x != null) ? x[0].getId() : null);";
        return (aut.executeScript(script));
    }

    public static String getSubsidiaryId(NetSuiteApp aut, String includeName, String excludeName) {
        String script = "var x = nlapiSearchRecord('subsidiary', null, [new nlobjSearchFilter('name', null, 'contains', ' " + includeName + "'), new nlobjSearchFilter('name', null, 'doesnotcontain', ' + excludeName + ')]);" +
                "return((x != null) ? x[0].getId() : null);";
        return (aut.executeScript(script));
    }

}