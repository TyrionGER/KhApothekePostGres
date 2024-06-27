package khApo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public record Compartment(
        Id <Compartment>id,
        int row,
        String column) {

    Compartment updateWith(
            int newrow,
            String newcolumn
    ) {
        return new Compartment(
                this.id,
                newrow,
                newcolumn
        );
    }
    public static sealed interface Command permits UpdateCompartment,AddCompartment,DeleteCompartment {}
    public static record UpdateCompartment
            (
                    Id<Compartment> id,
                    int row,
                    String column,
                    Date date,
                    Id<Medication> medication)
            implements Command
    {}
    public static record AddCompartment
            (
                    Id<Medication>medication,

                    int row,
                    String column
            ) implements Command
    {}
    public static record DeleteCompartment
            (
                    Id<Compartment> id
            ) implements Command
    {}
    public static record Filter
            (
                    Optional<Integer> row,
                    Optional<String> column)
    {}

    public static interface Operations
    {
        Compartment process(Command cmd) throws Exception;

        List<Compartment> getCompartment(Filter filter);

        Optional<Compartment> getCompartment(Id<Compartment> id);
    }
}



