package net.Indyuce.mmocore.manager.profession;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.block.BlockInfo;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.SkullBlockType;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.chest.condition.ConditionInstance;
import net.Indyuce.mmocore.paperlib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomBlockManager extends SpecificProfessionManager {
   private final Map<BlockType, BlockInfo> map = new HashMap<>();
   private final Set<BlockInfo.RegeneratingBlock> active = new HashSet<>();
   private final List<Condition> customMineConditions = new ArrayList<>();
   private final List<Function<Block, Optional<BlockType>>> blockTypes = new ArrayList<>();
   private boolean protect;

   public CustomBlockManager() {
      super("on-mine");
      this.registerBlockType(block -> MMOCoreUtils.isPlayerHead(block.getType()) ? Optional.of(new SkullBlockType(block)) : Optional.empty());
   }

   public void registerBlockType(Function<Block, Optional<BlockType>> function) {
      this.blockTypes.add(function);
   }

   public void register(@NotNull BlockInfo regen) {
      this.map.put(regen.getBlock(), regen);
   }

   @Nullable
   public BlockInfo getInfo(@NotNull Block block) {
      return this.map.get(this.findBlockType(block));
   }

   @NotNull
   public BlockType findBlockType(Block block) {
      for (Function<Block, Optional<BlockType>> blockType : this.blockTypes) {
         Optional<BlockType> type = blockType.apply(block);
         if (type.isPresent()) {
            return type.get();
         }
      }

      return new VanillaBlockType(block);
   }

   public void initialize(BlockInfo.RegeneratingBlock info, boolean scheduleRegen) {
      if (scheduleRegen) {
         this.active.add(info);
         Bukkit.getScheduler().runTaskLater(MMOCore.plugin, () -> this.regen(info, false), info.getRegeneratingBlock().getRegenerationInfo().getTime());
      }

      if (info.getRegeneratingBlock().getRegenerationInfo().hasTemporaryBlock()) {
         info.getRegeneratingBlock().getRegenerationInfo().getTemporaryBlock().place(info);
      }
   }

   private void regen(BlockInfo.RegeneratingBlock info, boolean shutdown) {
      PaperLib.getChunkAtAsync(info.getLocation()).whenComplete((chunk, ex) -> {
         info.getRegeneratingBlock().getBlock().regenerate(info);
         info.getLocation().getBlock().getState().update();
         if (!shutdown) {
            this.active.remove(info);
         }
      });
   }

   public void resetRemainingBlocks() {
      this.active.forEach(info -> this.regen(info, true));
   }

   public boolean isTemporaryBlock(Block block) {
      Location loc = block.getLocation();

      for (BlockInfo.RegeneratingBlock info : this.active) {
         if (info.getLocation().getBlockX() == loc.getBlockX()
            && info.getLocation().getBlockY() == loc.getBlockY()
            && info.getLocation().getBlockZ() == loc.getBlockZ()) {
            return true;
         }
      }

      return false;
   }

   public boolean isEnabled(Entity entity) {
      return this.isEnabled(entity, entity.getLocation());
   }

   public boolean isEnabled(Entity entity, Location loc) {
      if (this.customMineConditions.isEmpty()) {
         return false;
      }

      ConditionInstance conditionEntity = new ConditionInstance(entity, loc);

      for (Condition condition : this.customMineConditions) {
         if (!condition.isMet(conditionEntity)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void loadProfessionConfiguration(ConfigurationSection config) {
      for (String key : config.getKeys(false)) {
         try {
            this.register(new BlockInfo(config.getConfigurationSection(key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not load custom block '" + key + "': " + exception.getMessage());
         }
      }
   }

   public boolean shouldProtect() {
      return this.protect;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.customMineConditions.clear();
         this.map.clear();
      }

      this.protect = MMOCore.plugin.getConfig().getBoolean("protect-custom-mine");

      for (String key : MMOCore.plugin.getConfig().getStringList("custom-mine-conditions")) {
         try {
            this.customMineConditions.add(MMOCore.plugin.loadManager.loadCondition(new MMOLineConfig(key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load custom mining condition '" + key + "': " + exception.getMessage());
         }
      }
   }
}
