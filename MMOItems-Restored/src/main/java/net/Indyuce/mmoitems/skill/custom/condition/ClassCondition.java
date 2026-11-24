package net.Indyuce.mmoitems.skill.custom.condition;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;

@Deprecated
public class ClassCondition extends Condition {
   private final List<String> classes;

   public ClassCondition(ConfigObject configObject) {
      super(configObject);
      configObject.validateKeys(new String[]{"list"});
      this.classes = Arrays.asList(configObject.getString("list").split(","));
   }

   public boolean isMet(SkillMetadata skillMetadata) {
      PlayerData playerData = PlayerData.get(skillMetadata.getCaster().getPlayer());
      return this.classes.contains(playerData.getRPG().getClassName());
   }
}
