package khApo;

import java.util.List;
import java.util.Optional;
import java.util.Date;

//// Die Klasse khApo.PharmacyServiceImpl implementiert das Interface khApo.PharmacyService
final class PharmacyServiceImpl implements PharmacyService {

    private final Repository repository;//khApo.Repository-Instanz, die für Datenzugriff verwendet wird

    // Privater Konstruktor, der das khApo.Repository initialisiert
    private PharmacyServiceImpl(Repository repository) {
        this.repository = repository;
    }

    // Singleton-Instanz von khApo.PharmacyServiceImpl
    private static final PharmacyServiceImpl INSTANCE =
            new PharmacyServiceImpl(
                    Repository.loadInstance()
            );

    public PharmacyServiceImpl instance(){
        return INSTANCE;
    }






    @Override
    public Compartment process(Compartment.Command cmd) throws Exception {
        return switch (cmd) {
            case Compartment.AddCompartment add -> {
                var compartment = new Compartment(
                        repository.compartmentId(),
                        add.row(),
                        add.column()
                );
                repository.save(compartment);
                yield compartment;
            }
            case Compartment.UpdateCompartment update -> {
                var compartment = repository.getCompartment(update.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Message ID"))
                        .updateWith(


                                update.row(),
                                update.column()
                        );
                repository.save(compartment);
                yield compartment;
            }
            case Compartment.DeleteCompartment deleteCompartment -> {
                var deletecompartment = repository.getCompartment(deleteCompartment.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Message ID"));
                repository.deletecompartment(deleteCompartment.id());
                repository.deletecompartment(deleteCompartment.id());
                yield deletecompartment;

            }
        };
    }

    @Override
    public Order process(Order.Command cmd) throws Exception {
        return switch (cmd) {
            case Order.SaveList save -> {
                // Ensure that the referenced khApo.Order exists and is still active
                if (repository.getOrder(save.id()).isPresent()) {
                    var order = new Order(
                            repository.orderId(),
                            save.supplier(),
                            save.amount(),
                            save.price()
                    );
                    repository.save(order);
                    yield order;
                } else {
                    throw new IllegalArgumentException("Invalid khApo.Order reference");
                }
            }
            case Order.SendOrder send -> {
                // Ensure that the referenced khApo.Order exists and is still active
                if (repository.getOrder(send.id()).isPresent()) {
                    // i want that you send the order to the supplier
                    var order = new Order(
                            repository.orderId(),
                            send.supplier(),
                            send.amount(),
                            send.price()
                    );
                    repository.save(order);
                    yield order;

                } else {
                    throw new IllegalArgumentException("Invalid khApo.Order reference");
                }


            }
            case Order.DeleteOrder delete -> {
                var deletedOrder = repository.getOrder(delete.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid khApo.Order ID"));
                repository.delete(delete.id());
                yield deletedOrder;
            }
        };
    }

    @Override
    public Stock process(Stock.Command cmd) throws Exception {
        return switch (cmd) {
            case Stock.UpdateStock update -> {


                var updatestock = repository.getStock(update.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Message ID"))
                        .updateWith(
                                update.name(),
                                update.amount(),
                                update.date()
                        );
                repository.save(updatestock);
                yield updatestock;
            }
        };
    }

    @Override
    public Orderitem process(Orderitem.Command cmd) throws Exception {
        return switch (cmd) {
            case Orderitem.UpdateOrderitem update -> {
                var updateorderitem = repository.getOrderitem(update.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Message ID"))
                        .updateWith(
                                update.amount(),
                                update.status()
                        );
                repository.save(updateorderitem);
                yield updateorderitem;
            }

            case Orderitem.AddOrderitem add -> {

                var orderitem = new Orderitem(
                        repository.orderitemId(),
                        add.status(),
                        add.amount(),
                        add.medication(),
                        add.order()
                );
                repository.save(orderitem);
                yield orderitem;
            }
            case Orderitem.DeleteOrderitem delete -> {
                var orderitem = repository.getOrderitem(delete.id())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Message ID")
                        );
                repository.deleteorderitem(delete.id());
                yield orderitem;
            }
        };
    }

    @Override
    public List<Compartment> getCompartment(Compartment.Filter filter) {
        return repository.get(filter);
    }

    @Override
    public List<Order> getOrder(Order.Filter filter) {
        return repository.get(filter);
    }

    @Override
    public List<Stock> getStock(Stock.Filter filter) {
        return repository.get(filter);
    }

    @Override
    public List<Orderitem> getOrderitem(Orderitem.Filter filter) {
        return repository.get(filter);
    }

    @Override
    public Optional<Compartment> getCompartment(Id<Compartment> id) {
        return repository.getCompartment(id);
    }

    @Override
    public Optional<Order> getOrder(Id<Order> id) {
        return repository.getOrder(id);
    }

    @Override
    public Optional<Stock> getStock(Id<Stock> id) {
        return repository.getStock(id);
    }

    @Override
    public Optional<Orderitem> getOrderitem(Id<Orderitem> id) {
        return repository.getOrderitem(id);
    }





}