package ca.uwaterloo.cs.platform

import ca.uwaterloo.cs.product.ProductInformation


class PlatformState(data: ProductInformation) {
    val platformsUI = PlatformsUI(data)

    fun validate(): Boolean {
        return true
    }

    fun getData(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["platform1"] = platformsUI.platform1CheckBoxState.toString()
        map["platform2"] = platformsUI.platform2CheckBoxState.toString()
        map["platform1_amount"]= platformsUI.platform1AmountState.toString()
        map["platform2_amount"]= platformsUI.platform2AmountState.toString()
        map["platform1_price"]= platformsUI.platform1PriceState.toString()
        map["platform2_price"]= platformsUI.platform2PriceState.toString()
        return map
    }
}
