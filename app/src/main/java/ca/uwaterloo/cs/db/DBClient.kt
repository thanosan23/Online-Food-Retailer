package ca.uwaterloo.cs.db

import android.content.Context
import android.net.Uri
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.bemodels.HasOneImage
import ca.uwaterloo.cs.dbmodels.Address
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*


class DBClient {
    val db = Firebase.database.reference
    private val storage = FirebaseStorage.getInstance().reference
    var context: Context? = null

    fun clearStorage(){
        val sas = storage.bucket.filterNot { false }
        println("sas")
    }

    inline fun <reified T> store(key: String, data: T){
        if (data is HasOneImage){
                storeImage(data.image.toUri())
                    println("attempt to store JPEG image, can't do that")
        }
        val stringData = Json.encodeToString(data)
        db.child(key).setValue(stringData).addOnFailureListener{
            println("failure storing data $it")
        }
    }

    inline fun <reified T> get(key: String, listener: Listener<T>){
        db.child(key).get().addOnSuccessListener {
            if (it.exists()){
                val stringData = it.value as String
                val data = Json.decodeFromString<T>(stringData)
                if (data is HasOneImage){
                        val result = getImage(data.image)
                        if (result == null){
                            data.image = ""
                        }
                        else{
                            data.image = result.toString()
                        }
                }
                listener.activate(data!!)
            }
            else{
                println("attempt to get something that does not exist, key: $key")
                assert(true)
            }
        }
            .addOnFailureListener{
                println("failure to get $it")
            }
    }

    fun getIfExists(key: String, listener: Listener<String?>){
        db.child(key).get()
            .addOnSuccessListener {
            if (it.exists()){
                val stringData = it.value as String
                listener.activate(stringData)
            }
            else{
                listener.activate(null)
            }
        }
            .addOnFailureListener{
                listener.activate(null)
            }
    }

    fun storeImage(file: Uri){
        println(file.toString())
        if (file.toString() == ""){
            return
        }
        val ref: StorageReference = storage.child(file.toString())
        try {
            ref.putFile(file).addOnFailureListener {
                println("failure to store image $it")
            }
        }
        catch (e: SecurityException){
            println("fail to store image")
        }
    }

    fun getImage(imageName: String): Uri?{
        if (imageName == ""){
            return null
        }
        val ref: StorageReference = storage.child(imageName)
        val timeStamp = SimpleDateFormat.getDateTimeInstance().format(Date())
        val localFile = File(context!!.filesDir, "JPEG_$timeStamp.jpg")
        ref.getFile(localFile).addOnFailureListener{
            println("failure to get image $it, $imageName")
        }
        Thread.sleep(1000)
        return FileProvider.getUriForFile(
            context!!,
            context!!.applicationContext.packageName + ".provider",
            localFile
        )
    }
}


class DBClientTest(){
    @Composable
    fun test(){
        println("what is going on?")
        val textValue = remember{
            mutableStateOf("puta que pariu")
        }
        Text(textValue.value)

        class ListenerImpl() : Listener<Address>() {
            override fun activate(input: Address) {
                textValue.value = input.city
            }
        }
        val listener = ListenerImpl()
        val databaseManager = DBClient()
        val address = Address("Caruaru", "Brazil", "N2L", "Rua Doutor Pedro Jordao")
        databaseManager.store("2", address)
        databaseManager.get("2", listener)
    }

}