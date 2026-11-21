package com.nexomc.nexo.glyphs;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0001HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0004HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0004HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\tHÆ\u0003JA\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00012\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00042\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\tHÆ\u0001J\u0014\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eHÖ\u0083\u0004J\n\u0010\u001f\u001a\u00020 HÖ\u0081\u0004J\n\u0010!\u001a\u00020\u0004HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/glyphs/ReferenceGlyph;", "Lcom/nexomc/nexo/glyphs/Glyph;", "glyph", "referenceId", "", "index", "Lkotlin/ranges/IntRange;", "_permission", "_placeholders", "", "<init>", "(Lcom/nexomc/nexo/glyphs/Glyph;Ljava/lang/String;Lkotlin/ranges/IntRange;Ljava/lang/String;Ljava/util/List;)V", "getGlyph", "()Lcom/nexomc/nexo/glyphs/Glyph;", "getReferenceId", "()Ljava/lang/String;", "getIndex", "()Lkotlin/ranges/IntRange;", "get_permission", "get_placeholders", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class ReferenceGlyph extends Glyph {
   @NotNull
   private final Glyph glyph;
   @NotNull
   private final String referenceId;
   @NotNull
   private final IntRange index;
   @NotNull
   private final String _permission;
   @NotNull
   private final List<String> _placeholders;

   public ReferenceGlyph(@NotNull Glyph glyph, @NotNull String referenceId, @NotNull IntRange index, @NotNull String _permission, @NotNull List<String> _placeholders) {
      Key font = glyph.getFont();
      Key texture = glyph.getTexture();
      int ascent = glyph.getAscent();
      int height = glyph.getHeight();
      String str = CollectionsKt.joinToString((Iterable)glyph.getUnicodes(), (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null).substring(index.getFirst() - 1, index.getLast());
      super(referenceId, font, texture, ascent, height, CollectionsKt.listOf(str), _permission, _placeholders, false, false, (GlyphShadow)null);
      this.glyph = glyph;
      this.referenceId = referenceId;
      this.index = index;
      this._permission = _permission;
      this._placeholders = _placeholders;
   }

   @NotNull
   public final Glyph getGlyph() {
      return this.glyph;
   }

   @NotNull
   public final String getReferenceId() {
      return this.referenceId;
   }

   @NotNull
   public final IntRange getIndex() {
      return this.index;
   }

   @NotNull
   public final String get_permission() {
      return this._permission;
   }

   @NotNull
   public final List<String> get_placeholders() {
      return this._placeholders;
   }

   @NotNull
   public final Glyph component1() {
      return this.glyph;
   }

   @NotNull
   public final String component2() {
      return this.referenceId;
   }

   @NotNull
   public final IntRange component3() {
      return this.index;
   }

   @NotNull
   public final String component4() {
      return this._permission;
   }

   @NotNull
   public final List<String> component5() {
      return this._placeholders;
   }

   @NotNull
   public final ReferenceGlyph copy(@NotNull Glyph glyph, @NotNull String referenceId, @NotNull IntRange index, @NotNull String _permission, @NotNull List<String> _placeholders) {
      return new ReferenceGlyph(glyph, referenceId, index, _permission, _placeholders);
   }

   @NotNull
   public String toString() {
      return "ReferenceGlyph(glyph=" + this.glyph + ", referenceId=" + this.referenceId + ", index=" + this.index + ", _permission=" + this._permission + ", _placeholders=" + this._placeholders + ")";
   }

   public int hashCode() {
      int result = this.glyph.hashCode();
      result = result * 31 + this.referenceId.hashCode();
      result = result * 31 + this.index.hashCode();
      result = result * 31 + this._permission.hashCode();
      result = result * 31 + this._placeholders.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ReferenceGlyph)) {
         return false;
      } else {
         ReferenceGlyph referenceGlyph = (ReferenceGlyph)other;
         if (!Intrinsics.areEqual(this.glyph, referenceGlyph.glyph)) {
            return false;
         } else if (!Intrinsics.areEqual(this.referenceId, referenceGlyph.referenceId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.index, referenceGlyph.index)) {
            return false;
         } else if (!Intrinsics.areEqual(this._permission, referenceGlyph._permission)) {
            return false;
         } else {
            return Intrinsics.areEqual(this._placeholders, referenceGlyph._placeholders);
         }
      }
   }
}
