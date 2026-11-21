package com.nexomc.libs.gui.builder.item;

import com.nexomc.libs.gui.components.exception.GuiException;
import com.nexomc.libs.gui.components.util.Legacy;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BookBuilder extends BaseItemBuilder<BookBuilder> {
   private static final EnumSet<Material> BOOKS;

   BookBuilder(@NotNull ItemStack itemStack) {
      super(itemStack);
      if (!BOOKS.contains(itemStack.getType())) {
         throw new GuiException("BookBuilder requires the material to be a WRITABLE_BOOK/WRITTEN_BOOK!");
      }
   }

   @Contract("_ -> this")
   public @NotNull BookBuilder author(@Nullable Component author) {
      BookMeta bookMeta = (BookMeta)this.getMeta();
      if (author == null) {
         bookMeta.setAuthor((String)null);
         this.setMeta(bookMeta);
         return this;
      } else {
         bookMeta.setAuthor(Legacy.SERIALIZER.serialize(author));
         this.setMeta(bookMeta);
         return this;
      }
   }

   @Contract("_ -> this")
   public @NotNull BookBuilder generation(BookMeta.@Nullable Generation generation) {
      BookMeta bookMeta = (BookMeta)this.getMeta();
      bookMeta.setGeneration(generation);
      this.setMeta(bookMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull BookBuilder page(Component... pages) {
      return this.page(Arrays.asList(pages));
   }

   @Contract("_ -> this")
   public @NotNull BookBuilder page(@NotNull List<Component> pages) {
      BookMeta bookMeta = (BookMeta)this.getMeta();

      for(Component page : pages) {
         bookMeta.addPage(new String[]{Legacy.SERIALIZER.serialize(page)});
      }

      this.setMeta(bookMeta);
      return this;
   }

   @Contract("_, _ -> this")
   public @NotNull BookBuilder page(int page, @NotNull Component data) {
      BookMeta bookMeta = (BookMeta)this.getMeta();
      bookMeta.setPage(page, Legacy.SERIALIZER.serialize(data));
      this.setMeta(bookMeta);
      return this;
   }

   @Contract("_ -> this")
   public @NotNull BookBuilder title(@Nullable Component title) {
      BookMeta bookMeta = (BookMeta)this.getMeta();
      if (title == null) {
         bookMeta.setTitle((String)null);
         this.setMeta(bookMeta);
         return this;
      } else {
         bookMeta.setTitle(Legacy.SERIALIZER.serialize(title));
         this.setMeta(bookMeta);
         return this;
      }
   }

   static {
      BOOKS = EnumSet.of(Material.WRITABLE_BOOK, Material.WRITTEN_BOOK);
   }
}
