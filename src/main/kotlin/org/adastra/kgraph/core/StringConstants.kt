package org.adastra.kgraph.core

interface StringConstantsProvider : ConstantsProvider {
    val constantProviderNotInitException: String
}

open class StringConstantsProviderDefault : StringConstantsProvider {
    override val constantProviderNotInitException: String get() = CONSTANT_PROVIDER_NOT_INIT_EXCEPTION
}

object StringConstants : AutoMethodInitConstants<StringConstantsProvider>() {
    override fun defaultProvider(): StringConstantsProvider = StringConstantsProviderDefault()
}

