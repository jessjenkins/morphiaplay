package uk.jessjenkins.morphiaplay.entities;

import lombok.Builder;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@Data
@Builder
@Entity("records")
public class Record {
    @Id
    private UUID recordId;
    private String name;
}
