package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.targeters.IEntityTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.AbstractModelEngineSupport;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Marker;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

public abstract class IEntitySelector extends SkillTargeter implements IEntityTargeter {
   protected boolean targetSelf = ConfigExecutor.isTargetSelf();
   private boolean targetPlayers = ConfigExecutor.isTargetPlayers();
   private boolean targetArmorStands = ConfigExecutor.isTargetArmorStands();
   private boolean targetMarkers = ConfigExecutor.isTargetMarkers();
   private boolean targetTextDisplays = ConfigExecutor.isTargetTextDisplays();
   private boolean targetItemDisplays = ConfigExecutor.isTargetItemDisplays();
   private boolean targetBlockDisplays = ConfigExecutor.isTargetBlockDisplays();
   private boolean targetInteractions = ConfigExecutor.isTargetInteractions();
   private boolean targetCreativeMode = ConfigExecutor.isTargetCreativeMode();
   private boolean targetSpectatorMode = ConfigExecutor.isTargetSpectatorMode();
   private boolean targetInvulnerable = ConfigExecutor.isTargetInvulnerable();
   private boolean targetCitizensNPCs = ConfigExecutor.isTargetCitizensNPCs();
   private boolean targetAnimals = ConfigExecutor.isTargetAnimals();
   private boolean targetCreatures = ConfigExecutor.isTargetCreatures();
   private boolean targetMonsters = ConfigExecutor.isTargetMonsters();
   private boolean targetGroundMobs = true;
   private boolean targetWaterMobs = ConfigExecutor.isTargetWaterMobs();
   private boolean targetFlyingMobs = ConfigExecutor.isTargetFlyingMobs();
   private boolean targetSameFaction = ConfigExecutor.isTargetSameFaction();
   private boolean targetOwner = ConfigExecutor.isTargetOwner();
   private boolean targetNonMythic = ConfigExecutor.isTargetNonMythic();
   private boolean targetMythic = true;
   private boolean targetVillagers = ConfigExecutor.isTargetVillagers();
   protected boolean targetAll = false;
   protected boolean useBoundingBox;
   protected PlaceholderInt unique;
   protected boolean filterMegHitbox;
   private IEntitySelector.FilterSorter sorter = IEntitySelector.FilterSorter.NONE;
   private PlaceholderInt skipTargetsUpToIndex;
   private PlaceholderInt limit;
   private List<String> ignoreTypes = null;

   public IEntitySelector(SkillExecutor manager, MythicLineConfig mlc) {
      this(manager, mlc, null);
   }

