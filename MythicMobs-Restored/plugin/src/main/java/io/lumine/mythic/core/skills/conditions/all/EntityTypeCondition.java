package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersion;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.entity.ZombieVillager;

@MythicCondition(
   author = "Ashijin",
   name = "entityType",
   aliases = {"mobtype"},
   description = "Tests the entity type of the target"
)
public class EntityTypeCondition extends SkillCondition implements IEntityCondition {
   private static final MinecraftVersion COPPER_GOLEM_VERSION = MinecraftVersion.parse("1.21.9");
   private static final MinecraftVersion ZOMBIE_NAUTILUS_VERSION = MinecraftVersion.parse("1.21.11");
   @MythicField(
      name = "type",
      aliases = {"types", "t"},
      description = "A list of entity types to match"
   )
   protected EnumSet<EntityType> entityTypes;
   protected final Map<EntityType, List<EntityTypeCondition.TypeConstraint>> constraintsByType = new EnumMap<>(EntityType.class);

   public EntityTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      String entityTypesStringList = mlc.getString(new String[]{"types", "type", "t"}, null, this.conditionVar);
      Collection<EntityType> collection = new HashSet<>();
      if (entityTypesStringList != null) {
         for (String entityTypeString : mlc.parseStringList(entityTypesStringList)) {
            EntityTypeCondition.ParsedTypeEntry parsedTypeEntry = parseTypeEntry(entityTypeString);

            for (EntityType e : EntityType.values()) {
               if (e.toString().matches(Patterns.toRegex(parsedTypeEntry.typePattern().toUpperCase(Locale.ROOT)))) {
                  collection.add(e);
                  this.constraintsByType.computeIfAbsent(e, key -> new ArrayList<>()).add(parsedTypeEntry.constraint());
               }
            }
         }
      }

