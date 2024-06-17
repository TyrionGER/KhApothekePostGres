package Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compartment_id")
    private Compartment compartment;

    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private int amount;
    private Date expirationDate;

    // Getters and Setters
}
