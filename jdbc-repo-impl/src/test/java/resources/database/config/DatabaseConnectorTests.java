package resources.database.config;

import org.junit.Test;
import org.junit.BeforeClass;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

import khApo.*;

public final class DatabaseConnectorTests {

    private static Connection conn = null;
    private static DatabaseConnector instance = null;

    @BeforeClass
    public static void init() throws SQLException {
        String user = "postgres";
        String password = "postgres";

        instance = DatabaseConnector.instance(user, password);
    }

    @Test
    public void testCompartmentLifeCycle() throws Exception {
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
    }

    @Test
    public void testOrderLifeCycle() throws Exception {
        Id<Order> id = instance.orderId();
        Order order = new Order(id, Reference.to("supplier123"), 100, 50.0);

        instance.save(order);
        Optional<Order> loadedOrder = instance.getOrder(id);

        assertTrue(loadedOrder.isPresent());
        assertEquals(order.supplier(), loadedOrder.get().supplier());
        assertEquals(order.amount(), loadedOrder.get().amount());
        assertEquals(order.price(), loadedOrder.get().price(), 0.01);

        instance.delete(id);
        loadedOrder = instance.getOrder(id);
        assertFalse(loadedOrder.isPresent());
    }

    @Test
    public void testOrderitemLifeCycle() throws Exception {
        Id<Orderitem> id = instance.orderitemId();
        Orderitem orderitem = new Orderitem(id, Orderitem.status.IN_BEARBEITUNG, 10, Reference.to("med123"), Reference.to("order123"));

        instance.save(orderitem);
        Optional<Orderitem> loadedOrderitem = instance.getOrderitem(id);

        assertTrue(loadedOrderitem.isPresent());
        assertEquals(orderitem.status(), loadedOrderitem.get().status());
        assertEquals(orderitem.amount(), loadedOrderitem.get().amount());
        assertEquals(orderitem.medication(), loadedOrderitem.get().medication());
        assertEquals(orderitem.order(), loadedOrderitem.get().order());

        instance.deleteorderitem(id);
        loadedOrderitem = instance.getOrderitem(id);
        assertFalse(loadedOrderitem.isPresent());
    }

    @Test
    public void testStockLifeCycle() throws Exception {
        Id<Stock> id = new Id<>("stock123");
        Stock stock = new Stock(id, 20, new java.sql.Date(System.currentTimeMillis()), Reference.to("med123"), Reference.to("comp123"));

        instance.save(stock);
        Optional<Stock> loadedStock = instance.getStock(id);

        assertTrue(loadedStock.isPresent());
        assertEquals(stock.amount(), loadedStock.get().amount());
        assertEquals(stock.expirationDate().toString(), loadedStock.get().expirationDate().toString());
        assertEquals(stock.medication().id().value(), loadedStock.get().medication().id().value());
        assertEquals(stock.compartment().id().value(), loadedStock.get().compartment().id().value());
    }

    @Test
    public void testGetMedicationInventory() throws Exception {
        List<Map<String, Object>> inventory = instance.getMedicationInventory();
        assertNotNull(inventory);
        for (Map<String, Object> item : inventory) {
            System.out.println("Inventory item: " + item);
        }
    }

    @Test
    public void testGetMedicationsWithLowStock() throws Exception {
        int threshold = 10;
        List<Medication> medications = instance.getMedicationsWithLowStock(threshold);
        assertNotNull(medications);
        for (Medication med : medications) {
            System.out.println("Medication with low stock: " + med);
        }
    }

    @Test
    public void testGetMedicationsWithExpiredStock() throws Exception {
        List<Medication> medications = instance.getMedicationsWithExpiredStock();
        assertNotNull(medications);
        for (Medication med : medications) {
            System.out.println("Medication with expired stock: " + med);
        }
    }

    // Tests for filters
    @Test
    public void testCompartmentFilter() throws Exception {
        Compartment.Filter filter = new Compartment.Filter(Optional.of(1), Optional.of("A"));
        List<Compartment> compartments = instance.get(filter);

        for (Compartment comp : compartments) {
            System.out.println("Filtered Compartment: " + comp);
        }

        assertFalse(compartments.isEmpty());
    }

    @Test
    public void testOrderFilter() throws Exception {
        Order.Filter filter = new Order.Filter(Optional.of(Reference.to("supplier123")));
        List<Order> orders = instance.get(filter);

        for (Order order : orders) {
            System.out.println("Filtered Order: " + order);
        }

        assertFalse(orders.isEmpty());
    }

    @Test
    public void testOrderitemFilter() throws Exception {
        Orderitem.Filter filter = new Orderitem.Filter(Optional.of(Reference.to("order123")), Optional.of(Orderitem.status.IN_BEARBEITUNG), Optional.of(Reference.to("med123")));
        List<Orderitem> orderitems = instance.get(filter);

        for (Orderitem orderitem : orderitems) {
            System.out.println("Filtered Orderitem: " + orderitem);
        }

        assertFalse(orderitems.isEmpty());
    }

    @Test
    public void testStockFilter() throws Exception {
        Stock.Filter filter = new Stock.Filter(Optional.of(Reference.to("comp123")), Optional.empty(), Optional.of(new Id<>("med123")));
        List<Stock> stocks = instance.get(filter);

        for (Stock stock : stocks) {
            System.out.println("Filtered Stock: " + stock);
        }

        assertFalse(stocks.isEmpty());
    }
}
