package khApo;


import java.util.Date;
import java.util.List;
import java.util.Optional;

public record Order(
        Id<Order> id,
        Reference<Supplier> supplier,
        int amount,
        double price
        ) {
    public static sealed interface Command permits SaveList,SendOrder,DeleteOrder {}
    public static record SaveList
            (
                    Id<Order> id,
                    Reference<Supplier> supplier,
                    int amount,
                    double price
            ) implements Command
    {}
    public static record SendOrder
            (
                    Id<Order> id,
                    Reference<Supplier> supplier,
                    int amount,
                    double price
            ) implements Command
    {}
    public static record DeleteOrder
            (
                    Id<Order>id

            ) implements Command
    {}
    public static record Filter
            (
                    Optional<Reference<Supplier>> supplier

            )
    {}
    public static interface Operations
    {
        Order process(Command cmd) throws Exception;

        List<Order> getOrder(Filter filter);

        Optional<Order> getOrder(Id<Order> id);
    }


}