   public IEntitySelector(SkillExecutor manager, MythicLineConfig mlc, @Nullable IEntityTargeter delegate) {
      super(manager, mlc);
      if (this instanceof SelfTargeter) {
         this.targetArmorStands = true;
      }

      this.limit = mlc.getPlaceholderInteger(new String[]{"limit"}, 0);
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Setting limit of the target to " + this.limit);
      String sort = mlc.getString(new String[]{"sort", "sortby"}, "NONE").toUpperCase();

      try {
         this.sorter = IEntitySelector.FilterSorter.valueOf(sort);
      } catch (Exception e) {
         MythicLogger.errorTargeterConfig(this, mlc, "'" + sort + "' is not a valid sorter.");
      }

      this.skipTargetsUpToIndex = mlc.getPlaceholderInteger(new String[]{"skipTargetsUpToIndex", "stuti"}, 0);
      if (this instanceof SelfTargeter) {
         this.targetSelf = true;
      }

      String target = mlc.getString("target", null);
      String ignore = mlc.getString("ignore", null);
      String ignoreTypes = mlc.getString("ignoretype", null);
      this.resolveFilters(delegate);
      if (target != null) {
         target = target.toLowerCase();
         if (target.equalsIgnoreCase("ground")) {
            this.targetGroundMobs = true;
            this.targetWaterMobs = false;
            this.targetFlyingMobs = false;
         } else if (target.equalsIgnoreCase("water")) {
            this.targetGroundMobs = false;
            this.targetWaterMobs = true;
            this.targetFlyingMobs = false;
         } else if (target.equalsIgnoreCase("flying")) {
            this.targetGroundMobs = false;
            this.targetWaterMobs = false;
            this.targetFlyingMobs = true;
         } else if (target.equalsIgnoreCase("vanilla")) {
            this.targetNonMythic = true;
            this.targetMythic = false;
         } else if (target.equalsIgnoreCase("mythic")) {
            this.targetNonMythic = false;
            this.targetMythic = true;
         } else {
            this.targetPlayers = false;
            this.targetArmorStands = false;
            this.targetMarkers = false;
            this.targetTextDisplays = false;
            this.targetItemDisplays = false;
            this.targetBlockDisplays = false;
            this.targetInteractions = false;
            this.targetCreativeMode = false;
            this.targetSpectatorMode = false;
            this.targetCitizensNPCs = false;
            this.targetAnimals = false;
            this.targetCreatures = false;
            this.targetMonsters = false;
            this.targetVillagers = false;
            if (target.contains("self") || target.contains("caster")) {
               this.targetSelf = true;
            }

            if (target.contains("player")) {
               this.targetPlayers = true;
            }

            if (target.contains("creative")) {
               this.targetCreativeMode = true;
            }

            if (target.contains("armorstand") || target.contains("armor_stand")) {
               this.targetArmorStands = true;
            }

            if (target.contains("marker")) {
               this.targetMarkers = true;
            }

            if (target.contains("textdisplay") || target.contains("text_display")) {
               this.targetTextDisplays = true;
            }

            if (target.contains("itemdisplay") || target.contains("item_display")) {
               this.targetItemDisplays = true;
            }

            if (target.contains("blockdisplay") || target.contains("block_display")) {
               this.targetBlockDisplays = true;
            }

            if (target.contains("interaction")) {
               this.targetInteractions = true;
            }

            if (target.contains("spectator")) {
               this.targetSpectatorMode = true;
            }

            if (target.contains("npc")) {
               this.targetCitizensNPCs = true;
            }

            if (target.contains("flying")) {
               this.targetMonsters = true;
               this.targetCreatures = true;
               this.targetFlyingMobs = true;
            }

            if (target.contains("animal")) {
               this.targetAnimals = true;
               this.targetCreatures = true;
               this.targetWaterMobs = true;
               this.targetFlyingMobs = true;
            }

            if (target.contains("monster")) {
               this.targetMonsters = true;
               this.targetCreatures = true;
               this.targetWaterMobs = true;
               this.targetFlyingMobs = true;
            }

            if (target.contains("creature")) {
               this.targetCreatures = true;
               this.targetWaterMobs = true;
               this.targetFlyingMobs = true;
            }

            if (target.contains("villager")) {
               this.targetVillagers = true;
            }

            boolean includesVanilla = target.contains("vanilla") || target.contains("nonmythic");
            boolean includesMythic = target.contains("mythic") && !target.contains("nonmythic");
            if (includesVanilla || includesMythic) {
               this.targetNonMythic = includesVanilla;
               this.targetMythic = includesMythic;
            }
         }
      }

      if (ignore != null) {
         ignore = ignore.toLowerCase();
         if (ignore.contains("player")) {
            this.targetPlayers = false;
         }

         if (ignore.contains("creative")) {
            this.targetCreativeMode = false;
         }

         if (ignore.contains("armorstand") || ignore.contains("armor_stand")) {
            this.targetArmorStands = false;
         }

         if (ignore.contains("marker")) {
            this.targetMarkers = false;
         }

         if (ignore.contains("textdisplay") || ignore.contains("text_display")) {
            this.targetTextDisplays = false;
         }

         if (ignore.contains("itemdisplay") || ignore.contains("item_display")) {
            this.targetItemDisplays = false;
         }

         if (ignore.contains("blockdisplay") || ignore.contains("block_display")) {
            this.targetBlockDisplays = false;
         }

         if (ignore.contains("interaction")) {
            this.targetInteractions = false;
         }

         if (ignore.contains("spectator")) {
            this.targetSpectatorMode = false;
         }

         if (ignore.contains("npc")) {
            this.targetCitizensNPCs = false;
         }

         if (ignore.contains("animal")) {
            this.targetAnimals = false;
         }

         if (ignore.contains("monster")) {
            this.targetMonsters = false;
         }

         if (ignore.contains("creature")) {
            this.targetCreatures = false;
         }

         if (ignore.contains("faction")) {
            this.targetSameFaction = false;
         }

         if (ignore.contains("vanilla") || ignore.contains("nonmythic")) {
            this.targetNonMythic = false;
         }

         if (ignore.contains("mythic") && !ignore.contains("nonmythic")) {
            this.targetMythic = false;
         }

         if (ignore.contains("villager")) {
            this.targetVillagers = false;
         }

         if (ignore.contains("owner")) {
            this.targetOwner = false;
         }
      }

      if (ignoreTypes != null) {
         this.ignoreTypes = new ArrayList<>();

         for (String s : ignoreTypes.split(",")) {
            this.ignoreTypes.add(s);
         }
      }

      this.targetSelf = mlc.getBoolean("targetself", this.targetSelf);
      this.targetPlayers = mlc.getBoolean("targetplayers", this.targetPlayers);
      this.targetCreativeMode = mlc.getBoolean("targetcreative", this.targetCreativeMode);
      this.targetSpectatorMode = mlc.getBoolean("targetspectator", this.targetSpectatorMode);
      this.targetInvulnerable = mlc.getBoolean("targetinvulnerable", this.targetInvulnerable);
      this.targetArmorStands = mlc.getBoolean("targetarmorstands", this.targetArmorStands);
      this.targetMarkers = mlc.getBoolean("targetmarkers", this.targetMarkers);
      this.targetTextDisplays = mlc.getBoolean(new String[]{"targettextdisplays", "targettextdisplay"}, this.targetTextDisplays);
      this.targetItemDisplays = mlc.getBoolean(new String[]{"targetitemdisplays", "targetitemdisplay"}, this.targetItemDisplays);
      this.targetBlockDisplays = mlc.getBoolean(new String[]{"targetblockdisplays", "targetblockdisplay"}, this.targetBlockDisplays);
      this.targetInteractions = mlc.getBoolean(new String[]{"targetinteractions", "targetinteraction"}, this.targetInteractions);
      this.targetCitizensNPCs = mlc.getBoolean("targetnpcs", this.targetCitizensNPCs);
      this.targetAnimals = mlc.getBoolean("targetanimals", this.targetAnimals);
      this.targetCreatures = mlc.getBoolean("targetcreatures", this.targetCreatures);
      this.targetSameFaction = mlc.getBoolean("targetsamefaction", this.targetSameFaction);
      this.targetOwner = mlc.getBoolean("targetowner", this.targetOwner);
      this.targetNonMythic = mlc.getBoolean(new String[]{"targetvanilla", "targetnonmythic"}, this.targetNonMythic);
      this.targetVillagers = mlc.getBoolean("targetvillagers", this.targetVillagers);
      this.targetAll = mlc.getBoolean("targetall", this.targetAll);
      this.useBoundingBox = mlc.getBoolean(new String[]{"bb", "useboundingbox"}, this.useBoundingBox);
      this.unique = mlc.getPlaceholderInteger(new String[]{"u", "unique"}, 1);
      this.filterMegHitbox = mlc.getBoolean(new String[]{"nomegbb", "nmb"}, false);
   }

