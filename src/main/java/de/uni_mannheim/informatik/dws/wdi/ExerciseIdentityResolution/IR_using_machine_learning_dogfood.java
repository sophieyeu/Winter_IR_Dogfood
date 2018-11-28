package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.DogfoodBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.*;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Dogfood;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.DogfoodXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.apache.logging.log4j.Logger;
import weka.classifiers.trees.REPTree;

import java.io.File;

public class IR_using_machine_learning_dogfood {

	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *
	 * To set the log level to trace and write the log to winter.log and console,
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("trace");

    public static void main( String[] args ) throws Exception
    {
		// loading data


//		// dogfood
		HashedDataSet<Dogfood, Attribute> dogfood = new HashedDataSet<>();
		new DogfoodXMLReader().loadFromXML(new File("data/input/dogfood.xml"),
				"/dogfood/product", dogfood);



		// ======================   Create a matching rule - Basic IR methods

		// -- SimpleLogistic
//		String options[] = new String[] { "-S" };
//		String modelType = "SimpleLogistic"; // use a logistic regression

//		// -- NaiveBayes
//        String options[] = new String[1];
//        options[0] = "-K";
//        String modelType = "NaiveBayes"; // use a NaiveBayes



		// -- AdaBoost
//       	String options[] = new String[1];
//        options[0] = "-Q";
//        String modelType = "AdaBoostM1";



		// ======================    Unsupervised IR methods
//
//		// --  TF-IDF cosine ==== > NEED CODE FOR THIS
////        String options[] = new String[1];
////        options[0] = "-K";
////        String modelType = "TF-IDF cosine"; // use a TF-IDF cosine similarity
//
//		// -- Embeddings ==== > NEED CODE FOR THIS
////        String options[] = new String[1];
////        options[0] = "-K";
////        String modelType = "Embeddings"; // use a Embeddings
//
//
//		// -- Domain-specific heuristics ==== > NEED CODE FOR THIS
////        String options[] = new String[1];
////        options[0] = "-K";
////        String modelType = "heuristics"; // use a heuristics


		// ======================    Supervised IR methods

		// --  Word weights  --> WEIGHT_SIMILARITY
//        String options[] = new String[2];
//        options[0] = "-X";
//        options[0] = "10";
//        String modelType = "ibk"; // use a IBK


		// -- Decision trees == HoeffdingTree
//		String options[] = new String[] {"-S", "0", "-L", "2"};
//		String modelType = "HoeffdingTree";


		// -- Decision trees ==  REP Tree
//		String options[] = new String[] {"-S", "42", "-M", "3.0"};
//		String modelType = "REPTree";


		// -- Random forest
		String options[] = new String[] {"-M", "3.0", "-S", "42"};
		String modelType = "RandomForest";

		// --  Deep learning ==== > NEED CODE FOR THIS --- NeuralNetwork????
//        String options[] = new String[1];
//        String modelType = "NeuralNetwork"; // use a NeuralNetwork ?????




//
        WekaMatchingRule<Dogfood, Attribute> matchingRule = new WekaMatchingRule<>(0.5, modelType, options);
		matchingRule.setClassifier(new REPTree());
        matchingRule.activateDebugReport("data/output/debugResultsMatchingRule_dogfood.csv", 1000);
//
//
//		// =============================  add comparators

//	    matchingRule.addComparator(new DogfoodNameComparatorEqual());
	    matchingRule.addComparator(new DogfoodNameComparatorJaccard());
//	    matchingRule.addComparator(new DogfoodNameComparatorJaroWinkler());
//	    matchingRule.addComparator(new DogfoodNameComparatorLevenshtein());
//		matchingRule.addComparator(new DogfoodComparatorJaroWinklerTfIdf());
		matchingRule.addComparator(new DogfoodComparatorMongeElkan());
		matchingRule.addComparator(new DogfoodComparatorMongeElkanTfIdf());
//		matchingRule.addComparator(new DogfoodComparatorSoftTfIdf());

////
//        // Adding CrossValidation?
//        //matchingRule.setForwardSelection(true);
		matchingRule.setRandomSeed(42);
        matchingRule.setBackwardSelection(true);
//
//
//		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/train_dogfood_final_300.csv"));
//
		// train the matching rule's model
		System.out.println("*\n*\tLearning matching rule\n*");
		RuleLearner<Dogfood, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dogfood, dogfood, null, matchingRule, gsTraining);
		System.out.println(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
//
//		 create a blocker (blocking strategy)
//		StandardRecordBlocker<Dogfood, Attribute> blocker = new StandardRecordBlocker<Dogfood, Attribute>(new DogfoodBlockingKeyByNameGenerator());
//		SortedNeighbourhoodBlocker<Game, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new GameBlockingKeyByPlatformNameGenerator(), 15);
		NoBlocker blocker = new NoBlocker();
		blocker.collectBlockSizeData("data/output/debugResultsBlocking_dogfood.csv", 100);
//
//		// Initialize Matching Engine
		MatchingEngine<Dogfood, Attribute> engine = new MatchingEngine<>();
//
//		// Execute the matching
		System.out.println("*\n*\tRunning identity resolution\n*");
		Processable<Correspondence<Dogfood, Attribute>> correspondences = engine.runIdentityResolution(
				dogfood, dogfood, null, matchingRule,
				blocker);
//
//		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/dogfood_correspondences.csv"), correspondences);
//
		// load the gold standard (test set)
		System.out.println("*\n*\tLoading gold standard\n*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/test_dogfood_final_300.csv"));

		// evaluate your result
		System.out.println("*\n*\tEvaluating result\n*");
		MatchingEvaluator<Dogfood, Attribute> evaluator = new MatchingEvaluator<Dogfood, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		// print the evaluation result
		System.out.println("Dogfood_Identity Resolution");
		System.out.println(String.format(
				"Precision: %.4f", perfTest.getPrecision()));
		System.out.println(String.format(
				"Recall: %.4f", perfTest.getRecall()));
		System.out.println(String.format(
				"F1: %.4f", perfTest.getF1()));
    }
}
