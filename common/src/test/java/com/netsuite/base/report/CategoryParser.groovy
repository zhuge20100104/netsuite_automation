package com.netsuite.base.report

class CategoryParser {
    static String parseCategory(String category) {
        String [] firstArr = category.split("\\[")
        String [] secondArr = firstArr[1].split("]")
        String retCategory = secondArr[0].replace("interface ","")
        retCategory = retCategory.replace("class ","")
        return retCategory
    }

    static void main(String[] args) {
        String categoryStr = "@org.junit.experimental.categories.Category(value=[interface com.netsuite.common.OW])"
        println parseCategory(categoryStr)
    }
}
