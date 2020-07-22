package kdl.api.util

/**
 * A Promise represents an async computation
 *
 * The code passed to the promise is executed immediately and
 * it must call either fulfill or reject.
 *
 * It's not necessary to call fulfill or reject immediately,
 * you can delegate the call to a process that may complete later.
 *
 * You can use then() to chain operations and catch() to handle errors.
 *
 * Example:
 * ```
 * val promise = Promise { fulfill, reject ->
 *     // This is executed immediately
 *     print("1,")
 *
 *     setTimeout({
 *          // This is executed after 5 seconds
 *          print("2,")
 *
 *          fulfill("ok")
 *     }, 5000)
 * }
 *
 * promise.then { result ->
 *      // This is executed after 5 seconds, result is the string "ok"
 *      print("3,")
 *
 *      "next"
 * }.then { result ->
 *      // This is executed after 5 seconds, result is the string "next"
 *      print("4,")
 * }
 * ```
 * The example prints: 1,2,3,4
 */
class Promise<T>(
    executor: (fulfill: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit
) {
    private var state: PromiseState = PromiseState.PENDING
    private var value: T? = null
    private var error: Throwable? = null
    private var callbacks = mutableListOf<() -> Unit>()

    init {
        try {
            executor(this::fulfillPromise, this::rejectPromise)
        } catch (e: Throwable) {
            rejectPromise(e)
        }
    }

    /**
     * Run code after the promise is resolved
     */
    fun <S> then(onFulfilled: (T) -> S): Promise<S> {
        return this.then(onFulfilled, { throw  it })
    }

    /**
     * Run code after the promise is resolved or rejected
     */
    fun <S> then(onFulfilled: (T) -> S, onRejected: (Throwable) -> S): Promise<S> {
        return Promise { fulfillCallback, rejectCallback ->
            afterResolve {
                when (state) {
                    PromiseState.FULFILLED -> {
                        fulfillCallback(onFulfilled(this.value!!))
                    }
                    PromiseState.REJECTED -> {
                        try {
                            fulfillCallback(onRejected(this.error!!))
                        } catch (e: Throwable) {
                            rejectCallback(e)
                        }
                    }
                    else -> error("Promise not resolved")
                }
            }
        }
    }

    /**
     * Run code after the promise is rejected
     */
    fun catch(onRejected: (Throwable) -> T): Promise<T> {
        return Promise { fulfillCallback, rejectCallback ->
            afterResolve {
                when (state) {
                    PromiseState.FULFILLED -> {
                        fulfillCallback(this.value!!)
                    }
                    PromiseState.REJECTED -> {
                        try {
                            fulfillCallback(onRejected(this.error!!))
                        } catch (e: Throwable) {
                            rejectCallback(e)
                        }
                    }
                    else -> error("Promise not resolved")
                }
            }
        }
    }

    private fun afterResolve(op: () -> Unit) {
        if (state == PromiseState.PENDING) {
            callbacks.add(op)
        } else {
            op()
        }
    }

    private fun fulfillPromise(value: T) {
        this.state = PromiseState.FULFILLED
        this.value = value
        callbacks.forEach(Function0<Unit>::invoke)
    }

    private fun rejectPromise(error: Throwable) {
        this.state = PromiseState.REJECTED
        this.error = error
        callbacks.forEach(Function0<Unit>::invoke)
    }

    companion object {
        /**
         * Run all promises and return one promise with the results
         * If one promise fails, the returned promise will fail with the same error
         */
        fun <S> all(promises: List<Promise<S>>): Promise<List<S>> {
            return Promise { fulfill, reject ->
                promises.forEach { p ->
                    if (p.state == PromiseState.REJECTED) {
                        reject(p.error!!)
                        return@Promise
                    }
                }

                fulfill(promises.map { it.value!! })
            }
        }

        /**
         * Create a promise that is rejected
         */
        fun <S> reject(e: Throwable): Promise<S> {
            return Promise { _, reject ->
                reject(e)
            }
        }

        /**
         * Create a promise that is resolved
         */
        fun <S> resolve(s: S): Promise<S> {
            return Promise { fulfill, _ ->
                fulfill(s)
            }
        }
    }

    override fun toString(): String {
        return "Promise(state = $state, value = $value, error = $error)"
    }

    private enum class PromiseState {
        PENDING,
        FULFILLED,
        REJECTED
    }
}