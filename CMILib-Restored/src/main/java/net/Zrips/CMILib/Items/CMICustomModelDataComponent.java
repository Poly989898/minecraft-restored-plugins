package net.Zrips.CMILib.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

public class CMICustomModelDataComponent {
  private List<Float> floats;
  private List<String> strings;
  private List<Boolean> flags;
  private List<Color> colors;

  public CMICustomModelDataComponent() {}

  public CMICustomModelDataComponent(int value) {
    this.floats = new ArrayList<>(1);
    this.floats.add((float) value);
  }

  public CMICustomModelDataComponent(
      List<Float> floats, List<String> strings, List<Boolean> flags, List<Color> colors) {
    if (floats != null) {
      new ArrayList<>(floats);
    } else {
      Object unusedFloats = null;
    }

    if (strings != null) {
      new ArrayList<>(strings);
    } else {
      Object unusedStrings = null;
    }

    if (flags != null) {
      new ArrayList<>(flags);
    } else {
      Object unusedFlags = null;
    }

    if (colors != null) {
      new ArrayList<>(colors);
    } else {
      Object unusedColors = null;
    }
  }

  public static CMICustomModelDataComponent empty() {
    return new CMICustomModelDataComponent();
  }

  public void removeAll(CMICustomModelDataComponent other) {
    if (other != null) {
      if (this.floats != null) {
        this.floats.removeAll(other.getFloats());
      }

      if (this.strings != null) {
        this.strings.removeAll(other.getStrings());
      }

      if (this.flags != null) {
        this.flags.removeAll(other.getFlags());
      }

      if (this.colors != null) {
        this.colors.removeAll(other.getColors());
      }
    }
  }

  public void combine(CMICustomModelDataComponent other) {
    if (other != null) {
      if (other.getFloats() != null && !other.getFloats().isEmpty()) {
        if (this.floats == null) {
          this.floats = new ArrayList<>();
        }

        for (Float f : other.getFloats()) {
          if (f != null && !this.floats.contains(f)) {
            this.floats.add(f);
          }
        }
      }

      if (other.getStrings() != null && !other.getStrings().isEmpty()) {
        if (this.strings == null) {
          this.strings = new ArrayList<>();
        }

        for (String s : other.getStrings()) {
          if (s != null && !this.strings.contains(s)) {
            this.strings.add(s);
          }
        }
      }

      if (other.getFlags() != null && !other.getFlags().isEmpty()) {
        if (this.flags == null) {
          this.flags = new ArrayList<>();
        }

        for (Boolean b : other.getFlags()) {
          if (b != null && !this.flags.contains(b)) {
            this.flags.add(b);
          }
        }
      }

      if (other.getColors() != null && !other.getColors().isEmpty()) {
        if (this.colors == null) {
          this.colors = new ArrayList<>();
        }

        for (Color c : other.getColors()) {
          if (c != null && !this.colors.contains(c)) {
            this.colors.add(c);
          }
        }
      }
    }
  }

  public boolean isEmpty() {
    return this.getFloats().isEmpty()
        && this.getStrings().isEmpty()
        && this.getFlags().isEmpty()
        && this.getColors().isEmpty();
  }

  public List<Float> getFloats() {
    return this.floats == null
        ? Collections.emptyList()
        : Collections.unmodifiableList(this.floats);
  }

  public List<String> getStrings() {
    return this.strings == null
        ? Collections.emptyList()
        : Collections.unmodifiableList(this.strings);
  }

  public List<Boolean> getFlags() {
    return this.flags == null ? Collections.emptyList() : Collections.unmodifiableList(this.flags);
  }

  public List<Color> getColors() {
    return this.colors == null
        ? Collections.emptyList()
        : Collections.unmodifiableList(this.colors);
  }

  public Integer getLegacyValue() {
    return this.floats != null && !this.floats.isEmpty() ? this.floats.get(0).intValue() : null;
  }

