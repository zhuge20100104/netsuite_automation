package com.netsuite.base.page.steps.beans



//Selector type
enum SelType {
    MAIN_FIELDID,
    SUB_FIELDID,
    XPATH,
    ID,
    NAME,
    CLASS,
    //Sub tab
    TAB,
    //Form sub tab
    TAB1,
    //Action menus on the top nav bar
    MENUS,
    //dialogs
    DIALOG,
    SLEEP,
    WAIT_FOR_LOAD,
    SWITCH_WINDOW,
    UNKNOWN,
    DELETE_DIR,
    CHECK_REPORT_EXIST,
    //Common sub list row checks
    ROW,
    //Record row,for sublist customized from standard sub list
    RECORD_ROW,
    //Xpath row check, {//_row}
    XPATH_ROW,
    //XPath row edit check, used to click the edit button, {//_row_edit}
    XPATH_ROW_EDIT,

    //XPath column edit check, used to edit the columns in a table for team4, {//_column_edit}
    XPATH_COLUMN_EDIT,

    //XPath row select action, used to select a row in a table, for team4, will be refract in the future
    // {//_row_select}
    XPATH_ROW_SELECT,

    //Xpath rows expect, used to expect many rows in a table, {//_rows}
    XPATH_ROWS
}