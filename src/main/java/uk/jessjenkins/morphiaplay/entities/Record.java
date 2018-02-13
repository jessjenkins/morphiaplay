package uk.jessjenkins.morphiaplay.entities;

import lombok.Builder;
import lombok.Data;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@Data
@Builder
public class Record {
    @Id
    private UUID recordId;
    private String name;
}
