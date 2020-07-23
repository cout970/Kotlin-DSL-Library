package kdl.api.util

/**
 * Represents something that will be executed later
 *
 * Allows to configure a DSL multiple times
 */
class Deferred<T : Function<*>> {
    val pending = mutableListOf<T>()

    // Enqueues a function to be executed later
    fun onExecution(func: T) {
        pending += func
    }

    // Removes all pending functions
    fun reset() {
        pending.clear()
    }

    // Execute very pending function
    inline fun execute(func: (T) -> Unit) {
        pending.forEach(func)
    }
}