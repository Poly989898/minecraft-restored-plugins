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

   public ClassCondition(ConfigObject var1) {
      super(var1);
      var1.validateKeys(new String[]{"list"});
      this.classes = Arrays.asList(var1.getString("list").split(","));
   }

   public boolean isMet(SkillMetadata var1) {
      PlayerData var2 = PlayerData.get(var1.getCaster().getPlayer());
      return this.classes.contains(var2.getRPG().getClassName());
   }
}
