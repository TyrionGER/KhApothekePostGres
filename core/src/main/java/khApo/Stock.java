package khApo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public record Stock(
        Id<Stock> id,
        int amount,
        Date expirationDate,

        Reference<Medication> medication,
        Reference<Compartment> compartment) {

    Stock updateWith(
            String newname,
            int newamount,
            Date newdate
    ) {
        return new Stock(
                this.id,
                newamount,
                newdate,
                this.medication,
                this.compartment
        );
    }

    public static sealed interface Command permits UpdateStock {}

    public static record UpdateStock
            (
                    Id<Stock> id,
                    String name,
                    int amount,
                    Date date,
                    String note


            ) implements Command
    {}

    public static record Filter
            (
                    Optional<Reference<Compartment>> compartment,
                    Optional<Medication> amount,
                    Optional<Id<Medication>> id)
    {}
    public static interface Operations
    {
        Stock process(Command cmd) throws Exception;

        List<Stock> getStock(Filter filter);

        Optional<Stock> getStock(Id<Stock> id);
    }
}


