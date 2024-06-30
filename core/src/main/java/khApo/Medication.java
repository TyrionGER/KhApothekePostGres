package khApo;

public record Medication(
        Id<Medication> id,
        String displayName,
        String atcCode
) {

}

