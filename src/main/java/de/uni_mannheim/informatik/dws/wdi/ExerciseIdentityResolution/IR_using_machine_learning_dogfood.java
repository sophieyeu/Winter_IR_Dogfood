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
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.REPTree;
import com.github.jfasttext.JFastText;
import weka.core.Instances;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Date;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

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

    public static void main(String[] args) throws Exception {
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
////        String swvoptions[] = {"-W 2000", "-I", "-L", "-M 1"};
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

        System.out.println("Embeddings started");
        JFastText jft = new JFastText();
        jft.runCmd(new String[]{
                "cbow",
                "-input", "data/input/dogfood_Title.csv",
                "-output", "data/output/dogfood_Title_embeddings_cbow.model",
                "-bucket", "100",
                "-minCount", "1"
        });


        //Deep learning neural network
        Date beginDate = new Date();
        //network variables
        String backPropOptions =
                "-L " + 0.1 //learning rate
                        + " -M " + 0 //momentum
                        + " -N " + 10000 //epoch
                        + " -V " + 0 //validation
                        + " -S " + 0 //seed
                        + " -E " + 0 //error
                        + " -H " + "3"; // hidden nodes. //e.g. use "3,3" for 2 level hidden layer with 3 nodes

        try {
            //prepare historical data
            String historicalDataPath = "C:\\Users\\User\\workspace\\Winter_IR_Dogfood\\data\\input\\GS_Dogfood_300_title.arff";
            BufferedReader reader
                    = new BufferedReader(new FileReader(historicalDataPath));
            Instances trainingset = new Instances(reader);
            reader.close();

            trainingset.setClassIndex(trainingset.numAttributes() - 1);
            //final attribute in a line stands for output


//            //network training
            MultilayerPerceptron mlp = new MultilayerPerceptron();
            //MultilayerPerceptron mlp = readFromFile("C:\\Users\\User\\workspace\\Winter_IR_Dogfood\\data\\output\\");
//			System.out.println("network weights and structure are load...");
            mlp.setOptions(Utils.splitOptions(backPropOptions));
            mlp.buildClassifier(trainingset);
            System.out.println("final weights:");
            System.out.println(mlp);
            byte[] binaryNetwork = serialize(mlp);
            writeToFile(binaryNetwork,  "C:\\Users\\User\\workspace\\Winter_IR_Dogfood\\data\\output\\");
            //display actual and forecast values
            System.out.println("\nactual\tprediction");
            for (int i = 0; i < trainingset.numInstances(); i++) {

                double actual = trainingset.instance(i).classValue();
                double prediction =
                        mlp.distributionForInstance(trainingset.instance(i))[0];

                System.out.println(actual + "\t" + prediction);

            }

            //success metrics
            System.out.println("\nSuccess Metrics: ");
            Evaluation eval = new Evaluation(trainingset);
            eval.evaluateModel(mlp, trainingset);


            //display metrics
            //System.out.println("Correlation: " + eval.correlationCoefficient());
            System.out.println("Recall: " + eval.weightedRecall());
            System.out.println("Precision: " + eval.weightedPrecision());
            System.out.println("MAE: " + eval.meanAbsoluteError());
            System.out.println("RMSE: " + eval.rootMeanSquaredError());
            System.out.println("RAE: " + eval.relativeAbsoluteError() + "%");
            System.out.println("RRSE: " + eval.rootRelativeSquaredError() + "%");
            System.out.println("Instances: " + eval.numInstances());
            Date endDate = new Date();

         System.out.println("\nprogram ends in " +(double)(endDate.getTime() - beginDate.getTime())/1000+" seconds\n");

        } catch (Exception ex) {

            System.out.println(ex);

        }
    }


    public static byte[] serialize(Object obj) throws Exception {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();

    }

    public static Object deserialize(byte[] bytes) throws Exception {

        ByteArrayInputStream b = new ByteArrayInputStream(bytes);

        ObjectInputStream o = new ObjectInputStream(b);

        return o.readObject();

    }

    public static void writeToFile(byte[] binaryNetwork, String dumpLocation) throws Exception {

        FileOutputStream stream = new FileOutputStream(dumpLocation + "trained_network2.txt");
        stream.write(binaryNetwork);
        stream.close();

    }

    public static MultilayerPerceptron readFromFile(String dumpLocation) {

        MultilayerPerceptron mlp = new MultilayerPerceptron();

        //binary network is saved to following file
        File file = new File(dumpLocation + "trained_network.txt");

        FileInputStream fileInputStream = null;

        //binary content will be stored in the binaryFile variable
        byte[] binaryFile = new byte[(int) file.length()];

        try {

            fileInputStream = new FileInputStream(file);
            fileInputStream.read(binaryFile);
            fileInputStream.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }

        try {

            mlp = (MultilayerPerceptron) deserialize(binaryFile);

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return mlp;

    }
}
