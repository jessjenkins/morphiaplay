package uk.jessjenkins.morphiaplay.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.UUID;

@Data
@Builder
@Entity("records")
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    private UUID recordId;
    private String name;
    @Embedded
    private ArrayList<SubRecord> subRecords;
}
