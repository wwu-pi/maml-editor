package de.wwu.md2dot0.inference;

import md2dot0data.Multiplicity;

public class HypergraphCardinalityNode extends MamlHypergraphNode<Multiplicity> {

	protected static HypergraphCardinalityNode one = null;
	protected static HypergraphCardinalityNode oneOpt = null;
	protected static HypergraphCardinalityNode many = null;
	protected static HypergraphCardinalityNode manyOpt = null;
	protected static HypergraphCardinalityNode id = null;
	protected static HypergraphCardinalityNode uniqueOne = null;
	protected static HypergraphCardinalityNode uniqueMany = null;

	Multiplicity cardinality;

	private HypergraphCardinalityNode() {
		// Internal Constructor
		super(null);
	}

	public static HypergraphCardinalityNode createHyperGraphCardinalityNode(Multiplicity cardinality) {
		if (cardinality.equals(Multiplicity.ONE)) {
			return getCardinalityOneNode();
		} else if (cardinality.equals(Multiplicity.MANY)) {
			return getCardinalityManyNode();
		}
		return null;
		// TODO others
	}

	protected static HypergraphCardinalityNode getCardinalityOneNode() {
		if (one == null) {
			one = new HypergraphCardinalityNode();
			one.setValue(Multiplicity.ONE);
		}
		return one;
	}

	protected static HypergraphCardinalityNode getCardinalityManyNode() {
		if (many == null) {
			many = new HypergraphCardinalityNode();
			many.setValue(Multiplicity.MANY);
		}
		return many;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof HypergraphCardinalityNode)
				&& ((HypergraphCardinalityNode) o).cardinality.equals(cardinality);
	}
}