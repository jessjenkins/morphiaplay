package uk.jessjenkins.morphiaplay;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.UpdateOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import uk.jessjenkins.morphiaplay.entities.Record;
import uk.jessjenkins.morphiaplay.entities.SubRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class MorphiaPlayUpsert {

    public static void main(String[] args) {
        System.out.println("Playing with morphia…");

        Morphia morphia = new Morphia();

        morphia.mapPackage(Record.class.getPackage().getName());

        final Datastore datastore = morphia.createDatastore(
                new MongoClient(),"morphiaplay");
        //datastore.ensureIndexes();


        UUID bobID = UUID.randomUUID();
        UUID daveID = UUID.randomUUID();


        Record r = Record.builder()
                .recordId(bobID)
                .name("Bob")
                .build();
        datastore.save(r);


        Stream.of(bobID,daveID).forEach(uuid -> {
            Query<Record> query = datastore.createQuery(Record.class)
                .field("recordId").equal(uuid);

            UpdateOperations<Record> ops;
            ops = datastore.createUpdateOperations(Record.class)
                    .set("recordId",uuid)
                    .setOnInsert("name", "Upserted");

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true);
            UpdateResults updateResults = datastore.update(
                    query,
                    ops,
                    updateOptions
            );


            System.out.printf("Update success, inserted [%s] updated [%s]\n",updateResults.getInsertedCount(),updateResults.getUpdatedCount());
        });


        datastore.getDB().dropDatabase();
    }
}
