package io.lumine.mythic.core.skills;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

public abstract class SkillTargeter {
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final SkillExecutor manager;
   protected MythicLineConfig config;
   @Getter
   protected Optional<SkillHolder> parent = Optional.empty();
   protected String targetConditionString;
   protected List<SkillCondition> targetConditions = null;
   @Getter
   protected boolean sudoParent;
   @Getter
   protected boolean sudoOwner;
   @Getter
   protected boolean sudoTrigger;
   @Getter
   protected SkillTargeter fallback;

   public SkillTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.config = mlc;
      this.targetConditionString = mlc.getString(new String[]{"targetconditions", "conditions", "cond", "c"}, "null");
      this.sudoParent = mlc.getBoolean(new String[]{"parent", "asparent", "ofparent", "fromparent", "sudoparent"}, false);
      this.sudoOwner = mlc.getBoolean(new String[]{"owner", "asowner", "ofowner", "fromowner", "sudoowner"}, false);
      this.sudoTrigger = mlc.getBoolean(new String[]{"trigger", "astrigger", "oftrigger", "fromtrigger", "sudotrigger"}, false);
      if (MythicBukkit.isVolatile() && this.targetConditionString != null) {
         this.targetConditions = this.getPlugin().getSkillManager().getConditions(this.targetConditionString);
      }

      String fallbackTargeterString = mlc.getString(new String[]{"fallback", "fb"}, "");
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (!fallbackTargeterString.isBlank()) {
            this.fallback = this.getPlugin().getSkillManager().getTargeter(fallbackTargeterString);
         }
      });
   }

   public void setParent(SkillHolder parent) {
      this.parent = Optional.ofNullable(parent);
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   protected Collection<AbstractLocation> getMetaLocations(SkillMetadata data) {
      Collection<AbstractLocation> locations = Lists.newArrayList();
      if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
         for (AbstractEntity entity : data.getEntityTargets()) {
            locations.add(entity.getLocation());
         }
      } else if (data.getLocationTargets() != null && !data.getLocationTargets().isEmpty()) {
         for (AbstractLocation location : data.getLocationTargets()) {
            locations.add(location);
         }
      }

      return locations;
   }
}
