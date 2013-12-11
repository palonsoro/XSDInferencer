package es.upm.dit.xsdinferencer.merge.mergerimpl.children;

import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;

/**
 * Children pattern comparator that returns true if the <i>reduce</i> distance 
 * between them is below a given threshold. For more information, see the 
 * Kore Nordmann Diploma Thesis. 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class ReducePatternComparator implements ChildrenPatternComparator {
	private float threshold;

	/**
	 * Constructor
	 * @param threshold the threshold
	 */
	public ReducePatternComparator(float threshold){
		this.threshold=threshold;
	}
	
	/**
	 * @return the threshold
	 */
	public float getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	/**
	 * Returns one of the (symmetric) addends of the distance, so that the whole distance may be 
	 * calculated as <i>semiDistance(A,B)+semiDistance(B,A)</i>
	 * @param automaton1 the first automaton (the normalization will be done against this)
	 * @param automaton2 the other automaton
	 * @return the value of the addend
	 */
	private float semiDistance(ExtendedAutomaton automaton1, ExtendedAutomaton automaton2){
		long diffDistSum=0;
		long tot1DistSum=0;
		for(SchemaElement src: automaton1){
			for(SchemaElement dst:automaton1.getOutgoingEdges(src).keySet()){
				tot1DistSum+=automaton1.getEdgeWeight(src, dst);
				try {
					if(!(automaton2.getEdgeWeight(src, dst)>0)){
						diffDistSum+=automaton1.getEdgeWeight(src, dst);
					}
				} catch (IllegalArgumentException e) {
					//If some of the nodes do not belong to automaton2
					//the edge does not exist as well.
					diffDistSum+=automaton1.getEdgeWeight(src, dst);
				}
			}
		}
		float semidistance = (float)diffDistSum/(float)tot1DistSum;
		if(Float.isNaN(semidistance)){
			semidistance=0f;
		}
		return semidistance;
	}

	/**
	 * @see ChildrenPatternComparator#compare(ExtendedAutomaton, ExtendedAutomaton)
	 */
	@Override
	public boolean compare(ExtendedAutomaton automaton1,
			ExtendedAutomaton automaton2) {
		float distance = semiDistance(automaton1, automaton2)+
						 semiDistance(automaton2, automaton1);
		return distance<=threshold;
	}
}
