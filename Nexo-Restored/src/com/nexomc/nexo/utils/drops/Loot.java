package com.nexomc.nexo.utils.drops;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.Indyuce.mmoitems.MMOItems;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u000f\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fB\u0019\b\u0016\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u000e\u0010\u0010B\u001b\b\u0016\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u000e\u0010\u0011B\u001b\b\u0016\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u000e\u0010\u0013B5\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0015¢\u0006\u0004\b\u000e\u0010\u0017J\u0006\u0010\u0004\u001a\u00020\u0005J\u0010\u0010\u0004\u001a\u00020\u00002\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005J\u0016\u0010*\u001a\u00020\u00152\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0015J\u0018\u0010.\u001a\u00020\u00152\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020\u0015H\u0002J\u000e\u0010/\u001a\u00020\u00052\u0006\u0010-\u001a\u00020\u0015J\u000b\u00100\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u00102\u001a\u00020\u0007HÆ\u0003J\t\u00103\u001a\u00020\tHÆ\u0003J\t\u00104\u001a\u00020\u000bHÆ\u0003J\t\u00105\u001a\u00020\rHÆ\u0003JI\u00106\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rHÆ\u0001J\u0014\u00107\u001a\u00020\u000b2\b\u00108\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u00109\u001a\u00020\u0015HÖ\u0081\u0004J\n\u0010:\u001a\u00020\u0003HÖ\u0081\u0004R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)¨\u0006;"},
   d2 = {"Lcom/nexomc/nexo/utils/drops/Loot;", "", "sourceID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "probability", "", "amount", "Lkotlin/ranges/IntRange;", "inExplosion", "", "config", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Ljava/lang/String;Lorg/bukkit/inventory/ItemStack;DLkotlin/ranges/IntRange;ZLorg/bukkit/configuration/ConfigurationSection;)V", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)V", "(Lorg/bukkit/inventory/ItemStack;D)V", "itemId", "(Ljava/lang/String;D)V", "minAmount", "", "maxAmount", "(Ljava/lang/String;Lorg/bukkit/inventory/ItemStack;DII)V", "getSourceID", "()Ljava/lang/String;", "getItemStack", "()Lorg/bukkit/inventory/ItemStack;", "setItemStack", "(Lorg/bukkit/inventory/ItemStack;)V", "getProbability", "()D", "getAmount", "()Lkotlin/ranges/IntRange;", "setAmount", "(Lkotlin/ranges/IntRange;)V", "getInExplosion", "()Z", "getConfig", "()Lorg/bukkit/configuration/ConfigurationSection;", "setConfig", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "dropNaturally", "location", "Lorg/bukkit/Location;", "amountMultiplier", "dropItems", "getItem", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "com.nexomc:core"}
)
public final class Loot {
   @Nullable
   private final String sourceID;
   @Nullable
   private ItemStack itemStack;
   private final double probability;
   @NotNull
   private IntRange amount;
   private final boolean inExplosion;
   @NotNull
   private ConfigurationSection config;

   public Loot(@Nullable String sourceID, @Nullable ItemStack itemStack, double probability, @NotNull IntRange amount, boolean inExplosion, @NotNull ConfigurationSection config) {
      super();
      this.sourceID = sourceID;
      this.itemStack = itemStack;
      this.probability = probability;
      this.amount = amount;
      this.inExplosion = inExplosion;
      this.config = config;
   }

   @Nullable
   public final String getSourceID() {
      return this.sourceID;
   }

   @Nullable
   public final ItemStack getItemStack() {
      return this.itemStack;
   }

   public final void setItemStack(@Nullable ItemStack itemStack2) {
      this.itemStack = itemStack2;
   }

   public final double getProbability() {
      return this.probability;
   }

   @NotNull
   public final IntRange getAmount() {
      return this.amount;
   }

   public final void setAmount(@NotNull IntRange intRange) {
      this.amount = intRange;
   }

   public final boolean getInExplosion() {
      return this.inExplosion;
   }

   @NotNull
   public final ConfigurationSection getConfig() {
      return this.config;
   }

   public final void setConfig(@NotNull ConfigurationSection configurationSection) {
      this.config = configurationSection;
   }

   public Loot(@NotNull ConfigurationSection config, @NotNull String sourceID) {
      double value = config.getDouble("probability", (double)1.0F);
      IntRange intRange = NexoYamlKt.intRangeOrNull(config, "min_amount", "max_amount", "amount");
      if (intRange == null) {
         intRange = new IntRange(1, 1);
      }

      IntRange intRange2 = intRange;
      boolean flag = config.getBoolean("in_explosion");
      this(sourceID, null, value, intRange2, flag, config);
   }

   public Loot(@Nullable ItemStack itemStack, double probability) {
      this(null, itemStack, RangesKt.coerceAtMost(probability, (double)1.0F), new IntRange(1, 1), false, ((org.bukkit.configuration.ConfigurationSection)new org.bukkit.configuration.file.YamlConfiguration()));
   }

   public Loot(@Nullable String itemId, double probability) {
      this(itemId, null, Math.min((double)1.0F, probability), new IntRange(1, 1), false, ((org.bukkit.configuration.ConfigurationSection)new org.bukkit.configuration.file.YamlConfiguration()));
   }

   public Loot(@Nullable String sourceID, @Nullable ItemStack itemStack, double probability, int minAmount, int maxAmount) {
      this(sourceID, itemStack, RangesKt.coerceAtMost(probability, (double)1.0F), new IntRange(minAmount, maxAmount), false, ((org.bukkit.configuration.ConfigurationSection)new org.bukkit.configuration.file.YamlConfiguration()));
   }

