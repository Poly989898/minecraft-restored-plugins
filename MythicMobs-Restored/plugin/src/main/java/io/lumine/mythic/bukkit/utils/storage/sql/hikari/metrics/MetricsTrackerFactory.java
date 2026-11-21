package io.lumine.mythic.bukkit.utils.storage.sql.hikari.metrics;

public interface MetricsTrackerFactory {
   MetricsTracker create(String str, PoolStats poolStats);
}
