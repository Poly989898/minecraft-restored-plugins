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

   public ItemTier(@NotNull ConfigurationSection section) {
      this.id = section.getName().toUpperCase().replace("-", "_");
      this.unparsedName = section.getString("name");
      this.name = MythicLib.plugin.parseColors(this.unparsedName);
      this.deconstructTable = section.contains("deconstruct-item") ? new DropTable(section.getConfigurationSection("deconstruct-item")) : null;
      ConfigurationSection unidentification = section.getConfigurationSection("unidentification");
      this.unidentificationInfo = unidentification == null ? ItemTier.UnidentificationInfo.DEFAULT : new ItemTier.UnidentificationInfo(unidentification);
      if (section.contains("item-glow")) {
         this.itemHint = section.getBoolean("item-glow.hint");
         this.glowColor = ChatColor.valueOf(UtilityMethods.enumName(section.getString("item-glow.color", "WHITE")));
      } else {
         this.itemHint = false;
         this.glowColor = null;
      }

      this.tooltip = section.isConfigurationSection("tooltip") ? new TooltipTexture(section.getConfigurationSection("tooltip")) : null;
      this.chance = section.getDouble("generation.chance");
      this.capacity = section.contains("generation.capacity") ? new NumericStatFormula(section.get("generation.capacity")) : null;
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

   public List<ItemStack> getDeconstructedLoot(@NotNull PlayerData playerData) {
      return this.hasDropTable() ? this.deconstructTable.read(playerData, false) : new ArrayList<>();
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
   public static ItemTier ofItem(NBTItem nbtItem) {
      String mMOITEMSTIER = nbtItem.getString("MMOITEMS_TIER");
      return mMOITEMSTIER == null ? null : MMOItems.plugin.getTiers().get(mMOITEMSTIER);
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

      public UnidentificationInfo(@NotNull ConfigurationSection section) {
         this(section.getString("name", "Unidentified Item"), section.getString("prefix", "Unknown"), section.getInt("range"));
      }

      public UnidentificationInfo(@NotNull String text, @NotNull String prefix, int count) {
         this.name = text;
         this.prefix = prefix;
         this.range = count;
      }

      @NotNull
      public String getPrefix() {
         return this.prefix;
      }

      @NotNull
      public String getDisplayName() {
         return this.name;
      }

      public int[] calculateRange(int count) {
         int reducedCount = (int)Math.max(1.0, count - this.range * ItemTier.RANDOM.nextDouble());
         return new int[]{reducedCount, reducedCount + this.range};
      }
   }
}