  public ItemStack apply(ItemStack item) {
    if (item == null) {
      return item;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return item;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_21_4)) {
      CustomModelDataComponent component = meta.getCustomModelDataComponent();
      component.setFloats(this.floats != null ? this.floats : Collections.emptyList());
      component.setStrings(this.strings != null ? this.strings : Collections.emptyList());
      component.setFlags(this.flags != null ? this.flags : Collections.emptyList());
      component.setColors(this.colors != null ? this.colors : Collections.emptyList());
      meta.setCustomModelDataComponent(component);
    } else if (Version.isCurrentEqualOrHigher(Version.v1_14_0)) {
      meta.setCustomModelData(this.getLegacyValue());
    } else {
      CMINBT nbt = new CMINBT(item);
      ItemStack newItem = (ItemStack) nbt.setInt("CustomModelData", this.getLegacyValue());
      meta = newItem.getItemMeta();
    }

    item.setItemMeta(meta);
    return item;
  }

  @NotNull
  public static CMICustomModelDataComponent fromItem(ItemStack item) {
    if (item != null && item.hasItemMeta()) {
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
        return empty();
      }

      CMICustomModelDataComponent data = new CMICustomModelDataComponent();
      if (Version.isCurrentEqualOrHigher(Version.v1_21_4)) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        if (component != null) {
          List<Float> f = component.getFloats();
          if (f != null && !f.isEmpty()) {
            data.floats = new ArrayList<>(f);
          }

          List<String> s = component.getStrings();
          if (s != null && !s.isEmpty()) {
            data.strings = new ArrayList<>(s);
          }

          List<Boolean> fl = component.getFlags();
          if (fl != null && !fl.isEmpty()) {
            data.flags = new ArrayList<>(fl);
          }

          List<Color> c = component.getColors();
          if (c != null && !c.isEmpty()) {
            data.colors = new ArrayList<>(c);
          }
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_14_0)) {
        if ((data.floats == null || data.floats.isEmpty()) && meta.hasCustomModelData()) {
          data.floats = new ArrayList<>(1);
          data.floats.add((float) meta.getCustomModelData());
        }
      } else {
        CMINBT nbt = new CMINBT(item);
        Integer value = nbt.getInt("CustomModelData");
        if (value != null) {
          data.floats = new ArrayList<>(1);
          data.floats.add((float) value.intValue());
        }
      }

      return data;
    } else {
      return empty();
    }
  }

  @NotNull
  public static CMICustomModelDataComponent fromString(String input) {
    CMICustomModelDataComponent data = new CMICustomModelDataComponent();
    if (input != null && !input.trim().isEmpty()) {
      String[] parts = input.split(",");
      List<Float> floats = new ArrayList<>();
      List<String> strings = new ArrayList<>();
      List<Boolean> flags = new ArrayList<>();

      for (String raw : parts) {
        if (raw != null) {
          String s = raw.trim();
          if (!s.isEmpty()) {
            if (s.equalsIgnoreCase("true")) {
              flags.add(true);
            } else if (s.equalsIgnoreCase("false")) {
              flags.add(false);
            } else {
              try {
                if (s.contains(".")) {
                  floats.add(Float.parseFloat(s));
                } else {
                  floats.add((float) Integer.parseInt(s));
                }
              } catch (NumberFormatException var12) {
                strings.add(s);
              }
            }
          }
        }
      }

      if (!floats.isEmpty()) {
        data.floats = floats;
      }

      if (!strings.isEmpty()) {
        data.strings = strings;
      }

      if (!flags.isEmpty()) {
        data.flags = flags;
      }

      return data;
    } else {
      return data;
    }
  }

  @Override
  public String toString() {
    List<String> out = new ArrayList<>();
    if (this.floats != null) {
      for (Float f : this.floats) {
        if (f != null) {
          if (f % 1.0F == 0.0F) {
            out.add(String.valueOf(f.intValue()));
          } else {
            out.add(String.valueOf(f));
          }
        }
      }
    }

    if (this.strings != null) {
      out.addAll(this.strings);
    }

    if (this.flags != null) {
      for (Boolean b : this.flags) {
        if (b != null) {
          out.add(String.valueOf(b));
        }
      }
    }

    if (this.colors != null) {
      for (Color c : this.colors) {
        if (c != null) {
          out.add(String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue()));
        }
      }
    }

    return String.join(",", out);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.floats, this.strings, this.flags, this.colors);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CMICustomModelDataComponent)) {
      return false;
    }

    CMICustomModelDataComponent other = (CMICustomModelDataComponent) obj;
    return Objects.equals(this.floats, other.floats)
        && Objects.equals(this.strings, other.strings)
        && Objects.equals(this.flags, other.flags)
        && Objects.equals(this.colors, other.colors);
  }
}
