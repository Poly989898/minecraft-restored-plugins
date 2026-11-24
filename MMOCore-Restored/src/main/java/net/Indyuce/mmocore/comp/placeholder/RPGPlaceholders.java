package net.Indyuce.mmocore.comp.placeholder;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.version.Attributes;
import java.util.Objects;
import java.util.UUID;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import net.Indyuce.mmocore.api.quest.PlayerQuests;
import net.Indyuce.mmocore.experience.PlayerProfessions;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.skill.CastableSkill;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RPGPlaceholders extends PlaceholderExpansion {
   private static final String ERROR_PLACEHOLDER = " ";

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   public String getAuthor() {
      return "Indyuce";
   }

   public String getIdentifier() {
      return "mmocore";
   }

   public String getVersion() {
      return MMOCore.plugin.getDescription().getVersion();
   }

   public String onRequest(OfflinePlayer player, String identifier) {
      if (!PlayerData.has(player.getUniqueId())) {
         return null;
      }

      PlayerData playerData = PlayerData.get(player);
      if (identifier.equals("mana_icon")) {
         return playerData.getProfess().getManaDisplay().getIcon();
      }

      if (identifier.equals("mana_name")) {
         return playerData.getProfess().getManaDisplay().getName();
      }

      if (identifier.equals("level")) {
         return String.valueOf(playerData.getLevel());
      }

      if (identifier.startsWith("skill_level_")) {
         String id = identifier.substring(12);
         RegisteredSkill skill = MMOCore.plugin.skillManager.getSkillOrThrow(id);
         return String.valueOf(playerData.getSkillLevel(skill));
      }

      if (identifier.startsWith("skill_tree_points_")) {
         int length = "skill_tree_points_".length();
         String id = identifier.substring(length);
         return String.valueOf(PlayerData.get(player).getSkillTreePoints(id));
      }

      if (identifier.startsWith("bound_skill_parameter_")) {
         String[] ids = identifier.substring(22).split(":");
         String parameterId = ids[0];
         int skillSlot = Integer.parseInt(ids[1]);
         ClassSkill found = playerData.getBoundSkill(skillSlot);
         if (found == null) {
            return "";
         }

         CastableSkill castable = found.toCastable(playerData);
         double value = playerData.getMMOPlayerData().getSkillModifierMap().calculateValue(castable, parameterId);
         return MythicLib.plugin.getMMOConfig().decimal.format(value);
      } else {
         if (identifier.startsWith("skill_modifier_") || identifier.startsWith("skill_parameter_")) {
            String[] ids = identifier.substring(identifier.startsWith("skill_modifier_") ? 15 : 16).split(":");
            String parameterId = ids[0];
            String skillId = ids[1];
            RegisteredSkill skill = Objects.requireNonNull(MMOCore.plugin.skillManager.getSkill(skillId), "Could not find skill with ID '" + skillId + "'");
            CastableSkill castable = playerData.getProfess().getSkill(skill).toCastable(playerData);
            double value = playerData.getMMOPlayerData().getSkillModifierMap().calculateValue(castable, parameterId);
            return MythicLib.plugin.getMMOConfig().decimal.format(value);
         }

         if (identifier.startsWith("attribute_points_spent_")) {
            String attributeId = identifier.substring(23);
            PlayerAttributes.AttributeInstance attributeInstance = Objects.requireNonNull(
               playerData.getAttributes().getInstance(attributeId), "Could not find attribute with ID '" + attributeId + "'"
            );
            return String.valueOf(attributeInstance.getBase());
         }

         if (identifier.equals("level_percent")) {
            double current = playerData.getExperience();
            double next = playerData.getLevelUpExperience();
            return MythicLib.plugin.getMMOConfig().decimal.format(current / next * 100.0);
         }

         if (identifier.equals("health")) {
            return StatManager.format("MAX_HEALTH", player.getPlayer().getHealth());
         }

         if (identifier.equals("max_health")) {
            return StatManager.format("MAX_HEALTH", player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue());
         }

         if (identifier.equals("health_bar") && player.isOnline()) {
            StringBuilder format = new StringBuilder();
            double ratio = 20.0 * player.getPlayer().getHealth() / player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue();

            for (double j = 1.0; j < 20.0; j++) {
               format.append(ratio >= j ? ChatColor.RED : (ratio >= j - 0.5 ? ChatColor.DARK_RED : ChatColor.DARK_GRAY)).append("■");
            }

            return format.toString();
         } else {
            if (identifier.equals("class_id")) {
               return playerData.getProfess().getId();
            }

            if (identifier.equals("class")) {
               return playerData.getProfess().getName();
            }

            if (identifier.startsWith("profession_percent_")) {
               PlayerProfessions professions = playerData.getCollectionSkills();
               String name = identifier.substring(19).replace(" ", "-").replace("_", "-").toLowerCase();
               Profession profession = MMOCore.plugin.professionManager.get(name);
               double current = professions.getExperience(profession);
               double next = professions.getLevelUpExperience(profession);
               return MythicLib.plugin.getMMOConfig().decimal.format(current / next * 100.0);
            }

            if (identifier.equals("is_casting")) {
               return String.valueOf(playerData.isCasting());
            }

            if (identifier.equals("in_combat")) {
               return String.valueOf(playerData.isInCombat());
            }

            if (identifier.equals("pvp_mode")) {
               return String.valueOf(playerData.getCombat().isInPvpMode());
            }

            if (identifier.startsWith("since_enter_combat")) {
               return playerData.isInCombat()
                  ? MythicLib.plugin.getMMOConfig().decimal.format((System.currentTimeMillis() - playerData.getCombat().getLastEntry()) / 1000.0)
                  : "-1";
            }

            if (identifier.startsWith("invulnerability_left")) {
               return MythicLib.plugin
                  .getMMOConfig()
                  .decimal
                  .format(Math.max(0.0, (playerData.getCombat().getInvulnerableTill() - System.currentTimeMillis()) / 1000.0));
            }

            if (identifier.startsWith("since_last_hit")) {
               return playerData.isInCombat()
                  ? MythicLib.plugin.getMMOConfig().decimal.format((System.currentTimeMillis() - playerData.getCombat().getLastHit()) / 1000.0)
                  : "-1";
            }

            if (identifier.startsWith("id_bound_")) {
               int slot = Math.max(1, Integer.parseInt(identifier.substring(9)));
               ClassSkill info = playerData.getBoundSkill(slot);
               return info == null ? "" : info.getSkill().getHandler().getId();
            }

            if (identifier.startsWith("cast_slot_offset_")) {
               Player online = player.getPlayer();
               Validate.notNull(online, "Player is offline");
               int query = Integer.parseInt(identifier.substring(17));
               BoundSkillInfo bound = playerData.getBoundSkills().get(query);
               if (bound != null && !bound.isPassive()) {
                  int slot = bound.skillBarCastSlot;
                  if (online.getInventory().getHeldItemSlot() < slot) {
                     slot++;
                  }

                  return String.valueOf(slot);
               } else {
                  return String.valueOf(0);
               }
            } else {
               if (identifier.startsWith("passive_bound_")) {
                  int slot = Integer.parseInt(identifier.substring(14));
                  ClassSkill skill = playerData.getBoundSkill(slot);
                  return String.valueOf(skill != null && skill.getSkill().getTrigger().isPassive());
               }

               if (identifier.startsWith("bound_")) {
                  int slot = Math.max(1, Integer.parseInt(identifier.substring(6)));
                  ClassSkill skill = playerData.getBoundSkill(slot);
                  return skill == null
                     ? MMOCore.plugin.configManager.noSkillBoundPlaceholder
                     : (playerData.getCooldownMap().isOnCooldown(skill) ? ChatColor.RED : ChatColor.GREEN) + skill.getSkill().getName();
               }

               if (identifier.startsWith("cooldown_bound_")) {
                  int slot = Math.max(0, Integer.parseInt(identifier.substring(15)));
                  return playerData.hasSkillBound(slot)
                     ? Double.toString(playerData.getCooldownMap().getCooldown(playerData.getBoundSkill(slot)))
                     : MMOCore.plugin.configManager.noSkillBoundPlaceholder;
               }

               if (identifier.startsWith("profession_experience_")) {
                  return MythicLib.plugin
                     .getMMOConfig()
                     .decimal
                     .format(playerData.getCollectionSkills().getExperience(identifier.substring(22).replace(" ", "-").replace("_", "-").toLowerCase()));
               }

               if (identifier.startsWith("profession_next_level_")) {
                  return String.valueOf(
                     PlayerData.get(player)
                        .getCollectionSkills()
                        .getLevelUpExperience(identifier.substring(22).replace(" ", "-").replace("_", "-").toLowerCase())
                  );
               }

               if (identifier.startsWith("party_count")) {
                  AbstractParty party = playerData.getParty();
                  return party == null ? "0" : String.valueOf(party.countMembers());
               }

               if (identifier.startsWith("party_member_")) {
                  int n = Integer.parseInt(identifier.substring(13)) - 1;
                  AbstractParty party = playerData.getParty();
                  if (party == null) {
                     return " ";
                  }

                  if (n >= party.countMembers()) {
                     return " ";
                  }

                  PlayerData member = party.getMember(n);
                  return member == null ? " " : member.getPlayer().getName();
               } else if (identifier.equals("online_friends")) {
                  int count = 0;

                  for (UUID friendId : playerData.getFriends()) {
                     if (Bukkit.getPlayer(friendId) != null) {
                        count++;
                     }
                  }

                  return String.valueOf(count);
               } else if (identifier.startsWith("online_friend_")) {
                  int n = Integer.parseInt(identifier.substring(14)) - 1;
                  if (n >= playerData.getFriends().size()) {
                     return " ";
                  }

                  Player friend = Bukkit.getPlayer(playerData.getFriends().get(n));
                  return friend == null ? " " : friend.getName();
               } else {
                  if (identifier.startsWith("profession_")) {
                     return String.valueOf(
                        playerData.getCollectionSkills().getLevel(identifier.substring(11).replace(" ", "-").replace("_", "-").toLowerCase())
                     );
                  }

                  if (identifier.equals("experience")) {
                     return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getExperience());
                  }

                  if (identifier.equals("next_level")) {
                     return String.valueOf(playerData.getLevelUpExperience());
                  }

                  if (identifier.equals("class_points")) {
                     return String.valueOf(playerData.getClassPoints());
                  }

                  if (identifier.equals("skill_points")) {
                     return String.valueOf(playerData.getSkillPoints());
                  }

                  if (identifier.equals("attribute_points")) {
                     return String.valueOf(playerData.getAttributePoints());
                  }

                  if (identifier.equals("attribute_reallocation_points")) {
                     return String.valueOf(playerData.getAttributeReallocationPoints());
                  }

                  if (identifier.startsWith("attribute_")) {
                     return String.valueOf(
                        playerData.getAttributes().getAttribute(MMOCore.plugin.attributeManager.get(identifier.substring(10).toLowerCase().replace("_", "-")))
                     );
                  }

                  if (identifier.equals("mana")) {
                     return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getMana());
                  }

                  if (identifier.equals("mana_bar")) {
                     return playerData.getProfess().getManaDisplay().generateBar(playerData.getMana(), playerData.getStats().getStat("MAX_MANA"));
                  }

                  if (identifier.startsWith("exp_multiplier_")) {
                     String format = identifier.substring(15).toLowerCase().replace("_", "-").replace(" ", "-");
                     Profession profession = format.equals("main") ? null : MMOCore.plugin.professionManager.get(format);
                     return MythicLib.plugin.getMMOConfig().decimal.format(MMOCore.plugin.boosterManager.getMultiplier(profession) * 100.0);
                  }

                  if (identifier.startsWith("exp_boost_")) {
                     String format = identifier.substring(10).toLowerCase().replace("_", "-").replace(" ", "-");
                     Profession profession = format.equals("main") ? null : MMOCore.plugin.professionManager.get(format);
                     return MythicLib.plugin.getMMOConfig().decimal.format((MMOCore.plugin.boosterManager.getMultiplier(profession) - 1.0) * 100.0);
                  }

                  if (identifier.equals("stamina")) {
                     return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getStamina());
                  }

                  if (identifier.equals("stamina_bar")) {
                     StringBuilder format = new StringBuilder();
                     double ratio = 20.0 * playerData.getStamina() / playerData.getStats().getStat("MAX_STAMINA");

                     for (double j = 1.0; j < 20.0; j++) {
                        format.append(
                              ratio >= j
                                 ? MMOCore.plugin.configManager.staminaFull
                                 : (ratio >= j - 0.5 ? MMOCore.plugin.configManager.staminaHalf : MMOCore.plugin.configManager.staminaEmpty)
                           )
                           .append("■");
                     }

                     return format.toString();
                  } else {
                     if (identifier.startsWith("stat_")) {
                        String stat = UtilityMethods.enumName(identifier.substring(5));
                        return StatManager.format(stat, playerData.getMMOPlayerData());
                     }

                     if (identifier.equals("stellium")) {
                        return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getStellium());
                     }

                     if (identifier.equals("stellium_bar")) {
                        StringBuilder format = new StringBuilder();
                        double ratio = 20.0 * playerData.getStellium() / playerData.getStats().getStat("MAX_STELLIUM");

                        for (double j = 1.0; j < 20.0; j++) {
                           format.append(ratio >= j ? ChatColor.BLUE : (ratio >= j - 0.5 ? ChatColor.AQUA : ChatColor.WHITE)).append("■");
                        }

                        return format.toString();
                     } else {
                        if (identifier.equals("quest")) {
                           PlayerQuests data = playerData.getQuestData();
                           return data.hasCurrent() ? data.getCurrent().getQuest().getName() : "None";
                        }

                        if (identifier.equals("quest_progress")) {
                           PlayerQuests data = playerData.getQuestData();
                           return data.hasCurrent()
                              ? MythicLib.plugin
                                 .getMMOConfig()
                                 .decimal
                                 .format((double)data.getCurrent().getObjectiveNumber() / data.getCurrent().getQuest().getObjectives().size() * 100.0)
                              : "0";
                        }

                        if (identifier.equals("quest_objective")) {
                           PlayerQuests data = playerData.getQuestData();
                           return data.hasCurrent() ? data.getCurrent().getFormattedLore() : "None";
                        }

                        if (identifier.startsWith("guild_")) {
                           String placeholder = identifier.substring(6);
                           if (playerData.getGuild() == null) {
                              return "";
                           }

                           if (placeholder.equalsIgnoreCase("name")) {
                              return playerData.getGuild().getName();
                           }

                           if (placeholder.equalsIgnoreCase("tag")) {
                              return playerData.getGuild().getTag();
                           }

                           if (placeholder.equalsIgnoreCase("leader")) {
                              return Bukkit.getOfflinePlayer(playerData.getGuild().getOwner()).getName();
                           }

                           if (placeholder.equalsIgnoreCase("members")) {
                              return String.valueOf(playerData.getGuild().countMembers());
                           }

                           if (placeholder.equalsIgnoreCase("online_members")) {
                              return String.valueOf(playerData.getGuild().countOnlineMembers());
                           }
                        }

                        return null;
                     }
                  }
               }
            }
         }
      }
   }
}