   @Override
   public abstract Collection<AbstractEntity> getEntities(SkillMetadata skillMetadata);

   protected void resolveFilters(@Nullable IEntityTargeter delegate) {
      if (delegate == null) {
         delegate = this;
      }

      for (IEntitySelector.TargetFilter filter : IEntitySelector.TargetFilter.values()) {
         filter.modify(this, delegate.getFilter(filter));
      }
   }

   public void filter(SkillMetadata data, boolean targetCreative) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Filtering entity for {0}", data.getCause().name());
      if (this.targetConditions != null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Applying target conditions");

         for (SkillCondition mc : this.targetConditions) {
            if (!mc.evaluateTargets(data)) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "! Skill not usable: TargetConditions failed.");
               return;
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ Applying entity target filters");
      Collection<AbstractEntity> targets = data.getEntityTargets();
      if (targets == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "+ No targets to filter?");
      } else {
         int uniqueCount = this.unique.get(data);
         Map<UUID, Integer> uniqueMap = uniqueCount > 0 && uniqueCount < targets.size() ? new HashMap<>(targets.size()) : null;
         Optional<AbstractModelEngineSupport> megSupport = this.getPlugin().getCompatibility().getModelEngine();
         List<AbstractEntity> filteredTargets = new ArrayList<>(targets.size());

         for (AbstractEntity t : targets) {
            if (t != null) {
               if (megSupport.isPresent()) {
                  if (this.filterMegHitbox && megSupport.get().isSubHitbox(t.getUniqueId())) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering SUB_HITBOX");
                     continue;
                  }

                  t = megSupport.get().getParent(t);
               }

               if (uniqueMap != null) {
                  int count = uniqueMap.getOrDefault(t.getUniqueId(), 0);
                  if (count >= uniqueCount) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering DUPLICATES");
                     continue;
                  }

                  uniqueMap.put(t.getUniqueId(), count + 1);
               }

               if (this.targetAll) {
                  filteredTargets.add(t);
               } else if (t.getUniqueId().equals(data.getCaster().getEntity().getUniqueId())) {
                  if (!this.targetSelf && !(this instanceof SelfTargeter)) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering SELF");
                  } else {
                     filteredTargets.add(t);
                  }
               } else {
                  if (t.isPlayer()) {
                     if (!this.targetPlayers) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering PLAYER");
                        continue;
                     }

                     AbstractPlayer player = t.asPlayer();
                     if ((!this.targetCreativeMode || !targetCreative) && player.isInCreativeMode()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering CREATIVE");
                        continue;
                     }

                     if ((!this.targetSpectatorMode || !targetCreative) && player.isInSpectatorMode()) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering SPECTATOR");
                        continue;
                     }
                  }

                  Entity bukkitEntity = t.getBukkitEntity();
                  if (!this.targetArmorStands && bukkitEntity instanceof ArmorStand) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering ARMOR_STAND");
                  } else if (!this.targetMarkers && bukkitEntity instanceof Marker) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering MARKER");
                  } else if (!this.targetTextDisplays && bukkitEntity instanceof TextDisplay) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering TEXT_DISPLAY");
                  } else if (!this.targetItemDisplays && bukkitEntity instanceof ItemDisplay) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering ITEM_DISPLAY");
                  } else if (!this.targetBlockDisplays && bukkitEntity instanceof BlockDisplay) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering BLOCK_DISPLAY");
                  } else if (!this.targetInteractions && bukkitEntity instanceof Interaction) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering INTERACTION");
                  } else if (!this.targetInvulnerable && bukkitEntity.isInvulnerable()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering INVULNERABLE");
                  } else if (!this.targetWaterMobs && t.isWaterMob()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering WATER MOB");
                  } else if (!this.targetFlyingMobs && t.isFlyingMob()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering FLYING MOB");
                  } else if (!this.targetGroundMobs && !t.isWaterMob() && !t.isFlyingMob()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering GROUND MOB");
                  } else if (!this.targetAnimals && t.isAnimal()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering ANIMAL");
                  } else if (!this.targetCreatures && t.isCreature()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering CREATURE");
                  } else if (!this.targetMonsters && t.isMonster()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering MONSTER");
                  } else if (!this.targetCitizensNPCs && t.isLiving() && t.isCitizensNPC()) {
                     MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering NPC");
                  } else {
                     if (!this.targetNonMythic || !this.targetMythic) {
                        boolean mythic = MythicBukkit.inst().getMobManager().isActiveMob(t);
                        if (!this.targetNonMythic && !mythic) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering NON-MYTHIC");
                           continue;
                        }

                        if (!this.targetMythic && mythic) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering MYTHIC");
                           continue;
                        }
                     }

                     if (!this.targetSameFaction) {
                        SkillCaster caster = data.getCaster();
                        String casterFaction;
                        if (caster instanceof ActiveMob activeMob) {
                           casterFaction = activeMob.getFaction() == null ? null : activeMob.getFaction();
                        } else if (caster.getEntity().isPlayer()) {
                           casterFaction = this.getPlugin().getPlayerManager().getFactionProvider().getFaction(caster.getEntity().asPlayer()).orElse(null);
                        } else {
                           casterFaction = null;
                        }

                        String targetFaction;
                        if (t.isPlayer()) {
                           Optional<String> maybeFaction = this.getPlugin().getPlayerManager().getFactionProvider().getFaction(t.asPlayer());
                           targetFaction = maybeFaction.orElse(null);
                        } else {
                           ActiveMob activeMob = this.getPlugin().getMobManager().getMythicMobInstance(t);
                           if (activeMob != null) {
                              targetFaction = activeMob.getFaction() == null ? null : activeMob.getFaction();
                           } else if (bukkitEntity.hasMetadata("Faction")) {
                              targetFaction = (bukkitEntity.getMetadata("Faction").get(0)).asString();
                           } else {
                              targetFaction = null;
                           }
                        }

                        if (casterFaction != null && casterFaction.equals(targetFaction)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering SAMEFACTION");
                           continue;
                        }
                     }

                     if (!this.targetOwner && this.getPlugin().getMobManager().isActiveMob(data.getCaster().getEntity())) {
                        ActiveMob am = (ActiveMob)data.getCaster();
                        if (am.getOwnerUUID().isPresent() && am.getOwnerUUID().get().equals(t.getUniqueId())) {
                           MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering OWNER");
                           continue;
                        }
                     }

                     if (!this.targetVillagers && bukkitEntity instanceof Villager) {
                        MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Filtering VILLAGERS");
                     } else {
                        filteredTargets.add(t);
                     }
                  }
               }
            }
         }

         int limit = this.limit.get(data);
         int skipTargetsUpToIndex = this.skipTargetsUpToIndex.get(data);
         if (this.sorter != IEntitySelector.FilterSorter.NONE) {
            MythicLogger.debug(
               MythicLogger.DebugLevel.SKILL, "Applying " + this.sorter.name() + " sort (limit=" + limit + ", skip=" + skipTargetsUpToIndex + ")"
            );
            switch (this.sorter) {
               case RANDOM:
                  Collections.shuffle(filteredTargets);
                  break;
               case NEAREST: {
                  AbstractLocation origin = data.getOrigin();
                  MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Sorting from nearest...");
                  filteredTargets.sort((o1, o2) -> {
                     double d1 = origin.distanceSquared(o1.getLocation());
                     double d2 = origin.distanceSquared(o2.getLocation());
                     return Double.compare(d1, d2);
                  });
                  break;
               }
               case FURTHEST: {
                  AbstractLocation origin = data.getOrigin();
                  filteredTargets.sort((o1, o2) -> {
                     double d1 = origin.distanceSquared(o1.getLocation());
                     double d2 = origin.distanceSquared(o2.getLocation());
                     return Double.compare(d2, d1);
                  });
                  break;
               }
               case OLDEST:
                  filteredTargets.sort((o1, o2) -> Integer.compare(o2.getBukkitEntity().getTicksLived(), o1.getBukkitEntity().getTicksLived()));
                  break;
               case NEWEST:
                  filteredTargets.sort((o1, o2) -> Integer.compare(o1.getBukkitEntity().getTicksLived(), o2.getBukkitEntity().getTicksLived()));
                  break;
               case HIGHEST_HEALTH:
                  filteredTargets.sort((o1, o2) -> Double.compare(o2.getHealth(), o1.getHealth()));
                  break;
               case LOWEST_HEALTH:
                  filteredTargets.sort((o1, o2) -> Double.compare(o1.getHealth(), o2.getHealth()));
                  break;
               case HIGHEST_THREAT:
                  if (data.getCaster() instanceof ActiveMob && ((ActiveMob)data.getCaster()).hasThreatTable()) {
                     ActiveMob am = (ActiveMob)data.getCaster();
                     ActiveMob.ThreatTable tt = am.getThreatTable();
                     filteredTargets.sort((o1, o2) -> Double.compare(tt.getThreat(o2), tt.getThreat(o1)));
                  } else {
                     MythicLogger.errorTargeterConfig(this, this.config, "Threat sorters can only be used with mobs that have ThreatTables enabled");
                  }
                  break;
               case LOWEST_THREAT:
                  if (data.getCaster() instanceof ActiveMob && ((ActiveMob)data.getCaster()).hasThreatTable()) {
                     ActiveMob am = (ActiveMob)data.getCaster();
                     ActiveMob.ThreatTable tt = am.getThreatTable();
                     filteredTargets.sort((o1, o2) -> Double.compare(tt.getThreat(o1), tt.getThreat(o2)));
                  } else {
                     MythicLogger.errorTargeterConfig(this, this.config, "Threat sorters can only be used with mobs that have ThreatTables enabled");
                  }
            }
         }

         if (skipTargetsUpToIndex > 0) {
            if (skipTargetsUpToIndex < filteredTargets.size()) {
               filteredTargets = filteredTargets.subList(skipTargetsUpToIndex, filteredTargets.size());
            } else {
               filteredTargets.clear();
            }
         }

         if (limit > 0 && filteredTargets.size() > limit) {
            filteredTargets = filteredTargets.subList(0, limit);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Returning {0} targets", filteredTargets.size());
         data.setEntityTargets(filteredTargets);
      }
   }

   protected double distanceSquared(AbstractLocation location, AbstractEntity entity) {
      return this.useBoundingBox ? location.distanceSquared(entity) : location.distanceSquared(entity.getLocation());
   }

   protected AbstractVector closestPoint(AbstractLocation location, AbstractEntity entity) {
      return this.useBoundingBox ? location.closestPoint(entity) : entity.getLocation().subtract(location).toVector();
   }

   private static enum FilterSorter {
      NONE,
      RANDOM,
      NEAREST,
      FURTHEST,
      OLDEST,
      NEWEST,
      HIGHEST_HEALTH,
      LOWEST_HEALTH,
      HIGHEST_THREAT,
      LOWEST_THREAT;
   }

   @AllArgsConstructor(access = AccessLevel.PRIVATE)
   public static enum TargetFilter {
      SELF((selector, flag) -> selector.targetSelf = flag),
      PLAYERS((selector, flag) -> selector.targetPlayers = flag),
      ARMOR_STANDS((selector, flag) -> selector.targetArmorStands = flag),
      MARKERS((selector, flag) -> selector.targetMarkers = flag),
      TEXT_DISPLAYS((selector, flag) -> selector.targetTextDisplays = flag),
      ITEM_DISPLAYS((selector, flag) -> selector.targetItemDisplays = flag),
      BLOCK_DISPLAYS((selector, flag) -> selector.targetBlockDisplays = flag),
      INTERACTIONS((selector, flag) -> selector.targetInteractions = flag),
      CREATIVE_MODE((selector, flag) -> selector.targetCreativeMode = flag),
      SPECTATOR_MODE((selector, flag) -> selector.targetSpectatorMode = flag),
      INVULNERABLE((selector, flag) -> selector.targetInvulnerable = flag),
      CITIZENS_NPCS((selector, flag) -> selector.targetCitizensNPCs = flag),
      ANIMALS((selector, flag) -> selector.targetAnimals = flag),
      CREATURES((selector, flag) -> selector.targetCreatures = flag),
      MONSTERS((selector, flag) -> selector.targetMonsters = flag),
      GROUND_MOBS((selector, flag) -> selector.targetGroundMobs = flag),
      WATER_MOBS((selector, flag) -> selector.targetWaterMobs = flag),
      FLYING_MOBS((selector, flag) -> selector.targetFlyingMobs = flag),
      SAME_FACTION((selector, flag) -> selector.targetSameFaction = flag),
      OWNER((selector, flag) -> selector.targetOwner = flag),
      NON_MYTHIC((selector, flag) -> selector.targetNonMythic = flag),
      MYTHIC((selector, flag) -> selector.targetMythic = flag),
      VILLAGERS((selector, flag) -> selector.targetVillagers = flag),
      ALL((selector, flag) -> selector.targetAll = flag);

      private final BiConsumer<IEntitySelector, Boolean> setter;

      public void modify(IEntitySelector selector, IEntitySelector.TargetMode mode) {
         switch (mode) {
            case TARGET:
               this.setter.accept(selector, true);
               break;
            case IGNORE:
               this.setter.accept(selector, false);
         }
      }
   }

   public static enum TargetMode {
      TARGET,
      IGNORE,
      INHERIT;
   }
}
