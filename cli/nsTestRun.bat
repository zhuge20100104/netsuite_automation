rem start run test cases in command line
cd /d %~dp0

rem accept CI setting via System environment
set ns.automation.tests=%ns.auto.tests%

rem com.netsuite.chinalocalization.voucher.VoucherPrintParamsTest
rem com.netsuite.chinalocalization.voucher.VoucherPrintTest
rem com.netsuite.chinalocalization.voucher.*Test
rem com.netsuite.chinalocalization.**.*Test
rem com.netsuite.chinalocalization.**.**.*Test
rem com.netsuite.chinalocalization.**/**/*Test
if not defined ns.automation.tests (set ns.automation.tests=com.netsuite.chinalocalization.**.*Test)
rem set ns.automation.tests=com.netsuite.chinalocalization.voucher.VoucherPrintTest

rem output the passed tests suite
echo %ns.automation.tests%

rem set script path
set ns_groovy_automation=%~dp0..\..\ChinaLocalization_Automation
set result_dir=results
set log4j_configuration=file:src/resources/mylog4j-with-custom-html-reporting.xml
set report_folder=%ns_groovy_automation%\results

rem ***delete previous report files**
rem cd %report_folder%
rem call del *.* /F /Q /S
rem call del /S /F /Q *.*
rem call rmdir /S /Q *.*
if EXIST %report_folder% (call rmdir /S /Q %report_folder%)

rem clean previous target files and re-build the project
cd %ns_groovy_automation%
rem call mvn clean install
call mvn clean test-compile

rem **build ns_groovy_automation and run it**
cd %ns_groovy_automation%
call mvn -Dresults.dir=%result_dir% -Dlog4j.configuration=%log4j_configuration% -Dtest=%ns.automation.tests% test
rem call mvn -Dresults.dir=results -Dlog4j.configuration=file:src/resources/mylog4j-with-custom-html-reporting.xml -Dtest=com.netsuite.chinalocalization.voucher.VoucherPrintTest test