package resources.database.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import khApo.*;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public final class DatabaseConnectorTests {

    private static Repository instance;

    @BeforeClass
    public static void init() throws SQLException {
        System.out.println("Initializing repository instance...");
        instance = Repository.loadInstance();
    }

    @Test
    public void testCompartmentLifeCycle() throws Exception {
        System.out.println("Starting testCompartmentLifeCycle...");
        Id<Compartment> id = instance.compartmentId();
        Compartment compartment = new Compartment(id, 2, "B");

        instance.save(compartment);
        Optional<Compartment> loadedCompartment = instance.getCompartment(id);

        if (!loadedCompartment.isPresent()) {
            System.out.println("Failed to load compartment with ID: " + id.value());
        } else {
            System.out.println("Loaded compartment: " + loadedCompartment.get());
        }

        assertTrue("Compartment should be present", loadedCompartment.isPresent());
        assertEquals("Row should match", compartment.row(), loadedCompartment.get().row());
        assertEquals("Column position should match", compartment.column_position(), loadedCompartment.get().column_position());

        instance.deletecompartment(id);
        loadedCompartment = instance.getCompartment(id);
        assertFalse("Compartment should be deleted", loadedCompartment.isPresent());
        System.out.println("Finished testCompartmentLifeCycle.");
    }

    @Test
    public void testOrderLifeCycle() throws Exception {
        System.out.println("Starting testOrderLifeCycle...");
        Id<Order> id = instance.orderId();
        Order order = new Order(id, Reference.to("supplier123"), 100, 50.0);

        instance.save(order);
        Optional<Order> loadedOrder = instance.getOrder(id);

        if (loadedOrder.isPresent()) {
            System.out.println("Loaded order: " + loadedOrder.get());
        } else {
            System.out.println("Failed to load order with ID: " + id.value());
        }

        assertTrue(loadedOrder.isPresent());
        assertEquals(order.supplier(), loadedOrder.get().supplier());
        assertEquals(order.amount(), loadedOrder.get().amount());
        assertEquals(order.price(), loadedOrder.get().price(), 0.01);

        instance.delete(id);
        loadedOrder = instance.getOrder(id);
        assertFalse(loadedOrder.isPresent());
        System.out.println("Finished testOrderLifeCycle.");
    }

    @Test
    public void testOrderitemLifeCycle() throws Exception {
        System.out.println("Starting testOrderitemLifeCycle...");
        Id<Orderitem> id = instance.orderitemId();
        Orderitem orderitem = new Orderitem(id, Orderitem.status.IN_BEARBEITUNG, 10, Reference.to("med123"), Reference.to("order123"));

        instance.save(orderitem);
        Optional<Orderitem> loadedOrderitem = instance.getOrderitem(id);

        if (loadedOrderitem.isPresent()) {
            System.out.println("Loaded orderitem: " + loadedOrderitem.get());
        } else {
            System.out.println("Failed to load orderitem with ID: " + id.value());
        }

        assertTrue(loadedOrderitem.isPresent());
        assertEquals(orderitem.status(), loadedOrderitem.get().status());
        assertEquals(orderitem.amount(), loadedOrderitem.get().amount());
        assertEquals(orderitem.medication(), loadedOrderitem.get().medication());
        assertEquals(orderitem.order(), loadedOrderitem.get().order());

        instance.deleteorderitem(id);
        loadedOrderitem = instance.getOrderitem(id);
        assertFalse(loadedOrderitem.isPresent());
        System.out.println("Finished testOrderitemLifeCycle.");
    }

    @Test
    public void testStockLifeCycle() throws Exception {
        System.out.println("Starting testStockLifeCycle...");
        Id<Stock> id = new Id<>("stock123");
        Stock stock = new Stock(id, 20, new java.sql.Date(System.currentTimeMillis()), Reference.to("med123"), Reference.to("comp123"));

        instance.save(stock);
        Optional<Stock> loadedStock = instance.getStock(id);

        if (loadedStock.isPresent()) {
            System.out.println("Loaded stock: " + loadedStock.get());
        } else {
            System.out.println("Failed to load stock with ID: " + id.value());
        }

        assertTrue(loadedStock.isPresent());
        assertEquals(stock.amount(), loadedStock.get().amount());
        assertEquals(stock.expirationDate().toString(), loadedStock.get().expirationDate().toString());
        assertEquals(stock.medication().id().value(), loadedStock.get().medication().id().value());
        assertEquals(stock.compartment().id().value(), loadedStock.get().compartment().id().value());
        System.out.println("Finished testStockLifeCycle.");
    }

    @Test
    public void testGetMedicationInventory() throws Exception {
        System.out.println("Starting testGetMedicationInventory...");
        List<Map<String, Object>> inventory = instance.getMedicationInventory();
        assertNotNull(inventory);
        for (Map<String, Object> item : inventory) {
            System.out.println("Inventory item: " + item);
        }
        System.out.println("Finished testGetMedicationInventory.");
    }

    @Test
    public void testGetMedicationsWithLowStock() throws Exception {
        System.out.println("Starting testGetMedicationsWithLowStock...");
        int threshold = 10;
        List<Stock> lowstock = instance.getMedicationsWithLowStock(threshold);
        assertNotNull(lowstock);
        for (Stock stock : lowstock) {
            System.out.println("Medication with low stock: " + stock);
        }
        System.out.println("Finished testGetMedicationsWithLowStock.");
    }

    @Test
    public void testGetMedicationsWithExpiredStock() throws Exception {
        System.out.println("Starting testGetMedicationsWithExpiredStock...");
        List<Stock> expiredStock = instance.getMedicationsWithExpiredStock();
        assertNotNull(expiredStock);
        for (Stock stock : expiredStock) {
            System.out.println("Medication with expired stock: " + stock);
        }
        System.out.println("Finished testGetMedicationsWithExpiredStock.");
    }

    // Tests for filters
    @Test
    public void testCompartmentFilter() throws Exception {
        System.out.println("Starting testCompartmentFilter...");
        Compartment.Filter filter = new Compartment.Filter(Optional.of(1), Optional.of("A"));
        List<Compartment> compartments = instance.get(filter);

        for (Compartment comp : compartments) {
            System.out.println("Filtered Compartment: " + comp);
        }

        assertFalse(compartments.isEmpty());
        System.out.println("Finished testCompartmentFilter.");
    }

    @Test
    public void testOrderFilter() throws Exception {
        System.out.println("Starting testOrderFilter...");
        Order.Filter filter = new Order.Filter(Optional.of(Reference.to("supplier123")));
        List<Order> orders = instance.get(filter);

        for (Order order : orders) {
            System.out.println("Filtered Order: " + order);
        }

        assertFalse(orders.isEmpty());
        System.out.println("Finished testOrderFilter.");
    }

    @Test
    public void testOrderitemFilter() throws Exception {
        System.out.println("Starting testOrderitemFilter...");
        Orderitem.Filter filter = new Orderitem.Filter(Optional.of(Reference.to("order123")), Optional.of(Orderitem.status.IN_BEARBEITUNG), Optional.of(Reference.to("med123")));
        List<Orderitem> orderitems = instance.get(filter);

        for (Orderitem orderitem : orderitems) {
            System.out.println("Filtered Orderitem: " + orderitem);
        }

        assertFalse(orderitems.isEmpty());
        System.out.println("Finished testOrderitemFilter.");
    }

    @Test
    public void testStockFilter() throws Exception {
        System.out.println("Starting testStockFilter...");
        Stock.Filter filter = new Stock.Filter(Optional.of(Reference.to("comp123")), Optional.empty(), Optional.of(new Id<>("med123")));
        List<Stock> stocks = instance.get(filter);

        for (Stock stock : stocks) {
            System.out.println("Filtered Stock: " + stock);
        }

        assertFalse(stocks.isEmpty());
        System.out.println("Finished testStockFilter.");
    }
}
