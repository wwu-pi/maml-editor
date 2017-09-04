package de.wwu.maml.inference;

import de.wwu.maml.dsl.mamlgui.AccessType;

public class HypergraphAccessNode extends MamlHypergraphNode<AccessType> {

	protected static HypergraphAccessNode readAccess = null;
	protected static HypergraphAccessNode writeAccess = null;
	
	protected static HypergraphAccessNode getReadAccessNode(){
		if(readAccess == null){
			readAccess = new HypergraphAccessNode(AccessType.READ); 
		}
		return readAccess;
	}
	
	protected static HypergraphAccessNode getWriteAccessNode(){
		if(writeAccess == null){
			writeAccess = new HypergraphAccessNode(AccessType.WRITE); 
		}
		return writeAccess;
	}
	
	private HypergraphAccessNode(AccessType type){
		// Internal Constructor
		super(type);
	}

	public static HypergraphAccessNode createHypergraphAccessNode(AccessType accessType){
		if(accessType.equals(AccessType.READ)){
			return getReadAccessNode();
		} else {
			return getWriteAccessNode();
		}
	}
	
	@Override
	public boolean equals(Object o){
		return (o instanceof HypergraphAccessNode) && ((HypergraphAccessNode) o).value.equals(value);
	}
}
