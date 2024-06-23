import java.lang.ref.Reference;
import java.util.List;
import java.util.Optional;

public record Orderitem(
        Id<Orderitem> id,
        int amount,
        Reference<Medication> medication,
        Orderitem.status status,
        Reference<Order> order,
        String note) {
    enum status {
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
            Id<Medication> id,

            Reference<Order> order,
            status status
    ) {
    }

    Orderitem updateWith(
            int newamount,
            status newstatus,
            String newnote
    ) {
        return new Orderitem(
                this.id,
                newamount,
                this.medication,
                newstatus,
                this.order,
                newnote
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
