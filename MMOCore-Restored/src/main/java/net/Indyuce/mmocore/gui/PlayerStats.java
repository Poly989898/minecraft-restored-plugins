package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.manager.StatManager;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import net.Indyuce.mmocore.experience.Booster;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.party.AbstractParty;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class PlayerStats extends EditableInventory {
   public PlayerStats() {
      super("player-stats");
   }

   public InventoryItem<?> resolveItem(String function, ConfigurationSection config) {
      if (function.equals("boost")) {
         return new PlayerStats.BoostItem(config);
      } else if (function.equals("boost-next")) {
         return new PlayerStats.BoostNextButton(config);
      } else if (function.equals("boost-previous")) {
         return new PlayerStats.BoostPreviousButton(config);
      } else if (function.equals("party")) {
         return new PlayerStats.PartyMoraleItem(config);
      } else if (function.startsWith("profession_")) {
         return new PlayerStats.ProfessionItem(function, config);
      } else if (function.equals("profile")) {
         return new PlayerStats.PlayerProfileItem(config);
      } else {
         return function.equals("stats") ? new PlayerStats.StatsItem(config) : null;
      }
   }

   public PlayerStats.PlayerStatsInventory newInventory(PlayerData invTarget, PlayerData opening) {
      return new PlayerStats.PlayerStatsInventory(invTarget, opening);
   }

   public PlayerStats.PlayerStatsInventory newInventory(PlayerData player) {
      return new PlayerStats.PlayerStatsInventory(player, player);
   }

   private ItemStack amount(ItemStack item, int amount) {
      item.setAmount(amount);
      return item;
   }

   public class BoostItem extends SimpleItem<PlayerStats.PlayerStatsInventory> {
      private final PhysicalItem<PlayerStats.PlayerStatsInventory> noBoost;
      private final PhysicalItem<PlayerStats.PlayerStatsInventory> mainLevel;
      private final PhysicalItem<PlayerStats.PlayerStatsInventory> profession;

      public BoostItem(ConfigurationSection config) {
         super(config);
         ConfigurationSection noBoost = config.getConfigurationSection("no-boost");
         Validate.notNull(noBoost, "Could not load 'no-boost' config");
         this.noBoost = new SimpleItem(noBoost);
         ConfigurationSection mainLevel = config.getConfigurationSection("main-level");
         Validate.notNull(mainLevel, "Could not load 'main-level' config");
         this.mainLevel = new PhysicalItem<PlayerStats.PlayerStatsInventory>(mainLevel) {
            public boolean hasDifferentDisplay() {
               return true;
            }

            @NotNull
            public Placeholders getPlaceholders(PlayerStats.PlayerStatsInventory inv, int n) {
               Placeholders holders = new Placeholders();
               Booster boost = MMOCore.plugin.boosterManager.get(inv.boostOffset + n);
               holders.register("author", boost.hasAuthor() ? boost.getAuthor() : "Server");
               holders.register("value", (int)(boost.getExtra() * 100.0));
               holders.register("left", boost.isTimedOut() ? ConfigMessage.fromKey("booster-expired").asLine() : new DelayFormat(2).format(boost.getLeft()));
               return holders;
            }
         };
         ConfigurationSection profession = config.getConfigurationSection("profession");
         Validate.notNull(profession, "Could not load 'profession' config");
         this.profession = new PhysicalItem<PlayerStats.PlayerStatsInventory>(profession) {
            public boolean hasDifferentDisplay() {
               return true;
            }

            @NotNull
            public Placeholders getPlaceholders(PlayerStats.PlayerStatsInventory inv, int n) {
               Placeholders holders = new Placeholders();
               Booster boost = MMOCore.plugin.boosterManager.get(inv.boostOffset + n);
               holders.register("author", boost.hasAuthor() ? boost.getAuthor() : "Server");
               holders.register("profession", boost.getProfession().getName());
               holders.register("value", (int)(boost.getExtra() * 100.0));
               holders.register("left", boost.isTimedOut() ? ConfigMessage.fromKey("booster-expired").asLine() : new DelayFormat(2).format(boost.getLeft()));
               return holders;
            }
         };
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public ItemStack getDisplayedItem(PlayerStats.PlayerStatsInventory inv, int n) {
         int offset = inv.boostOffset;
         if (n + offset >= MMOCore.plugin.boosterManager.getActive().size()) {
            return this.noBoost.getDisplayedItem(inv, n);
         }

         Booster boost = MMOCore.plugin.boosterManager.get(inv.boostOffset + n);
         return PlayerStats.this.amount(
            boost.hasProfession() ? this.profession.getDisplayedItem(inv, n) : this.mainLevel.getDisplayedItem(inv, n), n + offset + 1
         );
      }
   }

   public class BoostNextButton extends SimpleItem<PlayerStats.PlayerStatsInventory> {
      public BoostNextButton(ConfigurationSection config) {
         super(config);
      }

      public boolean isDisplayed(@NotNull PlayerStats.PlayerStatsInventory inv) {
         InventoryItem boost = inv.getByFunction("boost");
         return boost != null && inv.boostOffset + boost.getSlots().size() < MMOCore.plugin.boosterManager.getActive().size();
      }

      public void onClick(@NotNull PlayerStats.PlayerStatsInventory inv, @NotNull InventoryClickEvent event) {
         inv.boostOffset++;
         inv.open();
      }
   }

   public class BoostPreviousButton extends SimpleItem<PlayerStats.PlayerStatsInventory> {
      public BoostPreviousButton(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull PlayerStats.PlayerStatsInventory inv, @NotNull InventoryClickEvent event) {
         inv.boostOffset--;
         inv.open();
      }

      public boolean isDisplayed(@NotNull PlayerStats.PlayerStatsInventory inv) {
         return inv.boostOffset > 0;
      }
   }

   public static class PartyMoraleItem extends PhysicalItem<PlayerStats.PlayerStatsInventory> {
      public PartyMoraleItem(ConfigurationSection config) {
         super(config);
      }

      public Placeholders getPlaceholders(PlayerStats.PlayerStatsInventory inv, int n) {
         Placeholders holders = new Placeholders();
         int count = inv.target.getParty().getOnlineMembers().size();
         holders.register("count", String.valueOf(count));

         for (StatModifier buff : MMOCore.plugin.partyManager.getBonuses()) {
            holders.register("buff_" + buff.getStat().toLowerCase(), buff.multiply(count - 1).toString());
         }

         return holders;
      }

      public boolean isDisplayed(@NotNull PlayerStats.PlayerStatsInventory inv) {
         AbstractParty party = inv.target.getParty();
         return party != null && party.getOnlineMembers().size() > 1;
      }
   }

   public static class PlayerProfileItem extends PhysicalItem<PlayerStats.PlayerStatsInventory> {
      public PlayerProfileItem(ConfigurationSection config) {
         super(config);
      }

      public ItemStack getDisplayedItem(PlayerStats.PlayerStatsInventory inv, int n) {
         ItemStack disp = super.getDisplayedItem(inv, n);
         if (disp.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta)disp.getItemMeta();
            inv.asyncUpdate(this, n, disp, current -> {
               meta.setOwningPlayer(inv.target.getPlayer());
               current.setItemMeta(meta);
            });
         }

         return disp;
      }

      public Placeholders getPlaceholders(PlayerStats.PlayerStatsInventory inv, int n) {
         PlayerData data = inv.target;
         Placeholders holders = new Placeholders();
         long nextLevelExp = inv.target.getLevelUpExperience();
         double ratio = data.getExperience() / nextLevelExp;
         StringBuilder bar = new StringBuilder(ChatColor.BOLD + "");
         int chars = (int)(ratio * 20.0);

         for (int j = 0; j < 20; j++) {
            bar.append(j == chars ? "" + ChatColor.WHITE + ChatColor.BOLD : "").append("|");
         }

         holders.register("percent", this.ONE_DIGIT.format(ratio * 100.0));
         holders.register("exp", MythicLib.plugin.getMMOConfig().decimal.format(data.getExperience()));
         holders.register("level", data.getLevel());
         holders.register("class_points", data.getClassPoints());
         holders.register("skill_points", data.getSkillPoints());
         holders.register("attribute_points", data.getAttributePoints());
         holders.register("progress", bar.toString());
         holders.register("next_level", nextLevelExp);
         if (data.isOnline()) {
            holders.register("player", data.getPlayer().getName());
         }

         holders.register("class", data.getProfess().getName());
         return holders;
      }
   }

   public class PlayerStatsInventory extends GeneratedInventory {
      private final PlayerData target;
      private int boostOffset;

      public PlayerStatsInventory(PlayerData target, PlayerData opening) {
         super(new Navigator(opening.getMMOPlayerData()), PlayerStats.this);
         this.target = target;
      }
   }

   public class ProfessionItem extends PhysicalItem<PlayerStats.PlayerStatsInventory> {
      private final Profession profession;

      public ProfessionItem(String function, @NotNull ConfigurationSection config) {
         super(config);
         String id = function.substring("profession_".length()).toLowerCase();
         Validate.isTrue(MMOCore.plugin.professionManager.has(id));
         this.profession = MMOCore.plugin.professionManager.get(id);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public Placeholders getPlaceholders(PlayerStats.PlayerStatsInventory inv, int n) {
         Placeholders holders = new Placeholders();
         double ratio = inv.target.getCollectionSkills().getExperience(this.profession)
            / inv.target.getCollectionSkills().getLevelUpExperience(this.profession);
         String bar = ChatColor.BOLD + "";
         int chars = (int)(ratio * 20.0);

         for (int j = 0; j < 20; j++) {
            bar = bar + (j == chars ? "" + ChatColor.WHITE + ChatColor.BOLD : "") + "|";
         }

         holders.register("progress", bar);
         holders.register("level", String.valueOf(inv.target.getCollectionSkills().getLevel(this.profession)));
         holders.register("xp", inv.target.getCollectionSkills().getExperience(this.profession));
         holders.register("percent", this.ONE_DIGIT.format(ratio * 100.0));
         return holders;
      }
   }

   public class StatsItem extends PhysicalItem<PlayerStats.PlayerStatsInventory> {
      public StatsItem(ConfigurationSection config) {
         super(config);
      }

      @NotNull
      public Placeholders getPlaceholders(final PlayerStats.PlayerStatsInventory inv, int n) {
         return new Placeholders() {
            final net.Indyuce.mmocore.api.player.stats.PlayerStats stats = inv.target.getStats();

            @NotNull
            public String parsePlaceholder(String holder) {
               if (holder.endsWith("_base")) {
                  String stat = UtilityMethods.enumName(holder.substring(0, holder.length() - 5));
                  return StatManager.format(stat, this.stats.getBase(stat));
               } else if (holder.endsWith("_extra")) {
                  String stat = UtilityMethods.enumName(holder.substring(0, holder.length() - 6));
                  return StatManager.format(stat, this.stats.getStat(stat) - this.stats.getBase(stat));
               } else if (holder.startsWith("attribute_")) {
                  PlayerAttribute attr = MMOCore.plugin.attributeManager.get(holder.substring(10).replace("_", "-").toLowerCase());
                  return String.valueOf(inv.target.getAttributes().getAttribute(attr));
               } else {
                  String stat = UtilityMethods.enumName(holder);
                  return StatManager.format(stat, this.stats.getStat(stat));
               }
            }
         };
      }
   }
}
