package net.Zrips.CMILib.Placeholders;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.CMILibConfig;
import net.Zrips.CMILib.Chat.ChatFilterRule;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Placeholder {
  private CMILib plugin;
  Pattern placeholderKeepPatern = Pattern.compile("(\\%\\%)([^\\\"^\\%^ ]+)(\\%\\%)(\\B)");
  Pattern placeholderOthersKeepPatern =
      Pattern.compile(
          "(?i)(\\%parseother_)((\\{)([^\\\"^\\%^ ^\\{^\\}]+)(\\}))([^(\\}\\%).]+)(\\}\\%)");
  Pattern placeholderPatern = Pattern.compile("(%)([^\"^%^ ]+)(%)");
  Pattern placeholderPatern2 = Pattern.compile("(\\{)([^\"^%^ ^{^}]+)(\\})");
  private static ChatFilterRule numericalRule = new ChatFilterRule().setPattern("(\\$)(\\d)");
  Random random = new Random(System.nanoTime());
  private static final String checkItem = "%checkitem_";
  private HashMap<String, String> randomCache = new HashMap<>();

  public Placeholder(CMILib plugin) {
    this.plugin = plugin;
  }

  public List<String> updatePlaceHolders(Player player, List<String> messages) {
    List<String> ms = new ArrayList<>(messages);
    int i = 0;

    for (int l = messages.size(); i < l; i++) {
      ms.set(i, this.updatePlaceHolders(player, messages.get(i)));
    }

    return ms;
  }

  private void reportIssue() {
    CMIMessages.consoleMessage(
        "&c[CMIL] Placeholder got blocked due to security concerns (%checkitem_...%)");
  }

  public Placeholder.CMIPlaceholderType getPlaceHolderType(Player player, String placeholder) {
    if (placeholder == null) {
      return null;
    }

    if (placeholder.contains("%")
        && !placeholder.equals(this.translateOwnPlaceHolder(player, placeholder))) {
      return Placeholder.CMIPlaceholderType.CMI;
    }

    if (placeholder.contains("{")
        && !placeholder.equals(this.translateOwnPlaceHolder(player, placeholder))) {
      return Placeholder.CMIPlaceholderType.CMI;
    }

    if (this.plugin.isPlaceholderAPIEnabled()) {
      try {
        if (placeholder.contains("%")) {
          if (placeholder.toLowerCase().contains("%checkitem_")
              && CMILibConfig.ExploitPatcherCheckItem) {
            this.reportIssue();
          } else if (PlaceholderAPI.containsPlaceholders(placeholder)) {
            return Placeholder.CMIPlaceholderType.PAPI;
          }
        }
      } catch (Throwable var4) {
      }
    }

    return null;
  }

  public String updatePlaceHolders(UUID uuid, String message) {
    if (message == null) {
      return null;
    }

    if (message.contains("{")) {
      message = this.translateOwnPlaceHolder(uuid, message);
    }

    if (message.contains("%")) {
      message = this.translateOwnPlaceHolder(uuid, message);
    }

    if (this.plugin.isPlaceholderAPIEnabled()) {
      try {
        if (message.contains("%")) {
          Player player = Bukkit.getPlayer(uuid);
          if (message.toLowerCase().contains("%checkitem_")
              && CMILibConfig.ExploitPatcherCheckItem) {
            this.reportIssue();
          } else {
            message = PlaceholderAPI.setPlaceholders(player, message);
          }
        }
      } catch (Exception var4) {
      }
    }

    return message;
  }

  public String updatePlaceHolders(String message) {
    UUID uuid = null;
    return this.updatePlaceHolders(uuid, message);
  }

  public String updatePlaceHolders(Player player, String message) {
    if (message == null) {
      return null;
    }

    HashMap<String, String> temp = new HashMap<>();
    if (message.contains("{")) {
      Matcher match = this.placeholderOthersKeepPatern.matcher(message);

      while (match.find()) {
        try {
          if (!message.contains("%")) {
            break;
          }

          String group = match.group();
          int id = new Random().nextInt(Integer.MAX_VALUE);
          String with = "|" + id + "|";
          temp.put(with, group);
          message =
              message.replaceFirst(Matcher.quoteReplacement(group), Matcher.quoteReplacement(with));
        } catch (Throwable var9) {
        }
      }

      message = this.translateOwnPlaceHolder(player, message);
      if (message.contains("%")) {
        message = this.translateOwnPlaceHolder(player, message);
      }

      for (Entry<String, String> one : temp.entrySet()) {
        message = message.replace(one.getKey(), one.getValue());
      }
    } else if (message.contains("%")) {
      message = this.translateOwnPlaceHolder(player, message);
    }

    if (this.plugin.isPlaceholderAPIEnabled()) {
      try {
        if (message.contains("%")) {
          if (message.toLowerCase().contains("%checkitem_")
              && CMILibConfig.ExploitPatcherCheckItem) {
            this.reportIssue();
          } else {
            message = PlaceholderAPI.setPlaceholders(player, message);
          }
        }
      } catch (Throwable var8) {
      }
    }

    return message;
  }

  public boolean containsPlaceHolder(String msg) {
    Matcher match = this.placeholderPatern.matcher(msg);
    if (match.find()) {
      return true;
    }

    match = this.placeholderPatern2.matcher(msg);
    return match.find();
  }

  public String translateOwnPlaceHolder(Player player, String message) {
    return this.translateOwnPlaceHolder(player == null ? null : player.getUniqueId(), message);
  }

  private String matchInception(UUID uuid, String message, int depth) {
    if (message.contains("{")) {
      Matcher match = this.placeholderPatern2.matcher(message);
      int i = 0;

      try {
        while (match.find() && ++i <= 10) {
          String cmd = match.group(2);
          if (!message.contains("{")) {
            break;
          }

          Placeholder.CMIPlaceHolders place = Placeholder.CMIPlaceHolders.getByNameExact(cmd);
          if (place != null) {
            String group = match.group();
            String with = this.getValue(uuid, place, group);
            if (with == null) {
              with = "";
            }

            message =
                message.replaceFirst(
                    Matcher.quoteReplacement(group), Matcher.quoteReplacement(with));
          } else if (this.plugin.isPlaceholderAPIEnabled()) {
            try {
              Player player = Bukkit.getPlayer(uuid);
              String group = match.group();
              if (!group.startsWith("{#")) {
                if (cmd.toLowerCase().contains("%checkitem_")
                    && CMILibConfig.ExploitPatcherCheckItem) {
                  this.reportIssue();
                } else {
                  String with = PlaceholderAPI.setPlaceholders(player, "%" + cmd + "%");
                  if (with == null) {
                    with = "";
                  }

                  if (!with.equalsIgnoreCase("%" + cmd + "%")) {
                    message =
                        message.replaceFirst(Pattern.quote(group), Matcher.quoteReplacement(with));
                  }
                }
              }
            } catch (Exception var11) {
            }
          }
        }
      } catch (Throwable var12) {
      }
    }

    return message;
  }

  public String translateOwnPlaceHolder(UUID uuid, String message) {
    if (message == null) {
      return null;
    }

    if (CMILib.getInstance().isCmiPresent()) {
      try {
        message =
            CMI.getInstance().getPlaceholderAPIManager().translateOwnPlaceHolder(uuid, message);
      } catch (Throwable var10) {
      }
    }

    if (message.contains("{")) {
      HashMap<String, String> temp = new HashMap<>();
      Matcher match = this.placeholderOthersKeepPatern.matcher(message);

      while (match.find()) {
        try {
          if (!message.contains("%")) {
            break;
          }

          String group = match.group();
          int id = new Random().nextInt(Integer.MAX_VALUE);
          String with = "|" + id + "|";
          temp.put(with, group);
          message =
              message.replaceFirst(Matcher.quoteReplacement(group), Matcher.quoteReplacement(with));
        } catch (Throwable var13) {
        }
      }

      message = this.matchInception(uuid, message, 0);

      for (Entry<String, String> one : temp.entrySet()) {
        message = message.replace(one.getKey(), one.getValue());
      }
    }

    if (message.contains("%")) {
      HashMap<String, String> temp = new HashMap<>();
      Matcher matchKeep = this.placeholderKeepPatern.matcher(message);

      while (matchKeep.find()) {
        try {
          if (!message.contains("%")) {
            break;
          }

          String group = matchKeep.group();
          int id = new Random().nextInt(Integer.MAX_VALUE);
          String with = "|" + id + "|";
          temp.put(with, group.substring(1, group.length() - 1));
          message =
              message.replaceFirst(Matcher.quoteReplacement(group), Matcher.quoteReplacement(with));
        } catch (Throwable var12) {
        }
      }

      Matcher match = this.placeholderPatern.matcher(message);

      while (match.find()) {
        try {
          String cmd = match.group(2);
          if (!message.contains("%")) {
            break;
          }

          Placeholder.CMIPlaceHolders place = Placeholder.CMIPlaceHolders.getByNameExact(cmd);
          if (place != null) {
            String group = match.group();
            String with = this.getValue(uuid, place, group);
            if (with == null) {
              with = "";
            }

            message = message.replaceFirst(Pattern.quote(group), Matcher.quoteReplacement(with));
          }
        } catch (Throwable var11) {
        }
      }

      for (Entry<String, String> one : temp.entrySet()) {
        message = message.replace(one.getKey(), one.getValue());
      }
    }

    return message;
  }

  public String getValue(Player player, Placeholder.CMIPlaceHolders placeHolder) {
    return this.getValue(player, placeHolder, null);
  }

  public String getValue(Player player, Placeholder.CMIPlaceHolders placeHolder, String value) {
    return this.getValue(player != null ? player.getUniqueId() : null, placeHolder, value);
  }

  public String getValue(UUID uuid, Placeholder.CMIPlaceHolders placeHolder, String value) {
    return null;
  }

  private void informFailed(String value) {
    CMIMessages.consoleMessage("&cInccorrect placeholder format for " + value);
  }

  private String variable(Boolean state) {
    return state
        ? CMIMessages.getMsg(LC.info_variables_True)
        : CMIMessages.getMsg(LC.info_variables_False);
  }

  public enum CMIPlaceHolders {
    ;
    static LinkedHashMap<String, Placeholder.CMIPlaceHolders> byNameStatic = new LinkedHashMap<>();
    static LinkedHashMap<String, LinkedHashSet<Placeholder.CMIPlaceHolders>> byNameComplex =
        new LinkedHashMap<>();
    private String[] vars;
    private List<Integer> groups = new ArrayList<>();
    private ChatFilterRule rule = null;
    private boolean hidden = false;
    private String desc = null;

    CMIPlaceHolders() {}

    CMIPlaceHolders(boolean hideen) {
      this(null, hideen);
    }

    CMIPlaceHolders(String desc, String... vars) {
      this(desc, false, vars);
    }

    CMIPlaceHolders(String desc, boolean hidden, String... vars) {
      this.desc = desc;
      this.vars = vars;
      this.hidden = hidden;

      try {
        Matcher matcher = Placeholder.numericalRule.getMatcher(this.toString());
        if (matcher != null) {
          this.rule = new ChatFilterRule();
          List<String> ls = new ArrayList<>();
          ls.add("(%)" + this.toString().replaceAll("\\$\\d", "([^\"^%]*)") + "(%)");
          ls.add("(\\{)" + this.toString().replaceAll("\\$\\d", "([^\"^%]*)") + "(\\})");
          this.rule.setPattern(ls);

          while (matcher.find()) {
            try {
              int id = Integer.parseInt(matcher.group(2));
              this.groups.add(id);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
    }

    public static Placeholder.CMIPlaceHolders getByName(String name) {
      if (name.startsWith("%") || name.startsWith("{")) {
        name = name.replace("%", "").replace("{", "").replace("}", "");
      }

      String original = name;
      Placeholder.CMIPlaceHolders got = byNameStatic.get(name);
      if (got != null) {
        return got;
      }

      String[] split = name.split("_");
      if (split.length < 3) {
        return null;
      }

      String prefix = split[0] + "_" + split[1];
      Set<Placeholder.CMIPlaceHolders> main = byNameComplex.get(prefix);
      if (main == null) {
        return null;
      }

      for (Placeholder.CMIPlaceHolders mainOne : main) {
        if (!mainOne.getComplexRegexMatchers(original).isEmpty()) {
          return mainOne;
        }
      }

      return null;
    }

    public static Placeholder.CMIPlaceHolders getByNameExact(String name) {
      return getByName(name);
    }

    public String getFull() {
      if (this.isComplex()) {
        String name = this.name();
        int i = 0;

        for (String one : this.name().split("_")) {
          if (one.startsWith("$")) {
            if (this.vars.length >= i - 1) {
              name = name.replace(one, "[" + this.vars[i] + "]");
            }

            i++;
          }
        }

        return "%" + name + "%";
      } else {
        return "%" + this.name() + "%";
      }
    }

    public String getMVdW() {
      if (this.isComplex()) {
        String name = this.name();
        int i = 0;

        for (String one : this.name().split("_")) {
          if (one.startsWith("$")) {
            if (this.vars.length >= i - 1) {
              name = name.replace(one, "*");
            }

            i++;
          }
        }

        return name;
      } else {
        return this.name();
      }
    }

    public List<String> getComplexRegexMatchers(String text) {
      List<String> lsInLs = new ArrayList<>();
      if (!this.isComplex()) {
        return lsInLs;
      }

      if (!text.startsWith("%") && !text.endsWith("%")) {
        text = "%" + text + "%";
      }

      Matcher matcher = this.getRule().getMatcher(text);
      if (matcher == null) {
        return lsInLs;
      }

      while (matcher.find()) {
        lsInLs.add(matcher.group());
      }

      return lsInLs;
    }

    public List<String> getComplexValues(String text) {
      List<String> lsInLs = new ArrayList<>();
      if (this.isComplex() && text != null) {
        if (!text.startsWith("%") && !text.endsWith("%")) {
          text = "%" + text + "%";
        }

        Matcher matcher = this.getRule().getMatcher(text);
        if (matcher == null) {
          return lsInLs;
        }

        if (matcher.find()) {
          try {
            for (Integer oneG : this.groups) {
              lsInLs.add(matcher.group(oneG + 1));
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        return lsInLs;
      } else {
        return lsInLs;
      }
    }

    public boolean isComplex() {
      return this.rule != null;
    }

    public ChatFilterRule getRule() {
      return this.rule;
    }

    public void setRule(ChatFilterRule rule) {
      this.rule = rule;
    }

    public boolean isHidden() {
      return this.hidden;
    }

    public String getDescription() {
      return this.desc;
    }

    static {
      for (Placeholder.CMIPlaceHolders one : values()) {
        String fullName = one.toString();
        if (!one.isComplex()) {
          byNameStatic.put(fullName.toLowerCase(), one);
        } else {
          String[] split = fullName.split("_");
          String first = split[0] + "_" + split[1];
          LinkedHashSet<Placeholder.CMIPlaceHolders> old =
              byNameComplex.getOrDefault(first, new LinkedHashSet<>());
          old.add(one);
          byNameComplex.put(first, old);
        }
      }
    }
  }

  public enum CMIPlaceholderType {
    CMI,
    PAPI,
    MVdW;
  }
}
