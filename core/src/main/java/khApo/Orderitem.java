package khApo;


import java.util.List;
import java.util.Optional;

public record Orderitem(
        Id<Orderitem> id,
        Orderitem.status status,
        int amount,
        Reference<Medication> medication,
        Reference<Order> order) {
    public enum status {
        IN_BEARBEITUNG("in bearbeitung"),
        BESTELLUNG_AUFGEGEBEN("Bestellung aufgegeben"),
        BESTELLUNG_AKZEPTIERT("Bestellung akzeptiert"),
        WIRD_GELIEFERT("wird geliefert"),
        NICHT_ZUSTELLBAR("nicht zustellbar"),
        ANGEKOMMEN("angekommen");

        private final String status;

        status(String status) {
            this.status = status;
        }
    }
    public static record Filter(
            Optional<Reference<Order>> order,
            Optional<status> status,
            Optional<Reference<Medication>> medication
    ) {
    }

    Orderitem updateWith(
            int newamount,
            status newstatus
    ) {
        return new Orderitem(
                this.id,
                newstatus,
                newamount,
                this.medication,
                this.order
        );
    }

    public static sealed interface Command permits UpdateOrderitem,AddOrderitem,DeleteOrderitem {}
    public static record UpdateOrderitem(
            Id<Orderitem> id,
            int amount,

            status status,

            String note
    ) implements Command {
    }
    public static record AddOrderitem(
            Id<Orderitem> id,
            int amount,
            Reference<Medication> medication,
            status status,
            Reference<Order> order,
            String note

    ) implements Command {
    }
    public static record DeleteOrderitem(
            Id<Orderitem> id,

            Reference<Medication> medication
    ) implements Command {
    }


    public static interface Operations
    {
        Orderitem process(Command cmd) throws Exception;

        List<Orderitem> getOrderitem(Filter filter);

        Optional<Orderitem> getOrderitem(Id<Orderitem> id);
    }


}
