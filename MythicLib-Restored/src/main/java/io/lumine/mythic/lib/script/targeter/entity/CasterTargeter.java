package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Entity;

public class CasterTargeter implements EntityTargeter {
   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      return Arrays.asList(meta.getCaster().getPlayer());
   }
}
