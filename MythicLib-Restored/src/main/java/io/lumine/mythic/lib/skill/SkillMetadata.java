package io.lumine.mythic.lib.skill;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.VariableList;
import io.lumine.mythic.lib.script.variable.VariableScope;
import io.lumine.mythic.lib.script.variable.def.AttackMetadataVariable;
import io.lumine.mythic.lib.script.variable.def.DoubleVariable;
import io.lumine.mythic.lib.script.variable.def.EntityVariable;
import io.lumine.mythic.lib.script.variable.def.PlayerVariable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.script.variable.def.RandomVariable;
import io.lumine.mythic.lib.script.variable.def.StatsVariable;
import io.lumine.mythic.lib.util.EntityLocationType;
import io.lumine.mythic.lib.util.SkillOrientation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillMetadata {
   private final Skill cast;
   private final VariableList vars;
   @NotNull
   private final PlayerMetadata caster;
   @NotNull
   private final Location source;
   @Nullable
   private final Entity targetEntity;
   @Nullable
   private final Location targetLocation;
   @Nullable
   private final AttackMetadata attackSource;
   @Nullable
   public final SkillOrientation orientation;
   public static final List<String> RESERVED_VARIABLE_NAMES = Arrays.asList(
      "modifier",
      "parameter",
      "source",
      "targetLocation",
      "targetLoc",
      "target_loc",
      "target_location",
      "targetloc",
      "targetl",
      "caster",
      "attack",
      "stat",
      "target",
      "var",
      "rand",
      "random",
      "rdm"
   );
   public static final VariableList SERVER_VARIABLE_LIST = new VariableList(VariableScope.SERVER);
   private static final Pattern INTERNAL_PLACEHOLDER_PATTERN = UtilityMethods.internalPlaceholderPattern('<', '>');

   public SkillMetadata(Skill cast, @NotNull MMOPlayerData caster) {
      this(
         cast,
         caster.getStatMap().cache(EquipmentSlot.MAIN_HAND),
         new VariableList(VariableScope.SKILL),
         caster.getPlayer().getLocation(),
         null,
         null,
         null,
         null
      );
   }

   @Deprecated
   public SkillMetadata(
      Skill cast, @NotNull AttackMetadata attackMeta, @NotNull Location source, @Nullable Location targetLocation, @Nullable Entity targetEntity
   ) {
      this(cast, (PlayerMetadata)attackMeta.getAttacker(), new VariableList(VariableScope.SKILL), source, targetLocation, targetEntity, null, attackMeta);
   }

   @Deprecated
   public SkillMetadata(
      Skill cast,
      @NotNull PlayerMetadata caster,
      @NotNull VariableList vars,
      @Nullable AttackMetadata attackMeta,
      @NotNull Location source,
      @Nullable Location targetLocation,
      @Nullable Entity targetEntity,
      @Nullable SkillOrientation orientation
   ) {
      this(cast, caster, vars, source, targetLocation, targetEntity, orientation, attackMeta);
   }

   public SkillMetadata(
      Skill cast, @NotNull Location source, @Nullable Location targetLocation, @Nullable Entity targetEntity, @NotNull AttackMetadata attackMeta
   ) {
      this(cast, (PlayerMetadata)attackMeta.getAttacker(), new VariableList(VariableScope.SKILL), source, targetLocation, targetEntity, null, attackMeta);
   }

   public SkillMetadata(
      Skill cast,
      @NotNull PlayerMetadata caster,
      @NotNull VariableList vars,
      @NotNull Location source,
      @Nullable Location targetLocation,
      @Nullable Entity targetEntity,
      @Nullable SkillOrientation orientation
   ) {
      this(cast, caster, vars, source, targetLocation, targetEntity, orientation, null);
   }

   public SkillMetadata(
      Skill cast,
      @NotNull PlayerMetadata caster,
      @NotNull VariableList vars,
      @NotNull Location source,
      @Nullable Location targetLocation,
      @Nullable Entity targetEntity,
      @Nullable SkillOrientation orientation,
      @Nullable AttackMetadata attackSource
   ) {
      this.cast = cast;
      this.caster = caster;
      this.vars = vars;
      this.source = source;
      this.targetLocation = targetLocation;
      this.targetEntity = targetEntity;
      this.orientation = orientation;
      this.attackSource = attackSource;
   }

   @NotNull
   public Skill getCast() {
      return this.cast;
   }

   @NotNull
   public VariableList getVariableList() {
      return this.vars;
   }

   @NotNull
   public PlayerMetadata getCaster() {
      return this.caster;
   }

   @NotNull
   public Location getSourceLocation() {
      return this.source.clone();
   }

   @Deprecated
   public boolean hasAttackBound() {
      return this.hasAttackSource();
   }

   @Deprecated
   @NotNull
   public AttackMetadata getAttack() {
      return this.getAttackSource();
   }

   public boolean hasAttackSource() {
      return this.attackSource != null;
   }

   @NotNull
   public AttackMetadata getAttackSource() {
      return Objects.requireNonNull(this.attackSource, "Skill was not triggered by any attack");
   }

   @Deprecated
   public double getModifier(String param) {
      return this.getParameter(param);
   }

   public double getParameter(@NotNull String parameter) {
      return this.caster.getData().getSkillModifierMap().calculateValue(this.cast, parameter);
   }

   @NotNull
   public Entity getTargetEntity() {
      return Objects.requireNonNull(this.targetEntity, "Skill has no target entity");
   }

   @Nullable
   public Entity getTargetEntityOrNull() {
      return this.targetEntity;
   }

   public boolean hasTargetEntity() {
      return this.targetEntity != null;
   }

   @NotNull
   public Location getTargetLocation() {
      return Objects.requireNonNull(this.targetLocation, "Skill has no target location").clone();
   }

   @Nullable
   public Location getTargetLocationOrNull() {
      return this.targetLocation == null ? null : this.targetLocation.clone();
   }

   public boolean hasTargetLocation() {
      return this.targetLocation != null;
   }

   @NotNull
   public SkillOrientation getOrientation() {
      return Objects.requireNonNull(this.orientation, "Skill has no orientation");
   }

   @Nullable
   public SkillOrientation getOrientationOrNull() {
      return this.orientation;
   }

   public boolean hasOrientation() {
      return this.orientation != null;
   }

   @NotNull
   public Location getSkillLocation(boolean sourceLocation) {
      return sourceLocation
         ? this.source.clone()
         : (
            this.targetLocation != null
               ? this.targetLocation.clone()
               : (this.targetEntity != null ? EntityLocationType.BODY.getLocation(this.targetEntity) : this.source.clone())
         );
   }

   @NotNull
   public Entity getSkillEntity(boolean caster) {
      return (Entity)(!caster && this.targetEntity != null ? this.targetEntity : this.getCaster().getPlayer());
   }

   @NotNull
   public SkillOrientation getSkillOrientation() {
      return this.orientation != null
         ? this.orientation
         : new SkillOrientation(
            Objects.requireNonNull(this.targetLocation, "Skill has no orientation").clone(), this.targetLocation.clone().subtract(this.source).toVector()
         );
   }

   @NotNull
   public SkillMetadata clone(
      @NotNull Location source, @Nullable Location targetLocation, @Nullable Entity targetEntity, @Nullable SkillOrientation orientation
   ) {
      return new SkillMetadata(this.cast, this.caster, this.vars, source, targetLocation, targetEntity, orientation, this.attackSource);
   }

   @NotNull
   public SkillMetadata clone(@NotNull Location targetLocation) {
      return this.clone(this.source, targetLocation, this.targetEntity, this.orientation);
   }

   @Deprecated
   public Variable getReference(String name) {
      return this.getVariable(name);
   }

   @Deprecated
   public Variable getCustomVariable(String name) {
      return this.getUserVariable(name);
   }

   @NotNull
   public Variable getUserVariable(String name) {
      Variable var = this.vars.getVariable(name);
      if (var != null) {
         return var;
      }

      var = this.getCaster().getData().getVariableList().getVariable(name);
      return var != null ? var : Objects.requireNonNull(SERVER_VARIABLE_LIST.getVariable(name), "Could not find user variable with name '" + name + "'");
   }

   @NotNull
   public Variable getVariable(String name) {
      String[] args = name.split("\\.");
      int i = 1;
      Variable var;
      switch (args[0]) {
         case "modifier":
         case "parameter":
            Validate.isTrue(args.length > 1, "Please specify a modifier name");
            var = new DoubleVariable("temp", this.getParameter(args[i++]));
            break;
         case "source":
            var = new PositionVariable("temp", this.source.clone());
            break;
         case "targetLocation":
         case "target_location":
         case "targetloc":
         case "targetl":
         case "targetLoc":
         case "target_loc":
            var = new PositionVariable("temp", this.getTargetLocation());
            break;
         case "caster":
            var = new PlayerVariable("temp", this.getCaster().getPlayer());
            break;
         case "attack":
            var = new AttackMetadataVariable("temp", this.getAttackSource());
            break;
         case "random":
         case "rand":
         case "rdm":
            var = RandomVariable.INSTANCE;
            break;
         case "stat":
            var = new StatsVariable("temp", this.caster);
            break;
         case "target":
            Validate.notNull(this.targetEntity, "Skill has no target");
            var = this.targetEntity instanceof Player ? new PlayerVariable("temp", (Player)this.targetEntity) : new EntityVariable("temp", this.targetEntity);
            break;
         case "var":
            Validate.isTrue(args.length > 1, "User variable name is not specified. Also, 'var.xxxx' notation is deprecated. Use 'xxx' directly instead");
            var = this.getUserVariable(args[i++]);
            break;
         default:
            var = this.getUserVariable(args[0]);
      }

      while (i < args.length) {
         var = var.getVariable(args[i]);
         i++;
      }

      return var;
   }

   @NotNull
   public String parseString(String str) {
      for (Matcher match = INTERNAL_PLACEHOLDER_PATTERN.matcher(str); match.find(); match = INTERNAL_PLACEHOLDER_PATTERN.matcher(str)) {
         String placeholder = str.substring(match.start() + 1, match.end() - 1);
         str = str.replace("<" + placeholder + ">", this.getVariable(placeholder).toString());
      }

      return MythicLib.plugin.getPlaceholderParser().parse(this.getCaster().getPlayer(), str);
   }

   @Deprecated
   @NotNull
   public AttackMetadata attack(@NotNull LivingEntity target, double damage, DamageType... types) {
      return this.caster.attack(target, damage, types);
   }
}
