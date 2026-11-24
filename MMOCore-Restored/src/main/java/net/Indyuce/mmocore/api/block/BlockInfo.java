package net.Indyuce.mmocore.api.block;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.condition.type.BlockCondition;
import io.lumine.mythic.lib.api.condition.type.MMOCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.droptable.DropTable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockInfo {
   private final BlockType block;
   private final DropTable table;
   private final RegenInfo regen;
   private final List<Trigger> triggers = new ArrayList<>();
   private final List<BlockCondition> conditions = new ArrayList<>();
   private final Map<BlockInfo.BlockInfoOption, Boolean> options = new HashMap<>();

   public BlockInfo(ConfigurationSection config) {
      Validate.notNull(config, "Could not load config");
      Validate.isTrue(config.contains("material"), "Could not find block type");
      this.block = MMOCore.plugin.loadManager.loadBlockType(new MMOLineConfig(config.getString("material")));
      this.table = config.contains("drop-table") ? MMOCore.plugin.dropTableManager.loadDropTable(config.get("drop-table")) : null;
      this.regen = config.contains("regen") ? new RegenInfo(config.getConfigurationSection("regen")) : null;
      if (config.contains("options")) {
         for (String key : config.getConfigurationSection("options").getKeys(false)) {
            try {
               BlockInfo.BlockInfoOption option = BlockInfo.BlockInfoOption.valueOf(key.toUpperCase().replace("-", "_").replace(" ", "_"));
               this.options.put(option, config.getBoolean("options." + key));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not load option '" + key + "' from block info '" + this.block.display() + "': " + exception.getMessage());
            }
         }
      }

      if (config.contains("triggers")) {
         List<String> list = config.getStringList("triggers");
         Validate.notNull(list, "Could not load triggers");

         for (String key : list) {
            try {
               this.triggers.add(MMOCore.plugin.loadManager.loadTrigger(new MMOLineConfig(key)));
            } catch (IllegalArgumentException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not load trigger '" + key + "' from block info '" + this.block.display() + "': " + exception.getMessage());
            }
         }
      }

      if (config.isList("conditions")) {
         for (String key : config.getStringList("conditions")) {
            MMOCondition condition = UtilityMethods.getCondition(key);
            if (condition instanceof BlockCondition) {
               this.conditions.add((BlockCondition)condition);
            }
         }
      }
   }

   public boolean getOption(BlockInfo.BlockInfoOption option) {
      return this.options.getOrDefault(option, option.getDefault());
   }

   @NotNull
   public BlockType getBlock() {
      return this.block;
   }

   @NotNull
   public DropTable getDropTable() {
      return Objects.requireNonNull(this.table, "Block has no drop table");
   }

   public boolean hasDropTable() {
      return this.table != null;
   }

   @Deprecated
   public List<ItemStack> collectDrops(LootBuilder builder) {
      return this.table != null ? this.table.collect(builder) : new ArrayList<>();
   }

   public boolean hasRegen() {
      return this.regen != null;
   }

   public boolean regenerates() {
      return this.regen != null;
   }

   public RegenInfo getRegenerationInfo() {
      return this.regen;
   }

   public BlockInfo.RegeneratingBlock startRegeneration(BlockData data, Location loc) {
      return new BlockInfo.RegeneratingBlock(data, loc, this);
   }

   public boolean hasTriggers() {
      return !this.triggers.isEmpty();
   }

   public List<Trigger> getTriggers() {
      return this.triggers;
   }

   public boolean checkConditions(Block block) {
      for (BlockCondition condition : this.conditions) {
         if (!condition.check(block)) {
            return false;
         }
      }

      return true;
   }

   public enum BlockInfoOption {
      VANILLA_DROPS(true),
      EXP_HOLOGRAMS(true);

      private final boolean def;

      BlockInfoOption(boolean def) {
         this.def = def;
      }

      public boolean getDefault() {
         return this.def;
      }
   }

   public static class RegeneratingBlock {
      private final BlockData data;
      private final Location loc;
      private final BlockInfo regenerating;
      private final long date = System.currentTimeMillis();

      public RegeneratingBlock(BlockData data, Location loc, BlockInfo regenerating) {
         this.data = data;
         this.loc = loc;
         this.regenerating = regenerating;
      }

      public boolean isTimedOut() {
         return this.date + this.regenerating.getRegenerationInfo().getTime() * 50 < System.currentTimeMillis();
      }

      public BlockData getBlockData() {
         return this.data;
      }

      public Location getLocation() {
         return this.loc;
      }

      public BlockInfo getRegeneratingBlock() {
         return this.regenerating;
      }
   }
}
