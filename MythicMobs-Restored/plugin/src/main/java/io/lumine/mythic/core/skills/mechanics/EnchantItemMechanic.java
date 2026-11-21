package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.io.File;
import net.kyori.adventure.key.Key;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "enchantitem",
   description = "Enchants the target item"
)
public class EnchantItemMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   @MythicField(
      name = "enchantment",
      aliases = {"enchant", "ench", "e"},
      description = "The enchantment to apply"
   )
   private final PlaceholderString enchantment;
   @MythicField(
      name = "level",
      aliases = {"l"},
      description = "The level of the enchantment",
      defValue = "1"
   )
   private final PlaceholderInt level;
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The equipment slot to target"
   )
   private final PlaceholderString slot;

   public EnchantItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.enchantment = mlc.getPlaceholderString(new String[]{"enchantment", "enchant", "ench", "e"}, "minecraft:sharpness");
      this.level = mlc.getPlaceholderInteger(new String[]{"level", "l"}, 1);
      this.slot = mlc.getPlaceholderString(new String[]{"slot", "s"}, "HAND");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      return this.cast(data, data.getCaster().getEntity());
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.cast(data, target);
   }

   private SkillResult cast(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Invalid target");
         return SkillResult.INVALID_TARGET;
      } else {
         EquipmentSlot slot;
         try {
            slot = EquipSlot.of(this.slot.get(data, target)).getBukkitSlot();
         } catch (Throwable e) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Slot {0} not found", this.slot.get(data, target));
            return SkillResult.INVALID_CONFIG;
         }

         Player player = (Player)target.getBukkitEntity();
         String enchantString = this.enchantment.get(data);
         Key enchantKey = enchantString.contains(":") ? Key.key(enchantString) : Key.key("minecraft", enchantString);
         int level = this.level.get(data);
         Enchantment enchant = (Enchantment)RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(enchantKey);
         if (enchant == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Enchantment {0} not found", enchantKey);
            return SkillResult.INVALID_CONFIG;
         } else {
            Runnable applyRunnable = () -> {
               ItemStack item = player.getInventory().getItem(slot);
               item.addUnsafeEnchantment(enchant, level);
               Events.call(new MythicModifiedInventoryEvent(data.getCaster(), player));
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Enchantment {0} applied to {1}", enchantKey, slot.toString());
            };
            Schedulers.runOnRegion(player, applyRunnable);
            return SkillResult.SUCCESS;
         }
      }
   }
}
