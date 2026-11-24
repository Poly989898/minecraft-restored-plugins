package net.Indyuce.mmocore.loot.chest;

import io.lumine.mythic.lib.api.math.ScalingFormula;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.loot.Weighted;
import net.Indyuce.mmocore.loot.droptable.DropTable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestTier implements Weighted {
   private final TierEffect effect;
   @Nullable
   private final ScalingFormula capacity;
   private final DropTable table;
   private final double chance;

   public ChestTier(ConfigurationSection config) {
      this.effect = config.isConfigurationSection("effect") ? new TierEffect(config.getConfigurationSection("effect")) : null;
      this.capacity = config.contains("capacity") ? new ScalingFormula(config.get("capacity")) : null;
      this.chance = config.getDouble("chance");
      this.table = MMOCore.plugin.dropTableManager.loadDropTable(config.get("drops"));
   }

   public double rollCapacity(@NotNull PlayerData player) {
      return this.capacity == null ? this.table.getCapacity() : this.capacity.calculate(player.getLevel());
   }

   public double getChance() {
      return this.chance;
   }

   @Override
   public double getWeight() {
      return this.chance;
   }

   @NotNull
   public DropTable getDropTable() {
      return this.table;
   }

   public boolean hasEffect() {
      return this.effect != null;
   }

   public TierEffect getEffect() {
      return this.effect;
   }
}
