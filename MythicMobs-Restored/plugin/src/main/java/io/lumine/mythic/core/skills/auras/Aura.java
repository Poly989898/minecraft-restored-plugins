package io.lumine.mythic.core.skills.auras;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractBossBar;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicAuraStartEvent;
import io.lumine.mythic.bukkit.events.MythicAuraStopEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public abstract class Aura extends SkillMechanic {
   private static final int UNLOAD_GRACE_TICKS = 20;
   private static final long FOLIA_CLOCK_ORIGIN = System.nanoTime();
   @MythicField(
      name = "auraName",
      aliases = {"buffname", "debuffname"},
      description = "Optional name, required to use associated mechanics & conditions that reference a specific aura"
   )
   protected Optional<PlaceholderString> auraName;
   protected Optional<PlaceholderString> auraGroup = Optional.empty();
   protected List<PlaceholderString> auraTags = new ArrayList<>();
   protected Optional<AuraAttachmentType> attachmentType;
   protected AuraAttachment attachment;
   @MythicField(
      name = "charges",
      aliases = {"c"},
      description = "The amount of charges the aura can hold before decaying to zero and terminating the aura",
      defValue = "0"
   )
   protected PlaceholderInt charges;
   @MythicField(
      name = "interval",
      aliases = {"i"},
      description = "Sets how often the aura will tick. Bare numbers are ticks.",
      defValue = "1"
   )
   protected PlaceholderDuration interval;
   @MythicField(
      name = "duration",
      aliases = {"ticks", "t", "d", "time", "t"},
      description = "The max duration the aura will persist. Bare numbers are ticks.",
      defValue = "200"
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "maxStacks",
      aliases = {"ms"},
      description = "The amount of times the aura can stack on the same target",
      defValue = "1"
   )
   protected PlaceholderInt maxStacks;
   @MythicField(
      name = "decayStackOnExpire",
      aliases = {"dsoe", "decayonexpire", "decaystack"},
      description = "When the aura duration ends, remove one stack and restart the timer instead of terminating (requires stacks > 1)",
      defValue = "false"
   )
   protected boolean decayStackOnExpire;
   @MythicField(
      name = "mergeAll",
      aliases = {"ma"},
      description = "Merges all of the same auras applied by any and all entities to another into one aura (Prevents multiple mobs from being able to stack an aura multiple times on the same entity)",
      defValue = "false"
   )
   protected boolean mergeAll;
   @MythicField(
      name = "mergeSameCaster",
      aliases = {"msc", "mc"},
      description = "Merges all of the same auras applied by one entity to another into one aura (Prevents a mob from being able to stack an aura multiple times on the same entity)",
      defValue = "false"
   )
   protected boolean mergeSameCaster;
   @MythicField(
      name = "overwriteAll",
      aliases = {"overwrite", "ow"},
      description = "When applied, stops all of the same auras applied on the target and replaces them with the new aura",
      defValue = "false"
   )
   protected boolean overwriteAll;
   @MythicField(
      name = "overwriteSameCaster",
      aliases = {"osc", "oc"},
      description = "When applied, stops all of the same auras applied on the target by the same caster and replaces them with the new aura",
      defValue = "false"
   )
   protected boolean overwriteCaster;
   @MythicField(
      name = "refreshDuration",
      aliases = {"rd"},
      description = "Makes the aura's duration refresh to the amount defined in the mechanic should the entity have the same aura applied to it again",
      defValue = "true"
   )
   protected boolean refreshDuration;
   @MythicField(
      name = "showBarTimer",
      aliases = {"bartimer", "bt"},
      description = "Whether to display a bar timer for the caster while the aura is active",
      defValue = "false"
   )
   protected boolean showBarTimer;
   @MythicField(
      name = "barTimerDisplay",
      aliases = {"barTimerText"},
      description = "The text to display on the boss bar",
      defValue = "<skill.var.aura-name>"
   )
   protected PlaceholderString barTimerDisplay = null;
   @MythicField(
      name = "barTimerColor",
      description = "The color of the boss bar",
      defValue = "RED"
   )
   protected AbstractBossBar.BarColor barTimerColor = null;
   @MythicField(
      name = "barTimerStyle",
      description = "The style of the boss bar",
      defValue = "SOLID"
   )
   protected AbstractBossBar.BarStyle barTimerStyle = null;
   @MythicField(
      name = "cancelOnGiveDamage",
      aliases = {"cogd"},
      description = "Terminates the aura when the entity deals damage to another entity",
      defValue = "false"
   )
   protected boolean cancelOnGiveDamage;
   @MythicField(
      name = "cancelOnTakeDamage",
      aliases = {"cotd"},
      description = "Terminates the aura when the entity takes damage",
      defValue = "false"
   )
   protected boolean cancelOnTakeDamage;
   @MythicField(
      name = "cancelonDeath",
      aliases = {"cod"},
      description = "Terminates the aura when the entity when the entity dies",
      defValue = "true"
   )
   protected boolean cancelOnDeath;
   @MythicField(
      name = "cancelOnCasterDeath",
      aliases = {"cod"},
      description = "Terminates the aura when the caster dies",
      defValue = "false"
   )
   protected boolean cancelOnCasterDeath;
   @MythicField(
      name = "cancelOnTeleport",
      aliases = {"cod"},
      description = "Terminates the aura when the entity when the entity teleports",
      defValue = "false"
   )
   protected boolean cancelOnTeleport;
   @MythicField(
      name = "cancelOnChangeWorld",
      aliases = {"cocw"},
      description = "Terminates the aura when the entity changes world. Mostly applies to players",
      defValue = "false"
   )
   protected boolean cancelOnChangeWorld;
   @MythicField(
      name = "cancelOnSkillCaster",
      aliases = {"cosc"},
      description = "Terminates the aura if the entity with the aura uses another skill while the aura is active.",
      defValue = "false"
   )
   protected boolean cancelOnSkillCast;
   @MythicField(
      name = "cancelOnQuit",
      aliases = {"coq"},
      description = "Terminates the aura when the entity(player) quits the server",
      defValue = "true"
   )
   protected boolean cancelOnQuit;
   @MythicField(
      name = "targetIsCaster",
      aliases = {"tic", "targetcaster"},
      description = "If true, uses the target as the caster when applying the aura",
      defValue = "false"
   )
   protected boolean targetIsCaster;
   @MythicField(
      name = "persistId",
      aliases = {"persist", "pid"},
      description = "Unique id for persisting an aura when cancelOnQuit is false"
   )
   protected String persistId;
   @MythicField(
      name = "doEndSkillOnTerminate",
      aliases = {"desot", "alwaysrunendskill", "ares"},
      description = "Whether to execute the onEnd skill when the aura is terminated",
      defValue = "true"
   )
   protected boolean doEndSkillOnTerminate;
   @MythicField(
      name = "onStartSkill",
      aliases = {"onstart", "os"},
      description = "The meta-skill to execute when the aura first starts"
   )
   protected Optional<Skill> onStartSkill = Optional.empty();
   @MythicField(
      name = "onTick",
      aliases = {"ontick", "ot"},
      description = "The meta-skill to execute every time the aura is ticked"
   )
   protected Optional<Skill> onTickSkill = Optional.empty();
   @MythicField(
      name = "onEndSkill",
      aliases = {"onend", "oe"},
      description = "The meta-skill to execute when the aura is terminated"
   )
   protected Optional<Skill> onEndSkill = Optional.empty();
   protected String onStartSkillName;
   protected String onTickSkillName;
   protected String onEndSkillName;

   static int remainingTicksUntil(int expirationTick, int currentTick) {
      return Math.max(0, expirationTick - currentTick);
   }

   static int currentTick() {
      return ServerVersion.isFolia() ? (int)((System.nanoTime() - FOLIA_CLOCK_ORIGIN) / 50000000L) : Bukkit.getCurrentTick();
   }

   public Aura(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.auraName = Optional.ofNullable(
         mlc.getPlaceholderString(new String[]{"aura", "auraname", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"}, null)
      );
      this.auraGroup = Optional.ofNullable(mlc.getPlaceholderString(new String[]{"auragroup", "auratype", "group", "type", "g"}, null));
      List<String> tags = mlc.getStringList(new String[]{"tags", "tag"}, null);
      if (tags != null) {
         for (String tag : tags) {
            if (tag != null && !tag.isEmpty()) {
               this.auraTags.add(PlaceholderString.of(tag));
            }
         }
      }

      String attachmentType = mlc.getString(new String[]{"attachmenttype", "attachment", "attach"}, "NONE");

      try {
         this.attachmentType = AuraAttachmentType.get(attachmentType);
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid aura attachment type specified");
         this.attachmentType = Optional.empty();
      }

      if (this.attachmentType.isPresent()) {
         this.attachment = this.attachmentType.get().create(this, mlc);
      }

      this.charges = mlc.getPlaceholderInteger(new String[]{"charges", "c"}, 0);
      this.interval = mlc.getPlaceholderDuration(new String[]{"interval", "i"}, DurationUnit.TICKS, "1");
      this.duration = mlc.getPlaceholderDuration(new String[]{"ticks", "t", "duration", "d", "time", "t"}, DurationUnit.TICKS, "200");
      this.maxStacks = mlc.getPlaceholderInteger(new String[]{"maxstacks", "ms"}, 1);
      this.decayStackOnExpire = mlc.getBoolean(new String[]{"decaystackonexpire", "decayonexpire", "decaystack", "dsoe"}, false);
      this.mergeAll = mlc.getBoolean(new String[]{"mergeall", "ma"}, false);
      this.overwriteAll = mlc.getBoolean(new String[]{"overwriteall", "overwrite", "ow"}, false);
      this.overwriteCaster = mlc.getBoolean(new String[]{"overwritesamecaster", "osc", "oc"}, false);
      this.mergeSameCaster = mlc.getBoolean(new String[]{"mergesamecaster", "msc", "mc"}, !this.mergeAll && !this.overwriteAll && !this.overwriteCaster);
      this.refreshDuration = mlc.getBoolean(new String[]{"refreshduration", "rd"}, true);
      this.showBarTimer = mlc.getBoolean(new String[]{"showbartimer", "bartimer", "bt"}, false);
      if (this.showBarTimer) {
         this.barTimerDisplay = mlc.getPlaceholderString(new String[]{"bartimerdisplay", "bartimertext"}, "<skill.var.aura-name>");
         String barTimerColor = mlc.getString("bartimercolor", "RED");
         String barTimerStyle = mlc.getString("bartimerstyle", "SOLID");

         try {
            this.barTimerColor = AbstractBossBar.BarColor.valueOf(barTimerColor);
         } catch (Exception ex) {
            this.barTimerColor = AbstractBossBar.BarColor.RED;
         }

         try {
            this.barTimerStyle = AbstractBossBar.BarStyle.valueOf(barTimerStyle);
         } catch (Exception ex2) {
            this.barTimerStyle = AbstractBossBar.BarStyle.SOLID;
         }
      }

      this.cancelOnGiveDamage = mlc.getBoolean(new String[]{"cancelongivedamage", "cogd"}, false);
      this.cancelOnTakeDamage = mlc.getBoolean(new String[]{"cancelontakedamage", "cotd"}, false);
      this.cancelOnDeath = mlc.getBoolean(new String[]{"cancelondeath", "cod"}, true);
      this.cancelOnCasterDeath = mlc.getBoolean(new String[]{"canceloncasterdeath", "cocd"}, false);
      this.cancelOnTeleport = mlc.getBoolean(new String[]{"cancelonteleport", "cot"}, false);
      this.cancelOnChangeWorld = mlc.getBoolean(new String[]{"cancelonchangeworld", "cocw"}, false);
      this.cancelOnSkillCast = mlc.getBoolean(new String[]{"cancelonskilluse", "cosu"}, false);
      this.cancelOnQuit = mlc.getBoolean(new String[]{"cancelonquit", "coq"}, true);
      this.targetIsCaster = mlc.getBoolean(new String[]{"targetiscaster", "tic", "targetcaster"}, false);
      this.persistId = mlc.getString(new String[]{"persistid", "persist", "pid"}, null);
      this.doEndSkillOnTerminate = mlc.getBoolean(new String[]{"doendskillonterminate", "desot", "alwaysrunendskill", "ares"}, true);
      this.onStartSkillName = mlc.getString(new String[]{"onstartskill", "onstart", "os"});
      this.onTickSkillName = mlc.getString(new String[]{"ontickskill", "ontick", "ot"});
      this.onEndSkillName = mlc.getString(new String[]{"onendskill", "onend", "oe"});
      this.getManager().queueSecondPass(() -> {
         if (this.onStartSkillName != null) {
            this.onStartSkill = this.getManager().getSkill(file, this, this.onStartSkillName);
         }

         if (this.onTickSkillName != null) {
            this.onTickSkill = this.getManager().getSkill(file, this, this.onTickSkillName);
         }

         if (this.onEndSkillName != null) {
            this.onEndSkill = this.getManager().getSkill(file, this, this.onEndSkillName);
         }
      });
      AuraExecutor auraManager = this.getPlugin().getSkillManager().getAuraManager();
      auraManager.registerPersistentAura(this.getPersistId(), this);
   }

   protected String getPersistId() {
      if (this.persistId != null && !this.persistId.isEmpty()) {
         return this.persistId;
      } else {
         String configLine = this.getConfigLine();
         return configLine != null && !configLine.isEmpty() ? MythicUtil.getMD5(configLine) : this.getUuid().toString();
      }
   }

   protected boolean shouldPersistOnQuit() {
      return !this.cancelOnQuit;
   }

   public abstract class AuraTracker implements Terminable, TerminableConsumer, Runnable, IParentSkill {
      protected final TerminableRegistry components = TerminableRegistry.create();
      private Task task;
      private Task expirationTask;
      private volatile int expirationTick;
      private boolean expired;
      @Getter
      public SkillMetadata skillMetadata;
      protected Optional<AbstractEntity> entity;
      protected Optional<AbstractLocation> location;
      protected List<String> tags = new ArrayList<>();
      protected AuraAttachment.AttachmentTracker attachment;
      protected transient AbstractBossBar barTimer = null;
      @Getter
      private String name;
      @Getter
      protected int stacks = 1;
      @Getter
      protected int maxStacks;
      @Getter
      protected int chargesRemaining;
      @Getter @Setter
      protected int ticksRemaining;
      @Getter
      protected int startDuration;
      @Getter
      protected int startCharges;
      @Getter
      protected int interval;
      @Getter
      protected boolean hasEnded = false;
      private boolean suppressEndSkill;
      private boolean suppressStartSkill;
      private boolean asyncTick;
      private volatile boolean terminateQueued;
      private int unloadGraceUntil = -1;

      public AuraTracker(SkillMetadata data) {
         this.entity = Optional.of(data.getCaster().getEntity());
         this.location = Optional.empty();
         this.skillMetadata = data.deeperClone();
         this.skillMetadata.setCallingEvent(this);
         this.skillMetadata.setIsAsync(true);
         this.skillMetadata.setExecuteAfterDeath(true);
         this.chargesRemaining = Aura.this.charges.get(data);
         this.ticksRemaining = Aura.this.duration.get(data).getTicks();
         this.maxStacks = Aura.this.maxStacks.get(data);
         this.startDuration = Aura.this.duration.get(data).getTicks();
         this.startCharges = Aura.this.charges.get(data);
         this.interval = Math.max(1, Aura.this.interval.get(data).getTicks());
         this.resolveTags();
         this.applyPersistenceData();
      }

      public AuraTracker(SkillCaster caster, AbstractEntity target, SkillMetadata data) {
         this.entity = Optional.of(caster.getEntity());
         this.location = Optional.empty();
         this.skillMetadata = data.deeperClone();
         this.skillMetadata.setCallingEvent(this);
         this.skillMetadata.setEntityTarget(target);
         this.skillMetadata.setIsAsync(true);
         this.skillMetadata.setExecuteAfterDeath(true);
         this.chargesRemaining = Aura.this.charges.get(data, target);
         this.ticksRemaining = Aura.this.duration.get(data, target).getTicks();
         this.maxStacks = Aura.this.maxStacks.get(data, target);
         this.startDuration = Aura.this.duration.get(data, target).getTicks();
         this.startCharges = Aura.this.charges.get(data, target);
         this.interval = Math.max(1, Aura.this.interval.get(data, target).getTicks());
         this.resolveTags();
         this.applyPersistenceData();
      }

      public AuraTracker(AbstractEntity entity, SkillMetadata data) {
         this.entity = Optional.of(entity);
         this.location = Optional.empty();
         this.skillMetadata = data.deeperClone();
         this.skillMetadata.setCallingEvent(this);
         this.skillMetadata = this.skillMetadata.setEntityTarget(entity);
         this.skillMetadata = this.skillMetadata.setIsAsync(true);
         this.skillMetadata.setExecuteAfterDeath(true);
         this.chargesRemaining = Aura.this.charges.get(data, entity);
         this.ticksRemaining = Aura.this.duration.get(data, entity).getTicks();
         this.maxStacks = Aura.this.maxStacks.get(data, entity);
         this.startDuration = Aura.this.duration.get(data, entity).getTicks();
         this.startCharges = Aura.this.charges.get(data, entity);
         this.interval = Math.max(1, Aura.this.interval.get(data, entity).getTicks());
         this.resolveTags();
         this.applyPersistenceData();
      }

      public AuraTracker(SkillCaster caster, AbstractLocation target, SkillMetadata data) {
         this.entity = Optional.of(caster.getEntity());
         this.location = Optional.empty();
         this.skillMetadata = data.deeperClone();
         this.skillMetadata.setCallingEvent(this);
         this.skillMetadata = this.skillMetadata.setLocationTarget(target);
         this.skillMetadata = this.skillMetadata.setIsAsync(true);
         this.skillMetadata.setExecuteAfterDeath(true);
         this.chargesRemaining = Aura.this.charges.get(data);
         this.ticksRemaining = Aura.this.duration.get(data).getTicks();
         this.maxStacks = Aura.this.maxStacks.get(data);
         this.startDuration = Aura.this.duration.get(data).getTicks();
         this.startCharges = Aura.this.charges.get(data);
         this.interval = Math.max(1, Aura.this.interval.get(data).getTicks());
         this.resolveTags();
         this.applyPersistenceData();
      }

      public AuraTracker(AbstractLocation location, SkillMetadata data) {
         this.entity = Optional.empty();
         this.location = Optional.of(location);
         this.skillMetadata = data.deeperClone();
         this.skillMetadata.setCallingEvent(this);
         this.skillMetadata.setLocationTarget(location);
         this.skillMetadata.setIsAsync(true);
         this.skillMetadata.setExecuteAfterDeath(true);
         this.chargesRemaining = Aura.this.charges.get(data);
         this.ticksRemaining = Aura.this.duration.get(data).getTicks();
         this.maxStacks = Aura.this.maxStacks.get(data);
         this.startDuration = Aura.this.duration.get(data).getTicks();
         this.startCharges = Aura.this.charges.get(data);
         this.interval = Math.max(1, Aura.this.interval.get(data).getTicks());
         this.resolveTags();
         this.applyPersistenceData();
      }

      public void registerAuraComponent(Terminable component) {
         this.components.accept(component);
      }

      public UUID getCasterUUID() {
         return this.skillMetadata.getCaster().getEntity().getUniqueId();
      }

      public Optional<String> getGroup() {
         return Aura.this.auraGroup.map(placeholderString -> placeholderString.get(this.skillMetadata));
      }

      public List<String> getTags() {
         return this.tags;
      }

      public boolean hasTag(String tag) {
         return tag == null ? false : this.tags.contains(tag.trim().toLowerCase());
      }

      public Optional<AbstractEntity> getEntity() {
         return this.entity;
      }

      private void resolveTags() {
         if (Aura.this.auraTags != null && !Aura.this.auraTags.isEmpty()) {
            for (PlaceholderString tag : Aura.this.auraTags) {
               if (tag != null) {
                  String value;
                  if (this.entity.isPresent()) {
                     value = tag.get(this.skillMetadata, this.entity.get());
                  } else {
                     value = tag.get(this.skillMetadata);
                  }

                  if (value != null && !value.isEmpty()) {
                     for (String entry : value.split(",")) {
                        String normalized = entry.trim().toLowerCase();
                        if (!normalized.isEmpty() && !this.tags.contains(normalized)) {
                           this.tags.add(normalized);
                        }
                     }
                  }
               }
            }
         }
      }

      public void merge(Aura.AuraTracker tracker) {
         this.stacks = this.stacks + tracker.getStacks();
         if (this.stacks > this.maxStacks) {
            this.stacks = this.maxStacks;
         }

         if (Aura.this.refreshDuration) {
            this.chargesRemaining = Aura.this.charges.get(this.skillMetadata);
            this.ticksRemaining = Aura.this.duration.get(this.skillMetadata).getTicks();
            this.setDeadline(this.ticksRemaining);
         }

         if (tracker.attachment != null) {
            this.getEntity().ifPresent(e -> {
               if (this.attachment != null) {
                  this.attachment.despawn();
               }

               this.attachment = tracker.attachment;
               this.attachment.spawn(e);
               this.skillMetadata.setMetadata("attachment", this.attachment);
            });
         }
      }

      public boolean executeAuraSkill(Optional<Skill> skill, SkillMetadata data) {
         return this.executeAuraSkill(skill, data, false);
      }

      public boolean executeAuraSkill(Optional<Skill> skill, SkillMetadata data, boolean atCaster) {
         data = data.deepClone();
         if (atCaster) {
            data = data.setEntityTarget(data.getCaster().getEntity());
         } else if (this.entity.isPresent()) {
            data.setEntityTarget(this.entity.get());
         }

         return this.execute(skill, data);
      }

      public boolean executeTargetedAuraSkill(Optional<Skill> skill, SkillMetadata data, AbstractEntity target) {
         data = data.deepClone();
         data.setEntityTarget(target);
         return this.execute(skill, data);
      }

      public boolean executeTargetedAuraSkill(Optional<Skill> skill, SkillMetadata data, AbstractLocation target) {
         data = data.deepClone();
         if (data.getEntityTargets() != null) {
            data.getEntityTargets().clear();
         }

         if (data.getLocationTargets() != null) {
            data.getLocationTargets().clear();
         }

         data.setLocationTarget(target);
         return this.execute(skill, data);
      }

      protected boolean execute(Optional<Skill> skill, SkillMetadata data) {
         VariableRegistry vars = data.getVariables();
         if (Aura.this.auraName.isPresent()) {
            vars.putString("aura-name", Aura.this.auraName.get().get(this.skillMetadata));
         }

         if (Aura.this.auraGroup.isPresent()) {
            vars.putString("aura-type", Aura.this.auraGroup.get().get(this.skillMetadata));
         }

         vars.putInt("aura-charges", this.chargesRemaining);
         vars.putInt("aura-duration", this.ticksRemaining);
         vars.putInt("aura-duration-millis", this.ticksRemaining * 50);
         vars.putInt("aura-stacks", this.stacks);
         if (this.barTimer != null) {
            double progress = (double)this.ticksRemaining / this.startDuration;
            this.barTimer.setTitle(Aura.this.barTimerDisplay.get(data, this.entity.get()));
            this.barTimer.setProgress(progress);
         }

         if (skill != null && skill.isPresent()) {
            if (skill.get().isUsable(data)) {
               skill.get().execute(data);
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }

      public boolean start() {
         this.name = Aura.this.auraName.isPresent() ? Aura.this.auraName.get().get(this.skillMetadata) : Aura.this.getUuid().toString();
         String groupName = Aura.this.auraGroup.<String>map(pg -> pg.get(this.skillMetadata)).orElse(null);
         MythicAuraStartEvent startEvent = new MythicAuraStartEvent(
            Aura.this,
            this.skillMetadata.getCaster(),
            this.skillMetadata,
            this.name,
            groupName,
            this.entity.orElse(null),
            this.location.orElse(null),
            this.ticksRemaining,
            this.chargesRemaining,
            this.stacks
         );
         Events.callSync(startEvent);
         if (startEvent.isCancelled()) {
            return false;
         } else {
            if (this.entity.isPresent()) {
               if (Aura.this.attachment != null) {
                  this.attachment = Aura.this.attachment.create(this, this.entity.get());
               }

               if (!Aura.this.mergeAll && !Aura.this.mergeSameCaster) {
                  if (Aura.this.overwriteAll || Aura.this.overwriteCaster) {
                     Aura.this.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(this.entity.get()).removeAll(this.name);
                  }

                  Aura.this.getPlugin().getSkillManager().getAuraManager().getAuraRegistry(this.entity.get()).registerAura(this.name, this);
               } else if (Aura.this.getPlugin()
                  .getSkillManager()
                  .getAuraManager()
                  .getAuraRegistry(this.entity.get())
                  .mergeAura(this.name, this, Aura.this.mergeSameCaster)) {
                  return false;
               }

               if (this.attachment != null) {
                  this.attachment.spawn(this.entity.get());
                  this.skillMetadata.setMetadata("attachment", this.attachment);
               }
            }

            this.setupAuraEvents();
            this.auraStart();
            this.startAuraTask();
            return true;
         }
      }

      public void setupAuraEvents() {
         SkillCaster caster = this.skillMetadata.getCaster();
         if (Aura.this.cancelOnCasterDeath) {
            if (caster.getEntity().isPlayer()) {
               Events.subscribe(PlayerDeathEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(this.skillMetadata.getCaster().getEntity().getUniqueId()))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            } else if (caster instanceof ActiveMob am) {
               Events.subscribe(MythicMobDeathEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(this.skillMetadata.getCaster().getEntity().getUniqueId()))
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(MythicMobDespawnEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(this.skillMetadata.getCaster().getEntity().getUniqueId()))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            } else {
               Events.subscribe(EntityDeathEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(this.skillMetadata.getCaster().getEntity().getUniqueId()))
                  .handler(event -> {
                     if (!ServerVersion.isPaper() || !event.isCancelled()) {
                        this.terminate();
                     }
                  })
                  .bindWith(this);
            }
         }

         if (this.entity.isPresent()) {
            AbstractEntity entity = this.entity.get();
            UUID uuid = entity.getUniqueId();
            if (Aura.this.cancelOnGiveDamage) {
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(event -> event.getDamager().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (Aura.this.cancelOnTakeDamage) {
               Events.subscribe(EntityDamageEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(EntityDamageByBlockEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (Aura.this.cancelOnTeleport && entity.isPlayer()) {
               Events.subscribe(PlayerTeleportEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(PlayerPortalEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (Aura.this.cancelOnChangeWorld && entity.isPlayer()) {
               Events.subscribe(PlayerTeleportEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(uuid))
                  .filter(event -> event.getTo().getWorld() != event.getFrom().getWorld())
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(PlayerPortalEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(uuid))
                  .filter(event -> event.getTo().getWorld() != event.getFrom().getWorld())
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (Aura.this.cancelOnQuit && entity.isPlayer()) {
               Events.subscribe(PlayerQuitEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (!Aura.this.cancelOnQuit && entity.isPlayer()) {
               Events.subscribe(PlayerQuitEvent.class).filter(event -> event.getPlayer().getUniqueId().equals(uuid)).handler(event -> {
                  this.suppressEndSkillOnQuit();
                  this.persistSnapshotOnQuit();
               }).bindWith(this);
            }

            if (Aura.this.cancelOnDeath) {
               Events.subscribe(EntityDeathEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
               Events.subscribe(PlayerDeathEvent.class)
                  .filter(event -> event.getEntity().getUniqueId().equals(uuid))
                  .handler(event -> this.terminate())
                  .bindWith(this);
            }

            if (Aura.this.showBarTimer && entity.isPlayer()) {
               this.barTimer = MythicProvider.get().getBootstrap().createBossBar(" ", Aura.this.barTimerColor, Aura.this.barTimerStyle);
               this.barTimer.setProgress(1.0);
               this.barTimer.addPlayer(entity.asPlayer());
               this.components.accept(this.barTimer);
            }
         }
      }

      private void startAuraTask() {
         if (!this.hasEnded) {
            this.setDeadline(this.ticksRemaining);
            this.armExpiration();
            this.components.accept(this::cancelExpiration);
            if (!Aura.this.forceSync && Aura.this.threadSafetyLevel != ThreadSafetyLevel.SYNC_ONLY && !ServerVersion.isFolia()) {
               this.asyncTick = true;
               this.task = Schedulers.async().runRepeating(this, 0L, (long)this.interval);
               this.task.bindWith(this);
            } else {
               if (this.entity.isPresent()) {
                  this.task = Schedulers.of(this.entity.get().getBukkitEntity()).runRepeating(this, 0L, (long)this.interval);
               } else if (this.location.isPresent()) {
                  this.task = Schedulers.of(BukkitAdapter.adapt(this.location.get())).runRepeating(this, 0L, (long)this.interval);
               } else {
                  this.task = Schedulers.of(this.skillMetadata.getCaster().getEntity().getBukkitEntity()).runRepeating(this, 0L, (long)this.interval);
               }

               this.task.bindWith(this);
            }
         }
      }

      public int getTimesRan() {
         return this.task == null ? 0 : this.task.getTimesRan();
      }

      public boolean isValid() {
         if (this.components.hasTerminated() || this.hasEnded) {
            return false;
         } else if (this.startCharges > 0 && this.chargesRemaining <= 0) {
            return false;
         } else if (this.entity.isPresent() && !this.entity.get().isValid()) {
            AbstractEntity e = this.entity.get();
            return !Aura.this.cancelOnDeath && e.isPlayer() && e.asPlayer().isOnline();
         } else {
            return true;
         }
      }

      @Override
      public void run() {
         if (!this.hasEnded && !this.terminateQueued) {
            if (this.isValid()) {
               this.ticksRemaining = Aura.remainingTicksUntil(this.expirationTick, Aura.currentTick());
               if (this.ticksRemaining <= 0) {
                  this.expire();
                  if (this.hasEnded || this.expired) {
                     return;
                  }

                  this.ticksRemaining = Aura.remainingTicksUntil(this.expirationTick, Aura.currentTick());
               }

               if (this.entity.isPresent()) {
                  this.skillMetadata.setOrigin(this.entity.get().getLocation());
               }

               this.auraTick();
               this.ticksRemaining = Aura.remainingTicksUntil(this.expirationTick, Aura.currentTick());
               if (!this.hasEnded) {
                  if (!this.isValid()) {
                     this.terminateFromTick();
                  }
               }
            } else if (!this.awaitingUnload()) {
               this.terminateFromTick();
            }
         }
      }

      private void terminateFromTick() {
         if (this.asyncTick && !this.entity.isEmpty() && !this.entity.get().isValid()) {
            this.terminateQueued = true;
            Schedulers.sync().run(this::terminate);
         } else {
            this.terminate();
         }
      }

      private synchronized void setDeadline(int delay) {
         this.expirationTick = Aura.currentTick() + Math.max(0, delay);
      }

      private synchronized void armExpiration() {
         this.cancelExpiration();
         int delay = Math.max(0, this.expirationTick - Aura.currentTick());
         int period = Math.max(1, delay);
         Consumer<Task> expiration = t -> {
            t.stop();
            if (Aura.currentTick() < this.expirationTick) {
               this.armExpiration();
            } else {
               this.expire();
            }
         };
         if (!Aura.this.forceSync && Aura.this.threadSafetyLevel != ThreadSafetyLevel.SYNC_ONLY && !ServerVersion.isFolia()) {
            this.expirationTask = Schedulers.async().runRepeating(expiration, (long)delay, (long)period);
         } else if (this.entity.isPresent()) {
            this.expirationTask = Schedulers.of(this.entity.get().getBukkitEntity()).runRepeating(expiration, (long)delay, (long)period);
         } else if (this.location.isPresent()) {
            this.expirationTask = Schedulers.of(BukkitAdapter.adapt(this.location.get())).runRepeating(expiration, (long)delay, (long)period);
         } else {
            this.expirationTask = Schedulers.of(this.skillMetadata.getCaster().getEntity().getBukkitEntity())
               .runRepeating(expiration, (long)delay, (long)period);
         }
      }

      private synchronized void cancelExpiration() {
         if (this.expirationTask != null) {
            this.expirationTask.stop();
            if (!ServerVersion.isFolia() && (Aura.this.forceSync || Aura.this.threadSafetyLevel == ThreadSafetyLevel.SYNC_ONLY)) {
               Bukkit.getScheduler().cancelTask(this.expirationTask.getBukkitId());
            }

            this.expirationTask = null;
         }
      }

      private void expire() {
         synchronized (this) {
            if (this.hasEnded || this.expired || Aura.currentTick() < this.expirationTick) {
               return;
            }

            if (Aura.this.decayStackOnExpire && this.stacks > 1) {
               this.stacks--;
               this.ticksRemaining = this.startDuration;
               this.setDeadline(this.ticksRemaining);
               this.armExpiration();
               return;
            }

            this.expired = true;
            this.ticksRemaining = 0;
         }

         this.terminate();
      }

      public void consumeCharge() {
         if (this.startCharges > 0) {
            this.chargesRemaining--;
            if (this.chargesRemaining <= 0) {
               this.terminate();
            }
         }
      }

      public void auraStart() {
         if (!this.suppressStartSkill) {
            this.executeAuraSkill(Aura.this.onStartSkill, this.skillMetadata);
         }
      }

      public void auraTick() {
         this.executeAuraSkill(Aura.this.onTickSkill, this.skillMetadata);
      }

      public void auraStop() {
         if (!this.suppressEndSkill) {
            this.executeAuraSkill(Aura.this.onEndSkill, this.skillMetadata, false);
         }
      }

      private void emitAuraStopEvent() {
         String groupName = Aura.this.auraGroup.<String>map(pg -> pg.get(this.skillMetadata)).orElse(null);
         MythicAuraStopEvent stopEvent = new MythicAuraStopEvent(
            Aura.this,
            this.skillMetadata.getCaster(),
            this.skillMetadata,
            this.name,
            groupName,
            this.entity.orElse(null),
            this.location.orElse(null),
            this.ticksRemaining,
            this.chargesRemaining,
            this.stacks
         );
         Events.callSync(stopEvent);
      }

      public void terminateFromRegistry() {
         MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC,
            "++ AuraTracker cancelled from Registry for " + this.skillMetadata.getCaster().getEntity().getUniqueId() + ": skill = " + Aura.this.line
         );
         if (!this.hasEnded) {
            this.hasEnded = true;
            this.emitAuraStopEvent();

            try {
               if (!Aura.this.doEndSkillOnTerminate) {
                  this.suppressEndSkill = true;
               }

               this.auraStop();
            } catch (Exception e) {
               e.printStackTrace();
            }

            if (this.entity.isPresent()) {
               MythicProvider.get().getSkillManager().getAuraManager().getAuraRegistry(this.entity.get()).unregisterAura(this.name, this);
               if (this.attachment != null) {
                  this.attachment.despawn();
               }
            }

            if (this.barTimer != null) {
               this.barTimer = null;
            }
         }

         synchronized (this.components) {
            if (!this.components.hasTerminated()) {
               this.components.terminate();
            }
         }
      }

      @Override
      public void close() {
         MythicLogger.debug(
            MythicLogger.DebugLevel.MECHANIC, "++ AuraTracker cancelled for {0}: skill = {1}", this.skillMetadata.getCaster().getName(), Aura.this.line
         );
         if (!this.hasEnded) {
            this.hasEnded = true;
            this.emitAuraStopEvent();

            try {
               this.auraStop();
            } catch (Exception e) {
               e.printStackTrace();
            }

            if (this.entity.isPresent()) {
               MythicProvider.get().getSkillManager().getAuraManager().getAuraRegistry(this.entity.get()).unregisterAura(this.name, this);
               if (this.attachment != null) {
                  this.attachment.despawn();
               }
            }

            if (this.barTimer != null) {
               this.barTimer = null;
            }
         }

         synchronized (this.components) {
            if (!this.components.hasTerminated()) {
               this.components.terminate();
            }
         }
      }

      @Override
      public <T extends AutoCloseable> T bind(T terminable) {
         this.components.accept((Terminable)terminable);
         return terminable;
      }

      @Override
      public void setCancelled() {
         this.close();
      }

      @Override
      public boolean getCancelled() {
         return this.hasTerminated();
      }

      public void setStacks(int stacks) {
         this.stacks = stacks;
      }

      private void applyPersistenceData() {
         AuraPersistenceData persisted = this.skillMetadata.getMetadata(AuraPersistenceData.class, "aura-persist").orElse(null);
         if (persisted != null) {
            if (persisted.startDuration > 0) {
               this.startDuration = persisted.startDuration;
            }

            if (persisted.startCharges >= 0) {
               this.startCharges = persisted.startCharges;
            }

            this.ticksRemaining = persisted.ticksRemaining;
            this.chargesRemaining = persisted.chargesRemaining;
            if (persisted.stacks > 0) {
               this.stacks = Math.min(persisted.stacks, this.maxStacks);
            }

            this.suppressStartSkill = true;
         }
      }

      public boolean shouldPersistOnQuit() {
         return Aura.this.shouldPersistOnQuit();
      }

      public void suppressEndSkillOnQuit() {
         if (Aura.this.shouldPersistOnQuit()) {
            this.suppressEndSkill = true;
         }
      }

      public void persistSnapshotOnQuit() {
         if (Aura.this.shouldPersistOnQuit()) {
            if (!this.entity.isEmpty() && this.entity.get().isPlayer()) {
               PlayerData profile = MythicBukkit.inst().getPlayerManager().getProfile(this.entity.get().asPlayer());
               if (profile != null) {
                  AuraPersistenceData data = this.createPersistenceSnapshot();
                  if (data != null && data.id != null && !data.id.isEmpty()) {
                     profile.savePersistentAura(data);
                  }
               }
            }
         }
      }

      private boolean awaitingUnload() {
         if (!this.entity.isEmpty() && !this.hasEnded) {
            AbstractEntity holder = this.entity.get();
            if (holder.isValid() || holder.isPlayer() || !holder.isLiving() || holder.getHealth() <= 0.0) {
               return false;
            } else if (this.startCharges > 0 && this.chargesRemaining <= 0) {
               return false;
            } else {
               int now = Aura.currentTick();
               if (this.unloadGraceUntil < 0) {
                  this.unloadGraceUntil = now + 20;
                  return true;
               } else {
                  return now < this.unloadGraceUntil;
               }
            }
         } else {
            return false;
         }
      }

      public AuraPersistenceData snapshotForUnload() {
         if (this.hasEnded) {
            return null;
         } else {
            AuraPersistenceData data = this.createPersistenceSnapshot();
            if (data != null && data.id != null && !data.id.isEmpty()) {
               this.suppressEndSkill = true;
               return data;
            } else {
               return null;
            }
         }
      }

      public AuraPersistenceData createPersistenceSnapshot() {
         String id = Aura.this.getPersistId();
         AuraPersistenceData data = new AuraPersistenceData(id);
         data.casterUuid = this.skillMetadata.getCaster().getEntity().getUniqueId().toString();
         data.stacks = this.stacks;
         data.chargesRemaining = this.chargesRemaining;
         data.ticksRemaining = this.ticksRemaining;
         data.startCharges = this.startCharges;
         data.startDuration = this.startDuration;
         return data;
      }
   }
}
