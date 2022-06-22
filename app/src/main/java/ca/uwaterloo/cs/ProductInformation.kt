package ca.uwaterloo.cs

import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private val outDir = "${System.getProperty("user.dir")}/test/out"

private fun getNextID(): Int {
    return (File(outDir).list()?.size ?: 0) + 1
}

data class ProductInformation(
    val id: Int = getNextID(), // Internal id number of product, should we store this?
    var name: String = "",
    var description: String = "",
    var price: Int = 0,
    var amount: Int = 0,
    val images: ArrayList<String> = arrayListOf()
) : Serializable {
    fun exportData() {
        // TODO: platform compatibility
        // TODO: save to platform
        val file = File("$outDir/${id}.txt")
        if (file.exists()) {
            file.delete()
        }
        println(file.absolutePath)
        file.createNewFile()
        val fileOS = FileOutputStream(file)
        val outStream = ObjectOutputStream(fileOS)
        outStream.writeObject(this)
        outStream.close()
        fileOS.close()
    }
}
// TODO: FIX images to image array
// TODO: load data
