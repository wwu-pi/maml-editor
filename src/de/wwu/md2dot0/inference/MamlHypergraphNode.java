package de.wwu.md2dot0.inference;

public class MamlHypergraphNode<T> {

	protected T value;
	
	public MamlHypergraphNode(T value){
		this.value = value;
	}
	
	protected void setValue(T value) {
		this.value = value;
	}
}
