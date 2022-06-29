package ca.uwaterloo.cs.platform

import ca.uwaterloo.cs.ProductInformation


class PlatformState(data: ProductInformation) {
    val platformsUI = PlatformsUI(data)

    fun validate(): Boolean {
        return true
    }

    fun getData(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["platform1"] = platformsUI.platform1CheckBoxState.toString()
        map["platform2"] = platformsUI.platform2CheckBoxState.toString()
        println("what is going on")
        println(map)
        return map
    }
}