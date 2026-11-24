package net.Indyuce.mmocore.skill.cast.handler;

import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.SoundObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerKeyPressEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.cast.ComboMap;
import net.Indyuce.mmocore.skill.cast.KeyCombo;
import net.Indyuce.mmocore.skill.cast.Keybind;
import net.Indyuce.mmocore.skill.cast.PlayerKey;
import net.Indyuce.mmocore.skill.cast.SkillCastingHandler;
import net.Indyuce.mmocore.skill.cast.SkillCastingInstance;
import net.Indyuce.mmocore.skill.cast.SkillCastingMode;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyCombos extends SkillCastingHandler {
   private final ComboMap comboMap;
   @Nullable
   private final Keybind initializerKey;
   @Nullable
   private final Keybind quitKey;
   @Nullable
   private final KeyCombos.ActionBarOptions actionBarOptions;
   private final boolean stayIn;
   private final boolean ignoreSneak;
   @Nullable
   private final SoundObject beginComboSound;
   @Nullable
   private final SoundObject comboClickSound;
   @Nullable
   private final SoundObject failComboSound;
   @Nullable
   private final SoundObject failSkillSound;
   private static final List<TriggerType> IGNORED_WHEN_CASTING = Arrays.asList(
      TriggerType.RIGHT_CLICK, TriggerType.LEFT_CLICK, TriggerType.SHIFT_RIGHT_CLICK, TriggerType.SHIFT_LEFT_CLICK, TriggerType.SNEAK
   );

   public KeyCombos(@NotNull ConfigurationSection config) {
      super(config);
      this.comboMap = new ComboMap(config.getConfigurationSection("combos"));
      this.actionBarOptions = config.contains("action-bar") ? new KeyCombos.ActionBarOptions(config.getConfigurationSection("action-bar")) : null;
      this.stayIn = config.getBoolean("stay-in");
      this.beginComboSound = config.contains("sound.begin-combo") ? new SoundObject(config.getConfigurationSection("sound.begin-combo")) : null;
      this.comboClickSound = config.contains("sound.combo-key") ? new SoundObject(config.getConfigurationSection("sound.combo-key")) : null;
      this.failComboSound = config.contains("sound.fail-combo") ? new SoundObject(config.getConfigurationSection("sound.fail-combo")) : null;
      this.failSkillSound = config.contains("sound.fail-skill") ? new SoundObject(config.getConfigurationSection("sound.fail-skill")) : null;
      this.initializerKey = Keybind.fromConfig(config.get("initializer-key"));
      this.quitKey = Keybind.fromConfig(config.get("quit-key"));
      this.ignoreSneak = config.getBoolean("ignore-sneak", false);
   }

   @Override
   public SkillCastingInstance newInstance(@NotNull PlayerData player) {
      return new KeyCombos.CustomSkillCastingInstance(player);
   }

   @Override
   public SkillCastingMode getCastingMode() {
      return SkillCastingMode.KEY_COMBOS;
   }

   @EventHandler
   public void whenPressingKey(PlayerKeyPressEvent event) {
      PlayerData playerData = event.getData();
      Player player = playerData.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE || MMOCore.plugin.configManager.canCreativeCast) {
         if (playerData.hasActiveSkillBound()) {
            if (!event.getData().isCasting() && this.initializerKey != null) {
               if (this.initializerKey.matches(event)) {
                  if (event.getPressed().shouldCancelEvent()) {
                     event.setCancelled(true);
                  }

                  if (playerData.setSkillCasting() && this.beginComboSound != null) {
                     this.beginComboSound.playTo(player);
                  }
               }
            } else if (this.quitKey != null && this.quitKey.matches(event) && event.getData().isCasting()) {
               if (event.getPressed().shouldCancelEvent()) {
                  event.setCancelled(true);
               }

               event.getData().leaveSkillCasting(true);
               if (this.failComboSound != null) {
                  this.failComboSound.playTo(player);
               }
            } else {
               ComboMap comboMap = Objects.requireNonNullElse(playerData.getProfess().getComboMap(), this.comboMap);
               if (comboMap.isComboKey(event.getPressed())) {
                  KeyCombos.CustomSkillCastingInstance casting;
                  if (event.getData().isCasting()) {
                     casting = (KeyCombos.CustomSkillCastingInstance)playerData.getSkillCasting();
                  } else {
                     if (!comboMap.isComboStart(event.getPressed()) || !playerData.setSkillCasting()) {
                        return;
                     }

                     casting = (KeyCombos.CustomSkillCastingInstance)playerData.getSkillCasting();
                     if (this.beginComboSound != null) {
                        this.beginComboSound.playTo(player);
                     }
                  }

                  casting.refreshTimeOut();
                  casting.current.registerKey(event.getPressed());
                  casting.onTick();
                  if (this.comboClickSound != null) {
                     this.comboClickSound.playTo(player);
                  }

                  if (event.getPressed().shouldCancelEvent()) {
                     event.setCancelled(true);
                  }

                  if (casting.combos.getCombos().containsKey(casting.current)) {
                     int spellSlot = casting.combos.getCombos().get(casting.current);
                     if (this.stayIn) {
                        casting.resetCurrentCombo();
                     } else {
                        playerData.leaveSkillCasting(true);
                     }

                     ClassSkill boundSkill;
                     if (playerData.hasSkillBound(spellSlot) && !(boundSkill = playerData.getBoundSkill(spellSlot)).getSkill().getTrigger().isPassive()) {
                        PlayerMetadata caster = playerData.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
                        SkillResult result = boundSkill.toCastable(playerData).cast(new TriggerMetadata(caster, TriggerType.CAST, null, null));
                        if (!result.isSuccessful() && this.failSkillSound != null) {
                           this.failSkillSound.playTo(player);
                        }
                     } else if (this.stayIn && this.failComboSound != null) {
                        this.failComboSound.playTo(player);
                     }
                  } else {
                     if (casting.current.countKeys() >= casting.combos.getLongest()) {
                        if (this.stayIn) {
                           casting.resetCurrentCombo();
                        } else {
                           playerData.leaveSkillCasting(true);
                        }

                        if (this.failComboSound != null) {
                           this.failComboSound.playTo(player);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private class ActionBarOptions {
      private final String separator;
      private final String noKey;
      private final String prefix;
      private final String suffix;
      private final Map<PlayerKey, String> keyNames = new HashMap<>();
      private final boolean isSubtitle;

      ActionBarOptions(ConfigurationSection config) {
         this.prefix = config.contains("prefix") ? config.getString("prefix") : "";
         this.suffix = config.contains("suffix") ? config.getString("suffix") : "";
         this.separator = Objects.requireNonNull(config.getString("separator"), "Could not find action bar option 'separator'");
         this.noKey = Objects.requireNonNull(config.getString("no-key"), "Could not find action bar option 'no-key'");
         this.isSubtitle = config.getBoolean("is-subtitle", false);

         for (PlayerKey key : PlayerKey.values()) {
            this.keyNames.put(key, Objects.requireNonNull(config.getString("key-name." + key.name()), "Could not find translation for key " + key.name()));
         }
      }

      public String format(KeyCombos.CustomSkillCastingInstance casting) {
         StringBuilder builder = new StringBuilder();
         Placeholders holders = MMOCore.plugin.actionBarManager.getActionBarPlaceholders(casting.getCaster());
         builder.append(this.prefix);
         builder.append(casting.current.countKeys() == 0 ? this.noKey : this.keyNames.get(casting.current.getAt(0)));

         int j;
         for (j = 1; j < casting.current.countKeys(); j++) {
            builder.append(this.separator + this.keyNames.get(casting.current.getAt(j)));
         }

         while (j < casting.combos.getLongest()) {
            builder.append(this.separator + this.noKey);
            j++;
         }

         builder.append(this.suffix);
         return holders.apply(casting.getCaster().getPlayer(), builder.toString());
      }
   }

   public class CustomSkillCastingInstance extends SkillCastingInstance {
      private KeyCombo current;
      private final ComboMap combos;

      CustomSkillCastingInstance(PlayerData caster) {
         super(KeyCombos.this, caster);
         this.resetCurrentCombo();
         this.combos = Objects.requireNonNullElse(caster.getProfess().getComboMap(), KeyCombos.this.comboMap);
      }

      public void resetCurrentCombo() {
         this.current = new KeyCombo();
      }

      @Override
      public void onTick() {
         if (KeyCombos.this.actionBarOptions != null) {
            if (KeyCombos.this.actionBarOptions.isSubtitle) {
               this.getCaster().getPlayer().sendTitle(" ", KeyCombos.this.actionBarOptions.format(this), 0, 20, 0);
            } else {
               this.getCaster().displayActionBar(KeyCombos.this.actionBarOptions.format(this));
            }
         }
      }

      @EventHandler
      public void ignoreOtherSkills(PlayerCastSkillEvent event) {
         if (event.getPlayer().equals(this.getCaster().getPlayer()) && KeyCombos.IGNORED_WHEN_CASTING.contains(event.getMetadata())) {
            event.setCancelled(true);
         }
      }
   }
}
