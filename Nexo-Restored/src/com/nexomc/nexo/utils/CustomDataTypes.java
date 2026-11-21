package com.nexomc.nexo.utils;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.libs.morepersistentdatatypes.datatypes.collections.CollectionDataType;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0089\u0001\u0010\u0004\u001az\u0012(\u0012&\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007 \b*\u0012\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007\u0018\u00010\t0\u0006\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007 \b*<\u0012(\u0012&\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007 \b*\u0012\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007\u0018\u00010\t0\u0006\u0012\f\u0012\n \b*\u0004\u0018\u00010\u00070\u0007\u0018\u00010\u00050\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0089\u0001\u0010\f\u001az\u0012(\u0012&\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r \b*\u0012\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r\u0018\u00010\t0\u0006\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r \b*<\u0012(\u0012&\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r \b*\u0012\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r\u0018\u00010\t0\u0006\u0012\f\u0012\n \b*\u0004\u0018\u00010\r0\r\u0018\u00010\u00050\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000b¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/utils/CustomDataTypes;", "", "<init>", "()V", "UUID_LIST", "Lcom/nexomc/libs/morepersistentdatatypes/datatypes/collections/CollectionDataType;", "", "Ljava/util/UUID;", "kotlin.jvm.PlatformType", "", "getUUID_LIST", "()Lcom/nexomc/libs/morepersistentdatatypes/datatypes/collections/CollectionDataType;", "STRING_LIST", "", "getSTRING_LIST", "com.nexomc:core"}
)
public final class CustomDataTypes {
   @NotNull
   public static final CustomDataTypes INSTANCE = new CustomDataTypes();
   private static final CollectionDataType<List<UUID>, UUID> UUID_LIST;
   private static final CollectionDataType<List<String>, String> STRING_LIST;

   private CustomDataTypes() {
   }

   public final CollectionDataType<List<UUID>, UUID> getUUID_LIST() {
      return UUID_LIST;
   }

   public final CollectionDataType<List<String>, String> getSTRING_LIST() {
      return STRING_LIST;
   }

   static {
      UUID_LIST = DataType.asList(DataType.UUID);
      STRING_LIST = DataType.asList(DataType.STRING);
   }
}
