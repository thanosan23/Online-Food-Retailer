package ca.uwaterloo.cs.pushpull

import android.content.Context
import ca.uwaterloo.cs.Singleton
import ca.uwaterloo.cs.db.DBManager

class PullWorker(val context: Context) {
    val dbManager = DBManager(context)
    fun run(){
        pullHarvestDataFromDB(context, dbManager, Singleton.isFarmer, Singleton.userId)
        pullProductDataFromDB(context, dbManager, Singleton.isFarmer, Singleton.userId)
    }
}