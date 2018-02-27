package uk.jessjenkins.morphiaplay;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import uk.jessjenkins.morphiaplay.entities.Record;
import uk.jessjenkins.morphiaplay.entities.SubRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class MorphiaPlay {

    public static void main(String[] args) {
        System.out.println("Playing with morphia…");

        Morphia morphia = new Morphia();

        morphia.mapPackage(Record.class.getPackage().getName());

        final Datastore datastore = morphia.createDatastore(
                new MongoClient(),"morphiaplay");
        //datastore.ensureIndexes();


        Stream.of("Jess", "Fred", "Bob").forEach(
                s -> {
                    final ArrayList<SubRecord> al = new ArrayList<>();
                    Stream.of("A","B","C","J","F")
                            .filter(a -> !s.startsWith(a))
                            .forEach(
                                a -> al.add(SubRecord.builder().a(a).build())
                            );
                    Record r = Record.builder()
                            .recordId(UUID.randomUUID())
                            .name(s)
                            .subRecords(s.equals("Bob")? null : al)
                            .build();
                    datastore.save(r);
                }
        );


        //Query<SubRecord> subRecordQuery = datastore.createQuery(SubRecord.class);
        //subRecordQuery.field("a").equalIgnoreCase("J");


        Query<Record> query = datastore.createQuery(Record.class);
        query.field("subRecords.a").equalIgnoreCase("X");

        List<Record> foundRecords = query.asList();
        foundRecords.stream()
                .forEach(record -> System.out.println(record.getName()));

        UpdateOperations<Record> ops;
        ops = datastore.createUpdateOperations(Record.class)
                //.disableValidation()
                //.enableValidation()
                .set("subRecords.$.bool",Boolean.TRUE);

        UpdateResults results = datastore.update(query, ops);

        //datastore.getCollection(Record.class).drop();
        datastore.getDB().dropDatabase();
    }
}
