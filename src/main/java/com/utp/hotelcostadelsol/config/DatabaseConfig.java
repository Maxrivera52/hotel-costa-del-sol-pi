package com.utp.hotelcostadelsol.config;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestor centralizado del pool de conexiones HikariCP sobre PostgreSQL.
 * Integra registro estructurado con Logback (SLF4J), validaciones con Google Guava
 * y saneamiento de parametros con Apache Commons Lang3.
 */
public final class DatabaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;
    private static boolean dbAvailable = false;

    static {
        initDataSource();
    }

    private DatabaseConfig() {
        // Clase utilitaria no instanciable
    }

    private static synchronized void initDataSource() {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("config/db.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
            }

            String driver = StringUtils.defaultIfBlank(prop.getProperty("db.driver"), "org.postgresql.Driver");
            String url = StringUtils.defaultIfBlank(prop.getProperty("db.url"), "jdbc:postgresql://localhost:5432/hotel_costa_del_sol");
            String user = StringUtils.defaultIfBlank(prop.getProperty("db.user"), "postgres");
            String password = prop.getProperty("db.password", "1234");

            Preconditions.checkArgument(StringUtils.isNotBlank(url), "La URL JDBC no puede estar vacia");

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(Integer.parseInt(prop.getProperty("db.pool.maximumPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(prop.getProperty("db.pool.minimumIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(prop.getProperty("db.pool.connectionTimeout", "2500")));
            config.setInitializationFailTimeout(1200);

            dataSource = new HikariDataSource(config);
            try (Connection conn = dataSource.getConnection()) {
                dbAvailable = true;
                LOGGER.info("Conectado exitosamente al pool PostgreSQL/HikariCP en {}", url);
            }
        } catch (Exception ex) {
            LOGGER.info("PostgreSQL local no detectado en puerto 5432 ({}). Activando modo resiliente en memoria para evaluacion/demostracion.", ex.getMessage());
            dbAvailable = false;
        }
    }

    /**
     * Obtiene una conexion activa del pool HikariCP.
     *
     * @return Conexion JDBC a PostgreSQL
     * @throws SQLException si el motor PostgreSQL no se encuentra activo
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource != null && dbAvailable) {
            return dataSource.getConnection();
        }
        throw new SQLException("Base de datos PostgreSQL no iniciada en puerto 5432.");
    }

    /**
     * Indica si la base de datos PostgreSQL se encuentra activa y accesible.
     *
     * @return true si hay conexion activa, false si opera en modo memoria resiliente
     */
    public static boolean isDbAvailable() {
        return dbAvailable;
    }

    /**
     * Cierra de forma segura el pool de conexiones al detener el contenedor web.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Cerrando pool de conexiones HikariCP...");
            dataSource.close();
        }
    }
}
