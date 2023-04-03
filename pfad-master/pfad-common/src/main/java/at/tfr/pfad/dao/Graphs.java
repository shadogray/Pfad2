package at.tfr.pfad.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;

public class Graphs {

	public static String FETCHGRAPH = "javax.persistence.fetchgraph";

	public <T> Map<String, Object> createHints(EntityManager em, @SuppressWarnings("rawtypes") Class<T> rootType,
			String... nodes) {
		EntityGraph<T> graph = em.createEntityGraph(rootType);
		Stream.of(nodes).forEach((node) -> graph.addAttributeNodes(node));
		return createHints(em, graph);
	}

	public static Map<String, Object> createHints(EntityManager em, String graphName, String... nodes) {
		@SuppressWarnings("rawtypes")
		EntityGraph graph = createHint(em, graphName, nodes);
		return createHints(em, graph);
	}

	@SuppressWarnings("rawtypes")
	public static EntityGraph createHint(EntityManager em, String graphName, String... nodes) {
		EntityGraph graph = em.createEntityGraph(graphName);
		if (nodes != null && nodes.length > 0) {
			Stream.of(nodes).forEach((node) -> graph.addAttributeNodes(node));
		}
		return graph;
	}

	public static Map<String, Object> createHints(EntityManager em, @SuppressWarnings("rawtypes") EntityGraph graph) {
		Map<String, Object> hints = new HashMap<>();
		hints.put(FETCHGRAPH, graph);
		return hints;
	}

}
