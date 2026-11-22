package net.Zrips.CMILib.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CMIBook {
  ItemStack item;
  HashMap<Integer, List<String>> content = new HashMap<>();

  public CMIBook() {
    this.item = new ItemStack(Material.WRITTEN_BOOK);
  }

  public ItemStack getItemStack() {
    if (!this.hasAuthor()) {
      this.setAuthor("None");
    }

    if (!this.hasTitle()) {
      this.setTitle("None");
    }

    BookMeta bm = (BookMeta) this.item.getItemMeta();

    for (Entry<Integer, List<String>> oneP : this.content.entrySet()) {
      StringBuilder page = new StringBuilder();

      for (String one : oneP.getValue()) {
        page.append(CMIChatColor.translate(one) + " \n");
      }

      bm.addPage(new String[] {page.toString()});
    }

    this.item.setItemMeta(bm);
    return this.item;
  }

  public boolean hasAuthor() {
    BookMeta bm = (BookMeta) this.item.getItemMeta();
    return bm.hasAuthor();
  }

  public boolean hasTitle() {
    BookMeta bm = (BookMeta) this.item.getItemMeta();
    return bm.hasTitle();
  }

  public CMIBook setTitle(String text) {
    BookMeta bm = (BookMeta) this.item.getItemMeta();
    bm.setTitle(text);
    this.item.setItemMeta(bm);
    return this;
  }

  public CMIBook setAuthor(String text) {
    BookMeta bm = (BookMeta) this.item.getItemMeta();
    bm.setAuthor(text);
    this.item.setItemMeta(bm);
    return this;
  }

  public CMIBook addNewPage() {
    int page = this.content.size() + 1;
    List<String> lines = new ArrayList<>();
    this.content.put(page, lines);
    return this;
  }

  public CMIBook addLine(String text) {
    int page = 1;
    List<String> lines = new ArrayList<>();
    if (!this.content.isEmpty()) {
      page = this.content.size();
      lines = this.content.get(this.content.size());
    }

    if (lines.size() >= 14) {
      this.addNewPage();
      this.addLine(text);
      return this;
    } else {
      ArrayList<String> t = new ArrayList<>(lines);
      t.add(text);
      if (linesToString(t, true).length() > 300) {
        this.addNewPage();
        this.addLine(text);
        return this;
      } else {
        lines.add(text);
        this.content.put(page, lines);
        return this;
      }
    }
  }

  private static String linesToString(List<String> lines, boolean clean) {
    String page = "";

    for (String one : lines) {
      if (clean) {
        page = page + one;
      } else {
        page = page + one + " \n";
      }
    }

    return page;
  }
}
