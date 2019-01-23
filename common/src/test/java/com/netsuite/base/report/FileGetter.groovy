package com.netsuite.base.report

class FileGetter {

    File file
    String text
    String name

    FileGetter(String fileName) {
        this.name = fileName
        file = new File(name)
    }

    static class Builder {
        def build(String fileName) {
            return new FileGetter(fileName)
        }
    }

    def isFileExist() {
        return file.exists()
    }



    def append(String content) {
        file.append(content)
        return this
    }

    def write(String content) {
        file.write(content)
        return this
    }


    def toText() {
        this.text = this.file.getText("UTF-8")
        return this
    }


}
