package com.netsuite.chinalocalization.voucher.model.p2

class TestData {

    def tData
    def TestData(def tData){
        this.tData = tData
    }


    def labels() {
        return tData.labels
    }

    def data() {
        return tData.data
    }

    def expect() {
        return tData.expect
    }

    def record() {
        return tData.record
    }
}
