package org.adastra.kgraph.core

class KGraphException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

open class Group<E : Any, R : Relation>(
    open val entities: List<E>,
    open val relations: List<R>
) {
    fun selectEntities(entitySelector: (E) -> Boolean): List<E> {
        return this.entities
            .filter(entitySelector)
    }

    fun selectRelations(relationSelector: (R) -> Boolean): List<R> {
        return this.relations
            .filter(relationSelector)
    }

    fun <T : E> selectEntitiesAs(entitySelector: (E) -> Boolean): List<T> {
        return selectEntities(entitySelector).map { it as T }
    }

    fun <T : R> selectRelationsAs(relationSelector: (R) -> Boolean): List<T> {
        return selectRelations(relationSelector).map { it as T }
    }

    fun selectEntityRelations(entity: E): List<R> {
        return this.selectRelations { it.contains(entity) }
    }
}

open class Relation(
    open val firstEntity: Any,
    open val secondEntity: Any
) {
    fun contains(value: Any): Boolean {
        return firstEntity == value || secondEntity == value
    }

    fun <T : Any> selectOrNullAs(selector: (Any) -> Boolean): T? {
        return selectOrNull(selector) as T?
    }

    fun selectOrNull(selector: (Any) -> Boolean): Any? {
        if (selector(firstEntity)) return firstEntity
        if (selector(secondEntity)) return secondEntity
        return null
    }

    fun <T : Any> selectInvertedOrNullAs(selector: (Any) -> Boolean): T? {
        return selectInvertedOrNull(selector) as T?
    }

    fun selectInvertedOrNull(selector: (Any) -> Boolean): Any? {
        if (selector(secondEntity)) return secondEntity
        if (selector(firstEntity)) return firstEntity
        return null
    }

    fun verify(firstEntityPredicate: (Any) -> Boolean, secondEntityPredicate: (Any) -> Boolean): Boolean {
        return firstEntityPredicate(this.firstEntity) && secondEntityPredicate(this.secondEntity)
    }

    fun verifyInverted(firstEntityPredicate: (Any) -> Boolean, secondEntityPredicate: (Any) -> Boolean): Boolean {
        return firstEntityPredicate(this.secondEntity) && secondEntityPredicate(this.firstEntity)
    }

    fun verifyUnordered(firstEntityPredicate: (Any) -> Boolean, secondEntityPredicate: (Any) -> Boolean): Boolean {
        return verify(firstEntityPredicate, secondEntityPredicate) || verifyInverted(
            firstEntityPredicate,
            secondEntityPredicate
        )
    }

    fun <T : Any> firstAs(): T {
        return firstEntity as T
    }

    fun <T : Any> secondAs(): T {
        return secondEntity as T
    }
}

open class GenericGroup(
    override val entities: List<Any>,
    override val relations: List<Relation>
) : Group<Any, Relation>(entities, relations)

interface Node

interface Transition

open class RelationExplorer(
    val sourceEntityRelationsProvider: (Any) -> List<Relation>,
    val sourceEntitySelector: (Any) -> Boolean,
    val targetEntitySelector: (Any) -> Boolean
)

open class Graph<N : Node, T : Transition>(
    nodes: List<N>,
    transitions: List<T>,
    override val relations: List<Relation>
) : GenericGroup(
    nodes + transitions,
    relations
) {
    protected fun nodeRelationsProvider(): (Any) -> List<Relation> {
        return { this.selectNodeRelations(it as N) }
    }

    protected fun transitionRelationsProvider(): (Any) -> List<Relation> {
        return { this.selectTransitionRelations(it as T) }
    }

    fun selectNodes(nodesSelector: (Any) -> Boolean): List<N> {
        return this.selectEntitiesAs(nodesSelector)
    }

    fun selectTransitions(transitionSelector: (Any) -> Boolean): List<T> {
        return this.selectEntitiesAs(transitionSelector)
    }

    fun selectNodeRelations(node: N): List<Relation> {
        return this.selectEntityRelations(node)
    }

    fun selectTransitionRelations(transition: T): List<Relation> {
        return this.selectEntityRelations(transition)
    }

    fun selectAdjacentNodes(
        startNode: N,
        depth: Int = GraphConstants().graphAdjacentNodesDepthDefault,
        nodeSelector: (Any) -> Boolean = GraphConstants().graphAdjacentNodesNodeSelectorDefault,
        transitionSelector: (Any) -> Boolean = GraphConstants().graphAdjacentNodesTransitionSelectorDefault
    ): List<N> {
        return GraphAlgorithmsConstants.adjacentNodes(
            startNode,
            depth,
            nodeSelector,
            transitionSelector,
            nodeRelationsProvider(),
            transitionRelationsProvider()
        )
    }

    fun selectNeighbourNodes(
        startNode: N,
        depth: Int = GraphConstants().graphNeighbourNodesDepthDefault,
        nodeSelector: (Any) -> Boolean = GraphConstants().graphNeighbourNodesNodeSelectorDefault,
        transitionSelector: (Any) -> Boolean = GraphConstants().graphNeighbourNodesTransitionSelectorDefault
    ): List<N> {
        return GraphAlgorithmsConstants.neighbourNodes(
            startNode,
            depth,
            nodeSelector,
            transitionSelector,
            nodeRelationsProvider(),
            transitionRelationsProvider()
        )
    }

    companion object {
        fun <N : Node, T : Transition> createTransition(firstNode: N, secondNode: N, transition: T): List<Relation> {
            return listOf(
                Relation(firstNode, transition),
                Relation(secondNode, transition)
            )
        }
    }
}

open class StrictGraph<N : Node, T : Transition>(
    nodes: List<N>,
    transitions: List<T>,
    override val relations: List<Relation>,
    private val nodeSelector: (Any) -> Boolean = GraphConstants().strictGraphNodeSelectorDefault,
    private val transitionSelector: (Any) -> Boolean = GraphConstants().strictGraphTransitionSelectorDefault
) : Graph<N, T>(nodes, transitions, relations) {
    fun selectNodes(): List<N> {
        return this.selectNodes(nodeSelector)
    }

    fun selectTransitions(): List<T> {
        return this.selectTransitions(transitionSelector)
    }
}


