package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class launch implements Cmd {
  public static HashMap<UUID, Long> fall = new HashMap<>();
  public static HashMap<UUID, launch.extraLaunch> map = new HashMap<>();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "!actionbar!{gcp}[{gcu}Whoosh!{gcp}]");
  }

  public static Vector calculateVelocity(Vector vector, Vector target, double number) {
    double gravity = 0.24;
    double dy = target.getY() - vector.getY();
    double distance = Math.sqrt(distanceSquared(vector, target));
    double peakHeight = number > dy + number ? number : dy + number;
    double coefficient = -distance * distance / (4.0 * peakHeight);
    double horizontal = distance;
    double negativeDy = -dy;
    double timeFactor =
        -horizontal / (2.0 * coefficient)
            - Math.sqrt(horizontal * horizontal - 4.0 * coefficient * negativeDy)
                / (2.0 * coefficient);
    if (Double.isNaN(timeFactor)) {
      timeFactor = 1.0;
    }

    timeFactor *= 0.5;
    double sqrtTerm = Math.sqrt(peakHeight * gravity);
    double time = sqrtTerm / timeFactor;
    double dx = target.getX() - vector.getX();
    double dz = target.getZ() - vector.getZ();
    double horizontalLength = Math.sqrt(dx * dx + dz * dz);
    double dirX = dx / horizontalLength;
    double dirZ = dz / horizontalLength;
    double velocityX = time * dirX;
    double velocityZ = time * dirZ;
    if (Double.isNaN(velocityX)) {
      velocityX = 0.0;
    }

    if (Double.isNaN(sqrtTerm)) {
      sqrtTerm = 0.0;
    }

    if (Double.isNaN(velocityZ)) {
      velocityZ = 0.0;
    }

    return new Vector(velocityX, sqrtTerm, velocityZ);
  }

  private static double distanceSquared(Vector vector, Vector other) {
    double number = other.getX() - vector.getX();
    double dz = other.getZ() - vector.getZ();
    return number * number + dz * dz;
  }

  @CAnnotation(
      priority = 125,
      info = "Launch at direction you are looking or at angle",
      args = "(playerName) (p:[power]) (a:[angle]) (d:[direction]) (loc:[x]:[y]:[z]) (-nodamage)",
      tab = {"playerName", "-nodamage", "p:", "a:"},
      explanation = {
        "cmi launch - will launch at direction you are looking",
        "cmi launch -nodamage - will launch and prevent damage on fall",
        "cmi launch p:3.2 - will launch with power of 3.2",
        "cmi launch p:2.5 a:25 - will launch at direction you are looking with angle of 25 dgrees"
            + " and with power of 2.5",
        "cmi launch Zrips d:east - will launch to east direction same angle you are looking",
        "cmi launch Zrips d:0 - will launch at 0 degrees direction (south)",
        "cmi launch Zrips d:~180 - will launch backwards",
        "cmi launch d:45 a:30 p:2 - will launch south-west at 30 degree angle with power of 2",
        "cmi launch loc:150:120:123 - will launch player to target location"
      },
      regVar = {0, 1, 2, 3, 4, 5},
      consoleVar = {1, 2, 3, 4, 5},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    Double power = null;
    Float pitch = null;
    Float yaw = null;
    boolean flag = false;
    boolean useLocation = false;
    Vector vector = null;
    boolean useDirection = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("p:")) {
        try {
          power = Double.parseDouble(arg.replace("p:", ""));
          continue;
        } catch (Exception exception) {
        }
      }

      if (arg.equalsIgnoreCase("-nodamage")) {
        useDirection = true;
      } else {
        if (arg.startsWith("a:")) {
          String angleText = arg.replace("a:", "");
          if (angleText.contains("~")) {
            useLocation = true;
            angleText = angleText.replace("~", "");
          }

          try {
            pitch = Float.parseFloat(angleText);
            continue;
          } catch (Exception exception2) {
          }
        }

        if (arg.startsWith("loc:")) {
          try {
            String locText = arg.replace("loc:", "");
            vector = new Vector();
            vector.setX(Double.parseDouble(locText.split(":")[0]));
            vector.setY(Double.parseDouble(locText.split(":")[1]));
            vector.setZ(Double.parseDouble(locText.split(":")[2]));
            continue;
          } catch (Exception exception3) {
            vector = null;
          }
        }

        if (arg.startsWith("d:")) {
          String dText = arg.replace("d:", "");
          if (dText.contains("~")) {
            flag = true;
            dText = dText.replace("~", "");
          }

          try {
            yaw = Float.parseFloat(dText);
            continue;
          } catch (Exception exception4) {
            String modifierText;
            switch ((modifierText = arg.replace("d:", "").toLowerCase()).hashCode()) {
              case 3105789:
                if (modifierText.equals("east")) {
                  yaw = launch.dir.east.getDirection();
                  continue;
                }
                break;
              case 3645871:
                if (modifierText.equals("west")) {
                  yaw = launch.dir.west.getDirection();
                  continue;
                }
                break;
              case 105007365:
                if (modifierText.equals("north")) {
                  yaw = launch.dir.north.getDirection();
                  continue;
                }
                break;
              case 109627853:
                if (modifierText.equals("south")) {
                  yaw = launch.dir.south.getDirection();
                  continue;
                }
            }
          }
        }

        if (string == null) {
          string = arg;
        }
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      if (map.containsKey(player.getUniqueId())) {
        map.remove(player.getUniqueId()).getTask().cancel();
      }

      if (vector != null) {
        Vector velocity = calculateVelocity(player.getLocation().toVector(), vector, 1.5);
        if (velocity != null) {
          player.setVelocity(velocity);
          if (useDirection) {
            fall.put(player.getUniqueId(), System.currentTimeMillis());
          }

          return true;
        }
      }

      if (power == null) {
        power = 2.0;
      }

      if (power < 0.0) {
        LC.info_NoLessThan.sendMessage(commandSender, new Object[] {"[amount]", 0});
        return null;
      }

      if (power > 50.0) {
        LC.info_NoMoreThan.sendMessage(commandSender, new Object[] {"[amount]", 50});
        return null;
      }

      if (flag && yaw != null) {
        yaw = player.getLocation().getYaw() + yaw;
      }

      if (useLocation && pitch != null) {
        pitch = -player.getLocation().getPitch() + pitch;
        pitch = (float) CMINumber.clamp(pitch.floatValue(), -90.0, 90.0);
      }

      this.jumpByAngle(player, power, pitch, yaw);
      if (useDirection) {
        fall.put(player.getUniqueId(), System.currentTimeMillis());
      }

      CMILC.info(this, player, "feedback");
      return true;
    } else {
      return null;
    }
  }

  private static void launchFar(launch.extraLaunch extra) {
    map.put(extra.getPlayer().getUniqueId(), extra);
    extra.setTask(
        CMIScheduler.scheduleSyncRepeatingTask(
            CMI.getInstance(),
            () -> {
              Player player = extra.getPlayer();
              extra.setVector(extra.getVector().setY(extra.getVector().getY() - 0.075));
              if (map.containsKey(player.getUniqueId())
                  && extra.getTimes() > 0
                  && player.isOnline()
                  && (extra.getPrevLoc() == null
                      || !player.isOnGround()
                          && !player.isFlying()
                          && (!Version.isCurrentEqualOrHigher(Version.v1_9_R1)
                              || !player.isGliding())
                          && (!(extra.getVector().getY() > 1.0)
                              || !(extra.getPrevLoc().distance(player.getLocation()) < 0.1)))) {
                extra.setPrevLoc(extra.getPlayer().getLocation());
                extra.getPlayer().setVelocity(extra.getVector());
                extra.setTimes(extra.getTimes() - 1);
              } else {
                extra.getTask().cancel();
                map.remove(extra.getPlayer().getUniqueId());
              }
            },
            0L,
            2L));
  }

  private void jumpByAngle(Player player, Double power, Float pitch, Float yaw) {
    Location location = player.getLocation().clone();
    if (pitch != null) {
      location.setPitch(-pitch);
    }

    if (yaw != null) {
      location.setYaw(yaw);
    }

    double number = power > 4.0 ? 4.0 : power;
    Vector vector = location.getDirection().multiply(number);
    vector.setX(fixValue(vector.getX()));
    vector.setY(fixValue(vector.getY()));
    vector.setZ(fixValue(vector.getZ()));
    if (power > 4.0) {
      launch.extraLaunch extra = new extraLaunch();
      extra.setPlayer(player);
      extra.setTimes(Math.max(1, (int) ((power - 4.0) * 5.0)));
      extra.setVector(vector);
      launchFar(extra);
    } else {
      player.setVelocity(vector);
    }
  }

  private static double fixValue(double number) {
    return number > 4.0 ? 4.0 : (number < -4.0 ? -4.0 : number);
  }

  private enum dir {
    west(90.0F),
    east(-90.0F),
    north(-180.0F),
    south(0.0F);

    private Float direction;

    dir(Float angle) {
      this.direction = angle;
    }

    public Float getDirection() {
      return this.direction;
    }
  }

  private class extraLaunch {
    private Player player;
    private int times;
    private CMITask task;
    private Vector vector;
    private Location prevLoc;

    private extraLaunch() {}

    public Player getPlayer() {
      return this.player;
    }

    public void setPlayer(Player newPlayer) {
      this.player = newPlayer;
    }

    public int getTimes() {
      return this.times;
    }

    public void setTimes(int value) {
      this.times = value;
    }

    public CMITask getTask() {
      return this.task;
    }

    public void setTask(CMITask cmiTask) {
      this.task = cmiTask;
    }

    public Vector getVector() {
      return this.vector;
    }

    public void setVector(Vector newVector) {
      this.vector = newVector;
    }

    public Location getPrevLoc() {
      return this.prevLoc;
    }

    public void setPrevLoc(Location location) {
      this.prevLoc = location;
    }
  }
}
