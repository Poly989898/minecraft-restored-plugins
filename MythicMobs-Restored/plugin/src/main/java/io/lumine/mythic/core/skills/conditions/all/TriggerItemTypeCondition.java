package io.lumine.mythic.core.skills.conditions.all;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Collection;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Ashijin",
   name = "triggerItemType",
   aliases = {"triggeringItemType"},
   description = "Tests the item type that triggered the skill"
)
public class TriggerItemTypeCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private Collection<AbstractItemStack> types = Sets.newHashSet();

   public TriggerItemTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      getPlugin().getClock().queueSecondPass(() -> {
         for (String s : mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "items", "item", "i"}, "DIRT", this.conditionVar)) {
            AbstractItemStack block = getPlugin().getBootstrap().createItem(s);
            this.types.add(block);
         }
      });
   }

   @Override
   public boolean check(SkillMetadata meta) {
      Optional<Object> maybeItem = meta.getMetadata("item");
      if (maybeItem.isPresent()) {
         ItemStack itemStack = (ItemStack)maybeItem.get();

         for (AbstractItemStack type : this.types) {
            if (type.isSimilar(BukkitAdapter.adapt(itemStack))) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Type {0} == {1}", type.toString(), itemStack.toString());
               return true;
            }

            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Type {0} != {1}", type.toString(), itemStack.toString());
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "triggerItemType Condition Failed: Item Metadata not found");
      }

      return false;
   }
}
