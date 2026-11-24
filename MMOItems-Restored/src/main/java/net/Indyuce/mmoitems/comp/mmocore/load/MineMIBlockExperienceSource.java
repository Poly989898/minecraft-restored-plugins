package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MineMIBlockExperienceSource extends SpecificExperienceSource<Integer> {
   private final int id;
   private final boolean silkTouch;
   private final boolean playerPlaced;

   public MineMIBlockExperienceSource(ExperienceDispenser var1, MMOLineConfig var2) {
      super(var1, var2);
      var2.validate(new String[]{"id"});
      this.id = var2.getInt("id", 1);
      this.silkTouch = var2.getBoolean("silk-touch", true);
      this.playerPlaced = var2.getBoolean("player-placed", false);
   }

   public ExperienceSourceManager<MineMIBlockExperienceSource> newManager() {
      return new ExperienceSourceManager<MineMIBlockExperienceSource>() {
         @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
         public void a(BlockBreakEvent var1) {
            if (var1.getPlayer().getGameMode() == GameMode.SURVIVAL) {
               PlayerData var2 = PlayerData.get(var1.getPlayer());
               Optional var3 = MMOItems.plugin.getCustomBlocks().getFromBlock(var1.getBlock().getBlockData());
               if (var3.isPresent()) {
                  for (MineMIBlockExperienceSource var5 : this.getSources()) {
                     if ((!var5.silkTouch || !MineMIBlockExperienceSource.this.hasSilkTouch(var1.getPlayer().getInventory().getItemInMainHand()))
                        && (var5.playerPlaced || !var1.getBlock().hasMetadata("player_placed"))
                        && var5.matches(var2, ((CustomBlock)var3.get()).getId())) {
                        var5.giveExperience(var2, 1.0, var1.getBlock().getLocation());
                     }
                  }
               }
            }
         }
      };
   }

   private boolean hasSilkTouch(ItemStack var1) {
      return var1 != null && var1.hasItemMeta() && var1.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
   }

   public boolean matchesParameter(PlayerData var1, Integer var2) {
      return this.id == var2;
   }
}
