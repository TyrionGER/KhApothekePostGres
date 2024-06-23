public record Medication(
        Id<Medication> id,
        String name,
        String manufacturer,
        String displayName,
        String description,
        String atcCode
) {

}

