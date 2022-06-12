package org.adastra.kgraph.core

interface ConstantsProvider

open class Constants<P : ConstantsProvider> : DefaultInitializable<P>() {
    protected var nullableProvider: P? = null
    open var provider: P
        get() {
            if(this.isNotInitialized()) throw UninitializedPropertyAccessException(this.constantProviderNotInitException())
            return nullableProvider!!
        }
        set(constantsProvider) {
            nullableProvider = constantsProvider
        }

    protected open fun constantProviderNotInitException() : String {
        return CONSTANT_PROVIDER_NOT_INIT_EXCEPTION.format(this::class.java)
    }

    override fun init(initValue: P) {
        this.replace(initValue)
    }

    override fun isInitialized() : Boolean {
        return nullableProvider != null
    }

    open fun replace(constantsProvider: P?) {
        this.nullableProvider = constantsProvider
    }

    open fun clean() {
        this.replace(null)
    }

    operator fun invoke(): P {
        return provider
    }

}

interface StatelessInitializable<T : Any> {
    fun init(initValue : T)
}

interface StatelessAutoInitializable<T : Any> : StatelessInitializable<T> {
    fun init()
}

interface StatefullInitializable<T : Any> : StatelessInitializable<T> {
    fun isInitialized() : Boolean
    fun isNotInitialized() : Boolean
}

abstract class DefaultInitializable<T : Any> : StatefullInitializable<T> {
    override fun isNotInitialized() : Boolean {
        return !isInitialized()
    }
}

interface AutoStatefullInitializable<T : Any> : StatefullInitializable<T>, StatelessAutoInitializable<T>

abstract class AutoInitConstants<P : ConstantsProvider> : Constants<P>(), AutoStatefullInitializable<P>

abstract class DependantAutoMethodInitConstants<P : ConstantsProvider> : AutoMethodInitConstants<P>() {
    init {
        this.initDependencies()
    }
    abstract fun dependencies() : MutableList<AutoInitConstants<*>>
    private fun initDependencies() {
        dependencies()
            .filter(AutoInitConstants<*>::isNotInitialized)
            .onEach(AutoInitConstants<*>::init)
            .forEach(AutoInitConstants<*>::invoke)
    }
}

abstract class AutoMethodInitConstants<P : ConstantsProvider> : AutoInitConstants<P>() {
    protected abstract fun defaultProvider(): P
    override fun init() {
        this.init(defaultProvider())
    }
}