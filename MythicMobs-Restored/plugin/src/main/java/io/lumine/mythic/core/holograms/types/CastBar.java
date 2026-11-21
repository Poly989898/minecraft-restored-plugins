package io.lumine.mythic.core.holograms.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.holograms.HologramExecutor;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.mechanics.CastMechanic;
import org.bukkit.ChatColor;

public class CastBar implements Terminable {
   private final CastMechanic.CastTracker castTracker;
   private final SkillCaster skillCaster;
   private final HologramManager manager;
   private Task timer;
   private IHologram nameHologram;
   private IHologram castHologram;
   private float yOffset;
   private double castPercent = 0.0;
   private TerminableRegistry components = TerminableRegistry.create();

   public CastBar(HologramExecutor manager, CastMechanic.CastTracker castTracker, String castText) {
      this.manager = manager;
      this.castTracker = castTracker;
      this.skillCaster = castTracker.getSkillMetadata().getCaster();
      if (this.skillCaster instanceof ActiveMob am) {
         this.yOffset = am.getType().getConfig().getFloat("CastBar.Offset", manager.getPlugin().getConfiguration().getHologramCastingBarOffset());
         this.yOffset = this.yOffset + manager.getPlugin().getConfiguration().getGlobalMountedHologramOffset();
      } else {
         this.yOffset = 1.3F;
      }

      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         PacketTextDisplay.PacketTextDisplayBuilder optionsName = PacketTextDisplay.create()
            .lineWidth(200)
            .backgroundColor(manager.getPlugin().getConfiguration().getHologramCastingInfoBackgroundColor())
            .billboard(manager.getPlugin().getConfiguration().getHologramCastingInfoBillboarding())
            .scale(new AbstractVector(1, 1, 1))
            .brightnessBitshifted(-1)
            .yOffset(this.yOffset);
         PacketTextDisplay.PacketTextDisplayBuilder optionsCast = PacketTextDisplay.create()
            .lineWidth(200)
            .backgroundColor(manager.getPlugin().getConfiguration().getHologramCastingBarBackgroundColor())
            .billboard(manager.getPlugin().getConfiguration().getHologramCastingBarBillboarding())
            .scale(new AbstractVector(1, 1, 1))
            .brightnessBitshifted(-1)
            .yOffset(this.yOffset);
         this.nameHologram = manager.createHologram(this.skillCaster.getEntity(), castText, optionsName);
         this.castHologram = manager.createHologram(this.skillCaster.getEntity(), this.getCastBar(), optionsCast);
      } else {
         this.yOffset = (float)(this.yOffset + this.skillCaster.getEntity().getNameplateMountOffset());
         this.nameHologram = manager.createHologram(
            "#TempCastBarName" + this.skillCaster.getEntity().getUniqueId().toString(), this.skillCaster.getEntity().getLocation(), castText
         );
         this.castHologram = manager.createHologram(
            "#TempCastBarCast" + this.skillCaster.getEntity().getUniqueId().toString(), this.skillCaster.getEntity().getLocation(), this.getCastBar()
         );
      }

      this.components.accept(this.nameHologram);
      this.components.accept(this.castHologram);
      this.timer = Schedulers.async().runRepeating(() -> this.teleport(), 1L, 1L);
      this.components.accept(this.timer);
   }

   public void teleport() {
      AbstractLocation location = this.getLocation();
      if (location != null && !this.castTracker.isHasEnded()) {
         this.castHologram.teleport(this.getLocation());
         this.nameHologram.teleport(this.getLocation().add(0.0, 0.25, 0.0));
      } else {
         this.terminate();
      }
   }

   public void setInterrupted() {
      this.timer.terminate();
      this.castHologram.setText(ColorString.get("&c[Interrupted]"));
   }

   public void setFinished() {
      this.timer.terminate();
      this.castHologram.setText(ColorString.get("&a[Casted!]"));
   }

   @Override
   public void close() {
      Schedulers.sync().runLater(() -> this.components.terminate(), 10L);
   }

   private AbstractLocation getLocation() {
      return this.skillCaster.getEntity().getEyeLocation().add(0.0, this.yOffset, 0.0);
   }

   public void setCastPercent(double percent) {
      this.castPercent = percent;
      this.castHologram.setText(this.getCastBar());
   }

   private String getCastBar() {
      int length = 30;
      int gray = (int)Math.floor(this.castPercent * 30.0);
      StringBuilder line = new StringBuilder();
      boolean passed = false;

      for (int i = 30; i > 0; i--) {
         if (!passed && i < gray) {
            passed = true;
         }

         line.append(passed ? ChatColor.DARK_GRAY : ChatColor.AQUA);
         line.append("|");
      }

      return line.toString();
   }
}
