package ca.uwaterloo.cs.db

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ca.uwaterloo.cs.Listener
import ca.uwaterloo.cs.models.Address
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DBClient {
    val mDatabase = Firebase.database.reference

    inline fun <reified T> store(key: String, data: T){
        val stringData = Json.encodeToString(data)
        mDatabase.child(key).setValue(stringData)
    }

    inline fun <reified T> get(key: String, listener: Listener<T>){
        mDatabase.child(key).get().addOnSuccessListener {
            if (it.exists()){
                val stringData = it.value as String
                val data = Json.decodeFromString<T>(stringData)
                listener.activate(data!!)
            }
        }
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
        val address = Address("Caruaru")
        databaseManager.store("2", address)
        databaseManager.get("2", listener)
    }
}