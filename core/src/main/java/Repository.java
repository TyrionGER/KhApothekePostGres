import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public interface Repository {



    Optional<Compartment> getCompartment(Id<Compartment> id);

    void save(Stock stck) throws Exception;
    Optional<Stock> getStock(Id<Stock> id);

    void delete(Id<Order> id);
    void deletecompartment(Id<Compartment>id);

    List<Stock> get(Stock.Filter filter);

    Id<Compartment> compartmentId();

    void save(Compartment com) throws Exception;

    List<Compartment> get(Compartment.Filter filter);

    void save(Orderitem ordi) throws Exception;

    List<Orderitem> get(Orderitem.Filter filter);


    Id<Orderitem> orderitemId();
    Optional<Orderitem> getOrderitem(Id<Orderitem> id);

    void save(Order ord) throws Exception;

    List<Order> get(Order.Filter filter);

    Optional<Order> getOrder(Id<Order> id);

    Id<Order> orderId();

    void deleteorderitem(Id<Orderitem> id);




    public static interface Provider {
        Repository instance();

    }

    public static Repository loadInstance() {
        return
                ServiceLoader.load(Provider.class)
                        .iterator()
                        .next()
                        .instance();


    }
}

