package net.Indyuce.mmoitems.api;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.droptable.DropTable;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.tooltip.TooltipTexture;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTier {
   private final String id;
   private final String name;
   private final String unparsedName;
   private final ItemTier.UnidentificationInfo unidentificationInfo;
   @Nullable
   private final TooltipTexture tooltip;
   @Nullable
   private final DropTable deconstructTable;
   @Nullable
   private final ChatColor glowColor;
   private final boolean itemHint;
   @Nullable
   private final NumericStatFormula capacity;
   private final double chance;
   @NotNull
   private static final Random RANDOM = new Random();

   public ItemTier(@NotNull ConfigurationSection var1) {
      this.id = var1.getName().toUpperCase().replace("-", "_");
      this.unparsedName = var1.getString("name");
      this.name = MythicLib.plugin.parseColors(this.unparsedName);
      this.deconstructTable = var1.contains("deconstruct-item") ? new DropTable(var1.getConfigurationSection("deconstruct-item")) : null;
      ConfigurationSection var2 = var1.getConfigurationSection("unidentification");
      this.unidentificationInfo = var2 == null ? ItemTier.UnidentificationInfo.DEFAULT : new ItemTier.UnidentificationInfo(var2);
      if (var1.contains("item-glow")) {
         this.itemHint = var1.getBoolean("item-glow.hint");
         this.glowColor = ChatColor.valueOf(UtilityMethods.enumName(var1.getString("item-glow.color", "WHITE")));
      } else {
         this.itemHint = false;
         this.glowColor = null;
      }

      this.tooltip = var1.isConfigurationSection("tooltip") ? new TooltipTexture(var1.getConfigurationSection("tooltip")) : null;
      this.chance = var1.getDouble("generation.chance");
      this.capacity = var1.contains("generation.capacity") ? new NumericStatFormula(var1.get("generation.capacity")) : null;
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public boolean hasDropTable() {
      return this.deconstructTable != null;
   }

   @Nullable
   public DropTable getDropTable() {
      return this.deconstructTable;
   }

   @Nullable
   public TooltipTexture getTooltip() {
      return this.tooltip;
   }

   public List<ItemStack> getDeconstructedLoot(@NotNull PlayerData var1) {
      return this.hasDropTable() ? this.deconstructTable.read(var1, false) : new ArrayList<>();
   }

   public boolean hasColor() {
      return this.glowColor != null;
   }

   @Nullable
   public ChatColor getColor() {
      return this.glowColor;
   }

   public boolean isHintEnabled() {
      return this.itemHint;
   }

   public double getGenerationChance() {
      return this.chance;
   }

   public boolean hasCapacity() {
      return this.capacity != null;
   }

   @Nullable
   public NumericStatFormula getModifierCapacity() {
      return this.capacity;
   }

   @NotNull
   public ItemTier.UnidentificationInfo getUnidentificationInfo() {
      return this.unidentificationInfo;
   }

   @NotNull
   public String getUnparsedName() {
      return this.unparsedName;
   }

   @Nullable
   public static ItemTier ofItem(NBTItem var0) {
      String var1 = var0.getString("MMOITEMS_TIER");
      return var1 == null ? null : MMOItems.plugin.getTiers().get(var1);
   }

   public static class UnidentificationInfo {
      @NotNull
      private final String name;
      @NotNull
      private final String prefix;
      private final int range;
      public static final String DEFAULT_NAME = "Unidentified Item";
      public static final String DEFAULT_PREFIX = "Unknown";
      public static final ItemTier.UnidentificationInfo DEFAULT = new ItemTier.UnidentificationInfo("Unidentified Item", "Unknown", 0);

      public UnidentificationInfo(@NotNull ConfigurationSection var1) {
         this(var1.getString("name", "Unidentified Item"), var1.getString("prefix", "Unknown"), var1.getInt("range"));
      }

      public UnidentificationInfo(@NotNull String var1, @NotNull String var2, int var3) {
         this.name = var1;
         this.prefix = var2;
         this.range = var3;
      }

      @NotNull
      public String getPrefix() {
         return this.prefix;
      }

      @NotNull
      public String getDisplayName() {
         return this.name;
      }

      public int[] calculateRange(int var1) {
         int var2 = (int)Math.max(1.0, var1 - this.range * ItemTier.RANDOM.nextDouble());
         return new int[]{var2, var2 + this.range};
      }
   }
}
