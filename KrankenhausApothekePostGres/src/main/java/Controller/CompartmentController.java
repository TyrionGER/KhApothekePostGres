package Controller;

import Entity.Compartment;
import Service.CompartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compartments")
public class CompartmentController {
    @Autowired
    private CompartmentService compartmentService;

    @GetMapping
    public List<Compartment> getAllCompartments() {
        return compartmentService.getAllCompartments();
    }

    @GetMapping("/{id}")
    public Compartment getCompartmentById(@PathVariable Long id) {
        return compartmentService.getCompartmentById(id);
    }

    @PostMapping
    public Compartment createCompartment(@RequestBody Compartment compartment) {
        return compartmentService.saveCompartment(compartment);
    }

    @DeleteMapping("/{id}")
    public void deleteCompartment(@PathVariable Long id) {
        compartmentService.deleteCompartment(id);
    }
}
