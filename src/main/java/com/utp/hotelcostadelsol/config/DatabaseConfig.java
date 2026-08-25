package com.utp.hotelcostadelsol.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor centralizado del pool de conexiones HikariCP para la arquitectura APF2 sobre PostgreSQL.
 * Proporciona fallback inteligente y resiliente en memoria si la BD no esta levantada.
 */
public class DatabaseConfig {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static HikariDataSource dataSource;
    private static boolean dbAvailable = false;

    static {
        initDataSource();
    }

    private static synchronized void initDataSource() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config/db.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(prop.getProperty("db.driver", "org.postgresql.Driver"));
            config.setJdbcUrl(prop.getProperty("db.url", "jdbc:postgresql://localhost:5432/hotel_costa_del_sol"));
            config.setUsername(prop.getProperty("db.user", "postgres"));
            config.setPassword(prop.getProperty("db.password", "1234"));

            config.setMaximumPoolSize(Integer.parseInt(prop.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(prop.getProperty("db.pool.minimumIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(prop.getProperty("db.pool.connectionTimeout", "3000")));
            config.setInitializationFailTimeout(1500);

            dataSource = new HikariDataSource(config);
            try (Connection conn = dataSource.getConnection()) {
                dbAvailable = true;
                LOGGER.info(">>> Conectado exitosamente a la base de datos PostgreSQL/HikariCP.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, ">>> BD PostgreSQL no detectada localmente (puerto 5432). Activando modo Demo en memoria: {0}", ex.getMessage());
            dbAvailable = false;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null && dbAvailable) {
            return dataSource.getConnection();
        }
        throw new SQLException("Base de datos PostgreSQL no iniciada en puerto 5432.");
    }

    public static boolean isDbAvailable() {
        return dbAvailable;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}