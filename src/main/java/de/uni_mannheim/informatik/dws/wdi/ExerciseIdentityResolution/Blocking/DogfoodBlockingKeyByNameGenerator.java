package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Dogfood;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class DogfoodBlockingKeyByNameGenerator extends
        RecordBlockingKeyGenerator<Dogfood, Attribute> {
    private static final long serialVersionUID = 1L;
    //TODO try the blocking
    @Override
    public void generateBlockingKeys(Dogfood record, Processable<Correspondence<Attribute, Matchable>> correspondences,
                                     DataIterator<Pair<String, Dogfood>> resultCollector) {

            String[] tokens  = record.getProductname().split(" ");

            String blockingKeyValue = "";

            for(int i = 0; i <= 2 && i < tokens.length; i++) {
                blockingKeyValue += tokens[i].substring(0, Math.min(2,tokens[i].length())).toUpperCase();
            }

            resultCollector.next(new Pair<>(blockingKeyValue, record));

    }
}
