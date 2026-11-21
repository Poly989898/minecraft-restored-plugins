package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "giveitemfromslot",
   aliases = {"givefromslot"},
   description = "Gives the target an item in the caster's equipment"
)
public class GiveItemFromSlotMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The caster's equipment slot",
      defValue = "NONE"
   )
   private final EquipSlot slot;
   @MythicField(
      name = "doFakeLooting",
      aliases = {"fl", "fakelooting"},
      description = "Whether to play the pickup animation from the origin",
      defValue = "false"
   )
   private final boolean doFakeLooting;

   public GiveItemFromSlotMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doFakeLooting = mlc.getBoolean(new String[]{"dofakelooting", "fakelooting", "fl"}, false);
      String itemString = mlc.getString(new String[]{"slot", "s"}, "NONE");
      this.slot = EquipSlot.of(itemString);
      if (this.slot == EquipSlot.NONE) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid item slot supplied.");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Optional<AbstractItemStack> maybeItem = this.slot.get(data.getCaster().getEntity());
      if (!maybeItem.isPresent()) {
         return SkillResult.INVALID_CONFIG;
      } else if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Player player = (Player)target.getBukkitEntity();
         AbstractItemStack stack = maybeItem.orElse(null);
         if (stack == null) {
            return SkillResult.INVALID_TARGET;
         } else {
            Schedulers.runOnRegion(
               player,
               () -> player.getInventory()
                  .addItem(new ItemStack[]{BukkitAdapter.adapt(stack)})
                  .forEach((i, is) -> player.getWorld().dropItemNaturally(player.getLocation(), is))
            );
            if (this.doFakeLooting) {
               int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, BukkitAdapter.adapt(stack), data.getOrigin());
               Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
            }

            return SkillResult.SUCCESS;
         }
      }
   }
}
