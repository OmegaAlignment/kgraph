package org.adastra.kgraph.core

interface GraphConstantsProvider : ConstantsProvider {
    val graphAdjacentNodesDepthDefault: Int
    val graphAdjacentNodesNodeSelectorDefault: (Any) -> Boolean
    val graphAdjacentNodesTransitionSelectorDefault: (Any) -> Boolean
    val graphNeighbourNodesDepthDefault: Int
    val graphNeighbourNodesNodeSelectorDefault: (Any) -> Boolean
    val graphNeighbourNodesTransitionSelectorDefault: (Any) -> Boolean
    val strictGraphNodeSelectorDefault: (Any) -> Boolean
    val strictGraphTransitionSelectorDefault: (Any) -> Boolean
}

open class GraphConstantsProviderDefault : GraphConstantsProvider {
    override val graphAdjacentNodesDepthDefault: Int get() = CommonConstantsProviderDefaultObject.adjacentEntitiesDepthDefault
    override val graphNeighbourNodesDepthDefault: Int get() = CommonConstantsProviderDefaultObject.adjacentEntitiesDepthDefault
    override val graphAdjacentNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val graphAdjacentNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
    override val strictGraphNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val strictGraphTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
    override val graphNeighbourNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val graphNeighbourNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
}

open class GraphConstantsProviderV2 : GraphConstantsProviderDefault() {
    override val graphAdjacentNodesDepthDefault: Int get() = 5
    override val graphNeighbourNodesDepthDefault: Int get() = 5
    override val graphAdjacentNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val graphAdjacentNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
    override val strictGraphNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val strictGraphTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
    override val graphNeighbourNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.nodeSelectorDefault
    override val graphNeighbourNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstantsProviderDefaultObject.transitionSelectorDefault
}

object GraphConstantsProviderV3 : GraphConstantsProviderV2()

object GraphConstants : DefaultDependantConstants<GraphConstantsProvider>() {
    override fun defaultProvider(): GraphConstantsProvider = GraphConstantsProviderDefault()
}