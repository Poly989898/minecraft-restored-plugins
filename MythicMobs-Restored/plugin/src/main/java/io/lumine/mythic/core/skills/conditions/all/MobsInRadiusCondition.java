package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.targeters.shapes.ITargeterShape;
import io.lumine.mythic.core.skills.targeters.shapes.TargeterShapes;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.entity.EntityType;

@MythicCondition(
   author = "Ashijin",
   name = "mobsinradius",
   aliases = {},
   description = "Matches a range to how many mobs are in the given radius"
)
public class MobsInRadiusCondition extends SkillCondition implements ISkillMetaCondition, ILocationCondition {
   @MythicField(
      name = "types",
      aliases = {"types", "t"},
      description = "The mobtype to check for"
   )
   private HashSet<MythicMob> mmTypes = new HashSet<>();
   private HashSet<BukkitEntityType> meTypes = new HashSet<>();
   private HashSet<EntityType> vanillaTypes = new HashSet<>();
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number range to match"
   )
   private final RangedDouble amount;
   @MythicField(
      name = "shape",
      description = "Selection shape - SPHERE (default), CYLINDER, or SQUARE. Reads radius/r; CYLINDER also reads height/h and yOffset"
   )
   private final ITargeterShape shape;
   final Set<String> mappedEntityTypes = Arrays.stream(EntityType.values()).map(Enum::toString).collect(Collectors.toSet());

   public MobsInRadiusCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"amount", "a"}, "1", this.conditionVar);
      this.amount = new RangedDouble(k);
      String shapeName = mlc.getString(new String[]{"shape"}, "SPHERE");
      this.shape = TargeterShapes.from(shapeName).create(mlc);
      String types = mlc.getString(new String[]{"types", "type", "t"}, "");
      String[] ss = types.split(",");
      getPlugin().getSkillManager().queueSecondPass(() -> {
         for (String s : ss) {
            if (this.mappedEntityTypes.contains(s.toUpperCase())) {
               this.vanillaTypes.add(EntityType.valueOf(s.toUpperCase()));
            }

            MythicMob mm = getPlugin().getMobManager().getMythicMob(s).orElseGet(() -> null);
            if (mm != null) {
               this.mmTypes.add(mm);
            } else {
               BukkitEntityType me = BukkitEntityType.getMythicEntity(s);
               if (me != null) {
                  this.meTypes.add(me);
               } else {
                  MythicLogger.errorConditionConfig(this, mlc, "The 'type' attribute must be a valid MythicMob or MythicEntity type.");
               }
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "MobsInRadius loaded " + (this.meTypes.size() + this.mmTypes.size()) + " types");
      });
   }

   @Override
   public boolean check(SkillMetadata meta) {
      SkillCaster caster = meta.getCaster();
      AbstractLocation location = caster.getEntity().getLocation();
      Collection<AbstractEntity> found = this.shape
         .getEntities(meta, location, entity -> entity.getUniqueId().equals(caster.getEntity().getUniqueId()) ? false : this.matchesType(entity));
      return this.amount.equals(found.size());
   }

   @Override
   public boolean check(AbstractLocation location) {
      Collection<AbstractEntity> found = this.shape.getEntities(null, location, this::matchesType);
      return this.amount.equals(found.size());
   }

   private boolean matchesType(AbstractEntity entity) {
      if (this.vanillaTypes.contains(entity.getBukkitEntity().getType())) {
         return true;
      } else if (!getPlugin().getMobManager().isLoadedMob(entity)) {
         return false;
      } else {
         ActiveMob amCompare = getPlugin().getMobManager().getMythicMobInstance(entity);
         if (amCompare != null) {
            return this.mmTypes.contains(amCompare.getType());
         } else {
            for (BukkitEntityType me : this.meTypes) {
               if (me.compare(entity.getBukkitEntity())) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
