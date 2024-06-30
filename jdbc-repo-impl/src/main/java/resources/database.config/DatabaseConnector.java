package resources.database.config;

import java.sql.*;
import java.util.*;

import khApo.*;

import static resources.database.config.SQL.*;

public class DatabaseConnector implements Repository {
    private final Connection conn;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/KhApoDB";

    DatabaseConnector(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public static DatabaseConnector instance(String username, String password) {
        try {
            var conn = DriverManager.getConnection(
                    DB_URL,
                    username,
                    password
            );

            var connector = new DatabaseConnector(conn);
            connector.setup();
            return connector;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String CREATE_TABLE_MEDICATION = """
    CREATE TABLE IF NOT EXISTS Medication (
        id          varchar not null
            constraint medication_pk
                primary key,
        displayname varchar,
        atc         integer
    );
    """;

    private static final String CREATE_TABLE_COMPARTMENT = """
    CREATE TABLE IF NOT EXISTS Compartment (
        id       varchar not null
            constraint compartment_pk
                primary key,
        row      integer not null,
        column_position  varchar not null
    );
    """;

    private static final String CREATE_TABLE_SUPPLIER = """
    CREATE TABLE IF NOT EXISTS Supplier (
        id       varchar not null
            constraint supplier_pk
                primary key,
        name     varchar,
        address  varchar,
        mail     varchar,
        telefone varchar
    );
    """;

    private static final String CREATE_TABLE_STOCK = """
    CREATE TABLE IF NOT EXISTS Stock (
        id             varchar not null
            constraint stock_pk
                primary key,
        amount         integer,
        expirationdate date,
        medication_id  varchar
            constraint stock_medication_id_fk
                references Medication(id),
        compartment_id varchar
            constraint stock_compartment_id_fk
                references Compartment(id)
    );
    """;

    private static final String CREATE_TABLE_ORDER = """
    CREATE TABLE IF NOT EXISTS "Order" (
        id           varchar not null
            constraint order_pk
                primary key,
        supplier_id  varchar not null
            constraint order_supplier_id_fk
                references Supplier(id),
        amount       integer,
        price        numeric
    );
    """;

    private static final String CREATE_TABLE_ORDERITEM = """
    CREATE TABLE IF NOT EXISTS Orderitem (
        id            varchar not null
            constraint orderitem_pk
                primary key,
        status        varchar not null,
        amount        integer not null,
        medication_id varchar not null
            constraint orderitem_medication_id_fk
                references Medication(id),
        order_id      varchar not null
            constraint orderitem_order_id_fk
                references "Order"(id)
    );
    """;

    private void setup() {
        try (var stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_MEDICATION);
            stmt.execute(CREATE_TABLE_COMPARTMENT);
            stmt.execute(CREATE_TABLE_SUPPLIER);
            stmt.execute(CREATE_TABLE_STOCK);
            stmt.execute(CREATE_TABLE_ORDER);
            stmt.execute(CREATE_TABLE_ORDERITEM);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (var stmt = conn.createStatement()) {
            // Insert Medication data
            stmt.executeUpdate("INSERT INTO Medication (id, displayname, atc) VALUES ('med123', 'Medication1', 123456)");
            stmt.executeUpdate("INSERT INTO Medication (id, displayname, atc) VALUES ('med124', 'Medication2', 123457)");
            stmt.executeUpdate("INSERT INTO Medication (id, displayname, atc) VALUES ('med125', 'Medication3', 123458)");
            stmt.executeUpdate("INSERT INTO Medication (id, displayname, atc) VALUES ('med126', 'Medication4', 123459)");
            stmt.executeUpdate("INSERT INTO Medication (id, displayname, atc) VALUES ('med127', 'Medication5', 123460)");

            // Insert Supplier data
            stmt.executeUpdate("INSERT INTO Supplier (id, name, address, mail, telefone) VALUES ('supplier123', 'Supplier1', 'Address1', 'email1@example.com', '123456789')");
            stmt.executeUpdate("INSERT INTO Supplier (id, name, address, mail, telefone) VALUES ('supplier124', 'Supplier2', 'Address2', 'email2@example.com', '987654321')");
            stmt.executeUpdate("INSERT INTO Supplier (id, name, address, mail, telefone) VALUES ('supplier125', 'Supplier3', 'Address3', 'email3@example.com', '123123123')");
            stmt.executeUpdate("INSERT INTO Supplier (id, name, address, mail, telefone) VALUES ('supplier126', 'Supplier4', 'Address4', 'email4@example.com', '321321321')");
            stmt.executeUpdate("INSERT INTO Supplier (id, name, address, mail, telefone) VALUES ('supplier127', 'Supplier5', 'Address5', 'email5@example.com', '456456456')");

            // Insert Compartment data
            stmt.executeUpdate("INSERT INTO Compartment (id, row, column_position) VALUES ('comp123', 1, 'A')");
            stmt.executeUpdate("INSERT INTO Compartment (id, row, column_position) VALUES ('comp124', 2, 'B')");
            stmt.executeUpdate("INSERT INTO Compartment (id, row, column_position) VALUES ('comp125', 3, 'C')");
            stmt.executeUpdate("INSERT INTO Compartment (id, row, column_position) VALUES ('comp126', 4, 'D')");
            stmt.executeUpdate("INSERT INTO Compartment (id, row, column_position) VALUES ('comp127', 5, 'E')");

            // Insert Stock data
            stmt.executeUpdate("INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES ('stock123', 10, '2023-12-31', 'med123', 'comp123')");
            stmt.executeUpdate("INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES ('stock124', 5, '2023-11-30', 'med124', 'comp124')");
            stmt.executeUpdate("INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES ('stock125', 20, '2024-01-15', 'med125', 'comp125')");
            stmt.executeUpdate("INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES ('stock126', 15, '2023-10-20', 'med126', 'comp126')");
            stmt.executeUpdate("INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES ('stock127', 25, '2024-03-10', 'med127', 'comp127')");

            // Insert Order data
            stmt.executeUpdate("INSERT INTO \"Order\" (id, supplier_id, amount, price) VALUES ('order123', 'supplier123', 100, 50.0)");
            stmt.executeUpdate("INSERT INTO \"Order\" (id, supplier_id, amount, price) VALUES ('order124', 'supplier124', 50, 25.0)");
            stmt.executeUpdate("INSERT INTO \"Order\" (id, supplier_id, amount, price) VALUES ('order125', 'supplier125', 75, 37.5)");
            stmt.executeUpdate("INSERT INTO \"Order\" (id, supplier_id, amount, price) VALUES ('order126', 'supplier126', 200, 100.0)");
            stmt.executeUpdate("INSERT INTO \"Order\" (id, supplier_id, amount, price) VALUES ('order127', 'supplier127', 150, 75.0)");

            // Insert Orderitem data
            stmt.executeUpdate("INSERT INTO Orderitem (id, status, amount, medication_id, order_id) VALUES ('orderitem123', 'IN_BEARBEITUNG', 10, 'med123', 'order123')");
            stmt.executeUpdate("INSERT INTO Orderitem (id, status, amount, medication_id, order_id) VALUES ('orderitem124', 'BESTELLUNG_AUFGEGEBEN', 5, 'med124', 'order124')");
            stmt.executeUpdate("INSERT INTO Orderitem (id, status, amount, medication_id, order_id) VALUES ('orderitem125', 'BESTELLUNG_AKZEPTIERT', 15, 'med125', 'order125')");
            stmt.executeUpdate("INSERT INTO Orderitem (id, status, amount, medication_id, order_id) VALUES ('orderitem126', 'WIRD_GELIEFERT', 20, 'med126', 'order126')");
            stmt.executeUpdate("INSERT INTO Orderitem (id, status, amount, medication_id, order_id) VALUES ('orderitem127', 'ANGEKOMMEN', 25, 'med127', 'order127')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exists(String tableName, String id) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void save(Compartment compartment) throws SQLException {
        var query = exists("Compartment", compartment.id().value()) ?
                UPDATE("Compartment")
                        .WHERE("id", compartment.id().value())
                        .SET("row", compartment.row())
                        .SET("column_position", compartment.column_position())
                :
                INSERT_INTO("Compartment")
                        .VALUE("id", compartment.id().value())
                        .VALUE("row", compartment.row())
                        .VALUE("column_position", compartment.column_position());

        try (var stmt = conn.createStatement()) {
            System.out.println("Executing query: " + query.toString()); // Debugging statement
            stmt.executeUpdate(query.toString());
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query.toString());
            e.printStackTrace();
            throw e;
        }
    }







    @Override
    public void save(Orderitem orderitem) throws SQLException {
        var query =
                exists("Orderitem", orderitem.id().value()) ?
                        UPDATE("Orderitem")
                                .WHERE("id", orderitem.id().value())
                                .SET("status", orderitem.status())
                                .SET("medication_id", orderitem.medication().id().value())
                                .SET("amount", orderitem.amount())
                                .SET("order_id", orderitem.order().id().value())
                        :

                        INSERT_INTO("Orderitem")
                                .VALUE("id", orderitem.id().value())
                                .VALUE("amount", orderitem.amount())
                                .VALUE("status", orderitem.status())
                                .VALUE("medication_id", orderitem.medication().id().value())
                                .VALUE("order_id", orderitem.order().id().value());

        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(query.toString());
        }
    }




    @Override
    public void save(Order order) throws SQLException {
        var query =
                exists("\"Order\"", order.id().value()) ?
                        UPDATE("\"Order\"")
                                .WHERE("id", order.id())
                                .SET("supplier_id", order.supplier().id().value())
                                .SET("amount", order.amount())
                                .SET("price", order.price())
                        :
                        INSERT_INTO("\"Order\"")
                                .VALUE("id", order.id().value())
                                .VALUE("supplier_id", order.supplier().id().value())
                                .VALUE("amount", order.amount())
                                .VALUE("price", order.price());

        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(query.toString());
        }
    }


    @Override
    public void save(Stock stock) throws SQLException {
        String query;

        if (exists("Stock", stock.id().value())) {
            query = UPDATE("Stock")
                    .WHERE("id", stock.id().value())
                    .SET("amount", stock.amount())
                    .SET("expirationdate", stock.expirationDate())
                    .SET("medication_id", stock.medication().id().value())  // Ensure correct ID format
                    .SET("compartment_id", stock.compartment().id().value())
                    .toString();
        } else {
            query = INSERT_INTO("Stock")
                    .VALUE("id", stock.id().value())
                    .VALUE("amount", stock.amount())
                    .VALUE("expirationdate", stock.expirationDate())
                    .VALUE("medication_id", stock.medication().id().value())  // Ensure correct ID format
                    .VALUE("compartment_id", stock.compartment().id().value())
                    .toString();
        }

        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
    }



    @Override
    public void deletecompartment(Id<Compartment> id) throws SQLException {
        var query = DELETE_FROM(" compartment")
                .WHERE("id", id.value())
                .toString();

        try (var stmt = conn.createStatement()) {
            System.out.println("Executing query: " + query); // Debugging statement
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteorderitem(Id<Orderitem> id) {
        var query = DELETE_FROM(" Orderitem").WHERE("id", id.value()).toString();
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Id<Order> id) {
        var query = DELETE_FROM("\"Order\"").WHERE("id", id.value()).toString();
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Compartment readCompartment(ResultSet result) throws SQLException {
        return new Compartment(
                new Id<>(result.getString("id")),
                result.getInt("row"),
                result.getString("column_position")
        );
    }


    public Order readOrder(ResultSet result) throws SQLException {
        return new Order(
                new Id<>(result.getString("id")),
                Reference.to(result.getString("supplier_id")),
                result.getInt("amount"),
                result.getDouble("price")
        );
    }

    @Override
    public Optional<Order> getOrder(Id<Order> id) {
        var query =
                SQL.SELECT("*")
                        .FROM("\"Order\"")
                        .WHERE("id", id.value())
                        .toString();

        try (
                var result = conn.createStatement().executeQuery(query)
        ) {
            return result.next() ?
                    Optional.of(readOrder(result)) :
                    Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Stock readStock(ResultSet result) throws SQLException {
        return new Stock(
            new Id<>(result.getString("id")),
            result.getInt("amount"),
            result.getDate("expirationdate"),
            Reference.to(result.getString("medication_id")),
            Reference.to(result.getString("compartment_id"))
        );
    }

    @Override
    public Optional<Stock> getStock(Id<Stock> id) {
        var query =
            SQL.SELECT("*")
                .FROM("Stock")
                .WHERE("id", id.value())
                .toString();

        try (
            var result = conn.createStatement().executeQuery(query)
        ) {
            return result.next() ?
                Optional.of(readStock(result)) :
                Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    public Orderitem readOrderitem(ResultSet result) throws SQLException {
        return new Orderitem(
            new Id<>(result.getString("id")),
            Orderitem.status.valueOf(result.getString("status")),
                result.getInt("amount"),
            Reference.to(result.getString("medication_id")),
            Reference.to(result.getString("order_id"))
        );
    }


    @Override
    public Optional<Orderitem> getOrderitem(Id<Orderitem> id) {
        var query =
            SQL.SELECT("*")
                .FROM(" Orderitem")
                .WHERE("id", id.value())
                .toString();

        try (
            var result = conn.createStatement().executeQuery(query)
        ) {
            return result.next() ?
                Optional.of(readOrderitem(result)) :
                Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Compartment> getCompartment(Id<Compartment> id) {
        var query = SQL.SELECT("*")
                .FROM("Compartment")
                .WHERE("id", id.value())
                .toString();

        try (var stmt = conn.createStatement();
             var result = stmt.executeQuery(query)) {
            System.out.println("Executing query: " + query); // Debugging statement
            if (result.next()) {
                return Optional.of(readCompartment(result));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Error executing query: " + query);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Id<Compartment> compartmentId() {



        return new Id<>(java.util.UUID.randomUUID().toString());
    }

    @Override
    public Id<Order> orderId() {
        return new Id<>(java.util.UUID.randomUUID().toString());
    }

    @Override
    public Id<Orderitem> orderitemId() {
        return new Id<>(java.util.UUID.randomUUID().toString());
    }

    @Override
    public List<Compartment> get(Compartment.Filter filter) {
        var query = SQL.SELECT("*")
                .FROM("Compartment")
                .ORDER_BY("id");

        filter.row().ifPresent(
                ref -> query.WHERE("row", ref));
        filter.column().ifPresent(
                ref -> query.WHERE("column_position", ref));


        try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
            var compartments = new ArrayList<Compartment>();

            while (resultSet.next()) {
                compartments.add(readCompartment(resultSet));
            }

            return compartments;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Order> get(Order.Filter filter) {
        var query = SELECT("*")
                .FROM("\"Order\"")
                .ORDER_BY("price");

        filter.supplier().ifPresent(
                ref -> query.WHERE("supplier_id", ref.id().value()));


        try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
            var orders = new ArrayList<Order>();

            while (resultSet.next()) {
                orders.add(readOrder(resultSet));
            }

            return orders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

@Override
public List<Stock> get(Stock.Filter filter) {
    var query = SQL.SELECT("*")
        .FROM("Stock")
        .ORDER_BY("expirationdate");
    filter.compartment().ifPresent(
            ref -> query.WHERE("compartment_id", ref.id().value()));

    filter.amount().ifPresent(
            ref -> query.WHERE("amount", ref.id().value()));

    filter.id().ifPresent(
            id -> query.WHERE("medication_id", id.value()));




    try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
        var stocks = new ArrayList<Stock>();

        while (resultSet.next()) {
            stocks.add(readStock(resultSet));
        }

        return stocks;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}


@Override
public List<Orderitem> get(Orderitem.Filter filter) {
    var query = SQL.SELECT("*")
        .FROM("Orderitem")
        .ORDER_BY("id");

    filter.order().ifPresent(
            ref -> query.WHERE("order_id", ref.id().value()));

    filter.status().ifPresent(
            status -> query.WHERE("status", status.name()));
    filter.medication().ifPresent(
            ref -> query.WHERE("medication_id", ref.id().value()));

    try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
        var orderitems = new ArrayList<Orderitem>();

        while (resultSet.next()) {
            orderitems.add(readOrderitem(resultSet));
        }

        return orderitems;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

public List<Map<String, Object>> getMedicationInventory() {
    String query = "SELECT m.displayname, c.row, c.column_position, s.amount " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "JOIN Compartment c ON s.compartment_id = c.id";

    try (var resultSet = conn.createStatement().executeQuery(query)) {
        List<Map<String, Object>> inventory = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("displayname", resultSet.getString("displayname"));
            entry.put("row", resultSet.getInt("row"));
            entry.put("column_position", resultSet.getString("column_position"));
            entry.put("amount", resultSet.getInt("amount"));
            inventory.add(entry);
        }

        return inventory;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

//TODO: Implement the following methods

public List<Medication> getMedicationsWithLowStock(int threshold) {
    String query = "SELECT m.id, m.displayname, s.amount " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "WHERE s.amount < ?";

    List<Medication> medicationsWithLowStock = new ArrayList<>();

    return medicationsWithLowStock;
}

public List<Medication> getMedicationsWithExpiredStock() {
    String query = "SELECT m.id, m.displayname, s.expirationdate " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "WHERE s.expirationdate < CURRENT_DATE";

    List<Medication> medicationsWithExpiredStock = new ArrayList<>();

    return medicationsWithExpiredStock;


}
}
