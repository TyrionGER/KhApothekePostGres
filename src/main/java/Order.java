import java.lang.ref.Reference;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public record Order(
        Id<Order> id,
        String name,
        int amount,
        Date date,
        Reference<Medication> medication,
        Reference<Supplier> supplier,
        Reference<Compartment> compartment) {
    public static sealed interface Command permits SaveList,SendOrder,DeleteOrder {}
    public static record SaveList
            (
                    Id<Order> id,
                    String name,
                    int amount,
                    Date date,
                    Reference<Medication> medication,
                    Reference<Supplier> supplier,
                    Reference<Compartment> compartment
            ) implements Command
    {}
    public static record SendOrder
            (
                    Id<Order> id,
                    String name,
                    int amount,
                    Reference<Medication> medication,
                    Reference<Supplier> supplier,
                    Reference<Compartment> compartment
            ) implements Command
    {}
    public static record DeleteOrder
            (
                    Id<Order>id,
                    String notes,
                    Date date
            ) implements Command
    {}
    public static record Filter
            (
                    Reference<Medication> medication,
                    Reference<Supplier> supplier,
                    Date date

            )
    {}
    public static interface Operations
    {
        Order process(Command cmd) throws Exception;

        List<Order> getOrder(Filter filter);

        Optional<Order> getOrder(Id<Order> id);
    }


}


