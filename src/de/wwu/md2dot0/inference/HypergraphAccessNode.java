package de.wwu.md2dot0.inference;

import md2dot0gui.AccessType;

public class HypergraphAccessNode extends MamlHypergraphNode<AccessType> {

	protected static HypergraphAccessNode readAccess = null;
	protected static HypergraphAccessNode writeAccess = null;
	
	AccessType accessType;
	
	protected static HypergraphAccessNode getReadAccessNode(){
		if(readAccess == null){
			readAccess = new HypergraphAccessNode(); 
			readAccess.setAccessType(AccessType.READ);
		}
		return readAccess;
	}
	
	protected static HypergraphAccessNode getWriteAccessNode(){
		if(writeAccess == null){
			writeAccess = new HypergraphAccessNode(); 
			writeAccess.setAccessType(AccessType.WRITE);
		}
		return writeAccess;
	}
	
	private HypergraphAccessNode(){
		// Internal Constructor
		super(null);
	}

	public static HypergraphAccessNode createHypergraphAccessNode(AccessType accessType){
		if(accessType.equals(AccessType.READ)){
			return getReadAccessNode();
		} else {
			return getWriteAccessNode();
		}
	}
	
	protected void setAccessType(AccessType accessType){
		this.accessType = accessType;
	}
			
	@Override
	public boolean equals(Object o){
		return (o instanceof HypergraphAccessNode) && ((HypergraphAccessNode) o).accessType.equals(accessType);
	}
}
