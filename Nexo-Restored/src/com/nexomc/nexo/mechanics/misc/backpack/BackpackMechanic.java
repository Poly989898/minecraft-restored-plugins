package com.nexomc.nexo.mechanics.misc.backpack;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0012\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u001a\u001a\u00020\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d¨\u0006#"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "rows", "", "getRows", "()I", "title", "", "getTitle", "()Ljava/lang/String;", "openSound", "getOpenSound", "closeSound", "getCloseSound", "volume", "", "getVolume", "()F", "pitch", "getPitch", "blacklist", "Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackBlacklist;", "getBlacklist", "()Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackBlacklist;", "isBlacklisted", "", "item", "Lorg/bukkit/inventory/ItemStack;", "Companion", "com.nexomc:core"}
)
public final class BackpackMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   private final int rows;
   @NotNull
   private final String title;
   @NotNull
   private final String openSound;
   @NotNull
   private final String closeSound;
   private final float volume;
   private final float pitch;
   @NotNull
   private final BackpackBlacklist blacklist;
   @NotNull
   private static final NamespacedKey BACKPACK_KEY;

   public BackpackMechanic(@NotNull BackpackMechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.rows = section.getInt("rows", 6);
      String str = NexoYamlKt.stringOrNull(section, "title");
      if (str == null) {
         str = "Backpack";
      }

      this.title = str;
      str = NexoYamlKt.stringOrNull(section, "open_sound");
      if (str == null) {
         str = "minecraft:entity.shulker.open";
      }

      this.openSound = str;
      str = NexoYamlKt.stringOrNull(section, "close_sound");
      if (str == null) {
         str = "minecraft:entity.shulker.close";
      }

      this.closeSound = str;
      this.volume = (float)section.getDouble("volume", (double)1.0F);
      this.pitch = (float)section.getDouble("pitch", (double)1.0F);
      BackpackMechanic backpackMechanic = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "blacklist");
      BackpackBlacklist backpackBlacklist;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         backpackBlacklist = new BackpackBlacklist(p0);
         backpackMechanic = this;
      } else {
         backpackBlacklist = new BackpackBlacklist(kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.CollectionsKt.emptyList());
      }

      this.blacklist = backpackBlacklist;
   }

   public final int getRows() {
      return this.rows;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getOpenSound() {
      return this.openSound;
   }

   @NotNull
   public final String getCloseSound() {
      return this.closeSound;
   }

   public final float getVolume() {
      return this.volume;
   }

   public final float getPitch() {
      return this.pitch;
   }

   @NotNull
   public final BackpackBlacklist getBlacklist() {
      return this.blacklist;
   }

   public final boolean isBlacklisted(@Nullable ItemStack item) {
      return item != null && this.blacklist.contains(item);
   }

   static {
      BACKPACK_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "backpack");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/backpack/BackpackMechanic$Companion;", "", "<init>", "()V", "BACKPACK_KEY", "Lorg/bukkit/NamespacedKey;", "getBACKPACK_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getBACKPACK_KEY() {
         return BackpackMechanic.BACKPACK_KEY;
      }
   }
}
