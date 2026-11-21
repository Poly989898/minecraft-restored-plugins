package com.nexomc.nexo.utils;

import com.nexomc.libs.bukkit.Metrics;
import com.nexomc.libs.charts.DrilldownPie;
import com.nexomc.libs.charts.SimplePie;
import com.nexomc.nexo.NexoPlugin;
import io.papermc.paper.ServerBuildInfo;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006"},
   d2 = {"Lcom/nexomc/nexo/utils/NexoMetrics;", "", "<init>", "()V", "initializeMetrics", "", "com.nexomc:core"}
)
public final class NexoMetrics {
   @NotNull
   public static final NexoMetrics INSTANCE = new NexoMetrics();

   private NexoMetrics() {
   }

   public final void initializeMetrics() {
      Metrics metrics = new Metrics((Plugin)NexoPlugin.Companion.instance(), 23930);
      metrics.addCustomChart(new DrilldownPie("server_player_split", NexoMetrics::initializeMetrics$lambda$1));
   }

   private static final Map initializeMetrics$lambda$1() {
      String mainCategory = VersionUtil.INSTANCE.isFoliaServer() ? "Folia" : "Paper";
      return MapsKt.mapOf(kotlin.TuplesKt.to(mainCategory, MapsKt.mapOf(kotlin.TuplesKt.to(ServerBuildInfo.buildInfo().brandName(), Bukkit.getServer().getOnlinePlayers().size()))));
   }
}
