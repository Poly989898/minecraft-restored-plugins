package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

public class groundclean implements Cmd {
  public static final boolean isUsefull(ItemStack itemStack, boolean flag) {
    if (itemStack == null) {
      return false;
    } else {
      CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
      if (cmiMaterial.isTool()) {
        return true;
      } else if (cmiMaterial.isWeapon()) {
        return true;
      } else if (cmiMaterial.isArmor()) {
        return true;
      } else {
        return cmiMaterial.isShulkerBox() && flag
            ? true
            : CMI.getInstance()
                .getConfigManager()
                .getGroundCleanWhiteList()
                .contains(itemStack.getType());
      }
    }
  }

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("removed", "{gcp}Removed -[list]");
    configReader.get("items", " {gcp}Items: {gcs}[items]");
    configReader.get("arrows", " {gcp}Arrows: {gcs}[arrows]");
    configReader.get("exporbs", " {gcp}ExpOrbs: {gcs}[exporbs]");
    configReader.get("blocks", " {gcp}FallingBlocks: {gcs}[blocks]");
    configReader.get("projectiles", " {gcp}Projectiles: {gcs}[projectiles]");
    configReader.get("tnt", " {gcp}PrimedTnt: {gcs}[tnt]");
    configReader.get("boat", " {gcp}Boats: {gcs}[boat]");
    configReader.get("minecart", " {gcp}Minecarts: {gcs}[minecart]");
    configReader.get("nothing", "{gcp}Nothing to remove.");
  }

  @CAnnotation(
      info = "Clears server from unnecessary items",
      args = "(+cb) (+cm) (+ci) (+b) (+sh) (+tnt) (+all) (+fl) (+named) (-w:[worldName]) (-s)",
      multiTab = "+cb +cm +ci +b +sh +tnt +fl +named -w:",
      explanation = {
        "+cm will include minecarts into cleaning",
        "+cb will include boats into cleaning",
        "+sh will include shulker box into cleaning",
        "+ci defines if you want to include weapons and armors",
        "+tnt defines if you want to include primed tnt",
        "+named removes items with custom name or lore",
        "+b broadcasts clear message to everyone"
      },
      regVar = -66,
      consoleVar = -66,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    HashMap<removedTypes, Integer> hashMap = new HashMap<>();
    HashSet<com.Zrips.CMI.commands.list.groundclean.inclusionType> hashSet = new HashSet();
    boolean flag = false;
    boolean clearAll = false;
    World world = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (string.equalsIgnoreCase("-s")) {
        clearAll = true;
      } else if (string.equalsIgnoreCase("+all")) {
        hashSet.add(groundclean.inclusionType.All);
      } else if (string.equalsIgnoreCase("+cb")) {
        hashSet.add(groundclean.inclusionType.Boat);
      } else if (string.equalsIgnoreCase("+cm")) {
        hashSet.add(groundclean.inclusionType.Minecart);
      } else if (string.equalsIgnoreCase("+ci")) {
        hashSet.add(groundclean.inclusionType.Armor);
      } else if (string.equalsIgnoreCase("+tnt")) {
        hashSet.add(groundclean.inclusionType.Tnt);
      } else if (string.equalsIgnoreCase("+sh")) {
        hashSet.add(groundclean.inclusionType.Shulker);
      } else if (string.equalsIgnoreCase("+b")) {
        flag = true;
      } else if (string.equalsIgnoreCase("+fl")) {
        hashSet.add(groundclean.inclusionType.Falling);
      } else if (string.equalsIgnoreCase("+named") || string.equalsIgnoreCase("+nm")) {
        hashSet.add(groundclean.inclusionType.Named);
      } else if (string.startsWith("-w:")) {
        world = cmi.getUtilManager().getWorld(string.substring("-w:".length(), string.length()));
      }
    }

    boolean flagCopy = flag;
    boolean alsoClear = clearAll;
    if (hashSet.contains(groundclean.inclusionType.All)) {
      hashSet.addAll(Arrays.asList(groundclean.inclusionType.values()));
    }

    ArrayList<World> arrayList = new ArrayList();
    if (world == null) {
      arrayList.addAll(Bukkit.getWorlds());
    } else {
      arrayList.add(world);
    }

    HashSet<CompletableFuture<Void>> futures = new HashSet();

    for (World targetWorld : arrayList) {
      futures.add(
          CMIScheduler.runAtLocation(
              cmi,
              new Location(targetWorld, 0.0, 0.0, 0.0),
              () -> {
                for (Entity entity : targetWorld.getEntities()) {
                  if (!cmi.getConfigManager()
                      .getGroundCleanEntityWhiteList()
                      .contains(entity.getType())) {
                    if (entity instanceof Item) {
                      if (entity.getCustomName() != null
                          && entity.getCustomName().equalsIgnoreCase("ShopItem")) {
                        continue;
                      }

                      ItemStack itemStack = ((Item) entity).getItemStack();
                      if (!hashSet.contains(groundclean.inclusionType.Named)
                              && itemStack.getItemMeta() != null
                              && (itemStack.getItemMeta().getDisplayName() != null
                                      && !itemStack.getItemMeta().getDisplayName().isEmpty()
                                  || itemStack.getItemMeta().getLore() != null
                                      && !itemStack.getItemMeta().getLore().isEmpty())
                          || !hashSet.contains(groundclean.inclusionType.Armor)
                              && isUsefull(
                                  ((Item) entity).getItemStack(),
                                  !hashSet.contains(groundclean.inclusionType.Shulker))) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Items,
                          (object, count) -> count == null ? 1 : count + 1);
                    } else if (entity instanceof Arrow) {
                      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
                          && !hashSet.contains(groundclean.inclusionType.Armor)
                          && entity instanceof Trident) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Arrows,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else if (entity instanceof ExperienceOrb) {
                      hashMap.compute(
                          groundclean.removedTypes.Orbs,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else if (entity instanceof FallingBlock) {
                      if (!hashSet.contains(groundclean.inclusionType.Falling)) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Falling,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else if (entity instanceof Projectile) {
                      if (entity instanceof FishHook
                          || Version.isCurrentEqualOrHigher(Version.v1_13_R1)
                              && !hashSet.contains(groundclean.inclusionType.Armor)
                              && entity instanceof Trident) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Projectile,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else if (entity instanceof TNTPrimed) {
                      if (!hashSet.contains(groundclean.inclusionType.Tnt)) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Tnt,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else if (hashSet.contains(groundclean.inclusionType.Boat)
                        && entity instanceof Boat) {
                      hashMap.compute(
                          groundclean.removedTypes.Boat,
                          (key, count) -> count == null ? 1 : count + 1);
                    } else {
                      if (!hashSet.contains(groundclean.inclusionType.Minecart)
                          || !(entity instanceof Minecart)) {
                        continue;
                      }

                      hashMap.compute(
                          groundclean.removedTypes.Minecart,
                          (key, count) -> count == null ? 1 : count + 1);
                    }

                    entity.remove();
                  }
                }
              }));
    }

    CompletableFuture completableFuture =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    completableFuture.thenApply(
        result -> {
          String message = "";
          if (hashMap.get(groundclean.removedTypes.Items) != null) {
            message =
                message
                    + CMILC.getIM(
                        this, "items", "[items]", hashMap.get(groundclean.removedTypes.Items));
          }

          if (hashMap.get(groundclean.removedTypes.Arrows) != null) {
            message =
                message
                    + CMILC.getIM(
                        this, "arrows", "[arrows]", hashMap.get(groundclean.removedTypes.Arrows));
          }

          if (hashMap.get(groundclean.removedTypes.Orbs) != null) {
            message =
                message
                    + CMILC.getIM(
                        this, "exporbs", "[exporbs]", hashMap.get(groundclean.removedTypes.Orbs));
          }

          if (hashMap.get(groundclean.removedTypes.Falling) != null) {
            message =
                message
                    + CMILC.getIM(
                        this, "blocks", "[blocks]", hashMap.get(groundclean.removedTypes.Falling));
          }

          if (hashMap.get(groundclean.removedTypes.Projectile) != null) {
            message =
                message
                    + CMILC.getIM(
                        this,
                        "projectiles",
                        "[projectiles]",
                        hashMap.get(groundclean.removedTypes.Projectile));
          }

          if (hashMap.get(groundclean.removedTypes.Tnt) != null) {
            message =
                message
                    + CMILC.getIM(this, "tnt", "[tnt]", hashMap.get(groundclean.removedTypes.Tnt));
          }

          if (hashMap.get(groundclean.removedTypes.Boat) != null) {
            message =
                message
                    + CMILC.getIM(
                        this, "boat", "[boat]", hashMap.get(groundclean.removedTypes.Boat));
          }

          if (hashMap.get(groundclean.removedTypes.Minecart) != null) {
            message =
                message
                    + CMILC.getIM(
                        this,
                        "minecart",
                        "[minecart]",
                        hashMap.get(groundclean.removedTypes.Minecart));
          }

          if (!alsoClear) {
            if (!message.isEmpty()) {
              CMILC.info(this, commandSender, "removed", "[list]", message);
              if (flagCopy) {
                CMILC.broadcastMessage(
                    commandSender, CMILC.getIM(this, "removed", "[list]", message), false);
              }
            } else {
              CMILC.info(this, commandSender, "nothing");
            }
          }

          return null;
        });
    return true;
  }

  private enum inclusionType {
    Boat,
    Minecart,
    Armor,
    Shulker,
    Tnt,
    Named,
    Falling,
    All;
  }

  private enum removedTypes {
    Items,
    Arrows,
    Orbs,
    Falling,
    Projectile,
    Tnt,
    Boat,
    Minecart;
  }
}
