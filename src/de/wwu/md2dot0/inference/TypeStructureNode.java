package de.wwu.md2dot0.inference;

import java.util.ArrayList;

import md2dot0.ParameterSource;
import md2dot0data.DataTypeLiteral;
import md2dot0data.Multiplicity;

public class TypeStructureNode {

	protected String attributeName;
	protected DataTypeLiteral type;
	protected Multiplicity multiplicity;
	protected ParameterSource source;
	protected ArrayList<TypeStructureNode> children = new ArrayList<TypeStructureNode>();
	
	public TypeStructureNode(String attributeName, DataTypeLiteral type, Multiplicity multiplicity, ParameterSource source) {
		this.attributeName = attributeName;
		this.type = type;
		this.multiplicity = multiplicity;
		this.source = source;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public DataTypeLiteral getType() {
		return type;
	}

	public void setType(DataTypeLiteral type) {
		this.type = type;
	}

	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	public ParameterSource getSource() {
		return source;
	}

	public void setSource(ParameterSource source) {
		this.source = source;
	}

	public ArrayList<TypeStructureNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TypeStructureNode> children) {
		this.children = children;
	}
	
	public void addChild(TypeStructureNode child){
		this.children.add(child);
	}
	
	public void removeChild(TypeStructureNode child){
		this.children.remove(child);
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TypeStructureNode)) return false;
		TypeStructureNode node = (TypeStructureNode) obj;
		
		return this.attributeName.equals(node.getAttributeName()) && this.multiplicity.equals(node.getMultiplicity())
				&& this.getSource().equals(node.getSource()) && this.getType().equals(node.getType()); // ignore children
	}
}
