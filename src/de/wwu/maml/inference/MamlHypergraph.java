package de.wwu.maml.inference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.SetHypergraph;

public class MamlHypergraph<V, E> extends SetHypergraph<V, E> {

	private static final long serialVersionUID = 1L;

	/**
	 * Find edges that contain multiple specified vertices.
	 * 
	 * @param v1
	 * @param v2
	 * @param vMore
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<E> findEdgeSet(V v1, V v2, V... vMore) {
		Collection<E> edges = findEdgeSet(v1, v2);
		
		if(edges == null) return new HashSet<E>();
		
		Set<E> intermediateSet = new HashSet<E>(edges);
		
		for(V v : vMore){
			intermediateSet = intersect(intermediateSet, findEdgeSet(v1, v));
		}
		return intermediateSet;
	}

	public Collection<V> getEdge(E edge) {
		return edges.get(edge);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Collection<V>> findEdgeSetContent(V v1, V v2, V... vMore) {
		HashSet<Collection<V>> contents = new HashSet<Collection<V>>();  
		for(E edge : findEdgeSet(v1, v2, vMore)){
			contents.add(getEdge(edge));
		}
		return contents;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<V> findEdgeSetFlatContent(V v1, V v2, V... vMore) {
		Collection<V> contents = new HashSet<V>();  
		for(E edge : findEdgeSet(v1, v2, vMore)){
			contents.addAll(getEdge(edge));
		}
		return contents;
	}
	
	/**
	 * Returns one edge that contains the specified vertices.
	 * If this edge is not uniquely defined (that is, if the graph contains more 
	 * than one edge connecting the vertices through different other vertices), 
	 * any of these edges may be returned.  
	 *  
	 * @param v1
	 * @param v2
	 * @param vMore
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E findEdge(V v1, V v2, V... vMore) {
		Iterator<E> iterator = findEdgeSet(v1, v2, vMore).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<V> findEdgeContent(V v1, V v2, V... vMore) {
		return getEdge(findEdge(v1, v2, vMore));
	}
	
	protected Set<E> intersect(Collection<E> set1, Collection<E> set2) {
		HashSet<E> intersection = new HashSet<E>(set1); // Copy input for immutability
		intersection.retainAll(set2);
		return intersection;
	}
	
	protected Set<E> difference(Collection<E> set1, Collection<E> set2) {
		HashSet<E> difference = new HashSet<E>(set1); // Copy input for immutability
		difference.removeAll(set2);
		return difference;
	}
}
