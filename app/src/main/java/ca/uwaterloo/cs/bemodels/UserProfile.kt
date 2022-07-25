package ca.uwaterloo.cs.bemodels

import android.content.Context
import ca.uwaterloo.cs.dbmodels.Address
import ca.uwaterloo.cs.product.ProductInformation
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@kotlinx.serialization.Serializable
data class UserProfile (
    val firstName: String,
    val familyName: String,
    val password: String,
    val email: String,
    val address: Address?,
    val imageURI: String
        ) {

    fun exportData(context: Context) {
        // saving in the database
        val dir = File("${context.filesDir}/outUserProfile")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "UserProfile.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        val stringData = Json.encodeToString(this)
        file.writeText(stringData)
    }

    fun readUserProfileFromFiles(context: Context): UserProfile? {
        // TODO: platform compatibility
        // TODO: load from platform
        val dir = File("${context.filesDir}/outUserProfile")
        if (!dir.exists()) {
            return null
        }
        for (saveFile in dir.walk()) {
            if (saveFile.isFile && saveFile.canRead() && saveFile.name.contains("UserProfile") && saveFile.exists()) {
                try {
                    return Json.decodeFromString<UserProfile>(saveFile.readText())
                } catch (e: Throwable) {
                    println("error deserializing the product")
                }
                break
            }
        }
        return null
    }
}