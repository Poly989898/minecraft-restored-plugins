package litebans;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONParser {
   public static final int S_INIT = 0;
   public static final int S_IN_FINISHED_VALUE = 1;
   public static final int S_IN_OBJECT = 2;
   public static final int S_IN_ARRAY = 3;
   public static final int S_PASSED_PAIR_KEY = 4;
   public static final int S_IN_PAIR_VALUE = 5;
   public static final int S_END = 6;
   public static final int S_IN_ERROR = -1;
   private LinkedList linkedList3;
   private Yylex lexer = new Yylex((Reader)null);
   private Yytoken token = null;
   private int status = 0;

   private final int peekStatus(LinkedList linkedList) {
      if (linkedList.size() == 0) {
         return -1;
      }

      Integer first = (Integer)linkedList.getFirst();
      return first;
   }

   public void reset() {
      this.token = null;
      this.status = 0;
      this.linkedList3 = null;
   }

   public void reset(Reader reader) {
      this.lexer.yyreset(reader);
      this.reset();
   }

   public int getPosition() {
      return this.lexer.getIntValue8();
   }

   public Object computeObj(String text) {
      return this.computeObj(text, (ContainerFactory)null);
   }

   public Object computeObj(String text, ContainerFactory connectionOpener) {
      try {
         StringReader stringReader = new StringReader(text);

         try {
            return this.computeObj(stringReader, connectionOpener);
         } catch (Throwable e2) {
            if (!(e2 instanceof IOException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            IOException ioException = (IOException) e2;
            throw new JsonParseException(-1, 2, ioException);
         }
      } catch (JsonParseException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public Object computeObj(Reader reader) {
      return this.computeObj(reader, (ContainerFactory)null);
   }

   public Object computeObj(Reader reader, ContainerFactory connectionOpener) {
      try {
         this.reset(reader);
         LinkedList linkedList = new LinkedList();
         LinkedList linkedList2 = new LinkedList();

         try {
            do {
               this.perform2();
               label64:
               switch (this.status) {
                  case -1:
                     throw new JsonParseException(this.getPosition(), 1, this.token);
                  case 0:
                     switch (this.token.type) {
                        case 0:
                           this.status = 1;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(this.token.value);
                           break label64;
                        case 1:
                           this.status = 2;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(this.computeMap(connectionOpener));
                           break label64;
                        case 2:
                        default:
                           this.status = -1;
                           break label64;
                        case 3:
                           this.status = 3;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(this.computeList(connectionOpener));
                           break label64;
                     }
                  case 1:
                     if (this.token.type == -1) {
                        return linkedList2.removeFirst();
                     }

                     throw new JsonParseException(this.getPosition(), 1, this.token);
                  case 2:
                     switch (this.token.type) {
                        case 0:
                           if (this.token.value instanceof String) {
                              String string = (String)this.token.value;
                              linkedList2.addFirst(string);
                              this.status = 4;
                              linkedList.addFirst(new Integer(this.status));
                           } else {
                              this.status = -1;
                           }
                           break label64;
                        case 2:
                           if (linkedList2.size() > 1) {
                              linkedList.removeFirst();
                              linkedList2.removeFirst();
                              this.status = this.peekStatus(linkedList);
                           } else {
                              this.status = 1;
                           }
                        case 5:
                           break label64;
                        default:
                           this.status = -1;
                           break label64;
                     }
                  case 3:
                     switch (this.token.type) {
                        case 0:
                           List first = (List)linkedList2.getFirst();
                           first.add(this.token.value);
                           break label64;
                        case 1:
                           List first2 = (List)linkedList2.getFirst();
                           Map map = this.computeMap(connectionOpener);
                           first2.add(map);
                           this.status = 2;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(map);
                           break label64;
                        case 2:
                        default:
                           this.status = -1;
                           break label64;
                        case 3:
                           List first3 = (List)linkedList2.getFirst();
                           List list = this.computeList(connectionOpener);
                           first3.add(list);
                           this.status = 3;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(list);
                           break label64;
                        case 4:
                           if (linkedList2.size() > 1) {
                              linkedList.removeFirst();
                              linkedList2.removeFirst();
                              this.status = this.peekStatus(linkedList);
                           } else {
                              this.status = 1;
                           }
                        case 5:
                           break label64;
                     }
                  case 4:
                     switch (this.token.type) {
                        case 0:
                           linkedList.removeFirst();
                           String removeFirst2 = (String)linkedList2.removeFirst();
                           Map first4 = (Map)linkedList2.getFirst();
                           first4.put(removeFirst2, this.token.value);
                           this.status = this.peekStatus(linkedList);
                           break;
                        case 1:
                           linkedList.removeFirst();
                           String removeFirst3 = (String)linkedList2.removeFirst();
                           Map first5 = (Map)linkedList2.getFirst();
                           Map map2 = this.computeMap(connectionOpener);
                           first5.put(removeFirst3, map2);
                           this.status = 2;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(map2);
                           break;
                        case 2:
                        case 4:
                        case 5:
                        default:
                           this.status = -1;
                           break;
                        case 3:
                           linkedList.removeFirst();
                           String removeFirst4 = (String)linkedList2.removeFirst();
                           Map first6 = (Map)linkedList2.getFirst();
                           List list2 = this.computeList(connectionOpener);
                           first6.put(removeFirst4, list2);
                           this.status = 3;
                           linkedList.addFirst(new Integer(this.status));
                           linkedList2.addFirst(list2);
                        case 6:
                     }
               }

               if (this.status == -1) {
                  throw new JsonParseException(this.getPosition(), 1, this.token);
               }
            } while (this.token.type != -1);
         } catch (Throwable e2) {
            if (!(e2 instanceof IOException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            }
            IOException ioException = (IOException) e2;
            throw ioException;
         }

         throw new JsonParseException(this.getPosition(), 1, this.token);
      } catch (IOException | JsonParseException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void perform2() {
      this.token = this.lexer.yylex();
      if (this.token == null) {
         this.token = new Yytoken(-1, null);
      }
   }

   private final Map computeMap(ContainerFactory connectionOpener) {
      if (connectionOpener == null) {
         return new JSONObject();
      }

      Map map = connectionOpener.createObjectContainer();
      return map == null ? new JSONObject() : map;
   }

   private final List computeList(ContainerFactory connectionOpener) {
      if (connectionOpener == null) {
         return new JSONArray();
      }

      List list = connectionOpener.createArrayContainer();
      return list == null ? new JSONArray() : list;
   }
}
