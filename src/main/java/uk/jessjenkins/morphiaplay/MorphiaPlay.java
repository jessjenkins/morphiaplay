package uk.jessjenkins.morphiaplay;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import uk.jessjenkins.morphiaplay.entities.Record;

import java.util.UUID;

public class MorphiaPlay {
    public static void main(String[] args) {
        System.out.println("Playing with morphia…");

        Morphia morphia = new Morphia();

        morphia.mapPackage(Record.class.getPackage().getName());

        final Datastore datastore = morphia.createDatastore(
                new MongoClient(),"morphiaplay");
        datastore.ensureIndexes();


        final Record r1 = Record.builder().recordId(UUID.randomUUID()).name("Jess").build();
        final Record r2 = Record.builder().recordId(UUID.randomUUID()).name("Fred").build();

        datastore.save(r1);
        datastore.save(r2);

        datastore.getCollection(Record.class).drop();
        datastore.getDB().dropDatabase();
    }
}
