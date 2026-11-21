package io.lumine.mythic.core.skills.placeholders.all;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.metadata.Pair;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ConditionalPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private final List<Pair<PlaceholderString, List<SkillCondition>>> values = Lists.newArrayList();
   private PlaceholderString defaultValue;

   public ConditionalPlaceholder(PlaceholderExecutor manager, MythicConfig config) {
      super(GenericPlaceholder.GenericPlaceholderArguments.builder().usedPlaceholderArguments((short)0).manager(manager).build());
      manager.getPlugin().getSkillManager().queueSecondPass(() -> {
         this.defaultValue = config.getPlaceholderString("Default", null);

         for (String node : config.getKeys()) {
            if (!node.equals("Default")) {
               MythicConfig nestedConfig = config.getNestedConfig(node);
               PlaceholderString value = nestedConfig.getPlaceholderString("Value");
               List<SkillCondition> conditions = Lists.newArrayList();

               for (String line : nestedConfig.getStringList("Conditions")) {
                  conditions.add(manager.getPlugin().getSkillManager().getCondition(line));
               }

               this.values.add(Pair.of(value, conditions));
            }
         }
      });
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      for (Pair<PlaceholderString, List<SkillCondition>> entry : this.values) {
         Iterator iterator = entry.getValue().iterator();
         if (iterator.hasNext()) {
            SkillCondition condition = (SkillCondition)iterator.next();
            if (!(placeholderContext.meta() instanceof SkillMetadata skillMetadata) || condition.evaluateCaster(skillMetadata)) {
               return entry.getKey().get(placeholderContext);
            }
         }
      }

      return this.defaultValue.get(placeholderContext);
   }
}
