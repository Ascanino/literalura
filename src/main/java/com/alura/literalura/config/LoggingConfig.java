package com.alura.literalura.config;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    public LoggingConfig() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Ajuste del nivel de log para HikariCP y Hibernate
        Logger hikariLogger = loggerContext.getLogger("com.zaxxer.hikari");
        hikariLogger.setLevel(Level.WARN);

        Logger hibernateSqlLogger = loggerContext.getLogger("org.hibernate.SQL");
        hibernateSqlLogger.setLevel(Level.WARN);

        Logger hibernateBinderLogger = loggerContext.getLogger("org.hibernate.type.descriptor.sql.BasicBinder");
        hibernateBinderLogger.setLevel(Level.WARN);

        Logger hibernateHbm2ddlLogger = loggerContext.getLogger("org.hibernate.tool.hbm2ddl");
        hibernateHbm2ddlLogger.setLevel(Level.WARN);

        Logger hibernateTransactionLogger = loggerContext.getLogger("org.hibernate.engine.transaction.internal.TransactionImpl");
        hibernateTransactionLogger.setLevel(Level.WARN);

        Logger hibernateConnectionLogger = loggerContext.getLogger("org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl");
        hibernateConnectionLogger.setLevel(Level.WARN);

        // Eliminar configuraciones innecesarias, como la del dialecto PostgreSQL
        Logger postgresqlDialectLogger = loggerContext.getLogger("org.hibernate.dialect.PostgreSQLDialect");
        postgresqlDialectLogger.setLevel(Level.INFO);  // O eliminar si no es necesario
    }
}
