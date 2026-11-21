package litebans;

import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class HikariDataSource extends HikariConfig implements DataSource, Closeable {
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(HikariDataSource.class);
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private final HikariPool hikariPool2;
   private volatile HikariPool hikariPool3;

   public HikariDataSource() {
      this.hikariPool2 = null;
   }

   public HikariDataSource(HikariConfig hikariConfig) {
      hikariConfig.validatePoolName();
      hikariConfig.copyStateTo((HikariConfig) this);
      this.hikariPool3 = this.hikariPool2 = new HikariPool(this);
      this.seal();
   }

   @Override
   public Connection getConnection()  throws java.sql.SQLException {
      if (this.obtainFlag()) {
         throw new SQLException("HikariDataSource " + this + " has been closed.");
      }

      if (this.hikariPool2 != null) {
         return this.hikariPool2.getConnection();
      }

      HikariPool hikariPool = this.hikariPool3;
      if (hikariPool == null) {
         synchronized (this) {
            hikariPool = this.hikariPool3;
            if (hikariPool == null) {
               this.validatePoolName();

               try {
                  this.hikariPool3 = hikariPool = new HikariPool(this);
                  this.seal();
               } catch (PoolInitializationException e) {
                  if (e.getCause() instanceof SQLException) {
                     throw (SQLException)e.getCause();
                  }

                  throw e;
               }
            }
         }
      }

      return hikariPool.getConnection();
   }

   @Override
   public Connection getConnection(String text, String text2)  throws java.sql.SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public PrintWriter getLogWriter()  throws java.sql.SQLException {
      HikariPool hikariPool = this.hikariPool3;
      return hikariPool != null ? hikariPool.getUnwrappedDataSource().getLogWriter() : null;
   }

   @Override
   public void setLogWriter(PrintWriter printWriter)  throws java.sql.SQLException {
      HikariPool hikariPool = this.hikariPool3;
      if (hikariPool != null) {
         hikariPool.getUnwrappedDataSource().setLogWriter(printWriter);
      }
   }

   @Override
   public void setLoginTimeout(int value)  throws java.sql.SQLException {
      HikariPool hikariPool = this.hikariPool3;
      if (hikariPool != null) {
         hikariPool.getUnwrappedDataSource().setLoginTimeout(value);
      }
   }

   @Override
   public int getLoginTimeout()  throws java.sql.SQLException {
      HikariPool hikariPool = this.hikariPool3;
      return hikariPool != null ? hikariPool.getUnwrappedDataSource().getLoginTimeout() : 0;
   }

   @Override
   public Logger getParentLogger()  throws java.sql.SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public Object unwrap(Class class2)  throws java.sql.SQLException {
      if (class2.isInstance(this)) {
         return this;
      }

      HikariPool hikariPool = this.hikariPool3;
      if (hikariPool != null) {
         DataSource unwrappedDataSource = hikariPool.getUnwrappedDataSource();
         if (class2.isInstance(unwrappedDataSource)) {
            return unwrappedDataSource;
         }

         if (unwrappedDataSource != null) {
            return unwrappedDataSource.unwrap(class2);
         }
      }

      throw new SQLException("Wrapped DataSource is not an instance of " + class2);
   }

   @Override
   public boolean isWrapperFor(Class class2)  throws java.sql.SQLException {
      if (class2.isInstance(this)) {
         return true;
      }

      HikariPool hikariPool = this.hikariPool3;
      if (hikariPool != null) {
         DataSource unwrappedDataSource = hikariPool.getUnwrappedDataSource();
         if (class2.isInstance(unwrappedDataSource)) {
            return true;
         }

         if (unwrappedDataSource != null) {
            return unwrappedDataSource.isWrapperFor(class2);
         }
      }

      return false;
   }

   public HikariPoolMXBean obtainHikariPoolMXBean() {
      return this.hikariPool3;
   }

   @Override
   public void close() {
      if (!this.atomicBoolean.getAndSet(true)) {
         HikariPool hikariPool = this.hikariPool3;
         if (hikariPool != null) {
            try {
               hikariPool.shutdown();
            } catch (InterruptedException e) {
               POOL_LOGGER.handleText4("%s - Interrupted during closing", new Object[]{this.getPoolName(), e});
               Thread.currentThread().interrupt();
            }
         }
      }
   }

   public boolean obtainFlag() {
      return this.atomicBoolean.get();
   }

   @Override
   public String toString() {
      return "HikariDataSource (" + this.hikariPool3 + ")";
   }
}
