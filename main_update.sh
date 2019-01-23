#!/bin/bash
set -x
## Global  variables
RERUN_TIME=1

docker_name=""
GROUP="ALL"
EXCLUDE_GROUP="NO"
TESTS_TO_RUN=""
USERNAME=""
PASSWORD=""
ACCOUNT=""
BROWSER="chrome"
close_browser_on_teardown=false
close_browser_on_suite_end=true
Language="zh_CN"
RESULT_PATH="${WORKSPACE}/uitest/data/testrun"
tag="$( echo $BUILD_TAG | sed 's/.*Linux0.*-//' )"
Tag_Path=$RESULT_PATH/$tag

## Global proxy information for docker and maven
proxy="-Dhttp.proxyHost=www-proxy.us.oracle.com -Dhttp.proxyPort=80 -Dhttps.proxyHost=www-proxy.us.oracle.com -Dhttps.proxyPort=80"


##Temporoary group info string
group_str=""
exclude_group_str=""


function printWSInfo() {
    echo ${BUILD_TAG}
    echo "workspace"
    echo ${WORKSPACE}
}


function killChromeProc(){
    docker exec  $docker_name pkill -9 -f chrom*  &
    docker exec  $docker_name pkill -f java -9 &
    docker exec  $docker_name rm -rf results
}


function removeDetailExcel() {
   [ -f "${RESULT_PATH}/detail_excel.xlsx" ] && { mv  ${RESULT_PATH}/detail_excel.xlsx  ${RESULT_PATH}/old_${tag}_detail_excel.xlsx ; }
}


function reDownloadCommon() {
    docker exec $docker_name rm -rf /headless/.m2/repository/com/netsuite/nscommon_automation/
}

function mvnClean() {
    docker exec $docker_name  mvn clean  $proxy
}

function getGroupInfo() {
    if [ "${GROUP}" != "All" ]; then
	    echo "has a group ${GROUP}"
        group_str="-Dgroups="${GROUP}
    fi

    if [ "${GROUP}" = "com.netsuite.common.NSError" ]; then
	    echo "Rerun NSError cases!!"
        docker exec  $docker_name  mvn exec:java -Dexec.mainClass="com.netsuite.base.report.rerun.ReRun"  -Dexec.args="fail_watch_file_latest.txt"  -Dexec.classpathScope=test $proxy
    fi
}

function removeFiles() {
    [ -f "${RESULT_PATH}/fail_watch_file.txt" ] && { mv  ${RESULT_PATH}/fail_watch_file.txt  ${Tag_Path}_fail_watch_file.txt ; }
    [ -f "${RESULT_PATH}/pass_watch_file.txt" ] && { mv  ${RESULT_PATH}/pass_watch_file.txt  ${Tag_Path}_pass_watch_file.txt ; }
    [ -f "${RESULT_PATH}/pass_detail_file.txt" ] && { mv  ${RESULT_PATH}/pass_detail_file.txt ${Tag_Path}_pass_detail_file.txt ; }
}

function removeFailedDetailFile() {
    [ -f "${RESULT_PATH}/fail_detail_file.txt" ] && { mv  ${RESULT_PATH}/fail_detail_file.txt ${Tag_Path}_fail_detail_file.txt ; }
}


function getExcludeGroupInfo() {
    if [ "${EXCLUDE_GROUP}" != "NO" ]; then
        exclude_group_str="-DexcludedGroups="${EXCLUDE_GROUP}
    fi

    echo $group_str
    echo $exclude_group_str
}


function mvnRunTest() {
    docker exec  $docker_name  mvn -Dresults.dir=./results_${BUILD_TAG}  ${group_str} \
    ${exclude_group_str}  $proxy \
    -Dlog4j.configuration=file:src/resources/mylog4j-with-custom-html-reporting.xml \
    -Dtest=${TESTS_TO_RUN} -Dtestautomation.nsapp.default.user=${USERNAME} \
    -Dtestautomation.nsapp.default.password=${PASSWORD} \
    -Dtestautomation.nsapp.default.account="${ACCOUNT}" \
    -Dtestautomation.browser=${BROWSER} \
    -Dtestautomation.selenium.close_browser_on_teardown=${close_browser_on_teardown} \
    -Dtestautomation.selenium.close_browser_on_suite_end=${close_browser_on_suite_end} \
    -Dtestautomation.nsapp.language=${Language} test  || echo "continue execute"
}


function generateExcelAndMkCopy() {
    docker exec  $docker_name  mvn exec:java -Dexec.mainClass="com.netsuite.base.report.ConvertReportToExcel" -Dexec.classpathScope=test $proxy || echo "continue execute"
    datevar=`date +%Y%m%d%H%M%S`
    docker exec  $docker_name cp data/testrun/detail_excel.xlsx data/testrun/detail_excel_${tag}.xlsx || echo "continue execute"
}


