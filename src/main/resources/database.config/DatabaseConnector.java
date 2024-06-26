import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static database.config.SQL.*;

public class DatabaseConnector {
    private final Connection conn;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/KhApoDB";
    //private static final String username = "postgres";
    //private static final String password = "admin";


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

            //connector.setup();

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
                "column" varchar not null
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
            displayname varchar,
            id          varchar not null
                constraint medication_pk
                    primary key,
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

/* 
    private void setup() {
        try (var stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_COMPARTMENT);
            stmt.execute(CREATE_TABLE_STOCK);
            stmt.execute(CREATE_TABLE_ORDERITEM);
            stmt.execute(CREATE_TABLE_ORDER);
            stmt.execute(CREATE_TABLE_MEDICATION);
            stmt.execute(CREATE_TABLE_SUPPLIER);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    */

    //CRUD Operations

    public void insertCompartment(String id, int row, String column) {
        String query = "INSERT INTO Compartment (id, row, column) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setInt(2, row);
            stmt.setString(3, column);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertStock(String id, int amount, Date expirationDate, String medicationId, String compartmentId) {
        String query = "INSERT INTO Stock (id, amount, expirationdate, medication_id, compartment_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setInt(2, amount);
            stmt.setDate(3, expirationDate);
            stmt.setString(4, medicationId);
            stmt.setString(5, compartmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertOrderItem(String id, String status, String medicationId, Date date, int amount) {
        String query = "INSERT INTO orderitem (id, status, medication_id, date, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, status);
            stmt.setString(3, medicationId);
            stmt.setDate(4, date);
            stmt.setInt(5, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void insertOrder(String id, String orderItemId, String supplierId, int amount, BigDecimal price) {
        String query = "INSERT INTO \"order\" (id, orderitem_id, supplier_id, amount, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, orderItemId);
            stmt.setString(3, supplierId);
            stmt.setInt(4, amount);
            stmt.setBigDecimal(5, price);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    public void updateCompartment(String id, Integer row, String column) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Compartment SET");
        List<Object> params = new ArrayList<>();
        if (row != null) {
            queryBuilder.append(" row = ?,");
            params.add(row);
        }
        if (column != null) {
            queryBuilder.append(" column = ?,");
            params.add(column);
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        String query = queryBuilder.toString();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStock(String id, Integer amount, Date expirationDate, String medicationId, String compartmentId) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Stock SET");
        List<Object> params = new ArrayList<>();
        if (amount != null) {
            queryBuilder.append(" amount = ?,");
            params.add(amount);
        }
        if (expirationDate != null) {
            queryBuilder.append(" expirationdate = ?,");
            params.add(expirationDate);
        }
        if (medicationId != null) {
            queryBuilder.append(" medication_id = ?,");
            params.add(medicationId);
        }
        if (compartmentId != null) {
            queryBuilder.append(" compartment_id = ?,");
            params.add(compartmentId);
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        String query = queryBuilder.toString();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrderItem(String id, String status, String medicationId, Date date, Integer amount) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE orderitem SET");
        List<Object> params = new ArrayList<>();
        if (status != null) {
            queryBuilder.append(" status = ?,");
            params.add(status);
        }
        if (medicationId != null) {
            queryBuilder.append(" medication_id = ?,");
            params.add(medicationId);
        }
        if (date != null) {
            queryBuilder.append(" date = ?,");
            params.add(date);
        }
        if (amount != null) {
            queryBuilder.append(" amount = ?,");
            params.add(amount);
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        String query = queryBuilder.toString();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrder(String id, String orderItemId, String supplierId, Integer amount, BigDecimal price) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE \"order\" SET");
        List<Object> params = new ArrayList<>();
        if (orderItemId != null) {
            queryBuilder.append(" orderitem_id = ?,");
            params.add(orderItemId);
        }
        if (supplierId != null) {
            queryBuilder.append(" supplier_id = ?,");
            params.add(supplierId);
        }
        if (amount != null) {
            queryBuilder.append(" amount = ?,");
            params.add(amount);
        }
        if (price != null) {
            queryBuilder.append(" price = ?,");
            params.add(price);
        }
        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE id = ?");
        params.add(id);

        String query = queryBuilder.toString();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCompartment(String id) {
        String query = "DELETE FROM Compartment WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStock(String id) {
        String query = "DELETE FROM Stock WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteOrderItem(String id) {
        String query = "DELETE FROM orderitem WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteOrder(String id) {
        String query = "DELETE FROM \"order\" WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to view the medication inventory
    public List<MedicationStock> getMedicationStock() {
        var query = SQL.SELECT("*")
            .FROM("Stock s")
            .JOIN("medication m ON s.medication_id = m.id")
            .JOIN("Compartment c ON s.compartment_id = c.id")
            .ORDER_BY("m.displayname")
            .toString();

        try (var resultSet = conn.createStatement().executeQuery(query)) {
            var stockList = new ArrayList<MedicationStock>();

            while (resultSet.next()) {
                var stock = new MedicationStock(
                    resultSet.getString("displayname"),
                    resultSet.getInt("amount"),
                    resultSet.getInt("row"),
                    resultSet.getString("column")
                );
                stockList.add(stock);
            }

            return stockList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to view medications with less than N units
    public List<Medication> getMedicationsBelowThreshold(int threshold) {
        var query = SQL.SELECT("*")
            .FROM("Stock s")
            .JOIN("medication m ON s.medication_id = m.id")
            .WHERE(SQL.COLUMN("s.amount").lessThan(threshold))
            .ORDER_BY("m.displayname")
            .toString();

        try (var resultSet = conn.createStatement().executeQuery(query)) {
            var medications = new ArrayList<Medication>();

            while (resultSet.next()) {
                var medication = new Medication(
                    resultSet.getString("displayname"),
                    resultSet.getInt("amount")
                );
                medications.add(medication);
            }

            return medications;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}