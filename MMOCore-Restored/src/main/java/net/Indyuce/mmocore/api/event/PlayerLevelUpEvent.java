package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.Profession;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

public class PlayerLevelUpEvent extends PlayerDataEvent {
   private static final HandlerList handlers = new HandlerList();
   private final Profession profession;
   private final int oldLevel;
   private final int newLevel;

   public PlayerLevelUpEvent(PlayerData player, int oldLevel, int newLevel) {
      this(player, null, oldLevel, newLevel);
   }

   public PlayerLevelUpEvent(PlayerData player, Profession profession, int oldLevel, int newLevel) {
      super(player);
      this.profession = profession;
      this.oldLevel = oldLevel;
      this.newLevel = newLevel;
   }

   public int getNewLevel() {
      return this.newLevel;
   }

   public int getOldLevel() {
      return this.oldLevel;
   }

   public boolean hasProfession() {
      return this.profession != null;
   }

   @Nullable
   public Profession getProfession() {
      return this.profession;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
