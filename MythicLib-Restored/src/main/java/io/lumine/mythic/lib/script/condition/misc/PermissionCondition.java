package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public class PermissionCondition extends Condition {
   private final String permNode;

   public PermissionCondition(ConfigObject config) {
      super(config);
      config.validateKeys("name");
      this.permNode = config.getString("name");
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return meta.getCaster().getPlayer().hasPermission(this.permNode);
   }
}
