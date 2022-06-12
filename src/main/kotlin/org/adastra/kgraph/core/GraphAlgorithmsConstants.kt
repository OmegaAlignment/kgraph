package org.adastra.kgraph.core

interface GraphAlgorithmConstantsProvider : ConstantsProvider {
    val algoAdjacentEntitiesDepthMin: Int
    val algoAdjacentEntitiesDepthMinBound: Int
    val algoAdjacentEntitiesDepthDefault: Int
    val algoAdjacentNodesDepthDefault: Int
    val algoAdjacentNodesNodeSelectorDefault: (Any) -> Boolean
    val algoAdjacentNodesTransitionSelectorDefault: (Any) -> Boolean
    val algoNeighbourNodesDepthDefault: Int
    val algoNeighbourNodesNodeSelectorDefault: (Any) -> Boolean
    val algoNeighbourNodesTransitionSelectorDefault: (Any) -> Boolean
}

open class GraphAlgorithmConstantsProviderDefault : GraphAlgorithmConstantsProvider {
    override val algoAdjacentEntitiesDepthMin: Int get() = CommonConstants().adjacentEntitiesDepthMin
    override val algoAdjacentEntitiesDepthMinBound: Int get() = CommonConstants().adjacentEntitiesDepthMinBound
    override val algoAdjacentEntitiesDepthDefault: Int get() = CommonConstants().adjacentEntitiesDepthDefault
    override val algoAdjacentNodesDepthDefault: Int get() = algoAdjacentEntitiesDepthDefault
    override val algoNeighbourNodesDepthDefault: Int get() = algoAdjacentEntitiesDepthDefault
    override val algoAdjacentNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstants().nodeSelectorDefault
    override val algoAdjacentNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstants().transitionSelectorDefault
    override val algoNeighbourNodesNodeSelectorDefault: (Any) -> Boolean get() = CommonConstants().nodeSelectorDefault
    override val algoNeighbourNodesTransitionSelectorDefault: (Any) -> Boolean get() = CommonConstants().transitionSelectorDefault
}

object GraphAlgorithmsConstants : DefaultDependantConstants<GraphAlgorithmConstantsProvider>() {
    override fun defaultProvider(): GraphAlgorithmConstantsProvider = GraphAlgorithmConstantsProviderDefault()
    fun <E : Any, O : Any> adjacentEntities(
        startEntity: E,
        depth: Int = GraphAlgorithmsConstants().algoAdjacentEntitiesDepthDefault,
        relationsExplorers: List<RelationExplorer>
    ): List<O> {
        if (depth < GraphAlgorithmsConstants().algoAdjacentEntitiesDepthMin) return emptyList()
        var currentDepth = depth
        var adjacentEntitiesAtCurrentDepth = listOf<Any>(startEntity)
        val adjacentEntitiesDepthMinBound = GraphAlgorithmsConstants().algoAdjacentEntitiesDepthMinBound
        while (currentDepth > adjacentEntitiesDepthMinBound && adjacentEntitiesAtCurrentDepth.any()) {
            relationsExplorers.forEach { relationExplorer ->
                adjacentEntitiesAtCurrentDepth = adjacentEntitiesAtCurrentDepth
                    .flatMap { entity -> relationExplorer.sourceEntityRelationsProvider(entity) }
                    .filter { relation ->
                        relation.verifyUnordered(
                            relationExplorer.sourceEntitySelector,
                            relationExplorer.targetEntitySelector
                        )
                    }
                    .mapNotNull { relation -> relation.selectOrNullAs(relationExplorer.targetEntitySelector) }
                    .distinct()
            }
            currentDepth--
        }
        return adjacentEntitiesAtCurrentDepth as List<O>
    }

    fun <N : Node> adjacentNodes(
        startNode: N,
        depth: Int = GraphAlgorithmsConstants().algoAdjacentNodesDepthDefault,
        nodeSelector: (Any) -> Boolean = GraphAlgorithmsConstants().algoAdjacentNodesNodeSelectorDefault,
        transitionSelector: (Any) -> Boolean = GraphAlgorithmsConstants().algoAdjacentNodesTransitionSelectorDefault,
        nodeRelationsProvider: (Any) -> List<Relation>,
        transitionRelationsProvider: (Any) -> List<Relation>
    ): List<N> {
        return this.adjacentEntities(
            startNode,
            depth,
            listOf(
                RelationExplorer(nodeRelationsProvider, nodeSelector, transitionSelector),
                RelationExplorer(transitionRelationsProvider, transitionSelector, nodeSelector)
            )
        )
    }

    fun <N : Node> neighbourNodes(
        startNode: N,
        depth: Int = GraphAlgorithmsConstants().algoNeighbourNodesDepthDefault,
        nodeSelector: (Any) -> Boolean = GraphAlgorithmsConstants().algoNeighbourNodesNodeSelectorDefault,
        transitionSelector: (Any) -> Boolean = GraphAlgorithmsConstants().algoNeighbourNodesTransitionSelectorDefault,
        nodeRelationsProvider: (Any) -> List<Relation>,
        transitionRelationsProvider: (Any) -> List<Relation>
    ): List<N> {
        return this.adjacentNodes(
            startNode,
            depth,
            nodeSelector,
            transitionSelector,
            nodeRelationsProvider,
            transitionRelationsProvider
        ).filter { node -> node != startNode }
    }



}
