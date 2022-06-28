package ca.uwaterloo.cs.platform


class PlatformState {
    val platformsUI = PlatformsUI()

    fun validate(): Boolean {
        return true
    }

    fun getData(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["platform1"] = platformsUI.platform1CheckBoxState.toString()
        map["platform2"] = platformsUI.platform2CheckBoxState.toString()
        return map
    }
}