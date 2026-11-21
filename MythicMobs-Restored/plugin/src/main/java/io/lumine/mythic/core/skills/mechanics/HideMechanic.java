package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.utils.pdc.DataType;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.persistence.PersistentDataContainer;

@MythicMechanic(
   name = "hideFromPlayers",
   aliases = {"hideFromPlayer", "hide"},
   description = "Hides the caster from the targeted player(s)"
)
public class HideMechanic extends Aura implements ITargetedEntitySkill {
   private final boolean ignoreAuraOptions;
   private final boolean save;

   public HideMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig config) {
      super(manager, file, skill, config);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.ignoreAuraOptions = config.getBoolean(new String[]{"permanent", "perma", "ignoreAuraOptions", "iao"}, false);
      this.save = this.ignoreAuraOptions && config.getBoolean(new String[]{"save"}, false);
      if (!this.ignoreAuraOptions) {
         this.auraName = Optional.ofNullable(config.getPlaceholderString(new String[]{"auraname", "buffname", "debuffname"}, "#hiding"));
         this.charges = PlaceholderInt.of("1");
         this.maxStacks = PlaceholderInt.of("1");
         this.mergeAll = true;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer() && target.getUniqueId() != data.getCaster().getEntity().getUniqueId()) {
         if (this.ignoreAuraOptions) {
            if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
               target.asPlayer().hideEntity(data.getCaster().getEntity());
            } else if (data.getCaster().getEntity().isPlayer()) {
               target.asPlayer().hidePlayer(data.getCaster().getEntity().asPlayer());
            }

            if (this.save) {
               addHiddenPlayer(data.getCaster().getEntity().getDataContainer(), target.getUniqueId());
            }
         } else {
            new HideMechanic.Tracker(data, target);
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   public static void addHiddenPlayer(PersistentDataContainer pdc, UUID playerUuid) {
      Set<UUID> current = getHiddenPlayers(pdc);
      current.add(playerUuid);
      pdc.set(MobKeys.HIDDEN_FROM, DataType.UUID_ARRAY, current.toArray(new UUID[0]));
   }

   public static void removeHiddenPlayer(PersistentDataContainer pdc, UUID playerUuid) {
      Set<UUID> current = getHiddenPlayers(pdc);
      if (current.remove(playerUuid)) {
         if (current.isEmpty()) {
            pdc.remove(MobKeys.HIDDEN_FROM);
         } else {
            pdc.set(MobKeys.HIDDEN_FROM, DataType.UUID_ARRAY, current.toArray(new UUID[0]));
         }
      }
   }

   public static Set<UUID> getHiddenPlayers(PersistentDataContainer pdc) {
      if (pdc.has(MobKeys.HIDDEN_FROM, DataType.UUID_ARRAY)) {
         UUID[] arr = (UUID[])pdc.get(MobKeys.HIDDEN_FROM, DataType.UUID_ARRAY);
         if (arr != null) {
            return new HashSet<>(Arrays.asList(arr));
         }
      }

      return new HashSet<>();
   }

   private class Tracker extends Aura.AuraTracker {
      public Tracker(SkillMetadata data, AbstractEntity target) {
         super(target, data);
         this.start();
      }

      @Override
      public void auraStart() {
         super.auraStart();
         this.entity.ifPresent(e -> {
            if (e.isPlayer()) {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
                  e.asPlayer().hideEntity(this.skillMetadata.getCaster().getEntity());
               } else if (this.skillMetadata.getCaster().getEntity().isPlayer()) {
                  e.asPlayer().hidePlayer(this.skillMetadata.getCaster().getEntity().asPlayer());
               }
            }
         });
      }

      @Override
      public void auraStop() {
         super.auraStop();
         this.entity.ifPresent(e -> {
            if (e.isPlayer()) {
               if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18)) {
                  e.asPlayer().showEntity(this.skillMetadata.getCaster().getEntity());
               } else if (this.skillMetadata.getCaster().getEntity().isPlayer()) {
                  e.asPlayer().showPlayer(this.skillMetadata.getCaster().getEntity().asPlayer());
               }
            }
         });
      }
   }
}
