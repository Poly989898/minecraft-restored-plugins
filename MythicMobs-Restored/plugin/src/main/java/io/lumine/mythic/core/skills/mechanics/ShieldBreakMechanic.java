package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@MythicMechanic(
   author = "MarcatoSound",
   name = "shieldbreak",
   aliases = {"disableshield"},
   description = "Breaks the target player's shield block."
)
public class ShieldBreakMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private final PlaceholderDuration duration;

   public ShieldBreakMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "100");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return !(BukkitAdapter.adapt(target) instanceof Player player) ? SkillResult.INVALID_TARGET : this.breakShield(player, data);
   }

   private SkillResult breakShield(Player player, SkillMetadata data) {
      if (!player.isBlocking()) {
         return SkillResult.CONDITION_FAILED;
      } else {
         PlayerInventory inv = player.getInventory();
         ItemStack shield = inv.getItemInMainHand();
         EquipmentSlot hand = EquipmentSlot.HAND;
         if (shield.getType() != Material.SHIELD) {
            shield = inv.getItemInOffHand();
            hand = EquipmentSlot.OFF_HAND;
         }

         if (shield.getType() != Material.SHIELD) {
            return SkillResult.CONDITION_FAILED;
         } else {
            player.setCooldown(Material.SHIELD, this.duration.get(data, data.getCaster().getEntity()).getTicks());
            player.playSound(player.getLocation(), "minecraft:item.shield.block", 1.0F, 1.0F);
            player.playSound(player.getLocation(), "minecraft:item.shield.break", 1.0F, 1.0F);
            inv.setItem(hand, null);
            ItemStack finalShield = shield;
            EquipmentSlot finalHand = hand;
            Bukkit.getScheduler().runTaskLater(MythicBukkit.inst(), () -> {
               ItemStack newHandItem = inv.getItem(finalHand);
               if (newHandItem != null && newHandItem.getType() != Material.AIR) {
                  if (inv.firstEmpty() == -1) {
                     player.getWorld().dropItem(player.getLocation(), newHandItem);
                  } else {
                     inv.addItem(new ItemStack[]{newHandItem});
                  }

                  inv.setItem(finalHand, null);
               }

               inv.setItem(finalHand, finalShield);
            }, 1L);
            return SkillResult.SUCCESS;
         }
      }
   }
}