function reRun() {
for((i=1;i<=$RERUN_TIME;i++));  
do  
	docker exec  $docker_name mvn exec:java -Dexec.mainClass="com.netsuite.base.report.rerun.ReRun"  -Dexec.classpathScope=test $proxy

    removeFiles

    docker exec  $docker_name  mvn -Dresults.dir=./results_${BUILD_TAG}_rerun -Dgroups="com.netsuite.common.NSError" \
    $proxy \
    -Dlog4j.configuration=file:src/resources/mylog4j-with-custom-html-reporting.xml \
    -Dtest=${TESTS_TO_RUN} -Dtestautomation.nsapp.default.user=${USERNAME} \
    -Dtestautomation.nsapp.default.password=${PASSWORD} \
    -Dtestautomation.nsapp.default.account="${ACCOUNT}" \
    -Dtestautomation.browser=${BROWSER} \
    -Dtestautomation.selenium.close_browser_on_teardown=${close_browser_on_teardown} \
    -Dtestautomation.selenium.close_browser_on_suite_end=${close_browser_on_suite_end} \
    -Dtestautomation.nsapp.language=${Language} test  || echo "continue execute"
done  
}


function copyFailedRerunLatest() {
    if [ -f "${RESULT_PATH}/fail_watch_file.txt" ]; then
     cp ${RESULT_PATH}/fail_watch_file.txt ${RESULT_PATH}/fail_watch_file_latest.txt
    fi
}


function Usage() {
    echo "Usage information for this shell script"
    echo "./main.sh  
        -h  print this help message 
        -d  docker container name
        -g  group name
        -eg exclude group name
        -t  tests to run
        -u  user name
        -p  password
        -a  company id or account name
        -l  language, zh_CN or en_US
        -r  re-run times ,default rerun for 1 time
        -ct close_browser_on_teardown  true|false
        -cs close_browser_on_suite_end  true|false
    "
}


function readParams() {

for i in "$@"
do
case $i in
    -h|--help)
    Usage; exit 1;
    ;;

    -d=*|--docker=*)
    docker_name="${i#*=}"
    ;;


    -g=*|--group=*)
    GROUP="${i#*=}"
    ;;


    -eg=*|--exclude_group=*)
    EXCLUDE_GROUP="${i#*=}"
    ;;


    -t=*|--tests=*)
    TESTS_TO_RUN="${i#*=}"
    ;;


    -u=*|--user=*)
    USERNAME="${i#*=}"
    ;;

    
    -p=*|--password=*)
    PASSWORD="${i#*=}"
    ;;

    -a=*|--account=*)
    ACCOUNT="${i#*=}"
    echo "$ACCOUNT"
    ;;

    -b=*|--browser=*)
    BROWSER="${i#*=}"
    ;;


    -ct=*|--close-tear-down=*)
    close_browser_on_teardown="${i#*=}"
    ;;

    -cs=*|--close-suite-end=*)
    close_browser_on_suite_end="${i#*=}"
    ;;


    -l=*|--language=*)
    Language="${i#*=}"
    ;;

    -r=*|--rerun=*)
    RERUN_TIME="${i#*=}"
    ;;

    *)
            # unknown option
    ;;
esac
done

}



function Main() {
    ## print current jenkins workspace info
    echo "Print workspace info"
    printWSInfo
    ## read all command line parameters
    echo "Read command line parameters"
    readParams "$@"

    ## kill chrome process in docker
    echo "Kill all chrome processes in docker"
    killChromeProc

    ## remove detail excel file
    echo "Remove detail excel file"
    removeDetailExcel

    ## re-download common
    #echo "Re download common packages"
    reDownloadCommon
    if [ ! -d "lib" ]; then
    	mkdir "lib"

    	curl -XGET https://artifacthub.oraclecorp.com/oardcinfra-generic/netsuite-test/chromedriver-lin -o ./lib/chromedriver-lin
    fi

    #chmod a+x "${WORKSPACE}/uitest/"lib/chromedriver-lin

    ## maven clean command
    echo "Execute mvn clean"
    mvnClean

    ## get common or re-run group info
    echo "Get group info"
    getGroupInfo

    ## remove all files
    echo "Remove files"
    removeFiles
    removeFailedDetailFile

    ## get exclude group info
    echo "Get exclude group info"
    getExcludeGroupInfo

    ## use mvn to run selected tests
    echo "Execute mvn run test"
    mvnRunTest

    ## generate excel and make a copy
    echo "Generate excel and make copy"
    generateExcelAndMkCopy

    ## do re-run step
    echo "Do rerun step"
    reRun

    echo "Copy failed rerun latest"
    copyFailedRerunLatest
}





Main "$@"