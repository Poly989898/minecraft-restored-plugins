package io.lumine.mythic.lib.api.player;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.cooldown.CooldownMap;
import io.lumine.mythic.lib.player.cooldown.CooldownType;
import io.lumine.mythic.lib.player.particle.ParticleEffectMap;
import io.lumine.mythic.lib.player.potion.PermanentPotionEffectMap;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.player.skill.PassiveSkillMap;
import io.lumine.mythic.lib.player.skillmod.SkillModifierMap;
import io.lumine.mythic.lib.script.variable.VariableList;
import io.lumine.mythic.lib.script.variable.VariableScope;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.MMOPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOPlayerData {
   @NotNull
   private final UUID entityId;
   private final boolean lookup;
   @Nullable
   private UUID profileId;
   @Nullable
   private UUID officialId;
   @Nullable
   private Player player;
   private long lastLogActivity;
   @Nullable
   private List<MMOPlugin> pluginLoadQueue = new LinkedList<>();
   private final CooldownMap cooldownMap = new CooldownMap();
   private final StatMap statMap = new StatMap(this);
   private final SkillModifierMap skillModifierMap = new SkillModifierMap(this);
   private final PermanentPotionEffectMap permEffectMap = new PermanentPotionEffectMap(this);
   private final ParticleEffectMap particleEffectMap = new ParticleEffectMap(this);
   private final PassiveSkillMap passiveSkillMap = new PassiveSkillMap(this);
   private final VariableList variableList = new VariableList(VariableScope.PLAYER);
   private final Map<String, Object> externalData = new HashMap<>();
   private static final long CACHE_TIME_OUT = 86400000L;
   private static final Map<UUID, MMOPlayerData> PLAYER_DATA = new WeakHashMap<>();

   private MMOPlayerData(@NotNull Player player) {
      this.lookup = false;
      this.entityId = Objects.requireNonNull(player, "Player cannot be null").getUniqueId();
   }

   public MMOPlayerData(@NotNull UUID uniqueId) {
      this.lookup = true;
      this.entityId = Objects.requireNonNull(uniqueId, "UUID cannot be null");
      this.officialId = uniqueId;
      this.profileId = uniqueId;
   }

   @NotNull
   public UUID getUniqueId() {
      return this.entityId;
   }

   @NotNull
   public UUID getOfficialId() {
      return Objects.requireNonNull(this.officialId, "No official ID provided");
   }

   public void setOfficialId(@NotNull UUID officialId) {
      this.officialId = Objects.requireNonNull(officialId, "Official ID cannot be null");
   }

   public boolean hasOfficialId() {
      return this.officialId != null;
   }

   @NotNull
   public UUID getProfileId() {
      return Objects.requireNonNull(this.profileId, "No profile has been chosen yet");
   }

   public void setProfileId(@Nullable UUID profileId) {
      this.profileId = profileId;
   }

   public boolean hasProfile() {
      return this.profileId != null;
   }

   public boolean isLookup() {
      return this.lookup;
   }

   public boolean hasFullySynchronized() {
      return this.pluginLoadQueue == null || this.pluginLoadQueue.isEmpty();
   }

   public void markAsSynchronized(@NotNull MMOPlugin plugin, @NotNull SynchronizedDataHolder holder) {
      Validate.notNull(this.pluginLoadQueue, "Player is offline or MMO player data already synchronized");
      Validate.isTrue(this.pluginLoadQueue.remove(plugin), "Player data already marked synchronized");
      if (this.pluginLoadQueue.isEmpty()) {
         if (!this.lookup) {
            this.statMap.updateAll();
         }

         this.pluginLoadQueue = null;
      }
   }

   @NotNull
   public StatMap getStatMap() {
      return this.statMap;
   }

   @NotNull
   public SkillModifierMap getSkillModifierMap() {
      return this.skillModifierMap;
   }

   @Deprecated
   public PermanentPotionEffectMap getPermanentEffectMap() {
      return this.permEffectMap;
   }

   @Deprecated
   public ParticleEffectMap getParticleEffectMap() {
      return this.particleEffectMap;
   }

   @NotNull
   public PassiveSkillMap getPassiveSkillMap() {
      return this.passiveSkillMap;
   }

   @NotNull
   public Collection<PassiveSkill> isolateSkills(@NotNull TriggerMetadata triggerMetadata) {
      return triggerMetadata.getTriggerType().isActionHandSpecific()
         ? this.passiveSkillMap.isolateModifiers(triggerMetadata.getActionHand())
         : this.passiveSkillMap.getModifiers();
   }

   public void triggerSkills(@NotNull TriggerMetadata triggerMetadata) {
      this.triggerSkills(triggerMetadata, this.isolateSkills(triggerMetadata));
   }

   public void triggerSkills(@NotNull TriggerMetadata triggerMetadata, @NotNull Iterable<PassiveSkill> skills) {
      if (this.getPlayer().getGameMode() != GameMode.SPECTATOR && MythicLib.plugin.getFlags().isFlagAllowed(this.getPlayer(), CustomFlag.MMO_ABILITIES)) {
         for (PassiveSkill skill : skills) {
            SkillHandler<?> handler = skill.getTriggeredSkill().getHandler();
            if (handler.isTriggerable() && skill.getType().equals(triggerMetadata.getTriggerType())) {
               skill.getTriggeredSkill().cast(triggerMetadata);
            }
         }
      }
   }

   @NotNull
   public VariableList getVariableList() {
      return this.variableList;
   }

   public long getLastLogActivity() {
      return this.lastLogActivity;
   }

   public boolean isTimedOut() {
      return !this.isOnline() && this.lastLogActivity + 86400000L < System.currentTimeMillis();
   }

   public boolean isOnline() {
      return this.player != null;
   }

   @NotNull
   public Player getPlayer() {
      return Objects.requireNonNull(this.player, "Player is offline");
   }

   public void updatePlayer(@Nullable Player player) {
      this.player = player;
      this.lastLogActivity = System.currentTimeMillis();
      if (player != null) {
         this.pluginLoadQueue = new ArrayList<>();

         for (MMOPlugin mmoPlugin : MythicLib.plugin.getMMOPlugins()) {
            if (mmoPlugin.hasData()) {
               this.pluginLoadQueue.add(mmoPlugin);
            }
         }
      } else {
         this.pluginLoadQueue = null;
         this.statMap.flushCache();
      }
   }

   public void applyCooldown(CooldownType cd, double value) {
      this.cooldownMap.applyCooldown(cd.name(), value);
   }

   public boolean isOnCooldown(CooldownType cd) {
      return this.cooldownMap.isOnCooldown(cd.name());
   }

   @NotNull
   public CooldownMap getCooldownMap() {
      return this.cooldownMap;
   }

   @Nullable
   public <T> T getExternalData(String key, Class<T> objectType) {
      Object found = this.externalData.get(key);
      return (T)(found == null ? null : found);
   }

   public void setExternalData(String key, Object obj) {
      this.externalData.put(key, obj);
   }

   public boolean hasExternalData(String key) {
      return this.externalData.containsKey(key);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof MMOPlayerData)) {
         return false;
      }

      MMOPlayerData that = (MMOPlayerData)o;
      return this.getUniqueId().equals(that.getUniqueId());
   }

   @Override
   public int hashCode() {
      return this.getUniqueId().hashCode();
   }

   public static MMOPlayerData setup(@NotNull Player player) {
      MMOPlayerData found = PLAYER_DATA.computeIfAbsent(player.getUniqueId(), uuid -> new MMOPlayerData(player));
      found.updatePlayer(player);
      return found;
   }

   @NotNull
   public static MMOPlayerData get(@NotNull OfflinePlayer player) {
      return get(player.getUniqueId());
   }

   @NotNull
   public static MMOPlayerData get(@NotNull UUID uuid) {
      return Objects.requireNonNull(PLAYER_DATA.get(uuid), "Player data not loaded");
   }

   @Nullable
   public static MMOPlayerData online(@NotNull Player player) {
      if (!player.isOnline()) {
         return null;
      }

      MMOPlayerData found = PLAYER_DATA.get(player.getUniqueId());
      return found != null && found.isOnline() ? found : null;
   }

   @Nullable
   public static MMOPlayerData getOrNull(@NotNull Entity player) {
      return player instanceof Player ? getOrNull(player.getUniqueId()) : null;
   }

   @Nullable
   public static MMOPlayerData getOrNull(@NotNull UUID uuid) {
      return PLAYER_DATA.get(uuid);
   }

   public static boolean has(@NotNull OfflinePlayer player) {
      return has(player.getUniqueId());
   }

   public static boolean has(@NotNull UUID uuid) {
      return PLAYER_DATA.containsKey(uuid);
   }

   @NotNull
   public static Collection<MMOPlayerData> getLoaded() {
      return PLAYER_DATA.values();
   }

   public static void forEachOnline(Consumer<MMOPlayerData> action) {
      for (MMOPlayerData registered : PLAYER_DATA.values()) {
         if (registered.isOnline()) {
            action.accept(registered);
         }
      }
   }

   public static void flushOfflinePlayerData() {
      Iterator<MMOPlayerData> iterator = PLAYER_DATA.values().iterator();

      while (iterator.hasNext()) {
         MMOPlayerData tempData = iterator.next();
         if (tempData.isTimedOut()) {
            iterator.remove();
         }
      }
   }

   @Deprecated
   public long getLastLogin() {
      return this.getLastLogActivity();
   }

   @Deprecated
   public static boolean isLoaded(UUID uuid) {
      return has(uuid);
   }

   @Deprecated
   public void triggerSkills(@NotNull TriggerType triggerType, @Nullable Entity target) {
      Validate.isTrue(!triggerType.isActionHandSpecific(), "You must provide an action hand");
      this.triggerSkills(new TriggerMetadata(this, triggerType, target));
   }

   @Deprecated
   public void triggerSkills(@NotNull TriggerType triggerType, @NotNull EquipmentSlot actionHand, @Nullable Entity target) {
      Validate.notNull(actionHand, "Action hand cannot be null");
      this.triggerSkills(new TriggerMetadata(this, triggerType, actionHand, null, target, null, null, null));
   }

   @Deprecated
   public void triggerSkills(
      @NotNull TriggerType triggerType, @Nullable PlayerMetadata caster, @Nullable AttackMetadata attackMetadata, @Nullable Entity target
   ) {
      TriggerMetadata meta = new TriggerMetadata(this, triggerType, null, null, target, null, attackMetadata, caster);
      this.triggerSkills(meta);
   }

   @Deprecated
   public void triggerSkills(
      @NotNull TriggerType triggerType, @Nullable PlayerMetadata caster, @Nullable Entity target, @Nullable AttackMetadata attackMetadata
   ) {
      Iterable<PassiveSkill> candidates = triggerType.isActionHandSpecific()
         ? this.passiveSkillMap.isolateModifiers(caster == null ? EquipmentSlot.MAIN_HAND : caster.getActionHand())
         : this.passiveSkillMap.getModifiers();
      TriggerMetadata meta = new TriggerMetadata(this, triggerType, null, null, target, null, attackMetadata, caster);
      this.triggerSkills(meta, candidates);
   }

   @Deprecated
   public void triggerSkills(@NotNull TriggerType triggerType, @Nullable PlayerMetadata caster, @Nullable Entity target) {
      Iterable<PassiveSkill> candidates = triggerType.isActionHandSpecific()
         ? this.passiveSkillMap.isolateModifiers(caster == null ? EquipmentSlot.MAIN_HAND : caster.getActionHand())
         : this.passiveSkillMap.getModifiers();
      TriggerMetadata meta = new TriggerMetadata(this, triggerType, null, null, target, null, null, caster);
      this.triggerSkills(meta, candidates);
   }

   @Deprecated
   public void triggerSkills(@NotNull TriggerType triggerType, @Nullable PlayerMetadata caster, @NotNull Iterable<PassiveSkill> skills, @Nullable Entity target) {
      TriggerMetadata meta = new TriggerMetadata(this, triggerType, null, null, target, null, null, caster);
      this.triggerSkills(meta, skills);
   }

   @Deprecated
   public void triggerSkills(
      @NotNull TriggerType triggerType,
      @Nullable PlayerMetadata caster,
      @NotNull Iterable<PassiveSkill> skills,
      @Nullable Entity target,
      @Nullable AttackMetadata attack
   ) {
      TriggerMetadata meta = new TriggerMetadata(
         this, triggerType, caster == null ? EquipmentSlot.MAIN_HAND : caster.getActionHand(), null, target, null, attack, caster
      );
      this.triggerSkills(meta, skills);
   }
}
