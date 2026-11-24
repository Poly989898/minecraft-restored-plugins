package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerExperienceGainEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Profession profession;
   private final EXPSource source;
   private double experience;
   private boolean cancelled;

   public PlayerExperienceGainEvent(PlayerData player, double experience, EXPSource source) {
      this(player, null, experience, source);
   }

   public PlayerExperienceGainEvent(PlayerData player, @Nullable Profession profession, double experience, EXPSource source) {
      super(player);
      this.profession = profession;
      this.experience = experience;
      this.source = source;
   }

   public double getExperience() {
      return this.experience;
   }

   @Deprecated
   public void setExperience(int experience) {
      this.setExperience((double)experience);
   }

   public void setExperience(double experience) {
      this.experience = experience;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean hasProfession() {
      return this.profession != null;
   }

   public Profession getProfession() {
      return this.profession;
   }

   public EXPSource getSource() {
      return this.source;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
