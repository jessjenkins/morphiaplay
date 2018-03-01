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


        // Create the morphia boilerplate
        // With default options, this will connect to a locally running mongodb listening on 27017
        // To spin up a server with docker simply run…
        // `docker run --name jessmongo --publish 127.0.0.1:27017:27017 mongo`
        Morphia morphia = new Morphia();

        // Map a data class with mophia
        morphia.mapPackage(Record.class.getPackage().getName());

        // Create a datastore with the name of the database we want to be created
        final Datastore datastore = morphia.createDatastore(
                new MongoClient(),"morphiaplay");


        // Create a couple of known IDs
        UUID bobID = UUID.randomUUID();
        UUID daveID = UUID.randomUUID();

        // Save the first one explicitly
        Record r = Record.builder()
                .recordId(bobID)
                .name("Bob")
                .build();
        datastore.save(r);

        // Now save both using upserts. Note bob will be updated but dave will be inserted.
        Stream.of(bobID,daveID).forEach(uuid -> {

            // Set up a query of what to go looking for.
            // Anything found will be updated but if there are no results an insert will occur
            Query<Record> query = datastore.createQuery(Record.class)
                .field("recordId").equal(uuid);

            // What fields to be created/updated (note the ones only set on inserts)
            UpdateOperations<Record> ops;
            ops = datastore.createUpdateOperations(Record.class)
                    .set("recordId",uuid)
                    .setOnInsert("name", "Upserted");

            // Turn on upserting
            UpdateOptions updateOptions = new UpdateOptions().upsert(true);

            // Run the update
            UpdateResults updateResults = datastore.update(
                    query,
                    ops,
                    updateOptions
            );

            // Take a look at the results to see if we inserted or updated
            System.out.printf("%s success, inserted [%s] updated [%s]\n",
                    updateResults.getUpdatedExisting() ? "Update" : "Insert",
                    updateResults.getInsertedCount(),
                    updateResults.getUpdatedCount());
        });


        // Clean up after ourselves.
        // Note you may want to put a breakpoint here to see the data using Robo3T or similar before it's deleted
        datastore.getDB().dropDatabase();
    }
}
