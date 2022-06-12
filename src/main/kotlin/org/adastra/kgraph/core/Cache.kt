package org.adastra.kgraph.core

class CacheValue<T : Any>(
    valueProvider: () -> T
) {
    constructor(value: T) : this({value})
    val value: T by lazy(valueProvider)
}

class CacheMap<K : Any, V : Any?>(
    private val mappingProvider: (K) -> V
) {
    private val mutableMap by lazy { mutableMapOf<K, V>() }
    val map: Map<K, V> by lazy { mutableMap }
    operator fun get(key: K): V {
        val value = mutableMap[key]
        if (value != null) return value
        return with(mappingProvider(key)) {
            mutableMap[key] = this
            this
        }
    }

    fun clear() {
        this.mutableMap.clear()
    }

    fun reset(): CacheMap<K, V> {
        return CacheMap(mappingProvider)
    }

    fun remove(key: K): V? {
        return this.mutableMap.remove(key)
    }

}