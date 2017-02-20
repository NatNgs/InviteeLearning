package fr.unice.polytech.invitee.randomforest;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nathael on 20/02/17.
 */
public class RF {
	private final List<ClassifierInstance> dataset = new LinkedList<>();
	private RandomDecisionForest randomForest = null;

	public void learn(int treeNumbers) {
		randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
				// The default isn't desirable here because this dataset has so few attributes
				.attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
				.numTrees(treeNumbers)
				.buildPredictiveModel(dataset);
	}

	public void feedSet(Serializable setClass, Map<String,Serializable> set) {
		AttributesMap map = new AttributesMap();
		map.putAll(set);
		dataset.add(new ClassifierInstance(map, setClass));
	}

	public void unfeedSet() {
		dataset.clear();
	}

	public PredictionMap predictSet(Map<String,Serializable> set) {
		AttributesMap map = new AttributesMap();
		map.putAll(set);
		return randomForest.predict(map);
	}
	public Serializable trySet(Map<String,Serializable> set) {
		PredictionMap predict = predictSet(set);
		double d = 0;
		Serializable retClass = null;
		for(Map.Entry<Serializable, Double> entry : predict.entrySet()) {
			if(entry.getValue() > d) {
				d = entry.getValue();
				retClass = entry.getKey();
			}
		}

		return retClass;
	}
}