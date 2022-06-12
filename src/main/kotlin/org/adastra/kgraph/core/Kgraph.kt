package org.adastra.kgraph.core

abstract class LibManager : StatelessAutoInitializable<List<AutoInitConstants<*>>> {

    override fun init(initValue: List<AutoInitConstants<*>>) {
        initValue.forEach(AutoInitConstants<*>::init)
    }

    fun clean(cleanValue: List<AutoInitConstants<*>>){
        cleanValue.forEach(AutoInitConstants<*>::clean)
    }

    abstract fun clean()

}

fun <T : LibManager, R> withLib(libraryManager : T, block : () -> R) : R{
    libraryManager.init()
    val result = block()
    libraryManager.clean()
    return result
}

object Kgraph : LibManager() {

    private fun constants(): List<AutoInitConstants<*>> {
        return listOf(
            StringConstants,
            GraphAlgorithmsConstants,
            GraphConstants
        )
    }

    override fun init() {
        this.init(constants())
    }

    override fun clean() {
        this.clean(constants())
    }

}