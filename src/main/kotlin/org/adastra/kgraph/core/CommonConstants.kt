package org.adastra.kgraph.core

abstract class StringDependantConstants<P : ConstantsProvider> : DependantAutoMethodInitConstants<P>() {
    override fun dependencies(): MutableList<AutoInitConstants<*>> {
        return mutableListOf(StringConstants)
    }
    override fun constantProviderNotInitException(): String {
        return StringConstants().constantProviderNotInitException.format(this::class.java)
    }
}

abstract class DefaultDependantConstants<P : ConstantsProvider> : StringDependantConstants<P>() {
    override fun dependencies(): MutableList<AutoInitConstants<*>> {
        return super.dependencies().apply { add(CommonConstants) }
    }
}

interface CommonConstantsProvider : ConstantsProvider {
    val nodeSelectorDefault: (Any) -> Boolean
    val transitionSelectorDefault: (Any) -> Boolean
    val adjacentEntitiesDepthMin: Int
    val adjacentEntitiesDepthMinBound: Int
    val adjacentEntitiesDepthDefault: Int
}

open class CommonConstantsProviderDefault : CommonConstantsProvider {
    override val nodeSelectorDefault: (Any) -> Boolean get() = { node -> node is Node }
    override val transitionSelectorDefault: (Any) -> Boolean get() = { transition -> transition is Transition }
    override val adjacentEntitiesDepthMin: Int get() = 1
    override val adjacentEntitiesDepthMinBound: Int get() = 0
    override val adjacentEntitiesDepthDefault: Int get() = 1
}

object CommonConstantsProviderDefaultObject : CommonConstantsProviderDefault()

object CommonConstants : StringDependantConstants<CommonConstantsProvider>() {
    override fun defaultProvider(): CommonConstantsProvider = CommonConstantsProviderDefault()
}