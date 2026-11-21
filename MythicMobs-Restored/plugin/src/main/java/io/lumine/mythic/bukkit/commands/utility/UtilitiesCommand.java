package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.skills.IDummySkill;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class UtilitiesCommand extends Command<MythicBukkit> {
   public UtilitiesCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(
         new Command[]{
            new BroadcastCommand(this),
            new GetBlockCoordinatesCommand(this),
            new GetBlockInfoCommand(this),
            new GetItemInfoCommand(this),
            new GetTargetInfoCommand(this),
            new AddEntityNBTCommand(this),
            new TestEntityNBTCommand(this),
            new GetTargetNBTCommand(this),
            new GetPathStringCommand(this),
            new ActiveInfoCommand(this),
            new ActiveRemoveCommand(this),
            new ListAllEntitiesCommand(this),
            new GetLightLevelCommand(this),
            new GetStructuresCommand(this),
            new GetStatsCommand(this),
            new SetBaseStatCommand(this),
            new ListValidStructuresCommand(this),
            new GetSpawnStateCommand(this),
            new PluginDumpCommand(this),
            new GetDyeCombinationCommand(this),
            new GetOptimalDyesCommand(this),
            new ToggleTimerCommand(this),
            new OpenColorPickerCommand(this)
         }
      );
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("generatedocs")) {
         String docsFolder = this.getPlugin().getDataFolder() + System.getProperty("file.separator") + "Docs";
         String mechanicsFolder = docsFolder + System.getProperty("file.separator") + "Mechanics";
         String mechanicsFile = docsFolder + System.getProperty("file.separator") + "mechanics.txt";
         File file = new File(docsFolder);
         if (!file.exists()) {
            file.mkdir();
         }

         file = new File(mechanicsFolder);
         if (!file.exists()) {
            file.mkdir();
         }

         file = new File(mechanicsFile);
         if (file.exists()) {
            file.delete();
         }

         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }

         try (PrintWriter out = new PrintWriter(mechanicsFile)) {
            Collection<Class<?>> mechanicsClasses = AnnotationUtil.getAnnotatedClasses(
               this.plugin, "io.lumine.mythic.core.skills.mechanics", MythicMechanic.class
            );

            for (Class<?> clazz : mechanicsClasses.stream()
               .sorted((a, b) -> a.getAnnotation(MythicMechanic.class).name().compareTo(b.getAnnotation(MythicMechanic.class).name()))
               .collect(Collectors.toList())) {
               try {
                  String name = StringUtils.capitalize(clazz.getAnnotation(MythicMechanic.class).name());
                  String author = clazz.getAnnotation(MythicMechanic.class).author();
                  String[] aliases = clazz.getAnnotation(MythicMechanic.class).aliases();
                  String description = clazz.getAnnotation(MythicMechanic.class).description();
                  String version = clazz.getAnnotation(MythicMechanic.class).version();
                  String type = "";
                  if (Aura.class.isAssignableFrom(clazz)) {
                     type = type + "Aura";
                  } else {
                     if (IDummySkill.class.isAssignableFrom(clazz)) {
                        type = "Dummy";
                     }

                     if (IMetaSkill.class.isAssignableFrom(clazz)) {
                        if (type.length() > 0) {
                           type = type + ", ";
                        }

                        type = type + "Meta";
                     }

                     if (INoTargetSkill.class.isAssignableFrom(clazz)) {
                        if (type.length() > 0) {
                           type = type + ", ";
                        }

                        type = type + "No-Target";
                     } else if (ITargetedEntitySkill.class.isAssignableFrom(clazz)) {
                        if (type.length() > 0) {
                           type = type + ", ";
                        }

                        type = type + "Entity";
                     } else if (ITargetedLocationSkill.class.isAssignableFrom(clazz)) {
                        if (type.length() > 0) {
                           type = type + ", ";
                        }

                        type = type + "Location";
                     }
                  }

                  out.println("| [[:skills:mechanics:" + name.toLowerCase() + "|" + name + "]] | " + type + " | " + description + " |");
                  String cFile = name.toLowerCase() + ".txt";
                  File cfile = new File(mechanicsFolder + System.getProperty("file.separator") + cFile);
                  if (cfile.exists()) {
                     cfile.delete();
                  }

                  cfile.createNewFile();

                  try (PrintWriter cout = new PrintWriter(cfile)) {
                     List<Field> fields = new ArrayList<>();

                     for (Field f : clazz.getDeclaredFields()) {
                        if (f.getAnnotations().length > 0) {
                           fields.add(f);
                        }
                     }

                     cout.println("<WRAP infobox right>");
                     cout.println("<WRAP infoboxheader>" + name + "</WRAP>");
                     cout.println("<WRAP infoboxtable>");
                     cout.println("^Type| " + type + "|");
                     if (aliases.length > 0) {
                        cout.println("^Aliases| " + String.join(", ", aliases) + "|");
                     }

                     cout.println("^Added In| " + version + "|");
                     cout.println("^Author| " + author + "|");
                     cout.println("</WRAP>");
                     cout.println("</WRAP>");
                     cout.println("====== Mechanic: " + name + " ======");
                     cout.println(" ");
                     cout.println(description);
                     cout.println(" ");
                     cout.println("===== Attributes =====");
                     cout.println("^Attribute ^Aliases ^Description ^Default ^");
                     if (fields.size() == 0) {
                        cout.println("|  |  |  |");
                     } else {
                        for (Field field : fields) {
                           String aName = field.getAnnotation(MythicField.class).name();
                           String[] aAliases = field.getAnnotation(MythicField.class).aliases();
                           String aDescription = field.getAnnotation(MythicField.class).description();
                           String defValue = field.getAnnotation(MythicField.class).defValue();
                           cout.println("| " + aName + " | " + String.join(", ", aAliases) + " | " + aDescription + " | " + defValue + " |");
                        }
                     }

                     cout.println("\\\\");
                     cout.println(" ");
                     cout.println("===== Examples =====");
                     cout.println(" ");
                     cout.println("<code>");
                     cout.println("Skills:");
                     cout.println("- " + name.toLowerCase() + "{} true");
                     cout.println("</code>");
                     cout.println(" ");
                     cout.println(" ");
                  } catch (FileNotFoundException ex) {
                     ex.printStackTrace();
                  }
               } catch (Exception ex2) {
               }
            }
         } catch (FileNotFoundException ex3) {
            ex3.printStackTrace();
         }

         String conditionsFolder = docsFolder + System.getProperty("file.separator") + "Conditions";
         String conditionsFile = docsFolder + System.getProperty("file.separator") + "conditions.txt";
         file = new File(docsFolder);
         if (!file.exists()) {
            file.mkdir();
         }

         file = new File(conditionsFolder);
         if (!file.exists()) {
            file.mkdir();
         }

         file = new File(conditionsFile);
         if (file.exists()) {
            file.delete();
         }

         try {
            file.createNewFile();
         } catch (IOException ex4) {
            ex4.printStackTrace();
         }

         try (PrintWriter out = new PrintWriter(conditionsFile)) {
            Collection<Class<?>> conditionsClasses = AnnotationUtil.getAnnotatedClasses(
               this.plugin, "io.lumine.mythic.core.skills.conditions.all", MythicCondition.class
            );

            for (Class<?> clazz : conditionsClasses.stream()
               .sorted((a, b) -> a.getAnnotation(MythicCondition.class).name().compareTo(b.getAnnotation(MythicCondition.class).name()))
               .collect(Collectors.toList())) {
               try {
                  String namex = StringUtils.capitalize(clazz.getAnnotation(MythicCondition.class).name());
                  String authorx = clazz.getAnnotation(MythicCondition.class).author();
                  String[] aliasesx = clazz.getAnnotation(MythicCondition.class).aliases();
                  String descriptionx = clazz.getAnnotation(MythicCondition.class).description();
                  String versionx = clazz.getAnnotation(MythicCondition.class).version();
                  String typex = "";
                  if (ISkillMetaCondition.class.isAssignableFrom(clazz)) {
                     typex = typex + "Meta";
                  } else {
                     if (IEntityCondition.class.isAssignableFrom(clazz)) {
                        typex = "Entity";
                     }

                     if (ILocationCondition.class.isAssignableFrom(clazz)) {
                        if (typex.length() > 0) {
                           typex = typex + ", ";
                        }

                        typex = typex + "Location";
                     }

                     if (IEntityComparisonCondition.class.isAssignableFrom(clazz)) {
                        if (typex.length() > 0) {
                           typex = typex + ", ";
                        }

                        typex = typex + "Compare";
                     } else if (ILocationComparisonCondition.class.isAssignableFrom(clazz)) {
                        if (typex.length() > 0) {
                           typex = typex + ", ";
                        }

                        typex = typex + "Compare";
                     }
                  }

                  out.println("| [[:conditions:" + namex.toLowerCase() + "|" + namex + "]] | " + typex + " | " + descriptionx + " |");
                  String cFile = namex.toLowerCase() + ".txt";
                  File cfile = new File(conditionsFolder + System.getProperty("file.separator") + cFile);
                  if (cfile.exists()) {
                     cfile.delete();
                  }

                  cfile.createNewFile();

                  try (PrintWriter cout = new PrintWriter(cfile)) {
                     List<Field> fields = new ArrayList<>();

                     for (Field fx : clazz.getDeclaredFields()) {
                        if (fx.getAnnotations().length > 0) {
                           fields.add(fx);
                        }
                     }

                     cout.println("<WRAP infobox right>");
                     cout.println("<WRAP infoboxheader>" + namex + "</WRAP>");
                     cout.println("<WRAP infoboxtable>");
                     cout.println("^Type| " + typex + "|");
                     if (aliasesx.length > 0) {
                        cout.println("^Aliases| " + String.join(", ", aliasesx) + "|");
                     }

                     cout.println("^Added In| " + versionx + "|");
                     cout.println("^Author| " + authorx + "|");
                     cout.println("</WRAP>");
                     cout.println("</WRAP>");
                     cout.println("====== Condition: " + namex + " ======");
                     cout.println(" ");
                     cout.println(descriptionx);
                     cout.println(" ");
                     cout.println("===== Attributes =====");
                     cout.println("^Attribute ^Aliases ^Description ^");
                     if (fields.size() == 0) {
                        cout.println("|  |  |  |");
                     } else {
                        for (Field field : fields) {
                           String aName = field.getAnnotation(MythicField.class).name();
                           String[] aAliases = field.getAnnotation(MythicField.class).aliases();
                           String aDescription = field.getAnnotation(MythicField.class).description();
                           cout.println("| " + aName + " | " + String.join(", ", aAliases) + " | " + aDescription + " |");
                        }
                     }

                     cout.println("\\\\");
                     cout.println(" ");
                     cout.println("===== Examples =====");
                     cout.println(" ");
                     cout.println("<code>");
                     cout.println("Conditions:");
                     cout.println("- " + namex.toLowerCase() + "{} true");
                     cout.println("</code>");
                     cout.println(" ");
                     cout.println(" ");
                  } catch (FileNotFoundException ex5) {
                     ex5.printStackTrace();
                  }
               } catch (Exception ex6) {
               }
            }
         } catch (FileNotFoundException ex7) {
            ex7.printStackTrace();
         }
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "utilities";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"utility", "u"};
   }
}
