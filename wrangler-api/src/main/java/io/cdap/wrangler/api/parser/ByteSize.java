/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

 package io.cdap.wrangler.api.parser;
 
 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;

 import java.util.Locale;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 /**
  * Token representing a byte size value like "10KB", "5MB", "1GB", etc.
  */
 public class ByteSize implements Token {
 
   private static final Pattern BYTE_PATTERN =
     Pattern.compile("(\\d+(?:\\.\\d+)?)([kKmMgGtT]?)[bB]?");
   private final long bytes;
   private final String raw;
 
   public ByteSize(String value) {
     this.raw = value;
     this.bytes = parseBytes(value);
   }
 
   private long parseBytes(String value) {
     Matcher matcher = BYTE_PATTERN.matcher(value.trim());
     if (!matcher.matches()) {
       throw new IllegalArgumentException("Invalid byte size: " + value);
     }
     double number = Double.parseDouble(matcher.group(1));
     String unit = matcher.group(2).toUpperCase(Locale.ENGLISH);
 
     switch (unit) {
       case "":
         return (long) number;
       case "K":
         return (long) (number * 1024L);
       case "M":
         return (long) (number * 1024L * 1024L);
       case "G":
         return (long) (number * 1024L * 1024L * 1024L);
       case "T":
         return (long) (number * 1024L * 1024L * 1024L * 1024L);
       default:
         throw new IllegalArgumentException("Unknown byte size unit: " + unit);
     }
   }
 
   public long getBytes() {
     return bytes;
   }
 
   @Override
   public Object value() {
     return bytes;
   }
 
   @Override
   public TokenType type() {
     return TokenType.BYTE_SIZE;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(bytes);
   }
 
   @Override
   public String toString() {
     return String.format("%d bytes", bytes);
   }
 }