      this.entityTypes = !collection.isEmpty() ? EnumSet.copyOf(collection) : EnumSet.noneOf(EntityType.class);
   }

   @Override
   public boolean check(AbstractEntity targetEntity) {
      Entity entity = targetEntity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Boolean result = Schedulers.of(entity).supply(() -> this.checkEntityType(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkEntityType(entity);
      }
   }

   private boolean checkEntityType(Entity entity) {
      EntityType entityType = entity.getType();
      if (!this.entityTypes.contains(entityType)) {
         return false;
      } else {
         List<EntityTypeCondition.TypeConstraint> constraints = this.constraintsByType.get(entityType);
         if (constraints != null && !constraints.isEmpty()) {
            for (EntityTypeCondition.TypeConstraint constraint : constraints) {
               if (matchesConstraint(entity, constraint)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   private static boolean matchesConstraint(Entity entity, EntityTypeCondition.TypeConstraint constraint) {
      for (Entry<EntityTypeCondition.EntityField, Set<String>> requirement : constraint.requirements().entrySet()) {
         if (!EntityTypeCondition.EntityFieldResolver.matches(entity, requirement.getKey(), requirement.getValue())) {
            return false;
         }
      }

      return true;
   }

   private static EntityTypeCondition.ParsedTypeEntry parseTypeEntry(String rawTypeEntry) {
      if (rawTypeEntry == null) {
         return new EntityTypeCondition.ParsedTypeEntry("", EntityTypeCondition.TypeConstraint.empty());
      } else {
         String typeEntry = rawTypeEntry.trim();
         int optionsStart = typeEntry.indexOf(123);
         if (optionsStart >= 0 && typeEntry.endsWith("}")) {
            String type = typeEntry.substring(0, optionsStart).trim();
            String options = typeEntry.substring(optionsStart + 1, typeEntry.length() - 1);
            EntityTypeCondition.TypeConstraint constraint = parseConstraintOptions(new MythicLineConfigImpl("entityType{" + options + "}"));
            return new EntityTypeCondition.ParsedTypeEntry(type, constraint);
         } else {
            return new EntityTypeCondition.ParsedTypeEntry(typeEntry, EntityTypeCondition.TypeConstraint.empty());
         }
      }
   }

   private static EntityTypeCondition.TypeConstraint parseConstraintOptions(MythicLineConfig options) {
      EntityTypeCondition.TypeConstraint constraint = new EntityTypeCondition.TypeConstraint();
      constraint.add(EntityTypeCondition.EntityField.VARIANT, options.getStringList(new String[]{"variant", "v"}, null));
      constraint.add(EntityTypeCondition.EntityField.COLOR, options.getStringList(new String[]{"color", "c"}, null));
      constraint.add(EntityTypeCondition.EntityField.STYLE, options.getStringList(new String[]{"style", "s"}, null));
      constraint.add(EntityTypeCondition.EntityField.MAIN_GENE, options.getStringList(new String[]{"mainGene", "maingene", "main", "mg"}, null));
      constraint.add(EntityTypeCondition.EntityField.HIDDEN_GENE, options.getStringList(new String[]{"hiddenGene", "hiddengene", "hidden", "hg"}, null));
      constraint.add(EntityTypeCondition.EntityField.BIOME, options.getStringList(new String[]{"biome", "b"}, null));
      constraint.add(EntityTypeCondition.EntityField.PROFESSION, options.getStringList(new String[]{"profession", "prof", "proffesion"}, null));
      constraint.add(EntityTypeCondition.EntityField.CRACK, options.getStringList(new String[]{"crack", "cracks", "crackedness", "crackiness"}, null));
      constraint.add(EntityTypeCondition.EntityField.BODY_COLOR, options.getStringList(new String[]{"bodyColor", "bodycolor", "body", "bc"}, null));
      constraint.add(EntityTypeCondition.EntityField.PATTERN_COLOR, options.getStringList(new String[]{"patternColor", "patterncolor", "pc"}, null));
      constraint.add(EntityTypeCondition.EntityField.PATTERN, options.getStringList(new String[]{"pattern", "p"}, null));
      return constraint;
   }

   private static Set<String> parseValueList(List<String> rawValues) {
      Set<String> values = new HashSet<>();
      if (rawValues == null) {
         return values;
      } else {
         for (String rawValue : rawValues) {
            String normalized = normalizeVariant(rawValue);
            if (normalized != null) {
               values.add(normalized);
            }
         }

         return values;
      }
   }

   private static String normalizeVariant(String rawVariant) {
      if (rawVariant == null) {
         return null;
      } else {
         String normalized = rawVariant.trim();
         if (normalized.isEmpty()) {
            return null;
         } else {
            int namespaceSplit = normalized.indexOf(58);
            if (namespaceSplit >= 0) {
               normalized = normalized.substring(namespaceSplit + 1);
            }

            normalized = normalized.replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
            return normalized.replace("OXIDISED", "OXIDIZED");
         }
      }
   }

   private static boolean isRainbowSheep(Sheep sheep) {
      return sheep.customName() == null ? false : "jeb_".equals(PlainTextComponentSerializer.plainText().serialize(sheep.customName()));
   }

   private static String getIronGolemCrackState(IronGolem ironGolem) {
      AttributeInstance maxHealthAttr = ironGolem.getAttribute(Attribute.MAX_HEALTH);
      if (maxHealthAttr == null) {
         return "NONE";
      } else {
         double healthRatio = ironGolem.getHealth() / maxHealthAttr.getValue();
         if (healthRatio < 0.25) {
            return "HIGH";
         } else if (healthRatio < 0.5) {
            return "MEDIUM";
         } else {
            return healthRatio < 0.75 ? "LOW" : "NONE";
         }
      }
   }

   private static enum EntityField {
      VARIANT,
      COLOR,
      STYLE,
      MAIN_GENE,
      HIDDEN_GENE,
      BIOME,
      PROFESSION,
      CRACK,
      BODY_COLOR,
      PATTERN_COLOR,
      PATTERN;
   }

   private static final class EntityFieldResolver {
      private static boolean matches(Entity entity, EntityTypeCondition.EntityField field, Set<String> required) {
         return switch (field) {
            case VARIANT -> matchesVariant(entity, required);
            case COLOR -> matchesColor(entity, required);
            case STYLE -> {
               if (entity instanceof Horse horse && matchesValue(required, horse.getStyle().toString())) {
                  yield true;
               }

               yield false;
            }
            case MAIN_GENE -> {
               if (entity instanceof Panda panda && matchesValue(required, panda.getMainGene().toString())) {
                  yield true;
               }

               yield false;
            }
            case HIDDEN_GENE -> {
               if (entity instanceof Panda panda && matchesValue(required, panda.getHiddenGene().toString())) {
                  yield true;
               }

               yield false;
            }
            case BIOME -> matchesBiome(entity, required);
            case PROFESSION -> matchesProfession(entity, required);
            case CRACK -> {
               if (entity instanceof IronGolem ironGolem && matchesValue(required, EntityTypeCondition.getIronGolemCrackState(ironGolem))) {
                  yield true;
               }

               yield false;
            }
            case BODY_COLOR -> {
               if (entity instanceof TropicalFish fish && matchesValue(required, fish.getBodyColor().toString())) {
                  yield true;
               }

               yield false;
            }
            case PATTERN_COLOR -> {
               if (entity instanceof TropicalFish fish && matchesValue(required, fish.getPatternColor().toString())) {
                  yield true;
               }

               yield false;
            }
            case PATTERN -> {
               if (entity instanceof TropicalFish fish && matchesValue(required, fish.getPattern().toString())) {
                  yield true;
               }

               yield false;
            }
         };
      }

      private static boolean matchesVariant(Entity entity, Set<String> required) {
         if (entity instanceof Frog frog) {
            return matchesKeyedValue(required, frog.getVariant());
         } else if (entity instanceof Axolotl axolotl) {
            return matchesValue(required, axolotl.getVariant().name());
         } else if (entity instanceof Cat cat) {
            return matchesKeyedValue(required, cat.getCatType());
         } else if (entity instanceof Rabbit rabbit) {
            return matchesValue(required, rabbit.getRabbitType().name());
         } else if (entity instanceof Fox fox) {
            return matchesValue(required, fox.getFoxType().name());
         } else if (entity instanceof Villager villager) {
            return matchesKeyedValue(required, villager.getVillagerType());
         } else if (ServerVersion.isAfterOrEq(EntityTypeCondition.ZOMBIE_NAUTILUS_VERSION) && entity instanceof ZombieNautilus zombieNautilus) {
            return matchesKeyedValue(required, zombieNautilus.getVariant());
         } else if (ServerVersion.isAfterOrEq(EntityTypeCondition.COPPER_GOLEM_VERSION) && entity instanceof CopperGolem copperGolem) {
            return matchesValue(required, copperGolem.getWeatheringState().name());
         } else if (entity instanceof MushroomCow mooshroom) {
            return matchesValue(required, mooshroom.getVariant().name());
         } else if (entity instanceof Cow cow) {
            return matchesValues(required, MythicBukkit.inst().getVolatileCodeHandler().getCompatibilityHandler().getCowVariantValues(cow));
         } else if (entity instanceof Chicken chicken) {
            return matchesValues(required, MythicBukkit.inst().getVolatileCodeHandler().getCompatibilityHandler().getChickenVariantValues(chicken));
         } else if (entity instanceof Pig pig) {
            return matchesValues(required, MythicBukkit.inst().getVolatileCodeHandler().getCompatibilityHandler().getPigVariantValues(pig));
         } else {
            return entity instanceof Wolf wolf
               ? matchesValues(required, MythicBukkit.inst().getVolatileCodeHandler().getCompatibilityHandler().getWolfVariantValues(wolf))
               : false;
         }
      }

      private static boolean matchesColor(Entity entity, Set<String> required) {
         if (entity instanceof Horse horse) {
            return matchesValue(required, horse.getColor().toString());
         } else if (entity instanceof Sheep sheep) {
            if (EntityTypeCondition.isRainbowSheep(sheep)) {
               return matchesValue(required, "RAINBOW");
            } else {
               DyeColor sheepColor = sheep.getColor();
               return sheepColor != null && matchesValue(required, sheepColor.toString());
            }
         } else if (!(entity instanceof Shulker shulker)) {
            if (entity instanceof Parrot parrot) {
               return matchesValue(required, parrot.getVariant().toString());
            } else if (entity instanceof TraderLlama traderLlama) {
               return matchesValue(required, traderLlama.getColor().toString());
            } else {
               return entity instanceof Llama llama ? matchesValue(required, llama.getColor().toString()) : false;
            }
         } else {
            DyeColor shulkerColor = shulker.getColor();
            return shulkerColor != null && matchesValue(required, shulkerColor.toString());
         }
      }

      private static boolean matchesBiome(Entity entity, Set<String> required) {
         return entity instanceof Villager villager
            ? matchesKeyedValue(required, villager.getVillagerType())
            : entity instanceof ZombieVillager zombieVillager && matchesKeyedValue(required, zombieVillager.getVillagerType());
      }

      private static boolean matchesProfession(Entity entity, Set<String> required) {
         return entity instanceof Villager villager
            ? matchesKeyedValue(required, villager.getProfession())
            : entity instanceof ZombieVillager zombieVillager && matchesKeyedValue(required, zombieVillager.getVillagerProfession());
      }

      private static boolean matchesValue(Set<String> required, String rawValue) {
         String normalized = EntityTypeCondition.normalizeVariant(rawValue);
         return normalized != null && required.contains(normalized);
      }

      private static boolean matchesKeyedValue(Set<String> required, Keyed value) {
         return matchesValue(required, value.toString())
            || matchesValue(required, value.getKey().toString())
            || matchesValue(required, value.getKey().getKey());
      }

      private static boolean matchesValues(Set<String> required, Set<String> rawValues) {
         if (rawValues == null) {
            return false;
         } else {
            for (String rawValue : rawValues) {
               if (matchesValue(required, rawValue)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private record ParsedTypeEntry(String typePattern, EntityTypeCondition.TypeConstraint constraint) {
   }

   private static final class TypeConstraint {
      private final Map<EntityTypeCondition.EntityField, Set<String>> requirements = new EnumMap<>(EntityTypeCondition.EntityField.class);

      static EntityTypeCondition.TypeConstraint empty() {
         return new EntityTypeCondition.TypeConstraint();
      }

      void add(EntityTypeCondition.EntityField field, List<String> rawValues) {
         Set<String> values = EntityTypeCondition.parseValueList(rawValues);
         if (!values.isEmpty()) {
            this.requirements.put(field, values);
         }
      }

      boolean isEmpty() {
         return this.requirements.isEmpty();
      }

      Map<EntityTypeCondition.EntityField, Set<String>> requirements() {
         return this.requirements;
      }
   }
}