   @NotNull
   public final ItemStack itemStack() {
      if (this.itemStack != null) {
         ItemUpdater.Companion companion = ItemUpdater.Companion;
         ItemStack itemStack2 = this.itemStack;
         return companion.updateItem(itemStack2);
      } else {
         Loot loot = this;
         ItemStack itemStack3;
         if (this.config.contains("nexo_item")) {
            ItemBuilder itemBuilder = NexoItems.itemFromId(NexoYamlKt.stringOrNull(this.config, "nexo_item"));
            itemStack3 = itemBuilder != null ? itemBuilder.build() : null;
         } else if (this.config.contains("crucible_item")) {
            itemStack3 = (new WrappedCrucibleItem(NexoYamlKt.stringOrNull(this.config, "crucible_item"), true)).build();
         } else if (this.config.contains("mmoitems_id") && this.config.contains("mmoitems_type")) {
            itemStack3 = MMOItems.plugin.getItem(NexoYamlKt.stringOrNull(this.config, "mmoitems_type"), NexoYamlKt.stringOrNull(this.config, "mmoitems_id"));
         } else if (this.config.contains("minecraft_type")) {
            ConfigurationSection configurationSection = this.config;
            String key = "minecraft_type";
            Material material = EnumUtils.getEnum(Material.class, NexoYamlKt.stringOrNull(configurationSection, key));
            if (material != null) {
               Material p0 = material;
               itemStack3 = ItemStack.of(p0);
               loot = this;
            } else {
               itemStack3 = null;
            }
         } else if (this.config.contains("minecraft_item")) {
            Object obj = this.config.get("minecraft_item");
            itemStack3 = obj instanceof ItemStack ? (ItemStack)obj : null;
         } else {
            itemStack3 = null;
         }

         loot.itemStack = itemStack3;
         if (this.itemStack == null) {
            ItemBuilder itemBuilder2 = NexoItems.itemFromId(this.sourceID);
            this.itemStack = itemBuilder2 != null ? itemBuilder2.build() : null;
         }

         ItemStack itemStack4 = this.itemStack;
         if (itemStack4 != null) {
            ItemStack it = itemStack4;
            int value = 0;
            itemStack4 = ItemUpdater.Companion.updateItem(it);
            if (itemStack4 != null) {
               return itemStack4;
            }
         }

         itemStack4 = ItemStack.empty();
         return itemStack4;
      }
   }

   @NotNull
   public final Loot itemStack(@Nullable ItemStack itemStack) {
      this.itemStack = itemStack;
      return this;
   }

   public final int dropNaturally(@NotNull Location location, int amountMultiplier) {
      return Math.random() <= this.probability ? this.dropItems(location, amountMultiplier) : 0;
   }

   private final int dropItems(Location location, int amountMultiplier) {
      World world2 = location.getWorld();
      if (world2 == null) {
         return 0;
      } else {
         World world = world2;
         ItemStack it = this.getItem(amountMultiplier);
         int value = 0;
         world.dropItemNaturally(location, it);
         return it.getAmount();
      }
   }

   @NotNull
   public final ItemStack getItem(int amountMultiplier) {
      ItemUpdater.Companion companion = ItemUpdater.Companion;
      ItemStack itemStack2 = this.itemStack().clone();
      ItemStack it = companion.updateItem(itemStack2);
      int value = 0;
      it.setAmount(it.getAmount() * RangesKt.random(this.amount, (Random)Random.Default) * amountMultiplier);
      return it;
   }

   @Nullable
   public final String component1() {
      return this.sourceID;
   }

   @Nullable
   public final ItemStack component2() {
      return this.itemStack;
   }

   public final double component3() {
      return this.probability;
   }

   @NotNull
   public final IntRange component4() {
      return this.amount;
   }

   public final boolean component5() {
      return this.inExplosion;
   }

   @NotNull
   public final ConfigurationSection component6() {
      return this.config;
   }

   @NotNull
   public final Loot copy(@Nullable String sourceID, @Nullable ItemStack itemStack, double probability, @NotNull IntRange amount, boolean inExplosion, @NotNull ConfigurationSection config) {
      return new Loot(sourceID, itemStack, probability, amount, inExplosion, config);
   }

   @NotNull
   public String toString() {
      return "Loot(sourceID=" + this.sourceID + ", itemStack=" + this.itemStack + ", probability=" + this.probability + ", amount=" + this.amount + ", inExplosion=" + this.inExplosion + ", config=" + this.config + ")";
   }

   public int hashCode() {
      int result = this.sourceID == null ? 0 : this.sourceID.hashCode();
      result = result * 31 + (this.itemStack == null ? 0 : this.itemStack.hashCode());
      result = result * 31 + Double.hashCode(this.probability);
      result = result * 31 + this.amount.hashCode();
      result = result * 31 + Boolean.hashCode(this.inExplosion);
      result = result * 31 + this.config.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Loot)) {
         return false;
      } else {
         Loot loot = (Loot)other;
         if (!Intrinsics.areEqual(this.sourceID, loot.sourceID)) {
            return false;
         } else if (!Intrinsics.areEqual(this.itemStack, loot.itemStack)) {
            return false;
         } else if (Double.compare(this.probability, loot.probability) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.amount, loot.amount)) {
            return false;
         } else if (this.inExplosion != loot.inExplosion) {
            return false;
         } else {
            return Intrinsics.areEqual(this.config, loot.config);
         }
      }
   }
}
