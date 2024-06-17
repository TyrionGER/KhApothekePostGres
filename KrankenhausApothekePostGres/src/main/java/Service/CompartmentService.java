package Service;

import Entity.Compartment;
import Repository.CompartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompartmentService {
    @Autowired
    private CompartmentRepository compartmentRepository;

    public List<Compartment> getAllCompartments() {
        return compartmentRepository.findAll();
    }

    public Compartment getCompartmentById(Long id) {
        return compartmentRepository.findById(id).orElse(null);
    }

    public Compartment saveCompartment(Compartment compartment) {
        return compartmentRepository.save(compartment);
    }

    public void deleteCompartment(Long id) {
        compartmentRepository.deleteById(id);
    }
}
