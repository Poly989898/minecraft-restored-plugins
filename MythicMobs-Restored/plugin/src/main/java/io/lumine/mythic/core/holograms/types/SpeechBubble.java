package io.lumine.mythic.core.holograms.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.holograms.HologramExecutor;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;

public class SpeechBubble implements Terminable {
   private final HologramManager manager;
   private final SkillCaster activemob;
   private IHologram hologram;
   private String text = "";
   @Setter
   private String linePrefix = "";
   @Getter @Setter
   private int lineLength = 24;
   @Getter @Setter
   private float yOffset = 0.4F;
   private float currentOffset = this.yOffset;
   private TerminableRegistry components = TerminableRegistry.create();

   public SpeechBubble(
      HologramExecutor manager, SkillCaster caster, Supplier<Collection<AbstractPlayer>> audience, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      this.manager = manager;
      this.activemob = caster;
      if (caster instanceof ActiveMob activeMob) {
         this.yOffset = activeMob.getType().getConfig().getFloat("Speech.Offset", manager.getPlugin().getConfiguration().getHologramSpeechOffset());
         this.linePrefix = activeMob.getType().getConfig().getString("Speech.LinePrefix", manager.getPlugin().getConfiguration().getHologramSpeechLinePrefix());
      }

      this.yOffset = this.yOffset + manager.getPlugin().getConfiguration().getGlobalMountedHologramOffset();
      this.yOffset = this.yOffset + options.yOffset();
      options.yOffset(this.yOffset);
      this.hologram = manager.createHologram(audience, this.activemob.getEntity(), " ", options);
      this.components.accept(this.hologram);
      this.components.accept(Events.subscribe(MythicMobDeathEvent.class).handler(event -> {
         UUID uuid = event.getEntity().getUniqueId();
         if (this.activemob.getEntity().getUniqueId().equals(uuid)) {
            this.terminate();
         }
      }));
      this.components.accept(Events.subscribe(MythicMobDespawnEvent.class).handler(event -> {
         UUID uuid = event.getEntity().getUniqueId();
         if (this.activemob.getEntity().getUniqueId().equals(uuid)) {
            this.terminate();
         }
      }));
      this.components.accept(Schedulers.async().runRepeating(() -> this.check(), 1L, 10L));
      this.components.accept(Schedulers.async().runRepeating(() -> this.teleport(), 1L, 1L));
   }

   public boolean check() {
      if (!this.activemob.getEntity().isDead() && this.activemob.getEntity().isValid()) {
         return true;
      } else {
         this.terminate();
         return false;
      }
   }

   public void teleport() {
      AbstractLocation location = this.getLocation();
      if (location == null) {
         this.terminate();
      } else {
         this.hologram.teleport(this.getLocation());
      }
   }

   @Override
   public void close() {
      this.components.terminate();
   }

   private AbstractLocation getLocation() {
      return this.activemob.getEntity().getEyeLocation().add(0.0, this.currentOffset, 0.0);
   }

   public void setText(String text) {
      if (text.length() > this.lineLength) {
         String[] lines = MythicUtil.wrapString(text, this.lineLength);
         this.currentOffset = this.yOffset + lines.length * 0.25F;
         this.hologram.setText(lines, this.linePrefix);
      } else {
         this.currentOffset = this.yOffset;
         this.hologram.setText(this.linePrefix + text);
      }
   }
}
