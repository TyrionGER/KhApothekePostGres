package khApo;

import java.util.Optional;


public record Reference<T>
        (
                Id<T> id,
                Optional<String> display
        )
{

    public Reference<T> withDisplay(String d){
        return new Reference<>(
                this.id,
                Optional.of(d)
        );
    }

    public static <T> Reference<T> to(String id){
        return new Reference<>(
                new Id<>(id),
                Optional.empty()
        );
    }

}
