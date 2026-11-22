package com.Zrips.CMI.Modules.Ranks;

import com.Zrips.CMI.CMI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;

public class CMIRankPlaceholder {
  private static final String regex = "^(.*?)(>=|<=|==)(.*?)(;)(.+)";
  private static final Pattern pattern = Pattern.compile("^(.*?)(>=|<=|==)(.*?)(;)(.+)", 8);
  private String placeholder = "";
  private Object value = "";
  private rankPlaceholderType type = rankPlaceholderType.invalid;
  private rankPlaceholderAction action = rankPlaceholderAction.equal;
  private String message = "";

  public CMIRankPlaceholder(String string) {
    Matcher matcher = pattern.matcher(string);

    try {
      while (matcher.find()) {
        this.placeholder = matcher.group(1);
        this.value = matcher.group(3);
        this.message = matcher.group(5);
        String operator = matcher.group(2);
        if (operator.equalsIgnoreCase(">=")) {
          this.action = rankPlaceholderAction.more;
        } else if (operator.equalsIgnoreCase("<=")) {
          this.action = rankPlaceholderAction.less;
        } else {
          this.action = rankPlaceholderAction.equal;
        }

        try {
          this.value = Double.parseDouble((String) this.value);
          this.type = rankPlaceholderType.number;
        } catch (Throwable throwable) {
          this.type = rankPlaceholderType.string;
        }
      }
    } catch (Throwable throwable2) {
      this.type = rankPlaceholderType.invalid;
    }
  }

  public String getPlaceholder() {
    return this.placeholder;
  }

  public void setPlaceholder(String string) {
    this.placeholder = string;
  }

  public Object getValue() {
    return this.value;
  }

  public void setValue(Object object) {
    this.value = object;
  }

  public rankPlaceholderType getType() {
    return this.type;
  }

  public void setType(rankPlaceholderType rankPlaceholderType) {
    this.type = rankPlaceholderType;
  }

  public rankPlaceholderAction getAction() {
    return this.action;
  }

  public void setAction(rankPlaceholderAction rankPlaceholderAction) {
    this.action = rankPlaceholderAction;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String string) {
    this.message = string;
  }

  public double percent(Player player) {
    if (player == null) {
      return 0.0;
    }

    String string =
        CMI.getInstance()
            .getPlaceholderAPIManager()
            .updatePlaceHolders(player, this.getPlaceholder());
    switch (this.type) {
      case number:
        switch (this.action) {
          case more:
            try {
              double number = Double.parseDouble(string);
              return number >= Double.parseDouble((String) this.getValue())
                  ? 100.0
                  : number / Double.parseDouble((String) this.getValue()) * 100.0;
            } catch (Throwable throwable) {
              return 0.0;
            }
          case less:
            try {
              double parsedValue = Double.parseDouble(string);
              return parsedValue <= Double.parseDouble((String) this.getValue()) ? 100.0 : 0.0;
            } catch (Throwable throwable2) {
              return 0.0;
            }
          case equal:
            return string.equalsIgnoreCase((String) this.getValue()) ? 100.0 : 0.0;
        }
      default:
        return 0.0;
      case string:
        return string.equalsIgnoreCase((String) this.getValue()) ? 100.0 : 0.0;
      case invalid:
        return 0.0;
    }
  }
}
