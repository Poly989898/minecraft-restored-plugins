package com.nexomc.nexo.mechanics.furniture.compatibility;

import com.nexomc.nexo.api.events.custom_block.NexoBlockPlaceEvent;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.io.File;
import java.util.Locale;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import me.frep.vulcan.api.event.VulcanGhostBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0007\u001a\u00020\b*\u00020\tH\u0007b\u0002\b\nJ&\u0010\u000b\u001a\u00020\b*\u00020\fH\u0007b\u0018\b\n\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002J&\u0010\u0011\u001a\u00020\b*\u00020\u0012H\u0007b\u0018\b\n\u0012\n\b\r\u0012\u0006\b\n0\u000e8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/compatibility/VulcanCompatibility;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "recentBlockPlace", "Lit/unimi/dsi/fastutil/objects/Object2LongOpenHashMap;", "Ljava/util/UUID;", "onFurnitureBarrier", "", "Lme/frep/vulcan/api/event/VulcanGhostBlockEvent;", "Lorg/bukkit/event/EventHandler;", "onCustomBlockPlace", "Lcom/nexomc/nexo/api/events/custom_block/NexoBlockPlaceEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "ignoreCancelled", "onVulcanFlag", "Lme/frep/vulcan/api/event/VulcanFlagEvent;", "com.nexomc:core"}
)
public final class VulcanCompatibility implements Listener {
   @NotNull
   private final Object2LongOpenHashMap<UUID> recentBlockPlace;

   public VulcanCompatibility() {
      boolean flag;
      block: {
         /* implicit super() */
         Object2LongOpenHashMap object2LongOpenHashMap = new Object2LongOpenHashMap();
         int value = 0;
         object2LongOpenHashMap.defaultReturnValue(0L);
         this.recentBlockPlace = object2LongOpenHashMap;
         Logs.logSuccess("Initializing Vulcan-AntiCheat Hook!", false);
         Plugin plugin = Bukkit.getPluginManager().getPlugin("Vulcan");
         if (plugin != null) {
            File dataFolder = plugin.getDataFolder();
            if (dataFolder != null) {
               File p0 = FilesKt.resolve(dataFolder, "config.yml");
               if (p0 != null) {
                  NexoYaml.Companion companion = NexoYaml.Companion;
                  int value2 = 0;
                  YamlConfiguration yamlConfiguration = companion.loadConfiguration(p0);
                  if (yamlConfiguration != null) {
                     flag = yamlConfiguration.getBoolean("settings.enable-api", false);
                     break block;
                  }
               }
            }
         }

         flag = false;
      }

      boolean isApiEnabled = flag;
      if (!isApiEnabled) {
         Logs.logWarn("Vulcan's API is disabled, please enable <i>settings.enable-api</i>...", false);
      }

   }

   @EventHandler
   public final void onFurnitureBarrier(@NotNull VulcanGhostBlockEvent vulcanGhostBlockEvent) {
      IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
      Player player = vulcanGhostBlockEvent.getPlayer();
      if (companion.standingOnFurniture(player)) {
         vulcanGhostBlockEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onCustomBlockPlace(@NotNull NexoBlockPlaceEvent nexoBlockPlaceEvent) {
      this.recentBlockPlace.put(nexoBlockPlaceEvent.getPlayer().getUniqueId(), System.currentTimeMillis());
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onVulcanFlag(@NotNull VulcanFlagEvent vulcanFlagEvent) {
      String name = vulcanFlagEvent.getCheck().getName();
      name = name.toLowerCase(Locale.ROOT);
      String checkName = name;
      name = vulcanFlagEvent.getCheck().getCategory();
      name = name.toLowerCase(Locale.ROOT);
      String category = name;
      if (Intrinsics.areEqual(category, "movement") || StringsKt.contains((CharSequence)checkName, (CharSequence)"flight", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"speed", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"noslow", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"motion", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"velocity", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"step", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"jump", false)) {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Player player = vulcanFlagEvent.getPlayer();
         if (companion.standingOnFurniture(player)) {
            EventUtils.INSTANCE.returnCancelled((Cancellable)vulcanFlagEvent);
            return;
         }
      }

      if (StringsKt.contains((CharSequence)checkName, (CharSequence)"fastbreak", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"fastplace", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"break", false) || StringsKt.contains((CharSequence)checkName, (CharSequence)"place", false)) {
         long last = this.recentBlockPlace.getLong(vulcanFlagEvent.getPlayer().getUniqueId());
         if (last != 0L && System.currentTimeMillis() - last < 1500L) {
            vulcanFlagEvent.setCancelled(true);
         }
      }

   }
}
