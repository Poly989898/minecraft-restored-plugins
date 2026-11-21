package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

public class GlowAuraComponent implements AuraComponent {
   private final AuraMechanic mechanic;
   private final Aura.AuraTracker tracker;
   private final SkillAudience audience;
   private final GlowColor color;
   private Collection<AbstractPlayer> resolvedAudience;

   public GlowAuraComponent(AuraMechanic mechanic, Aura.AuraTracker tracker, SkillAudience audience, GlowColor color) {
      this.mechanic = mechanic;
      this.tracker = tracker;
      this.audience = audience;
      this.color = color;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         AbstractEntity entity = maybeEntity.get();
         this.resolvedAudience = SkillAudience.getEffective(this.audience, this.tracker.getSkillMetadata(), entity);
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
            this.mechanic.getPlugin().getGlowManager().setColor(entity, this.color, this.resolvedAudience);
            this.mechanic.getPlugin().getGlowManager().setGlowing(entity, true, this.resolvedAudience);
         } else {
            List<Player> players = this.resolvedAudience.stream().map(BukkitAdapter::adapt).toList();
            GlowAPI.setGlowing(BukkitAdapter.adapt(entity), Color.valueOf(this.color.toString()), players);
         }
      }
   }

   @Override
   public void auraStop() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty() && this.resolvedAudience != null) {
         AbstractEntity entity = maybeEntity.get();
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_18_2)) {
            this.mechanic.getPlugin().getGlowManager().setGlowing(entity, false, this.resolvedAudience);
         } else {
            List<Player> players = this.resolvedAudience.stream().map(BukkitAdapter::adapt).toList();
            GlowAPI.setGlowing(BukkitAdapter.adapt(entity), false, players);
         }
      }
   }

   @MythicAuraComponent(
      name = "glow",
      aliases = {"effect:glow", "e:glow"},
      description = "Makes the aura holder glow"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final SkillAudience audience;
      private final GlowColor color;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.audience = mlc.getAudience("audience", "nearby");
         String color = mlc.getString(new String[]{"color", "c"}, "WHITE");
         GlowColor parsed = GlowColor.WHITE;

         try {
            parsed = GlowColor.valueOf(color.toUpperCase());
         } catch (Error | Exception e) {
            MythicLogger.errorMechanicConfig(
               mechanic, mlc, "The 'color' attribute must be a valid ChatColor. see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html"
            );
         }

         this.color = parsed;
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new GlowAuraComponent(this.mechanic, tracker, this.audience, this.color);
      }
   }
}
