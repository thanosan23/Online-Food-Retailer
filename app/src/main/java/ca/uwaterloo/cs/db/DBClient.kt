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
    private val storage = FirebaseStorage.getInstance("gs://cs446-project-2.appspot.com").reference
    var context: Context? = null

    inline fun <reified T> store(key: String, data: T){
        if (data is HasOneImage){
            if (data.image != "") {
                storeImage(data.image.toUri())
            }
        }
        val stringData = Json.encodeToString(data)
        db.child(key).setValue(stringData)
    }

    inline fun <reified T> get(key: String, listener: Listener<T>){
        db.child(key).get().addOnSuccessListener {
            if (it.exists()){
                val stringData = it.value as String
                val data = Json.decodeFromString<T>(stringData)
                if (data is HasOneImage){
                    data.image = getImage().toString()
                }
                listener.activate(data!!)
            }
        }
    }

    fun storeImage(file: Uri){
        val ref: StorageReference = storage.child("messi image")
        ref.putFile(file)
    }

    fun getImage(): Uri{
        val ref: StorageReference = storage.child("messi lost.jpg")
        val timeStamp = SimpleDateFormat.getDateTimeInstance().format(Date())
        val localFile = File(context!!.filesDir, "JPEG_$timeStamp.jpg")
        ref.getFile(localFile).addOnFailureListener{
            println("failture to get image $it")
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