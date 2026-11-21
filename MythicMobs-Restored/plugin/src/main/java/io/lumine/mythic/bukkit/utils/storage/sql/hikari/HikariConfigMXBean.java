package io.lumine.mythic.bukkit.utils.storage.sql.hikari;

public interface HikariConfigMXBean {
   long getConnectionTimeout();

   void setConnectionTimeout(long value);

   long getValidationTimeout();

   void setValidationTimeout(long value);

   long getIdleTimeout();

   void setIdleTimeout(long value);

   long getLeakDetectionThreshold();

   void setLeakDetectionThreshold(long value);

   long getMaxLifetime();

   void setMaxLifetime(long value);

   int getMinimumIdle();

   void setMinimumIdle(int value);

   int getMaximumPoolSize();

   void setMaximumPoolSize(int value);

   void setPassword(String str);

   void setUsername(String str);

   String getPoolName();
}
