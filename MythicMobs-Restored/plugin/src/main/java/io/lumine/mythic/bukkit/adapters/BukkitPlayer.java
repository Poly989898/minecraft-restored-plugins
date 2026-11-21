package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractGameMode;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class BukkitPlayer extends BukkitEntity implements AbstractPlayer {
   public BukkitPlayer(Player p) {
      super(p);
   }

   private boolean offOwningRegion() {
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(this.getBukkitEntity());
   }

   @Override
   public boolean isInCreativeMode() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::isInCreativeMode).join();
            } catch (Exception e) {
               return false;
            }
         }
      } else {
         return this.getEntityAsPlayer().getGameMode().equals(GameMode.CREATIVE);
      }
   }

   @Override
   public boolean isInSpectatorMode() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::isInSpectatorMode).join();
            } catch (Exception e) {
               return false;
            }
         }
      } else {
         return this.getEntityAsPlayer().getGameMode().equals(GameMode.SPECTATOR);
      }
   }

   @Override
   public void sendMessage(String message) {
      Text.sendMessage(this.getBukkitEntity(), message);
   }

   @Override
   public void sendActionBarMessage(String message) {
      Text.sendActionBar(this.getBukkitEntity(), message);
   }

   @Override
   public void sendTitle(String title, String subTitle, int fadeIn, int stayTime, int fadeOut) {
      Audience.audience(new Audience[]{this.getEntityAsPlayer()})
         .showTitle(
            Title.title(Text.parse(title), Text.parse(subTitle), Times.times(Ticks.duration(fadeIn), Ticks.duration(stayTime), Ticks.duration(fadeOut)))
         );
   }

   @Override
   public boolean hasPermission(String perm) {
      return this.getEntityAsPlayer().hasPermission(perm);
   }

   @Override
   public float getExperience() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0.0F;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getExperience).join();
            } catch (Exception e) {
               return 0.0F;
            }
         }
      } else {
         return this.getEntityAsPlayer().getExp();
      }
   }

   @Override
   public void setExperience(float exp) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setExp(exp));
      } else {
         this.getEntityAsPlayer().setExp(exp);
      }
   }

   @Override
   public void hidePlayer(AbstractPlayer target) {
      this.getEntityAsPlayer().hidePlayer((Player)target.getBukkitEntity());
   }

   @Override
   public void hideEntity(AbstractEntity target) {
      this.getEntityAsPlayer().hideEntity(MythicBukkit.inst(), target.getBukkitEntity());
   }

   @Override
   public void showPlayer(AbstractPlayer target) {
      this.getEntityAsPlayer().showPlayer((Player)target.getBukkitEntity());
   }

   @Override
   public void showEntity(AbstractEntity target) {
      this.getEntityAsPlayer().showEntity(MythicBukkit.inst(), target.getBukkitEntity());
   }

   @Override
   public boolean canSee(AbstractPlayer target) {
      return this.getEntityAsPlayer().canSee((Player)target.getBukkitEntity());
   }

   @Override
   public boolean canSee(AbstractEntity target) {
      return this.getEntityAsPlayer().canSee(target.getBukkitEntity());
   }

   @Override
   public boolean isOnline() {
      return this.getEntityAsPlayer().isOnline();
   }

   @Override
   public int getLevel() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getLevel).join();
            } catch (Exception e) {
               return 0;
            }
         }
      } else {
         return this.getEntityAsPlayer().getLevel();
      }
   }

   @Override
   public void setLevel(int level) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setLevel(level));
      } else {
         this.getEntityAsPlayer().setLevel(level);
      }
   }

   @Override
   public void setHealthScale(double scale) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setHealthScale(scale));
      } else {
         this.getEntityAsPlayer().setHealthScale(scale);
      }
   }

   @Override
   public void setHealthScaled(boolean scale) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setHealthScaled(scale));
      } else {
         this.getEntityAsPlayer().setHealthScaled(scale);
      }
   }

   @Override
   public void setPersonalTime(long time, boolean relative) {
      this.getEntityAsPlayer().setPlayerTime(time, relative);
   }

   @Override
   public void resetPersonalTime() {
      this.getEntityAsPlayer().resetPlayerTime();
   }

   @Override
   public void setPersonalWeather(String type) {
      this.getEntityAsPlayer().setPlayerWeather(WeatherType.valueOf(type.toUpperCase()));
   }

   @Override
   public void resetPersonalWeather() {
      this.getEntityAsPlayer().resetPlayerWeather();
   }

   @Override
   public void setAllowFlight(boolean b) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setAllowFlight(b));
      } else {
         this.getEntityAsPlayer().setAllowFlight(b);
      }
   }

   @Override
   public boolean getAllowFlight() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return false;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getAllowFlight).join();
            } catch (Exception e) {
               return false;
            }
         }
      } else {
         return this.getEntityAsPlayer().getAllowFlight();
      }
   }

   @Override
   public void setFlying(boolean b) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setFlying(b));
      } else {
         this.getEntityAsPlayer().setFlying(b);
      }
   }

   @Override
   public void setFlyingSpeed(float f) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setFlySpeed(f));
      } else {
         this.getEntityAsPlayer().setFlySpeed(f);
      }
   }

   @Override
   public void setWalkSpeed(float f) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setWalkSpeed(f));
      } else {
         this.getEntityAsPlayer().setWalkSpeed(f);
      }
   }

   @Override
   public int getFoodLevel() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getFoodLevel).join();
            } catch (Exception e) {
               return 0;
            }
         }
      } else {
         return this.getEntityAsPlayer().getFoodLevel();
      }
   }

   @Override
   public void setFoodLevel(int amount) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setFoodLevel(amount));
      } else {
         this.getEntityAsPlayer().setFoodLevel(amount);
      }
   }

   @Override
   public float getFoodSaturation() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0.0F;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getFoodSaturation).join();
            } catch (Exception e) {
               return 0.0F;
            }
         }
      } else {
         return this.getEntityAsPlayer().getSaturation();
      }
   }

   @Override
   public void setFoodSaturation(float amount) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setSaturation(amount));
      } else {
         this.getEntityAsPlayer().setSaturation(amount);
      }
   }

   @Override
   public int getRemainingAir() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getRemainingAir).join();
            } catch (Exception e) {
               return 0;
            }
         }
      } else {
         return this.getEntityAsPlayer().getRemainingAir();
      }
   }

   @Override
   public void setRemainingAir(int ticks) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setRemainingAir(ticks));
      } else {
         this.getEntityAsPlayer().setRemainingAir(ticks);
      }
   }

   @Override
   public int getMaximumAir() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getMaximumAir).join();
            } catch (Exception e) {
               return 0;
            }
         }
      } else {
         return this.getEntityAsPlayer().getMaximumAir();
      }
   }

   @Override
   public void setMaximumAir(int ticks) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().setMaximumAir(ticks));
      } else {
         this.getEntityAsPlayer().setMaximumAir(ticks);
      }
   }

   @Override
   public void setGameMode(AbstractGameMode mode) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.setGameMode(mode));
      } else {
         switch (mode) {
            case ADVENTURE:
               this.getEntityAsPlayer().setGameMode(GameMode.ADVENTURE);
               break;
            case CREATIVE:
               this.getEntityAsPlayer().setGameMode(GameMode.CREATIVE);
               break;
            case SPECTATOR:
               this.getEntityAsPlayer().setGameMode(GameMode.SPECTATOR);
               break;
            case SURVIVAL:
               this.getEntityAsPlayer().setGameMode(GameMode.SURVIVAL);
         }
      }
   }

   @Override
   public AbstractGameMode getGameMode() {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return AbstractGameMode.SURVIVAL;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(this::getGameMode).join();
            } catch (Exception e) {
               return AbstractGameMode.SURVIVAL;
            }
         }
      } else {
         GameMode bukkitMode = this.getEntityAsPlayer().getGameMode();
         switch (bukkitMode) {
            case ADVENTURE:
               return AbstractGameMode.ADVENTURE;
            case CREATIVE:
               return AbstractGameMode.CREATIVE;
            case SPECTATOR:
               return AbstractGameMode.SPECTATOR;
            default:
               return AbstractGameMode.SURVIVAL;
         }
      }
   }

   @Override
   public void updateInventory() {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.getEntityAsPlayer().updateInventory());
      } else {
         this.getEntityAsPlayer().updateInventory();
      }
   }

   @Override
   public int getFortuneLevel() {
      return this.getEnchantmentLevelHeld(Enchantment.FORTUNE);
   }

   @Override
   public float getItemGroupCooldownPercent(String namespacedKey) {
      if (this.offOwningRegion()) {
         if (Bukkit.isPrimaryThread()) {
            return 0.0F;
         } else {
            try {
               return Schedulers.of(this.getBukkitEntity()).supply(() -> this.getItemGroupCooldownPercent(namespacedKey)).join();
            } catch (Exception e) {
               return 0.0F;
            }
         }
      } else {
         return MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getItemGroupCooldownPercent(this, namespacedKey);
      }
   }

   @Override
   public boolean isItemGroupOnCooldown(String namespacedKey) {
      return this.getItemGroupCooldownPercent(namespacedKey) > 0.0F;
   }

   @Override
   public void setItemGroupCooldown(String namespacedKey, int ticks) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.setItemGroupCooldown(namespacedKey, ticks));
      } else {
         if (ticks <= 0) {
            MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().removeItemGroupCooldown(this, namespacedKey);
         } else {
            MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().setItemGroupCooldown(this, namespacedKey, ticks);
         }
      }
   }

   @Override
   public void removeItemGroupCooldown(String namespacedKey) {
      if (this.offOwningRegion()) {
         Schedulers.of(this.getBukkitEntity()).run(() -> this.removeItemGroupCooldown(namespacedKey));
      } else {
         MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().removeItemGroupCooldown(this, namespacedKey);
      }
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof BukkitPlayer) ? false : ((BukkitPlayer)o).getUniqueId().equals(this.getUniqueId());
   }

   @Override
   public int hashCode() {
      return this.getUniqueId().hashCode();
   }
}
