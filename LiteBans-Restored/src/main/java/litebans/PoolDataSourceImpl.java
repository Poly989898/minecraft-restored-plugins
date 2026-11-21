package litebans;

import java.sql.SQLException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;

public final class PoolDataSourceImpl implements DataSource {
   private final Driver driver;
   private final HikariConfig hikariConfig;

   public PoolDataSourceImpl(@NotNull Driver driver, @NotNull HikariConfig hikariConfig) {
      this.driver = driver;
      this.hikariConfig = hikariConfig;
      this.setLoginTimeout((int)this.hikariConfig.getConnectionTimeout());
   }

   @Override
   public Connection getConnection() {
      try {
         return this.driver.connect(this.hikariConfig.getJdbcUrl(), this.hikariConfig.getDataSourceProperties());
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Connection getConnection(@NotNull String text, @NotNull String text2) {
      return this.getConnection();
   }

   @Override
   public int getLoginTimeout() {
      return DriverManager.getLoginTimeout();
   }

   @Override
   public void setLoginTimeout(int value) {
      DriverManager.setLoginTimeout(value);
   }

   @Override
   public PrintWriter getLogWriter() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public Void computeVoid(@NotNull PrintWriter printWriter) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   @Override
   public Object unwrap(@NotNull Class class2) {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   @Override
   public boolean isWrapperFor(@NotNull Class class2) {
      return false;
   }

   @Override
   public Logger getParentLogger() {
      return null;
   }

   @Override
   public void setLogWriter(PrintWriter printWriter) {
      this.computeVoid(printWriter);
   }
}
