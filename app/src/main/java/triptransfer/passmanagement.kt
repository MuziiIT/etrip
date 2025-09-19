package com.example.etrip1.triptransfer

import triptransfer.TravelPass





class PassManager {
    private val passes = mutableListOf(
        TravelPass("Daily Pass", 3),
        TravelPass("Weekly Pass", 15),
        TravelPass("Monthly Pass", 60)
    )

    fun getAvailablePasses(): List<TravelPass> = passes

    fun transferPass(recipientId: String, pass: TravelPass): Boolean {
        return passes.remove(pass) // remove to simulate transfer
    }

    fun purchasePass(pass: TravelPass) {
        passes.add(pass)
    }
}


