package org.adastra.kgraph.main

import org.adastra.kgraph.core.*
import java.util.stream.IntStream
import kotlin.streams.asSequence

private const val NAMED_TRANSITION_DELIMITER : String = "-"

interface NamedObject {
    val name : String
}

open class NamedTransition(override val name : String) : Transition, NamedObject

open class NamedNode(override val name : String) : Node, NamedObject

private fun <N : Node> nodeList(size : Int, nodeBuilder : (Int) -> N) : List<N> {
    return IntStream.range(0, size)
        .asSequence()
        .map { nodeBuilder(it) }
        .toList()

}

private fun <N : NamedNode> namedNodeMap(size : Int, nodeBuilder : (Int) -> N) : Map<String, N> {
    val minSize = 1
    val maxSize = 'Z'.code - 'A'.code
    if(size !in minSize .. maxSize) throw KGraphException("size must be $minSize <= size <= $maxSize, provided $size")
    return nodeList(size, nodeBuilder).associateBy({ it.name }, { it })
}

private fun transitionName(firstNodeName : String, secondNodeName : String) : String {
    return "$firstNodeName$NAMED_TRANSITION_DELIMITER$secondNodeName"
}


private fun createTestGraph(): StrictGraph<NamedNode, NamedTransition> {
    val nodesMap = namedNodeMap(4) { NamedNode( ('A'.code+it).toChar().toString()) }
    val nodes = nodesMap.values.toList()
    val transitionNames = listOf(
        transitionName("A", "B"),
        transitionName("A", "C"),
        transitionName("B", "C"),
        transitionName("C", "D"),
    )
    val transitions = transitionNames.map { NamedTransition(it) }
    val relations : List<Relation> = transitions
        .flatMap { transition ->
            val nodesNames = transition.name.split(NAMED_TRANSITION_DELIMITER)
            if(nodesNames.size != 2 || nodesNames.any { it.isBlank() }) return@flatMap emptyList()
            val firstNode : NamedNode = nodesMap[nodesNames[0]] ?: return@flatMap emptyList()
            val secondNode : NamedNode = nodesMap[nodesNames[1]] ?: return@flatMap emptyList()
            Graph.createTransition(firstNode, secondNode, transition)
        }
    return StrictGraph(nodes, transitions, relations)
}

private fun selectNamedNodeRequired(graph : Graph<NamedNode, NamedTransition>, name : String) : NamedNode {
    return graph
        .selectNodes { it is NamedNode && it.name == name }
        .firstOrNull()
        ?: throw KGraphException("Selected node must exist")
}

private fun testGraph() {
    val graph = createTestGraph()
    val selectedNode = selectNamedNodeRequired(graph, "C")
    printGraphTest(graph, selectedNode)
}

private fun printGraphTest(graph : StrictGraph<NamedNode, NamedTransition>, selectedNode : NamedNode) {
    println("# Nodes :")
    graph.selectNodes().forEach { node -> println(node.name) }
    println("# Transitions :")
    graph.selectTransitions().forEach { transition -> println(transition.name) }
    println("# Relations :")
    graph.relations.forEach {
            relation ->
        with(relation) {
            with(firstAs<NamedNode>()){ print(name) }
            print(" <-> ")
            with(secondAs<NamedTransition>()){ print(name) }
            println()
        }
    }
    println("# Adjacent nodes :")
    graph.selectAdjacentNodes(selectedNode).forEach {
            node -> println(node.name)
    }
    println("# Neighbour nodes :")
    graph.selectNeighbourNodes(selectedNode).forEach {
            node -> println(node.name)
    }
}

private fun testCache() {
    val graph = createTestGraph()
    val selectedNodeC = selectNamedNodeRequired(graph, "C")
    val selectedNodeA = selectNamedNodeRequired(graph, "A")
    val nodesRelationsCache = CacheMap<Any, List<Relation>> { graph.selectNodeRelations(it as NamedNode) }
    val transitionRelationCache : CacheMap<Any, List<Relation>> = CacheMap { graph.selectTransitionRelations(it as NamedTransition) }
    val adjacentNodesCache : CacheMap<NamedNode, List<NamedNode>> = CacheMap { startNode ->
        GraphAlgorithmsConstants.neighbourNodes(
            startNode = startNode,
            depth = 1,
            nodeRelationsProvider = { nodesRelationsCache[it] },
            transitionRelationsProvider = { transitionRelationCache[it] },
            nodeSelector = { node -> node is NamedNode && node.name.length == 1},
        )
    }
    println("# Adjacent nodes using cache")
    adjacentNodesCache[selectedNodeC]
    adjacentNodesCache[selectedNodeC]
    println(adjacentNodesCache[selectedNodeC].map { it.name })
    adjacentNodesCache[selectedNodeA]
    adjacentNodesCache[selectedNodeA]
    println(adjacentNodesCache[selectedNodeA].map { it.name })
    println("# Relations cache")
    nodesRelationsCache.map.forEach { (k, v) ->
        val kAs = k as NamedNode
        val vL = v.map { "${it.firstAs<NamedNode>().name} <-> ${it.secondAs<NamedTransition>().name}" }
        println("${kAs.name} : $vL" )
    }
    printGraphTest(graph, selectedNodeC)
}

object MyKGraph : LibManager() {

    private fun initConstants() : List<AutoInitConstants<*>> {
        return listOf(StringConstants, GraphAlgorithmsConstants)
    }

    private fun cleanConstants() : List<AutoInitConstants<*>> {
        return initConstants() + listOf(GraphConstants)
    }

    override fun init() {
        this.init(initConstants())
        GraphConstants.init(GraphConstantsProviderV3)
    }

    override fun clean() {
        this.clean(cleanConstants())
    }

}



fun main(args: Array<String>) {
    if(args.isNotEmpty()) throw IllegalArgumentException("args must be empty")
    withLib(Kgraph){
        println(GraphConstants().graphAdjacentNodesDepthDefault)
    }
    withLib(MyKGraph){
        println(GraphConstants().graphAdjacentNodesDepthDefault)
    }
}