package ca.uwaterloo.cs

abstract class Listener<T> {
    abstract fun activate(input: T)
}