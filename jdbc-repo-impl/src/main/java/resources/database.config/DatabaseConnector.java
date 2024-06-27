import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public class DatabaseConnector implements Repository {
    private final Connection conn;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/KhApoDB";

    private DatabaseConnector(Connection conn) {
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

     private static final String CREATE_TABLE_COMPARTMENT = """
            CREATE TABLE IF NOT EXISTS Compartment (
                id       varchar not null
                    constraint compartment_pk
                        primary key,
                row      integer not null,
                column  varchar not null
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
                        references medication,
                compartment_id varchar
                    constraint stock_compartment_id_fk
                        references compartment
            );
            """;
    private static final String CREATE_TABLE_ORDERITEM = """
        CREATE TABLE IF NOT EXISTS orderitem (
            id            varchar not null
                constraint orderitem_pk
                    primary key,
            status        varchar not null,
            medication_id varchar not null
                constraint orderitem_medication_id_fk
                    references medication,
            date          date    not null,
            amount        integer not null
            );
            """;
    private static final String CREATE_TABLE_ORDER = """
        CREATE TABLE IF NOT EXISTS order (
            id           varchar not null
                constraint order_pk
                    primary key,
            orderitem_id varchar not null
                constraint order_orderitem_id_fk
                    references orderitem,
            supplier_id  varchar not null
                constraint order_supplier_id_fk
                    references supplier,
            amount       integer,
            price        numeric not null
            );
            """;

    private static final String CREATE_TABLE_MEDICATION = """
        CREATE TABLE IF NOT EXISTS medication (
            id          varchar not null
                constraint medication_pk
                    primary key,
            displayname varchar,
            manufacturer varchar,

            atc         integer
            );
            """;

    private static final String CREATE_TABLE_SUPPLIER = """
        CREATE TABLE IF NOT EXISTS supplier (
            id       varchar not null
                constraint supplier_pk
                    primary key,
            name     varchar,
            adress   varchar,
            mail     varchar,
            telefone varchar
            );
            """;


    private void setup() {
        try (var stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_COMPARTMENT);
            stmt.execute(CREATE_TABLE_STOCK);
            stmt.execute(CREATE_TABLE_ORDERITEM);
            stmt.execute(CREATE_TABLE_ORDER);
            stmt.execute(CREATE_TABLE_SUPPLIER);
            stmt.execute(CREATE_TABLE_MEDICATION);

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
        if (exists("Compartment", compartment.id().value())) {
            String sql = "UPDATE Compartment SET row = ?, column = ?, medication_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, compartment.row());
                pstmt.setString(2, compartment.column());
                pstmt.setString(3, compartment.medication().value());
                pstmt.setString(4, compartment.id().value());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO Compartment (id, row, column, medication_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, compartment.id().value());
                pstmt.setInt(2, compartment.row());
                pstmt.setString(3, compartment.column());
                pstmt.setString(4, compartment.medication().value());
                pstmt.executeUpdate();
            }
        }
    }

    @Override
    public void save(Orderitem orderitem) throws SQLException {
        if (exists("Orderitem", orderitem.id().value())) {
            String sql = "UPDATE Orderitem SET amount = ?, status = ?, note = ?, medication_id = ?, order_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, orderitem.amount());
                pstmt.setString(2, orderitem.status().name());
                pstmt.setString(3, orderitem.note());
                pstmt.setString(4, orderitem.medication().value());
                pstmt.setString(5, orderitem.order().value());
                pstmt.setString(6, orderitem.id().value());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO Orderitem (id, amount, status, note, medication_id, order_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, orderitem.id().value());
                pstmt.setInt(2, orderitem.amount());
                pstmt.setString(3, orderitem.status().name());
                pstmt.setString(4, orderitem.note());
                pstmt.setString(5, orderitem.medication().value());
                pstmt.setString(6, orderitem.order().value());
                pstmt.executeUpdate();
            }
        }
    }




    @Override
    public void save(Order order) throws SQLException {
        if (exists("\"Order\"", order.id().value())) {
            String sql = "UPDATE \"Order\" SET name = ?, amount = ?, date = ?, medication_id = ?, supplier_id = ?, compartment_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, order.name());
                pstmt.setInt(2, order.amount());
                pstmt.setDate(3, new java.sql.Date(order.date().getTime()));
                pstmt.setString(4, order.medication().value());
                pstmt.setString(5, order.supplier().value());
                pstmt.setString(6, order.compartment().value());
                pstmt.setString(7, order.id().value());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO \"Order\" (id, name, amount, date, medication_id, supplier_id, compartment_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, order.id().value());
                pstmt.setString(2, order.name());
                pstmt.setInt(3, order.amount());
                pstmt.setDate(4, new java.sql.Date(order.date().getTime()));
                pstmt.setString(5, order.medication().value());
                pstmt.setString(6, order.supplier().value());
                pstmt.setString(7, order.compartment().value());
                pstmt.executeUpdate();
            }
        }
    }

    @Override
    public void save(Stock stock) throws SQLException {
        if (exists("Stock", stock.id().value())) {
            String sql = "UPDATE Stock SET name = ?, amount = ?, date = ?, note = ?, medication_id = ?, compartment_id = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, stock.name());
                pstmt.setInt(2, stock.amount());
                pstmt.setDate(3, stock.date());
                pstmt.setString(4, stock.note());
                pstmt.setString(5, stock.medication().value());
                pstmt.setString(6, stock.compartment().value());
                pstmt.setString(7, stock.id().value());
                pstmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO Stock (id, name, amount, date, note, medication_id, compartment_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, stock.id().value());
                pstmt.setString(2, stock.name());
                pstmt.setInt(3, stock.amount());
                pstmt.setDate(4, stock.date());
                pstmt.setString(5, stock.note());
                pstmt.setString(6, stock.medication().value());
                pstmt.setString(7, stock.compartment().value());
                pstmt.executeUpdate();
            }
        }
    }

    @Override
    public void deletecompartment(Id<Compartment> id) {
        String sql = "DELETE FROM Compartment WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.value());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteorderitem(Id<Orderitem> id) {
        String sql = "DELETE FROM Orderitem WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.value());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Id<Order> id) {
        String sql = "DELETE FROM \"Order\" WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.value());
            pstmt.executeUpdate();
        }
    }

    public Compartment readCompartment(ResultSet result) throws SQLException {
        return new Compartment(
            new Id<>(result.getString("id")),
            result.getInt("row"),
            result.getString("column")
        );
    }

    @Override
    public Optional<Compartment> getCompartment(Id<Compartment> id) {
        var query = 
            SQL.SELECT("*")
                .FROM("Compartment")
                .WHERE("id", id.value())
                .toString();
    
        try (
            var result = conn.createStatement().executeQuery(query)
        ) {
            return result.next() ? 
                Optional.of(readCompartment(result)) : 
                Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Order readOrder(ResultSet result) throws SQLException {
        return new Order(
            new Id<>(result.getString("id")),
            result.getString("name"),
            result.getInt("amount"),
            result.getDate("date"),
            new Reference<>(result.getString("medication_id")),
            new Reference<>(result.getString("supplier_id"))
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
            result.getString("name"),
            result.getInt("amount"),
            result.getDate("date"),
            result.getString("note"),
            new Reference<>(result.getString("medication_id")),
            new Reference<>(result.getString("compartment_id"))
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
            result.getInt("amount"),
            Orderitem.Status.valueOf(result.getString("status")),
            result.getString("note"),
            new Reference<>(result.getString("medication_id")),
            new Reference<>(result.getString("order_id"))
        );
    }

    
    @Override
    public Optional<Orderitem> getOrderitem(Id<Orderitem> id) {
        var query = 
            SQL.SELECT("*")
                .FROM("Orderitem")
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
    
        if (!Objects.equals(filter.row(), null)) {
            query = query.WHERE("row", filter.row());
        }
    
        if (filter.column() != null && !filter.column().isEmpty()) {
            query = query.WHERE(COLUMN("column").like(filter.column()));
        }
    
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
        var query = SQL.SELECT("*")
            .FROM("\"Order\"")
            .ORDER_BY("date");
    
        if (filter.name() != null && !filter.name().isEmpty()) {
            query = query.WHERE("name", filter.name());
        }
    
        if (filter.period() != null) {
            if (filter.period().start() != null) {
                query = query.WHERE(COLUMN("date").greaterThanOrEqual(filter.period().start()));
            }
            filter.period().end().ifPresent(end -> query.WHERE(COLUMN("date").lessThanOrEqual(end)));
        }
    
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
        .ORDER_BY("date");

    if (filter.name() != null && !filter.name().isEmpty()) {
        query = query.WHERE("name", filter.name());
    }

    if (filter.period() != null) {
        if (filter.period().start() != null) {
            query = query.WHERE(COLUMN("date").greaterThanOrEqual(filter.period().start()));
        }
        filter.period().end().ifPresent(end -> query.WHERE(COLUMN("date").lessThanOrEqual(end)));
    }

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

    if (filter.status() != null) {
        query = query.WHERE("status", filter.status().name());
    }

    if (filter.medication() != null && filter.medication().value() != null && !filter.medication().value().isEmpty()) {
        query = query.WHERE(COLUMN("medication_id").equal(filter.medication().value()));
    }

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
    String query = "SELECT m.name, c.row, c.column, s.amount " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "JOIN Compartment c ON s.compartment_id = c.id";

    try (var resultSet = conn.createStatement().executeQuery(query)) {
        List<Map<String, Object>> inventory = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", resultSet.getString("name"));
            entry.put("row", resultSet.getInt("row"));
            entry.put("column", resultSet.getString("column"));
            entry.put("amount", resultSet.getInt("amount"));
            inventory.add(entry);
        }

        return inventory;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

public List<Medication> getMedicationsWithLowStock(int threshold) {
    String query = "SELECT m.id, m.name, s.amount " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "WHERE s.amount < ?";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setInt(1, threshold);
        try (var resultSet = pstmt.executeQuery()) {
            List<Medication> lowStockMedications = new ArrayList<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                int amount = resultSet.getInt("amount");
                Medication medication = new Medication(id, name, amount);
                lowStockMedications.add(medication);
            }

            return lowStockMedications;
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}

public List<Medication> getMedicationsWithExpiredStock() {
    String query = "SELECT m.id, m.name, s.expirationdate " +
                   "FROM Medication m " +
                   "JOIN Stock s ON m.id = s.medication_id " +
                   "WHERE s.expirationdate < CURRENT_DATE";

    try (var resultSet = conn.createStatement().executeQuery(query)) {
        List<Medication> expiredStockMedications = new ArrayList<>();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            Date expirationDate = resultSet.getDate("expirationdate");
            Medication medication = new Medication(id, name, expirationDate);
            expiredStockMedications.add(medication);
        }

        return expiredStockMedications;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }


}
}
