package ca.uwaterloo.cs.pushpull

import android.content.Context
import ca.uwaterloo.cs.db.DBManager

class PushWorker(val context: Context) {
    private val dbManager = DBManager(context)
    fun run(){
        pushHarvestData(context, dbManager)
    }
}