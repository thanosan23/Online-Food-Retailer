package ca.uwaterloo.cs.form

private const val REQUIRED_MESSAGE = "this field is required"
private const val NON_ZERO_MESSAGE = "value cannot be 0"
private const val REQUIRED_NUMBER_MESSAGE = "value must be a valid number"

sealed interface Validator
open class Required(var message: String = REQUIRED_MESSAGE): Validator
open class NonZero(var message: String = NON_ZERO_MESSAGE): Validator
open class IsNumber(var message: String = REQUIRED_NUMBER_MESSAGE): Validator
