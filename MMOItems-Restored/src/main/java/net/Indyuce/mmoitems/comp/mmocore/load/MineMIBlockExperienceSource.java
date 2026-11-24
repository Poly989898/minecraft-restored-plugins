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

   public MineMIBlockExperienceSource(ExperienceDispenser experienceDispenser, MMOLineConfig mmoLineConfig) {
      super(experienceDispenser, mmoLineConfig);
      mmoLineConfig.validate(new String[]{"id"});
      this.id = mmoLineConfig.getInt("id", 1);
      this.silkTouch = mmoLineConfig.getBoolean("silk-touch", true);
      this.playerPlaced = mmoLineConfig.getBoolean("player-placed", false);
   }

   public ExperienceSourceManager<MineMIBlockExperienceSource> newManager() {
      return new ExperienceSourceManager<MineMIBlockExperienceSource>() {
         @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
         public void a(BlockBreakEvent event) {
            if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
               PlayerData playerData = PlayerData.get(event.getPlayer());
               Optional optional = MMOItems.plugin.getCustomBlocks().getFromBlock(event.getBlock().getBlockData());
               if (optional.isPresent()) {
                  for (MineMIBlockExperienceSource source : this.getSources()) {
                     if ((!source.silkTouch || !MineMIBlockExperienceSource.this.hasSilkTouch(event.getPlayer().getInventory().getItemInMainHand()))
                        && (source.playerPlaced || !event.getBlock().hasMetadata("player_placed"))
                        && source.matches(playerData, ((CustomBlock)optional.get()).getId())) {
                        source.giveExperience(playerData, 1.0, event.getBlock().getLocation());
                     }
                  }
               }
            }
         }
      };
   }

   private boolean hasSilkTouch(ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
   }

   public boolean matchesParameter(PlayerData playerData, Integer value) {
      return this.id == value;
   }
}
