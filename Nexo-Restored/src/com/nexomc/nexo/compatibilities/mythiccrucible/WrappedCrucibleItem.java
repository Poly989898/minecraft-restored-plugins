package com.nexomc.nexo.compatibilities.mythiccrucible;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.logs.Logs;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nJ\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\r\u001a\t\u0018\u00010\u000e¢\u0006\u0002\b\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/mythiccrucible/WrappedCrucibleItem;", "", "id", "", "cache", "", "<init>", "(Ljava/lang/String;Z)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getCache", "()Z", "material", "Lorg/bukkit/Material;", "Lorg/jetbrains/annotations/NotNull;", "getMaterial", "()Lorg/bukkit/Material;", "build", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class WrappedCrucibleItem {
   @Nullable
   private final String id;
   private final boolean cache;
   @Nullable
   private final Material material;

   public WrappedCrucibleItem(@Nullable String id, boolean cache) {
      this.id = id;
      this.cache = cache;
      WrappedCrucibleItem wrappedCrucibleItem = this;

      Object obj;
      try {
         WrappedCrucibleItem wrappedCrucibleItem2 = wrappedCrucibleItem;
         int value = 0;
         obj = ResultShim.ok(MythicBukkit.inst().getItemManager().getItemStack(wrappedCrucibleItem2.id).getType());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      this.material = (Material)(ResultShim.isFailure(obj) ? null : obj);
   }

   public final boolean getCache() {
      return this.cache;
   }

   public WrappedCrucibleItem(@NotNull ConfigurationSection section) {
      this(section.getString("id"), section.getBoolean("cache", true));
   }

   @Nullable
   public final Material getMaterial() {
      return this.material;
   }

   @Nullable
   public final ItemStack build() {
      WrappedCrucibleItem wrappedCrucibleItem = this;

      Object obj2;
      try {
         WrappedCrucibleItem wrappedCrucibleItem2 = wrappedCrucibleItem;
         int value = 0;
         obj2 = ResultShim.ok(BukkitAdapter.adapt((MythicBukkit.inst().getItemManager().getItem(wrappedCrucibleItem2.id).orElseThrow()).generateItemStack(1)));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         Logs.logError("Failed to load MythicCrucible item " + this.id, false);
         if (!PluginUtils.INSTANCE.isMythicCrucibleEnabled()) {
            Logs.logWarn("MythicCrucible is not installed", false);
         }

         if (Settings.DEBUG.toBool()) {
            String message = it.getMessage();
            Logs.logWarn(message, false);
         }
      }

      return (ItemStack)(ResultShim.isFailure(obj) ? null : obj);
   }
}
