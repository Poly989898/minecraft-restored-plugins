package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import java.util.List;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import org.bukkit.entity.Player;

public class CMIDialogInputValue {
  private Object value = null;
  private boolean placeholder = false;

  public CMIDialogInputValue(Object object) {
    this.value = object;
    this.placeholder =
        this.value instanceof String
            && CMI.getInstance()
                .getPlaceholderAPIManager()
                .containsPlaceHolder((String) this.value);
  }

  public Object getValue() {
    return this.getValue(null, null);
  }

  public Object getValue(@Nullable Player player) {
    return this.getValue(player, null);
  }

  public boolean getBoolValue(@Nullable Player player) {
    return this.getBoolValue(player, null);
  }

  public boolean getBoolValue(@Nullable Player player, @Nullable Object object) {
    Object rawValue = this.getValue(player, object);
    if (rawValue instanceof Boolean) {
      return (Boolean) rawValue;
    }

    if (rawValue instanceof Integer) {
      return (Integer) rawValue == 1;
    }

    if (rawValue instanceof Float) {
      return (Float) rawValue == 1.0F;
    }

    if (rawValue == null) {
      return false;
    }

    if (rawValue instanceof String) {
      String string = CMIChatColor.stripColor(rawValue.toString()).toLowerCase();
      if (string.equals("true") || string.equals("1")) {
        return true;
      }

      if (string.equals("false") || string.equals("0")) {
        return false;
      }
    }

    return false;
  }

  public int getIntValue(@Nullable Player player) {
    return this.getIntValue(player, null);
  }

  public int getIntValue(@Nullable Player player, @Nullable Object object) {
    Object rawValue = this.getValue(player, object);
    if (rawValue instanceof Integer) {
      return (Integer) rawValue;
    }

    if (rawValue instanceof Float) {
      return (int) ((Float) rawValue).floatValue();
    }

    if (rawValue == null) {
      return 0;
    }

    try {
      return Integer.parseInt(rawValue.toString());
    } catch (NumberFormatException numberFormatException) {
      return 0;
    }
  }

  public String getStringValue(@Nullable Player player) {
    return this.getStringValue(player, null);
  }

  public String getStringValue(@Nullable Player player, @Nullable Object object) {
    Object rawValue = this.getValue(player, object);
    return rawValue == null ? null : String.valueOf(rawValue);
  }

  public float getFloatValue(@Nullable Player player) {
    return this.getFloatValue(player, null);
  }

  public float getFloatValue(@Nullable Player player, @Nullable Object object) {
    Float nullableFloat = this.getFloatValueNullable(player, object);
    return nullableFloat != null ? nullableFloat : 0.0F;
  }

  public Float getFloatValueNullable(@Nullable Player player) {
    return this.getFloatValueNullable(player, null);
  }

  public Float getFloatValueNullable(@Nullable Player player, @Nullable Object object) {
    Object rawValue = this.getValue(player, object);
    if (rawValue instanceof Float) {
      return (Float) rawValue;
    }

    if (rawValue == null) {
      return 0.0F;
    }

    try {
      return Float.parseFloat(rawValue.toString());
    } catch (NumberFormatException numberFormatException) {
      return null;
    }
  }

  public Object getValue(@Nullable Player player, @Nullable Object object) {
    if (this.isPlaceholder()) {
      return CMIDialog.updateVariable(player, (String) this.value);
    } else {
      return object != null ? object : this.value;
    }
  }

  public boolean isPlaceholder() {
    return this.placeholder;
  }

  public static CMIDialogInputValue getValue(Object object) {
    if (object instanceof Integer) {
      return new CMIDialogInputValue(((Integer) object).floatValue());
    } else if (object instanceof Float) {
      return new CMIDialogInputValue((Float) object);
    } else if (object instanceof Double) {
      return new CMIDialogInputValue(((Double) object).floatValue());
    } else if (object instanceof String) {
      return new CMIDialogInputValue((String) object);
    } else {
      return object instanceof List
          ? new CMIDialogInputValue(CMIList.listToString((List) object))
          : new CMIDialogInputValue(0.0F);
    }
  }
}
