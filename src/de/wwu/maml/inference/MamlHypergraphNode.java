package de.wwu.maml.inference;

public class MamlHypergraphNode<T> {

	protected T value;
	
	public MamlHypergraphNode(T value){
		this.value = value;
	}
	
	protected void setValue(T value) {
		this.value = value;
	}
	
	public T getValue(){
		return this.value;
	}
	
	@Override
	public String toString() {
		return value != null ? value.toString() : null;
	}
}
