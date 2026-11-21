package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:glow",
   aliases = {"glow", "e:glow"},
   description = "Makes the target entity glow",
   deprecated = true
)
public class GlowEffect extends Aura implements ITargetedEntitySkill {
   protected SkillAudience audience;
   @MythicField(
      name = "color",
      aliases = {"c"},
      description = "The color to set",
      defValue = "WHITE"
   )
   protected GlowColor color;

   public GlowEffect(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.auraName = Optional.ofNullable(mlc.getPlaceholderString(new String[]{"auraname", "buffname", "debuffname"}, "#glowing"));
      this.audience = mlc.getAudience("audience", "nearby");
      String color = mlc.getString(new String[]{"color", "c"}, "WHITE");

      try {
         this.color = GlowColor.valueOf(color.toUpperCase());
      } catch (Error | Exception e) {
         MythicLogger.errorMechanicConfig(
            this, mlc, "The 'color' attribute must be a valid ChatColor. see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html"
         );
      }

      this.charges = PlaceholderInt.of("1");
      this.maxStacks = PlaceholderInt.of("1");
      this.mergeAll = true;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> audience = SkillAudience.getEffective(this.audience, data, target);
      if (this.duration.get(data, target).getTicks() == 0) {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
            this.getPlugin().getGlowManager().setColor(target, this.color, audience);
            this.getPlugin().getGlowManager().setGlowing(target, true, audience);
         } else {
            List<Player> players = audience.stream().map(BukkitAdapter::adapt).toList();
            GlowAPI.setGlowing(BukkitAdapter.adapt(target), Color.valueOf(this.color.toString()), players);
         }
      } else {
         new GlowEffect.Tracker(target, data, this.color, audience);
      }

      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker {
      final Collection<AbstractPlayer> audience;
      final GlowColor color;

      public Tracker(AbstractEntity entity, SkillMetadata data, GlowColor color, Collection<AbstractPlayer> audience) {
         super(entity, data);
         this.audience = audience;
         this.color = color;
         this.start();
      }

      @Override
      public void auraStart() {
         super.auraStart();
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
            GlowEffect.this.getPlugin().getGlowManager().setColor(this.entity.get(), this.color, this.audience);
            GlowEffect.this.getPlugin().getGlowManager().setGlowing(this.entity.get(), true, this.audience);
         } else {
            List<Player> players = this.audience.stream().map(BukkitAdapter::adapt).toList();
            GlowAPI.setGlowing(BukkitAdapter.adapt(this.entity.get()), Color.valueOf(this.color.toString()), players);
         }
      }

      @Override
      public void merge(Aura.AuraTracker tracker) {
         super.merge(tracker);
         if (tracker instanceof GlowEffect.Tracker thisTracker) {
            GlowEffect.this.getPlugin().getGlowManager().setColor(this.entity.get(), thisTracker.color, this.audience);
         }
      }

      @Override
      public void auraStop() {
         super.auraStop();
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
            GlowEffect.this.getPlugin().getGlowManager().setGlowing(this.entity.get(), false, this.audience);
         } else {
            List<Player> players = this.audience.stream().map(BukkitAdapter::adapt).toList();
            GlowAPI.setGlowing(BukkitAdapter.adapt(this.entity.get()), false, players);
         }
      }
   }
}
