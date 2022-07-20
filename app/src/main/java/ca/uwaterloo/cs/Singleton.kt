package ca.uwaterloo.cs

object Singleton
{
    init
    {
        println("Singleton class invoked.")
    }

    var userId = ""
    var isFarmer = true
    var isNewUser = false
    var readFromDB = 0
    var jobScheduled = false
    var forTesting = true
}