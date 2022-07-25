package ca.uwaterloo.cs.platform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ca.uwaterloo.cs.product.ProductInformation
import ca.uwaterloo.cs.R
import ca.uwaterloo.cs.form.*

class PlatformsUI(data: ProductInformation) {
    var platform1CheckBoxState: Boolean by mutableStateOf(data.platform1)
    var platform2CheckBoxState: Boolean by mutableStateOf(data.platform2)
    var platform1AmountState: Long by mutableStateOf(data.platform1_amount)
    var platform2AmountState: Long by mutableStateOf(data.platform2_amount)
    var platform1PriceState: Int by mutableStateOf(data.platform1_price)
    var platform2PriceState: Int by mutableStateOf(data.platform2_price)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlatformsDropDown() {
        val formState1 by remember { mutableStateOf(FormState()) }
        val formState2 by remember { mutableStateOf(FormState()) }

        Column(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.socleo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .border(BorderStroke(1.dp, Color.Black))
                )
                Checkbox(
                    checked = platform1CheckBoxState,
                    onCheckedChange = { platform1CheckBoxState = it }
                )
                Form(
                    state = formState1,
                    fields = listOf(
                        Field(
                            name = "platformamount1",
                            initValue = formState1.getData()
                                .getOrDefault("platformamount1", platform1AmountState)
                                .toString(),
                            prompt = "amount",
                            label = "amount",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform1AmountState = try {
                                    value.toLong()
                                } catch (e: Exception) {
                                    0
                                }
                            }

                        ),
                        Field(
                            name = "platformprice1",
                            initValue = formState1.getData()
                                .getOrDefault("platformprice1", if (platform1PriceState == 0) "0.00" else (platform1PriceState / 100.0))
                                .toString(),
                            prompt = "price",
                            label = "price",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform1PriceState = try {
                                    (value.toDouble() * 100).toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            }
                        )
                    )
                )

                //Text(text = "what")
                //TextField(value = "platform1AmountState", onValueChange ={platform1AmountState} )
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    TextField(value = platform1AmountState.toString(), onValueChange ={if(it!=""){platform1AmountState=it.toLong()}} )
//                    TextField(value = "platform1AmountState", onValueChange ={it} )
//                }

            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(5.dp))

                Image(
                    painter = painterResource(id = R.drawable.loblaws),
                    contentDescription = null,
                    modifier = Modifier
                        .width(130.dp)
                        .height(120.dp)
                        .border(BorderStroke(5.dp, Color.White))
                )
                Checkbox(
                    checked = platform2CheckBoxState,
                    onCheckedChange = { platform2CheckBoxState = it }
                )
                Form(
                    state = formState2,
                    fields = listOf(
                        Field(
                            name = "platformamount2",
                            initValue = formState2.getData()
                                .getOrDefault("platformamount2", platform2AmountState)
                                .toString(),
                            prompt = "amount",
                            label = "amount",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform2AmountState = try {
                                    value.toLong()
                                } catch (e: Exception) {
                                    0
                                }
                            }
                        ),
                        Field(
                            name = "platformprice2",
                            initValue = formState2.getData()
                                .getOrDefault("platformprice2", if (platform2PriceState == 0) "0.00" else (platform2PriceState / 100.0))
                                .toString(),
                            prompt = "price",
                            label = "price",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform2PriceState = try {
                                    (value.toDouble() * 100).toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            }
                        )
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlatformsDropDown1() {
        val formState1 by remember { mutableStateOf(FormState()) }
        val formState2 by remember { mutableStateOf(FormState()) }

        Column(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.socleo),
                    contentDescription = null,
                    modifier = Modifier
                        .width(120.dp)
                        .height(100.dp)
                        .border(BorderStroke(1.dp, Color.Black))
                )
                Checkbox(
                    checked = platform1CheckBoxState,
                    onCheckedChange = { platform1CheckBoxState = it }
                )
                Form(
                    state = formState1,
                    fields = listOf(
                        Field(
                            name = "platformamount1",
                            initValue = formState1.getData()
                                .getOrDefault("platformamount1", platform1AmountState)
                                .toString(),
                            prompt = "amount",
                            label = "amount",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform1AmountState = try {
                                    value.toLong()
                                } catch (e: Exception) {
                                    0
                                }
                            },
                            readOnly = true

                        ),
                        Field(
                            name = "platformprice1",
                            initValue = formState1.getData()
                                .getOrDefault("platformprice1", if (platform1PriceState == 0) "0.00" else (platform1PriceState / 100.0))
                                .toString(),
                            prompt = "price",
                            label = "price",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform1PriceState = try {
                                    (value.toDouble() * 100).toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            },
                            readOnly = true
                        )
                    )
                )

                //Text(text = "what")
                //TextField(value = "platform1AmountState", onValueChange ={platform1AmountState} )
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    TextField(value = platform1AmountState.toString(), onValueChange ={if(it!=""){platform1AmountState=it.toLong()}} )
//                    TextField(value = "platform1AmountState", onValueChange ={it} )
//                }

            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(5.dp))

                Image(
                    painter = painterResource(id = R.drawable.loblaws),
                    contentDescription = null,
                    modifier = Modifier
                        .width(130.dp)
                        .height(120.dp)
                        .border(BorderStroke(5.dp, Color.White))
                )
                Checkbox(
                    checked = platform2CheckBoxState,
                    onCheckedChange = { platform2CheckBoxState = it }
                )
                Form(
                    state = formState2,
                    fields = listOf(
                        Field(
                            name = "platformamount2",
                            initValue = formState2.getData()
                                .getOrDefault("platformamount2", platform2AmountState)
                                .toString(),
                            prompt = "amount",
                            label = "amount",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform2AmountState = try {
                                    value.toLong()
                                } catch (e: Exception) {
                                    0
                                }
                            },
                            readOnly = true
                        ),
                        Field(
                            name = "platformprice2",
                            initValue = formState2.getData()
                                .getOrDefault("platformprice2", if (platform2PriceState == 0) "0.00" else (platform2PriceState / 100.0))
                                .toString(),
                            prompt = "price",
                            label = "price",
                            validators = listOf(Required(), IsNumber()),
                            inputType = KeyboardType.Number,
                            formatter = NumberTransformation(),
                            onChange = { value ->
                                platform2PriceState = try {
                                    (value.toDouble() * 100).toInt()
                                } catch (e: Exception) {
                                    0
                                }
                            },
                            readOnly = true
                        )
                    )
                )
            }
        }
    }
}


