package io.lumine.mythic.lib.script.targeter;

import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.List;
import org.bukkit.entity.Entity;

@FunctionalInterface
public interface EntityTargeter {
   List<Entity> findTargets(SkillMetadata var1);
}
