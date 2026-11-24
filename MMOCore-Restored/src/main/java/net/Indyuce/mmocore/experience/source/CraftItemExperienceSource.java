package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CraftItemExperienceSource extends SpecificExperienceSource<Material> {
   public final Material material;

   public CraftItemExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      config.validate(new String[]{"type"});
      this.material = Material.valueOf(config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
   }

   @Override
   public ExperienceSourceManager<CraftItemExperienceSource> newManager() {
      return new CraftItemExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, Material obj) {
      return this.material == obj;
   }

   private static class Manager extends ExperienceSourceManager<CraftItemExperienceSource> {
      @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
      public void a(CraftItemEvent event) {
         if (event.getAction() != InventoryAction.NOTHING && event.getInventory().getResult() != null) {
            PlayerData data = PlayerData.get((Player)event.getWhoClicked());
            int index = this.getLowerAmountIngredientIndex(event.getInventory().getMatrix());
            int oldAmount = event.getInventory().getMatrix()[index].getAmount();
            int itemsCraftedPerRecipe = event.getInventory().getResult().getAmount();
            Material resultType = event.getInventory().getResult().getType();
            Bukkit.getScheduler().runTask(MMOCore.plugin, () -> {
               int newAmount = this.getAmount(event.getInventory().getMatrix()[index]);
               if (newAmount < oldAmount) {
                  int amountCrafted = (event.getClick().isShiftClick() ? oldAmount - newAmount : 1) * itemsCraftedPerRecipe;

                  for (CraftItemExperienceSource source : this.getSources()) {
                     if (source.matches(data, resultType)) {
                        source.giveExperience(data, amountCrafted, event.getInventory().getLocation());
                     }
                  }
               }
            });
         }
      }

      private int getAmount(@Nullable ItemStack item) {
         return item != null && item.getType() != Material.AIR ? item.getAmount() : 0;
      }

      private int getLowerAmountIngredientIndex(ItemStack[] matrix) {
         int lower = Integer.MAX_VALUE;
         int index = -1;

         for (int i = 0; i < matrix.length; i++) {
            ItemStack checked = matrix[i];
            if (checked != null && checked.getType() != Material.AIR && checked.getAmount() > 0 && checked.getAmount() < lower) {
               lower = checked.getAmount();
               index = i;
            }
         }

         Validate.isTrue(index != -1, "No item in matrix");
         return index;
      }
   }
}
