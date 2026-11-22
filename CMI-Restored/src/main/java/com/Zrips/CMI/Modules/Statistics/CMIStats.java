package com.Zrips.CMI.Modules.Statistics;

import com.Zrips.CMI.Containers.CMIUser;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CMIStats {
  public static long getStat(CMIUser cmiUser, StatsManager.CMIStatistic cmiStatistic) {
    return getStat(cmiUser, cmiStatistic, null);
  }

  public static long getStat(
      CMIUser cmiUser, StatsManager.CMIStatistic cmiStatistic, Object object) {
    if (cmiStatistic == null) {
      return 0L;
    }

    try {
      Long time = null;
      switch (cmiStatistic) {
        case PLAY_ONE_TICK:
          if (cmiUser != null) {
            return cmiUser.getTotalPlayTime(false);
          }

          return 0L;
        case TIME_SINCE_DEATH:
        case SNEAK_TIME:
          return cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat()) * 50L;
        case TIME_SINCE_REST:
        default:
          if (object != null) {
            if (object instanceof CMIEntityType) {
              if (cmiStatistic.getBukkitStat() == null) {
                return 0L;
              }

              int value =
                  cmiUser
                      .getPlayer()
                      .getStatistic(
                          cmiStatistic.getBukkitStat(), ((CMIEntityType) object).getType());
              time = (long) value;
            } else if (object instanceof CMIMaterial) {
              if (cmiStatistic.getBukkitStat() == null) {
                return 0L;
              }

              int count =
                  cmiUser
                      .getPlayer()
                      .getStatistic(
                          cmiStatistic.getBukkitStat(), ((CMIMaterial) object).getMaterial());
              time = (long) count;
            } else if (object instanceof EntityType) {
              if (cmiStatistic.getBukkitStat() == null) {
                return 0L;
              }

              int countSecond =
                  cmiUser
                      .getPlayer()
                      .getStatistic(cmiStatistic.getBukkitStat(), (EntityType) object);
              time = (long) countSecond;
            } else if (object instanceof Material) {
              if (cmiStatistic.getBukkitStat() == null) {
                return 0L;
              }

              int countThird =
                  cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat(), (Material) object);
              time = (long) countThird;
            } else if (object instanceof String) {
              CMIEntityType cmiEntityType = CMIEntityType.get((String) object);
              if (cmiEntityType != null) {
                return cmiUser
                    .getPlayer()
                    .getStatistic(cmiStatistic.getBukkitStat(), cmiEntityType.getType());
              }

              CMIMaterial cmiMaterial = CMIMaterial.get((String) object);
              if (cmiMaterial != null && !cmiMaterial.isNone()) {
                return cmiUser
                    .getPlayer()
                    .getStatistic(cmiStatistic.getBukkitStat(), cmiMaterial.getMaterial());
              }

              if (((String) object).equalsIgnoreCase("none")) {
                return getStats(cmiUser, cmiStatistic);
              }
            }

            if (time == null) {
              return 0L;
            }

            return time;
          } else if (cmiStatistic.getBukkitStat() == null) {
            return 0L;
          } else {
            if (cmiStatistic.getBukkitStat().isSubstatistic()) {
              return getTotal(cmiUser, cmiStatistic);
            }

            return cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat());
          }
        case ACCOUNT_AGE:
          Player player = cmiUser.getPlayer();
          if (player != null) {
            return System.currentTimeMillis() - player.getFirstPlayed();
          }

          return 0L;
        case TRAVEL:
          return getTotalTravelDistance(cmiUser);
      }
    } catch (Error | Exception value5) {
      return 0L;
    }
  }

  private static long getStats(CMIUser cmiUser, StatsManager.CMIStatistic cmiStatistic) {
    if (cmiStatistic == StatsManager.CMIStatistic.PLAY_ONE_TICK && cmiUser != null) {
      return cmiUser.getTotalPlayTime(false);
    }

    if (cmiStatistic == StatsManager.CMIStatistic.TRAVEL) {
      return getTotalTravelDistance(cmiUser);
    }

    if (cmiStatistic == StatsManager.CMIStatistic.ACCOUNT_AGE && cmiUser != null) {
      Player player = cmiUser.getPlayer();
      if (player != null) {
        return System.currentTimeMillis() - player.getFirstPlayed();
      }
    }

    return getTotal(cmiUser, cmiStatistic);
  }

  public static long getTotalTravelDistance(CMIUser cmiUser) {
    long longValue = 0L;
    StatsManager.CMIStatistic[] cmiStatistics;
    int value = (cmiStatistics = StatsManager.CMIStatistic.values()).length;

    for (int count = 0; count < value; count++) {
      StatsManager.CMIStatistic cmiStatistic = cmiStatistics[count];
      if (cmiStatistic.getType() == StatsManager.svt.Distance
          && cmiStatistic.getBukkitStat() != null) {
        longValue += getStat(cmiUser, cmiStatistic);
      }
    }

    return longValue;
  }

  private static long getTotal(CMIUser cmiUser, StatsManager.CMIStatistic cmiStatistic) {
    long longValue = 0L;
    switch (cmiStatistic.getSubType()) {
      case None:
      default:
        longValue = cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat());
        break;
      case Material:
      case Block:
        Material[] materials;
        int value = (materials = Material.values()).length;

        for (int count = 0; count < value; count++) {
          Material material = materials[count];
          if (!material.toString().startsWith("LEGACY_")) {
            try {
              int countSecond =
                  cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat(), material);
              if (countSecond != 0) {
                try {
                  if (cmiStatistic.getSubType().equals(StatsManager.CMIType.Block)
                      && !material.isBlock()) {
                    continue;
                  }
                } catch (Throwable throwable) {
                }

                if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
                  CMIMaterial cmiMaterial = CMIMaterial.get(material);
                  if (cmiMaterial.equals(CMIMaterial.GRASS_BLOCK)
                      || cmiMaterial.equals(CMIMaterial.FARMLAND)) {
                    continue;
                  }
                }

                longValue += countSecond;
              }
            } catch (Exception exception) {
            }
          }
        }
        break;
      case Entity:
        EntityType[] entityTypes;
        int countThird = (entityTypes = EntityType.values()).length;

        for (int countFourth = 0; countFourth < countThird; countFourth++) {
          EntityType entityType = entityTypes[countFourth];

          try {
            longValue += cmiUser.getPlayer().getStatistic(cmiStatistic.getBukkitStat(), entityType);
          } catch (Exception exception2) {
          }
        }
    }

    return longValue;
  }
}
