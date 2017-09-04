package de.wwu.maml.inference;

import de.wwu.maml.dsl.mamldata.Multiplicity;
import de.wwu.maml.dsl.mamlgui.Attribute;

public class HypergraphCardinalityNode extends MamlHypergraphNode<Multiplicity> {

	protected static HypergraphCardinalityNode one = null;
	protected static HypergraphCardinalityNode zeroOne = null;
	protected static HypergraphCardinalityNode many = null;
	protected static HypergraphCardinalityNode zeroMany = null;
//	protected static HypergraphCardinalityNode id = null;
//	protected static HypergraphCardinalityNode uniqueOne = null;
//	protected static HypergraphCardinalityNode uniqueMany = null;

	private HypergraphCardinalityNode(Multiplicity value) {
		// Internal Constructor
		super(value);
	}
	
	public static HypergraphCardinalityNode getCardinalityNode(Attribute attribute){
		switch(attribute.getMultiplicity()){
			case ONE: 
				return getCardinalityOneNode();
			case MANY: 
				return getCardinalityManyNode();
			case ZEROONE: 
				return getCardinalityZeroOneNode();
			case ZEROMANY: 
				return getCardinalityZeroManyNode();
		}
		return getCardinalityOneNode();
	}

	protected static HypergraphCardinalityNode getCardinalityOneNode() {
		if (one == null) {
			one = new HypergraphCardinalityNode(Multiplicity.ONE);
		}
		return one;
	}

	protected static HypergraphCardinalityNode getCardinalityManyNode() {
		if (many == null) {
			many = new HypergraphCardinalityNode(Multiplicity.MANY);
		}
		return many;
	}
	
	protected static HypergraphCardinalityNode getCardinalityZeroOneNode() {
		if (zeroOne == null) {
			zeroOne = new HypergraphCardinalityNode(Multiplicity.ZEROONE);
		}
		return zeroOne;
	}

	protected static HypergraphCardinalityNode getCardinalityZeroManyNode() {
		if (zeroMany == null) {
			zeroMany = new HypergraphCardinalityNode(Multiplicity.ZEROMANY);
		}
		return zeroMany;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof HypergraphCardinalityNode)
				&& ((HypergraphCardinalityNode) o).value.equals(value);
	}
}