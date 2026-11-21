package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "giveitemfromtarget",
   aliases = {"givefromtarget", "giveitemsfromtarget", "itemgivefromtarget"},
   description = "Gives the target an item or droptable"
)
public class GiveItemFromTargetMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The item(s) or droptable to give"
   )
   private DropTable dropTable;
   @MythicField(
      name = "doFakeLooting",
      aliases = {"fl", "fakelooting"},
      description = "Whether to play the pickup animation from the origin",
      defValue = "false"
   )
   private final boolean doFakeLooting;
   private String itemString;

   public GiveItemFromTargetMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doFakeLooting = mlc.getBoolean(new String[]{"dofakelooting", "fakelooting", "fl"}, true);
      this.itemString = mlc.getString(new String[]{"items", "item", "i"});
      this.getPlugin().getDropManager().queueSecondPass(() -> {
         Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(this.itemString);
         if (maybeTable.isPresent()) {
            this.dropTable = maybeTable.get();
         } else {
            if (this.itemString.startsWith("\"")) {
               try {
                  this.itemString = this.itemString.substring(1, this.itemString.length() - 1);
               } catch (Exception var4x) {
                  MythicLogger.errorMechanicConfig(this, mlc, "The 'items' attribute is required.");
               }

               this.itemString = SkillString.parseMessageSpecialChars(this.itemString);
            }

            List<String> items = new ArrayList<>(Arrays.asList(this.itemString.split(",")));
            this.dropTable = new DropTable("DropItemMechanic", "DropItemMechanic", items);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.castAtLocation(data, target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      AbstractEntity caster = data.getCaster().getEntity();
      LootBag loot = this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      if (caster.isPlayer()) {
         loot.give(caster.asPlayer(), false);
      }

      if (this.doFakeLooting) {
         if (!caster.isPlayer()) {
            return SkillResult.SUCCESS;
         }

         Player player = (Player)caster.getBukkitEntity();

         for (Drop type : loot.getLootTable()) {
            double amount = type.getAmount();
            IDrop drop;
            if (type instanceof CustomDrop) {
               if (((CustomDrop)type).getDrop().isEmpty()) {
                  continue;
               }

               drop = ((CustomDrop)type).getDrop().get();
            } else {
               drop = type;
            }

            if (drop instanceof IItemDrop) {
               ItemStack stack = ((BukkitItemStack)((IItemDrop)drop).getDrop(new DropMetadataImpl(data.getCaster(), caster, data, null), amount))
                  .getItemStack();
               int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, stack, target);
               Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
            }
         }
      }

      return SkillResult.SUCCESS;
   }
}
