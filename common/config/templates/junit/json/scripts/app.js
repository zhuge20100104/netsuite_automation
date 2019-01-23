
var TestResultsRenderer = (function() {

    var TestExecutionRowView = function(data) {
        this.id = data.id;
        this.result = data.result;
        this.issue = data.issue;
        this.title = data.title;
        this.passed = data.passed;
        this.exceptions = data.exceptions;
        this.failed = data.failed;
        this.duration = data.duration;
		this.time = data.time;
        this.actions = data.actions;
        this.execution_log = data.execution_log;
        this.ownerEmail = data.owner;

        /** selectors **/
        this.element = null;
        this.details = null;
        this.buttonSet = null;
        this.minimalButton = null;
        this.assertionsButton = null;
        this.detailedButton = null;
        this.detailsTable = null;
    };

    TestExecutionRowView.prototype = {
        getMarkUp : function() {
            var printOwner = function (ownerEmail) {
                if (ownerEmail) {
                    var ownerName = ownerEmail.replace("@netsuite.com", "");
                    return '<span class="test_owner"><a href="mailto:' + ownerEmail + '" class="author">' + ownerName + '</a></span>';
                } else {
                    return "";
                }
            };
            var timeIssueText = this.result == 'ignored' ? ((this.issue != null) ? "[ <b><font color='blue'>Issue: " + this.issue + "</font></b> ]" : "") :
                        "[ " + (this.time > 180000 ? '<b><font color="red">' : '') + this.duration + (this.time > 180000 ? '</b></font>' : '') + " ]";
            return  '<tr id="_' + this.id +'" class="as_minimal" ><td>' +
                        '<div class="td_contents_wrapper">'+
                            '<div class="result ' + this.result  + '">' +
                                '<span class="text">' + this.title + '  ' + timeIssueText + '</span>' +
								'<span class="overtime">' + ((this.time > 180000) ? '~' : '*') + '</span>' +
									'<div class="stats_' + this.result  + '">' +
                                        printOwner(this.ownerEmail) +
										'&nbsp;|&nbsp;' + this.passed + '&nbsp;' + '<img style="width:24px; height:24px; vertical-align:middle" title="Assert Passed" alt="Assert Passed" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAAEgBckRAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAACuFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM2IDQ2LjI3NjcyMCwgTW9uIEZlYiAxOSAyMDA3IDIyOjEzOjQzICAgICAgICAiPgogPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgeG1sbnM6eGFwUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIKICAgeGFwUmlnaHRzOk1hcmtlZD0iwqkgICAgICAgICAgICYjeEE7IDIwMDkgYnkgT2xpdmVyIFR3YXJkb3dza2kiCiAgIHhhcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHA6Ly9ibG9nLmFkZGljdGVkdG9jb2ZmZWUuZGUiPgogICA8ZGM6cmlnaHRzPgogICAgPHJkZjpBbHQ+CiAgICAgPHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij7CqSAgICAgICAgICAgJiN4QTsgMjAwOSBieSBPbGl2ZXIgVHdhcmRvd3NraTwvcmRmOmxpPgogICAgPC9yZGY6QWx0PgogICA8L2RjOnJpZ2h0cz4KICA8L3JkZjpEZXNjcmlwdGlvbj4KIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAKPD94cGFja2V0IGVuZD0idyI/PkmqNOAAAASFSURBVHjadEwxDgAxCELr6v9/6OQX1At27ZEgJIAyM3hB8QPjycxxd4gI+CEibqCqOOcsqwpmdgO2unsXVHIDGjapLNF/AnA6xigAwCAMrN3E/3/EzYeJg0NKBD/QTCFEc/JHVVXgMMU3qip3qUgSEePPEvHC3dHdoGc245mJbZLMzOQJIJyuwusoELh3795/Pj4+sN1w+6Emg7CIiAgjiA+34e3bt/+BVsI1wMIZpuHly5cM8vLyjHAbQApAGKR4+/btYDFvb2+4R2G2sqBbDQLu7u5g+s+fP3BbQGwUJ8H8gWwbSA4mr6SkBPYDQADOyR0FgBAGorKVJ/PEIt4plQRs3U0gMv4KVwiC5KGTGa+n9LjLdQ100UTUcKQYzFLKqqHW2qwZASnvvfYON1ijxBABieNWgwFmnhQzd/goOqWke87ZSbaOU0KTYowuhDCc9Zf8Fo1/ewa2Pkh8UfwMfD7o/grAXBmjAAzCUNTByYsI3t/Fk3gUx5rCl9+YDC0IdZKS+Ejy8/taS8el9DtA1B9E4ymlh7DQa33QXvRe8uaOrz0wAVCqBWAID5TvbIAmAAEM0DB+VItEA8wZ8EN4eIyxqqu1htbaBrVaGS3A/SshbcOVccSdvQrcxWH31oHi4r33UEpZMd4MkO8C9Eqjkpzz5mWfAOJxIjck6XlYrcB9Snyf52mruARgvwpSIIRhYFjEey8K9g3VF+j/b9I3WP+xZqFS2kmsB/dkoWBRZ9JqJpNXi16CB+SaLVV00pLQ5WIXJ+dQKtUFAYP3fQ8lGhFIhVvdQQ6Ooo/382xGzzU14PkupIirCLQpRX+LQCOURiRDz4i/qUbmvT9NowRcnQf88r7v5zWDhxBonmcVuJrg6IB+oEdnRuu60rZttCwLca7UKHGjnWsaIfeEPKZpKsA1oo9WsHltjDnX4ziStbbIYI2oQemfD+ccdV1HbdvCYi85DEiAIhmGARb02wTp2V/JANKiKgKJRDO+2ocuftOjN4IRpmYLGa9oeS5dRWyxkQBefa+8FvzFtnwFaM9aUi2EYejEFSjOXIIbcPVuwZlbcKTgCh4nkEsJzaet73EfWBC8tleTpj3JOf31D/z7nP868Drw5a2LDEqhSasoJVx5qoMlFEhlsRqF2HCk93Ec/3RmwVUZ+C1H3AjAeBBmzkDaLHtihVauaNHBhOEd933XLyGtbLccsMpIr+zXNKumPeAZn6MMFgHK9aXFSzreYwuhCHhGR6OSc0Ir46wJKYJRD10spijlSv697zvpcud5unupOQIRzmbtg7QPjAh0C20YBrpyS6b6xKq1reuKwzUybFmWz/Nt24jK0ce6jvpwevFUEVnlgJRqr+si45mHwmAcWRzH8fkPc1ENPkvQKeyAzI4pUljUB0SaG3juNE3ZTJsjFfLec8p1IGe8fGnf98SXWWLAUgGHZjErx0a1SSqNShdZLpoTKY7P80yXdiji0TntWXMEZKKJOtxaSvC9jF71Ji6FutIxnjOPoVALZpdE5DFSn5bUUSHyCcMjpfSrSnxD+wE98vC/iVpOYAAAAABJRU5ErkJggg==" />' +
										'&nbsp;&nbsp;' + this.failed + '&nbsp;' +  '<img style="width:24px; height:24px; vertical-align:middle" title="Assert Failed" alt="Assert Failed" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAAEgBckRAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAACuFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM2IDQ2LjI3NjcyMCwgTW9uIEZlYiAxOSAyMDA3IDIyOjEzOjQzICAgICAgICAiPgogPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgeG1sbnM6eGFwUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIKICAgeGFwUmlnaHRzOk1hcmtlZD0iwqkgICAgICAgICAgICYjeEE7IDIwMDkgYnkgT2xpdmVyIFR3YXJkb3dza2kiCiAgIHhhcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHA6Ly9ibG9nLmFkZGljdGVkdG9jb2ZmZWUuZGUiPgogICA8ZGM6cmlnaHRzPgogICAgPHJkZjpBbHQ+CiAgICAgPHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij7CqSAgICAgICAgICAgJiN4QTsgMjAwOSBieSBPbGl2ZXIgVHdhcmRvd3NraTwvcmRmOmxpPgogICAgPC9yZGY6QWx0PgogICA8L2RjOnJpZ2h0cz4KICA8L3JkZjpEZXNjcmlwdGlvbj4KIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAKPD94cGFja2V0IGVuZD0idyI/PkmqNOAAAATRSURBVHjadEwxDgAxCELr6v9/6OQX1At27ZEgJIAyM3hB8QPjycxxd4gI+CEibqCqOOcsqwpmdgO2unsXVHIDGjapLNF/AoiRPFd9//79P8hiEAAZw8nJycgEc9XHjx/BrgGx4UaB7Pn16xfcchAAW/7169f/MJUgSW5ubkaAAHyQQQrAIAwEFXrOybvgL/y/78jdQ34gWFfYUAttIBCIu7PxM9VvKJSqThHZbLrSGZ1Sio5GmdlcSBfwnynovYecc3QrPEBT0Fo7xKRebzTnWmsYYzgF8xGJdzxp2HFfStk33AJQTu0qAIQwTG6Q/v+3iD/lKuLmXeRa4mOpUFwS0rSpe0pPcD43wUyXUgZPhYOJfBweWmtDwUxAicjELgoKRAyZgDhePSgBVWsNOedFzXDaUu996JVxQqEQY7SWTEH7/WMdUkrL4g4Ft2m+7Z1w3QPiy+Z3wreH+b8CMFfFOAyDMNBDJr7ByBsYEW9GYuIViIk/MLI0RjJyHUdth0hlsgnWwfnO+VlLj0vp7wAOuYEaN8a8CYu4lovoJe6x7vT49oEKQONEA+AgvKE85gNQpYgOcADcyznvmYZqr7WqF/kIIOmg4hgjpJSg975y55zuRZEfGgA+l2ubL/zlcFvK77fG4dNbHsS4lAIhhJXPOaG1BtbaSw+o/hZAWppe4r3fswx5Rqfyy3wNMMZYcqMi2Q+NCopPiV/7+fSoeAnAjrWjQAjE0CDrBbTQC9gLHsDCS4uNBxBhqqksPcgaITBkk0y22K0sAqNm8hIn33ly0QPwg3SNLRV10px4QHLCGEpT9QcACm+aRkzREoBWuE0LuHBJe/rOo1nie3mEcys0jV0AFmnaWwCFxUCCj+O4m2x6XtdVBXa7acrcdR0sy3K/Q0/p+z6ruTsO0pJ5nids2wZ1XauH/zUAuWFZlhBCgGmaXO6ZBeCbr24XhmGAeZ6zdVgFkDYSYU3GIESQa24Wi74EVOTCHynGCOM43uuqqmDfd5XXrGiYKtq2dUWypBD+Rhpjza7CkwakXCRZYDZe1uFxoe77BjRT0pAuEPg65cUgzHYVNGJLCdCyIB3B/9q2vAVoz4pxJIRhIFptDw3PgAJBRcfXKeAHFBT8gIKK8jSRssr64tiE3RMnYQmBQgA7DrZn/PUP/PucfxtwG3BxeWomuaGJqyhpuJJYhxBRQJnF6ChkFUednef5n64ssKoN/CFDRA9AeQBmm4G4VZbICq5c4byDBcM7tm2L30Jc2R4yIFRGSmU/x1md+gck5aXKPVSUuGy6vXbna+rqR6zyPu9gz+77/kbAuWfcW9fVGwA4mHnaAO22wZGmaTKOYzJN0y+WEGO4hzlahU97QIPZ6HXXdehqJcMwvBQAnoAAEQEdhZ7/Sh44KnVdJ8uyGIQFaZrGYJXLZGIOidFQiPYhWFV0UbgaXEJ1pzxAs6MbKULQB6gQgu0E6fseLcakLEsRDtFryaiHZqW5EoAaCKIcxAiSX1VVL2jVtq05457vOd+Y1itP7XbxxWoax9FKKIrC7HeqDMYRQud5fiPpQxBP4wHVFtKgZIhVzDYDqGRZZo7QStMx2hyIjkJudtT8bEfnSMZ8LIweNeSIkTHvE0G9W1JreFdNMtIorimlb1biCvID3OMs/q5znbcAAAAASUVORK5CYII=" />' + 
										'&nbsp;&nbsp;' + this.exceptions + '&nbsp;' +  '<img style="width:24px; height:24px; vertical-align:middle" title="Exceptions" alt="Exceptions" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAAEgBckRAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAACuFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM2IDQ2LjI3NjcyMCwgTW9uIEZlYiAxOSAyMDA3IDIyOjEzOjQzICAgICAgICAiPgogPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICAgeG1sbnM6eGFwUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIKICAgeGFwUmlnaHRzOk1hcmtlZD0iwqkgICAgICAgICAgICYjeEE7IDIwMDkgYnkgT2xpdmVyIFR3YXJkb3dza2kiCiAgIHhhcFJpZ2h0czpXZWJTdGF0ZW1lbnQ9Imh0dHA6Ly9ibG9nLmFkZGljdGVkdG9jb2ZmZWUuZGUiPgogICA8ZGM6cmlnaHRzPgogICAgPHJkZjpBbHQ+CiAgICAgPHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij7CqSAgICAgICAgICAgJiN4QTsgMjAwOSBieSBPbGl2ZXIgVHdhcmRvd3NraTwvcmRmOmxpPgogICAgPC9yZGY6QWx0PgogICA8L2RjOnJpZ2h0cz4KICA8L3JkZjpEZXNjcmlwdGlvbj4KIDwvcmRmOlJERj4KPC94OnhtcG1ldGE+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAKPD94cGFja2V0IGVuZD0idyI/PkmqNOAAAAPTSURBVHjadIzBDUAhCEMLcnUQ99+GATyxAvJTvPqbwGtCqVQVXlL8yLgiouacEBGwYe99P1QVYwy4e9PM7oGpcw7WWk1OV9FkZpMh+k8AMZLnqu/fv/8HWQoCIGM4OTkZWWCuArkE5hC4DpirMCRA2mECMAUAAYTTVXgdBQL37t37z8fHB3YHCMMALCBEREQYUWx4+/btf25ubrgGWDjDNLx8+ZJBXl6eEW4USAEIwzTcuXMHRTPMVhZ0q2FsRUVFhj9//sBtAbExPA3yB7JtIDmYvJKSEtgPAAFIJ4MVgGEQho6d/P/f9OTFa1eFjMyuMFlBevHRRNP2lM6jedrAbVpVB0+FI2Bmqwd3H2hmIEpEsvfxAhojhgxEnl4lAeAlhRzA/01Db8163dMC5E8sprcANH8CIr5svgJzD3lfAjBXBikAg0AMvPTk1a/4/wf4HT9QtxCJMR5aEFpYsLXrSDarr7103Eq/A1z6ITyeUpqMBa31gbzQPvJ6j48+sAA41QEYwgXlMR+AViL8oK2BqLVO77oRBVxON94pLxCRc17kYifqnAVEAntb5zR2MAvQO4uBEf3+GBeqqwHytwBtaXVRKWWcaZ8ArbXHbkjSejgpMO4WX+t5+qi4BWC/CnIABGFY4id4AZ/g+5z4lXCYmaPd5kFPHkhMxHWVrSu/Fv0AL8j1slTipO2KJGP1kJbqDWAFL6VAiWYCh/TIZWCDo+zlPdIrF4AFtyxYxikAb7HsPYDD22ABxhghcLpMERiazSxwug9YluzwHwPoMpz3uHR5hgBo1gpANIdpFUUf1lo3BmiyUQao/bWp771fz2xfigGj3Fq7DfXHALrlIxlAh50CYCCe8fVYb1U0L4QwQ/3fkfESyxO6Crlio8aKzsvOgk9syylAe1aQwkAIAy9+wYsX//8hH+ILShaEEEwmRlu2oLC0dGU3E20yM379BX/f8y+AC+DlI3km8dKkMUpZrpDrYBkF0lkMV6EROPHsnPNPM0tadRR+CwhcAQqeBPPoQFqWPTTPoisy+5QwekbvPb6FNNpuAbBoJKL9mme19R9AwSPmbpES7qaP73y+h1enaPCeVRl7mI4xZiA0GoekjLuMoupiKUVa+tbac83sS00Xz0zC433AeqH8jTuTnoBvJ/YMTYnJUkjZR+oBqbqtTiy7I68USPpoXXYVEAKVPJnWyhwqlbXW5560l7UgEaCwNYE+NR1OJ1900mhpeRT89grIRuMFTPNLKaZytrYVT842G5WBrQA5UQyOAIgCWQEZeR4U9ZxSrxiRu4F7qPR1Jd4wPrJM6z/MaYh0AAAAAElFTkSuQmCC" />' +
										'&nbsp;|&nbsp;'+ 
									'</div>'+
									'<div class="button_set">' +
										'<button type="submit" class="minimal" disabled></button><button type="submit" class="assertions"></button><button type="submit" class="detailed"></button>' +
									'</div>' +
                            '</div>' +
                            '<div style="display:block" class="details"></div>' +
                        '</div>' +
                    '</td></tr>';
        },
        initSelectors: function() {
            this.element = $('tr#_'+this.id);
            this.details = this.element.find('div.details');
            this.buttonSet = this.element.find('div.button_set');
            this.minimalButton = this.element.find('button.minimal');
            this.assertionsButton = this.element.find('button.assertions');
            this.detailedButton = this.element.find('button.detailed');
        },
        getButton: function(name) {
            var me = this;
            switch(name) {
                case 'minimal' :
                    return me.minimalButton;
                case 'assertions' :
                    return me.assertionsButton;
                case 'detailed' :
                    return me.detailedButton;
                default :
                    return null;
            }
        },
        addActionRow: function(screenshotMarkUp, actionDetailsMarkUp) {
            var actionRow = this.detailsTable.find('tbody').append('<tr>').find('tr:last');
            if (screenshotMarkUp) actionRow.append('<td class="left">' + screenshotMarkUp + '</td>'); else actionRow.append('<td class="left">');
            if (actionDetailsMarkUp) actionRow.append('<td class="right">' + actionDetailsMarkUp + '</td>'); else actionRow.append('<td class="right">');
        },
        initDetailsTable : function() {
            this.detailsTable = this.details.html("<table><tbody></tbody></table>");
        },
        createFailedAssertionMarkup: function(action) {
            var stats = '<span class="failure_stats">[Expected: "' + action.expected + '", Actual: "' + action.actual + '"]</span>';
            return  '<span class="failed"><b>' + action.assertion + ' '+ stats + '</b></span>';
        },
		createExceptionMarkup: function(action) {
            return  '<span class="failed"><b>Unexpected exception: ' + action.exception + '</b></span>';
        },
        createSreencapMarkUp: function(thumbUrl, fullUrl) {
			if (fullUrl != null)
				return '<a href="'+fullUrl+'" data-lightbox="image-1" title="' + thumbUrl + '"><img width=50 src="' + thumbUrl + '"</a>';
        },
        createExpandableDetails: function(details) {
            var splitDetails = details.split("<br/>");

            if (splitDetails.length > 6) {
                var initial = '<div class="initial">', remaining = '<div style="display:none" class="remaining">';

                initial += splitDetails.slice(0,6).join('<br/>') + '<br/><a class="see_more" href="">...</a></div>';
                remaining += splitDetails.slice(6,splitDetails.length).join('<br/>') + '<br/><a class="see_less" href="">show less</a></div>';

                return initial + remaining;
            } else {
				splitDetails = details.replace("<","&lt;").replace(">","&gt;").split("\r\n");
				if (splitDetails.length > 6) {
                var initial = '<div class="initial">', remaining = '<div style="display:none" class="remaining">';

                initial += splitDetails.slice(0,6).join('<br/>') + '<br/><a class="see_more" href="">...</a></div>';
                remaining += splitDetails.slice(6,splitDetails.length).join('<br/>') + '<br/><a class="see_less" href="">show less</a></div>';

                return initial + remaining;
				} else {
					return details;
				}
            }

        },
        addEventsToAssertionDetailsTabs: function(index) {
            $('#assertion_'+index).find("div.initial a.see_more").on("click", function() {
                var detailsDiv =  $(this).parent().parent();
                detailsDiv.find('div.initial a.see_more').hide();
                detailsDiv.find('div.remaining').show('slideDown');
                return false;
            });
            $('#assertion_'+index).find("div.remaining a.see_less").on("click", function() {
                var detailsDiv =  $(this).parent().parent();
                detailsDiv.parent().parent().find('div.remaining').hide('slideUp');
                detailsDiv.parent().parent().find('div.initial a.see_more').show();
                return false;
            });
        },
        createAssertionDetailsTabMarkup: function(action, index, isFull) {
            var me = this,
                tabHeaders = '<ul>',
                tabDetails = '',
                isFirst = true;

			_.each(action.error, function(details, tabHeader) {
				if (isFull) {
					tabHeaders += "<li><a href='#" + tabHeader +"_"+ index +"'>" + (!isFirst?'| ' : '') + tabHeader + "</a></li>";
					tabDetails += "<div id='" + tabHeader +"_"+ index +"'>" + me.createExpandableDetails(details) + "</div>";
					isFirst = false;
				} else {
					if (isFirst) {
						tabHeaders += "<li><a href='#" + tabHeader +"_"+ index +"'>" + tabHeader + "</a></li>";
						tabDetails += "<div id='" + tabHeader +"_"+ index +"'>" + me.createExpandableDetails(details) + "</div>";
						isFirst = false;
					}
				}
			});
			
            tabHeaders += '</ul>';

            return '<div id="assertion_' + index +'">'+
                        tabHeaders +
                        tabDetails +
                   '</div>';
        },
        renderDetails: function(executionData, isFull) {
            var me = this;

            me.actions = executionData.actions;
            me.execution_log = executionData.execution_log;
            me.initDetailsTable();

            _.each(me.actions, function(action, index) {
                if (action.assertion && ( action.passed  == 'true')) {
                    me.addActionRow( null, '<span class="passed">' + action.assertion + (isFull ? ' [ Expected: '+ action.expected + ' Actual: '+ action.actual +' ]' : '') + '</span>');
                } else if (action.assertion && (action.passed == 'false')) {
                    me.addActionRow( me.createSreencapMarkUp(action.screencap_thumb, action.screencap_full), me.createFailedAssertionMarkup(action) + me.createAssertionDetailsTabMarkup(action, index, isFull));
                    if (isFull) me.addEventsToAssertionDetailsTabs(index);
                    $('div#assertion_'+index).tabs();
                } else if (action.exception) {
                    me.addActionRow( me.createSreencapMarkUp(action.screencap_thumb, action.screencap_full), me.createExceptionMarkup(action) + me.createAssertionDetailsTabMarkup(action, index, isFull));
                    if (isFull) me.addEventsToAssertionDetailsTabs(index);
                    $('div#assertion_'+index).tabs();
                } else if (isFull && action.step) {
                    me.addActionRow( null, '<span class="step">' + action.step + '</span>');
                }
            });
            if (me.execution_log)
                me.addActionRow( null, '<span class="step"><a target="_blank" href="execution_log/'+ me.execution_log +'.log" >View Execution Log</a></span>');
        }
    };

    var TestExecutionResultsDomProxy = function(presenter) {
        this.stats = {};
        this.executionData = {};
        this.dataTable = null;
        this.tableRows = {};
        this.presenter = presenter;
        this.failedTestsFilter = null;
    };

    TestExecutionResultsDomProxy.prototype = {
        initSelectors: function() {
            this.stats.total_passed = $('span.tests_passed > span.count');
            this.stats.total_failed = $('span.tests_failed > span.count');
			this.stats.total_ignored = $('span.tests_ignored > span.count');
            this.stats.duration = $('span.duration > span.count');
            this.testDetails = $('div.test_details');
            this.failedTestsFilter = null;
        },
        render: function(data) {
            var deferred = $.Deferred(),
            me = this;

            me.stats.total_passed.html(data.total_passed);
            me.stats.total_failed.html(data.total_failed);
			me.stats.total_ignored.html(data.total_ignored);
            me.stats.duration.html(data.duration);

            _.each(data.details, function(content, title) {
                //details at the top of the page with execution summary
                me.testDetails.append('<h2>' + title + '</h2><div>' + content + '</div>');
            });

            _.each(data.execution_data, function(row) {
                //transform to map
                me.executionData[row.id] = row;
            });

            me.createTable(me.executionData);
            me.addButtonActions(me.tableRows);

            deferred.resolve(me.executionData);
            return deferred.promise();
        },
        createTable : function(executionData) {
            var dataTable = $('table.results_datatable'), me  = this;

            _.each(executionData, function(rowData, id) {
                var rowView = new TestExecutionRowView(rowData);
                me.tableRows[id] = rowView;
                dataTable.append(
                    rowView.getMarkUp()
               );
            });

            var dataTableSettings = {
                'bSort': false,
                'bPaginate': false,
                "bInfo": false,
                'aLengthMenu': [15, 25, 50, 100],
                'bFilter': true,
                'bJQueryUI': true,
                'oLanguage': {
                    'sSearch': 'Filter tests: ',
                    'sEmptyTable': 'No data available'
                },
                'aoColumns' : [{
                    'fnRender' : function(offset, value) {
                        return value;
                    }
                }]
            };

            me.dataTable = dataTable.dataTable(dataTableSettings);

            var dataTablesFilter = $('div.dataTables_filter');
            dataTablesFilter.append('<label><input type="checkbox" id="show_failed_only">Show failed tests only</label></div>');
            me.failedTestsFilter = dataTablesFilter.find('input[id=show_failed_only]');
            dataTablesFilter.append('<label><input type="checkbox" id="show_ignored_only">Show ignored tests only</label></div>');
            me.ignoredTestsFilter = dataTablesFilter.find('input[id=show_ignored_only]');

        },
        addButtonActions: function(tableRows) {
            var me = this;
            _.each(tableRows, function(tableRow, id) {
                tableRow.initSelectors();
                tableRow.minimalButton.on('click', function() {
                    if (!tableRow.element.hasClass('as_minimal')) {
                        me.setRowAndButtonClass(id, 'minimal');
                        me.resetRow(id);
                    }
                });

                tableRow.assertionsButton.on('click', function() {
                    if (!tableRow.element.hasClass('as_assertions')) {
                        me.setRowAndButtonClass(id, 'assertions');
                        me.resetRow(id);
                        me.loadData(id).done(function() {
                            me.tableRows[id].renderDetails(me.executionData[id], false);
                        });
                    }
                });

                tableRow.detailedButton.on('click', function() {
                    if (!tableRow.element.hasClass('as_detailed')) {
                        me.setRowAndButtonClass(id, 'detailed');
                        me.resetRow(id);
                        me.loadData(id).done(function() {
                            me.tableRows[id].renderDetails(me.executionData[id], true);
                        });
                    }
                });
            });

            $.fn.dataTableExt.afnFiltering.push(
                function( oSettings, aData, iDataIndex) {
					if (me.failedTestsFilter.prop('checked')) {
					    return ( $(aData[0]).find('div.failed').length > 0) ;
					}
                    if (me.ignoredTestsFilter.prop('checked')){
                        return ( $(aData[0]).find('div.ignored').length > 0) ;
                    }

					return true;
                }
           );

            $.fn.dataTableExt.afnSortData['dom-text'] = function(oSettings, iColumn)
            {
                var aData = [];
                $( 'td:eq('+iColumn+') input', oSettings.oApi._fnGetTrNodes(oSettings)).each(function() {
                    aData.push( this.value);
                });
                return aData;
            };

            me.failedTestsFilter.on('click', function() {
                me.ignoredTestsFilter.prop('checked',false);
                me.dataTable.fnDraw();
            });
            me.ignoredTestsFilter.on('click', function() {
                me.failedTestsFilter.prop('checked',false);
                me.dataTable.fnDraw();
            });
        },
        loadData: function(id) {
            var me = this,
                deferred = $.Deferred();

            me.presenter.loadData(id).done(function(data) {
                me.executionData[id] = data;
                deferred.resolve();
            }).fail(function(error) {
                alert('Failed to load execution data for id:' + id);
                deferred.reject(error);
            });

            return deferred.promise();
        },
        loadDetails: function(id, isFull) {
            var me = this;
            var data = me.executionData[id];
        },
        resetRow: function(id) {
            this.tableRows[id].details.html('');
        },
        setRowAndButtonClass: function(id, newClass) {
            var me = this, classes = ['minimal', 'assertions', 'detailed'];

            _.each(classes, function(rowClass) {
                var tableRow = me.tableRows[id];
                if (newClass != rowClass) {
                    tableRow.element.removeClass('as_'+rowClass);
                    tableRow.getButton(rowClass).removeAttr('disabled');
                } else {
                    tableRow.element.addClass('as_'+newClass);
                    tableRow.getButton(newClass).attr('disabled', true);
                }
            });
        },
        showErrorDialog: function(error) {
            console.log(error);
            if (error.status == 404 && typeof window.chrome === "object")
                $("#error_message_dialog").dialog({width:'400px'});
        }
    };

    var TestExecutionResultsPresenter = function(dataFacade) {
        this.domProxy = null;
        this.dataFacade = dataFacade;
        this.testResultsMap = {};
    };

    TestExecutionResultsPresenter.prototype = {
        renderPage: function() {
            var me = this;
            me.getTestExecutionResultsSummary().done(function(summary) {
                me.domProxy.render(summary).done(function() {
                });
            }).fail(function(error) {
                    me.domProxy.showErrorDialog(error);
                });
        },
        getTestExecutionResultsSummary: function() {
            var deferred = $.Deferred();

            this.dataFacade.getExecutionSummary().done(function(summaryData) {
                deferred.resolve(summaryData);
            }).fail(function(error){
                    deferred.reject(error);
                });

            return deferred.promise();
        },
        loadData: function(id) {
            var deferred = $.Deferred();

            this.dataFacade.getExecutionData(id).done(function(executionData) {
                deferred.resolve(executionData);
            }).fail(function(error){
                    deferred.reject(error);
                });

            return deferred.promise();
        }
    };

    var TestExecutionDataFacade = function() {
    };

    TestExecutionDataFacade.prototype = {
        getExecutionSummary: function() {
            var deferred = $.Deferred();

            $.getJSON('data/test_results_summary.json')
            .done(function(data) {
                deferred.resolve(data);
            }).fail(function(error){
                    deferred.reject(error);
                });

            return deferred.promise();
        },
        getExecutionData: function(id) {
            var deferred = $.Deferred();

            $.getJSON('data/result_id_' + id + '.json')
            .done(function(data) {
                deferred.resolve(data);
            }).fail(function(error){
                    deferred.reject(error);
                });

            return deferred.promise();
        }
    };

    var domProxy = null, presenter = null, dataFacade = null;

    return {
        init: function(options) {

            dataFacade = new TestExecutionDataFacade();
            presenter = new TestExecutionResultsPresenter(dataFacade);
            domProxy = new TestExecutionResultsDomProxy(presenter);

            presenter.domProxy = domProxy;

            domProxy.initSelectors();

            return this;
        },
        start: function() {
            if (presenter) presenter.renderPage();
        }
    };
})();
