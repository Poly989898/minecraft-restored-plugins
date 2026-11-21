package io.lumine.mythic.bukkit.commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.skills.cinematic.CameraPath;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.skills.cinematic.Easing;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CameraCommand extends Command<MythicBukkit> {
   public CameraCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args != null && args.length >= 2) {
         if (sender instanceof Player player) {
            String pathId = args[0];
            String action = args[1].toLowerCase();
            int index = 0;
            int hold = 0;
            Easing easing = null;
            boolean relative = false;
            boolean indexParsed = false;

            for (int mgr = 2; mgr < args.length; mgr++) {
               String arg = args[mgr];
               int eq = arg.indexOf(61);
               if (eq > 0) {
                  String key = arg.substring(0, eq).toLowerCase();
                  String value = arg.substring(eq + 1);
                  switch (key) {
                     case "hold":
                     case "h":
                        try {
                           hold = Math.max(0, Integer.parseInt(value));
                           break;
                        } catch (NumberFormatException e) {
                           sender.sendMessage(ChatColor.RED + "hold must be a number.");
                           return true;
                        }
                     case "easing":
                     case "e":
                        easing = Easing.fromString(value, null);
                        if (easing == null) {
                           sender.sendMessage(ChatColor.RED + "Unknown easing: " + value);
                           return true;
                        }
                        break;
                     case "relative":
                     case "r":
                     case "rel":
                        relative = Boolean.parseBoolean(value);
                        break;
                     case "index":
                     case "i":
                        try {
                           index = Integer.parseInt(value);
                           indexParsed = true;
                           break;
                        } catch (NumberFormatException ex) {
                           sender.sendMessage(ChatColor.RED + "index must be a number.");
                           return true;
                        }
                     default:
                        sender.sendMessage(ChatColor.YELLOW + "Unknown option: " + key);
                  }
               } else if (!indexParsed) {
                  try {
                     index = Integer.parseInt(arg);
                     indexParsed = true;
                  } catch (NumberFormatException ex2) {
                     sender.sendMessage(ChatColor.RED + "index must be a number, or use key=value (hold=, easing=, relative=).");
                     return true;
                  }
               }
            }

            CinematicManager mgrx = MythicBukkit.inst().getCinematicManager();
            if (mgrx == null) {
               sender.sendMessage(ChatColor.RED + "Cinematic system is not available.");
               return true;
            } else if (mgrx.getRegistry().get(pathId).isEmpty()) {
               sender.sendMessage(ChatColor.RED + "No camera path named '" + pathId + "'.");
               return true;
            } else {
               switch (action) {
                  case "add":
                     boolean okx = mgrx.addPoint(pathId, player.getLocation(), index, hold, easing, relative);
                     if (okx) {
                        int newSize = mgrx.getRegistry().get(pathId).map(CameraPath::getPoints).map(List::size).orElse(0);
                        StringBuilder msg = new StringBuilder()
                           .append(ChatColor.GREEN)
                           .append("Added keyframe to '")
                           .append(pathId)
                           .append("' ")
                           .append(index <= 0 ? "(appended)" : "at index " + index)
                           .append(". Now ")
                           .append(newSize)
                           .append(" points");
                        if (hold > 0) {
                           msg.append(", hold=").append(hold);
                        }

                        if (easing != null) {
                           msg.append(", easing=").append(easing.name());
                        }

                        if (relative) {
                           msg.append(", relative");
                        }

                        msg.append('.');
                        sender.sendMessage(msg.toString());
                     } else {
                        sender.sendMessage(ChatColor.RED + "Failed to add keyframe (no source file or write error).");
                     }
                     break;
                  case "remove":
                     boolean ok = mgrx.removePoint(pathId, index);
                     if (ok) {
                        int newSize = mgrx.getRegistry().get(pathId).map(CameraPath::getPoints).map(List::size).orElse(0);
                        sender.sendMessage(
                           ChatColor.GREEN
                              + "Removed keyframe from '"
                              + pathId
                              + "' "
                              + (index <= 0 ? "(last point)" : "at index " + index)
                              + ". Now "
                              + newSize
                              + " points."
                        );
                     } else {
                        sender.sendMessage(ChatColor.RED + "Failed to remove keyframe (no source file, write error, or path empty).");
                     }
                     break;
                  default:
                     this.usage(sender);
               }

               return true;
            }
         } else {
            sender.sendMessage(ChatColor.RED + "This command must be run by a player.");
            return true;
         }
      } else {
         this.usage(sender);
         return true;
      }
   }

   private void usage(CommandSender sender) {
      sender.sendMessage(ChatColor.YELLOW + "Usage:");
      sender.sendMessage(ChatColor.YELLOW + "  /mm camera <pathId> add [index] [hold=N] [easing=NAME] [relative=true]");
      sender.sendMessage(ChatColor.YELLOW + "  /mm camera <pathId> remove [index]");
      sender.sendMessage(ChatColor.GRAY + "  easing: LINEAR, EASE_IN_OUT_CUBIC, CATMULL_ROM, STEP, SMOOTHSTEP, ...");
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
      if (mgr == null) {
         return null;
      } else if (args.length == 1) {
         return startsWithFilter(mgr.getRegistry().keys(), args[0]);
      } else if (args.length == 2) {
         return startsWithFilter(List.of("add", "remove"), args[1]);
      } else {
         String cur = args[args.length - 1];
         String curLower = cur.toLowerCase();
         int eq = cur.indexOf(61);
         if (eq > 0) {
            String key = cur.substring(0, eq).toLowerCase();
            String prefix = cur.substring(0, eq + 1);
            String val = cur.substring(eq + 1);
            switch (key) {
               case "easing":
               case "e":
                  List<String> out = new ArrayList<>();

                  for (Easing e : Easing.values()) {
                     if (e.name().toLowerCase().startsWith(val.toLowerCase())) {
                        out.add(prefix + e.name());
                     }
                  }

                  return out;
               case "relative":
               case "r":
               case "rel":
                  List<String> outRel = new ArrayList<>();

                  for (String b : new String[]{"true", "false"}) {
                     if (b.startsWith(val.toLowerCase())) {
                        outRel.add(prefix + b);
                     }
                  }

                  return outRel;
               case "hold":
               case "h":
               case "index":
               case "i":
                  return suggestIndices(mgr, args[0], prefix, val, "add".equalsIgnoreCase(args[1]));
               default:
                  return null;
            }
         } else {
            boolean isAdd = "add".equalsIgnoreCase(args[1]);
            boolean indexAlreadyGiven = positionalIndexGiven(args);
            boolean curStartsLetter = !cur.isEmpty() && Character.isLetter(cur.charAt(0));
            List<String> out = new ArrayList<>();
            if (!indexAlreadyGiven && !curStartsLetter) {
               out.addAll(suggestIndices(mgr, args[0], "", cur, isAdd));
               return out;
            } else {
               if (isAdd) {
                  for (String s : new String[]{"hold=", "easing=", "relative=true"}) {
                     if (s.startsWith(curLower)) {
                        out.add(s);
                     }
                  }
               }

               return out;
            }
         }
      }
   }

   private static List<String> startsWithFilter(Iterable<String> source, String prefix) {
      List<String> out = new ArrayList<>();
      String p = prefix.toLowerCase();

      for (String s : source) {
         if (s.toLowerCase().startsWith(p)) {
            out.add(s);
         }
      }

      return out;
   }

   private static boolean positionalIndexGiven(String[] args) {
      for (int i = 2; i < args.length - 1; i++) {
         String a = args[i];
         if (a.indexOf(61) < 0) {
            try {
               Integer.parseInt(a);
               return true;
            } catch (NumberFormatException e) {
            }
         }
      }

      return false;
   }

   private static List<String> suggestIndices(CinematicManager mgr, String pathId, String prefix, String currentValue, boolean isAdd) {
      int size = mgr.getRegistry().get(pathId).map(p -> p.getPoints().size()).orElse(0);
      int max = isAdd ? size + 1 : size;
      List<String> out = new ArrayList<>();

      for (int i = 1; i <= max; i++) {
         String s = Integer.toString(i);
         if (s.startsWith(currentValue)) {
            out.add(prefix + s);
         }
      }

      return out;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.camera";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "camera";
   }
}
