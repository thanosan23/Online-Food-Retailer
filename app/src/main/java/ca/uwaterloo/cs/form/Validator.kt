package ca.uwaterloo.cs.form

private const val REQUIRED_MESSAGE = "this field is required"
private const val NON_ZERO_MESSAGE = "value cannot be 0"

sealed interface Validator
open class Required(var message: String = REQUIRED_MESSAGE): Validator
open class NonZero(var message: String = NON_ZERO_MESSAGE): Validator