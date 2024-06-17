package Entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Compartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int row;
    private String column;

    // Getters and Setters
}
