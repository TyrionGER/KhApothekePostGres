package khApo;

import java.util.Optional;

public record Supplier(
        Id<Supplier> id,
        String name,
        String address,
        String phoneNumber,
        String email) {
}
