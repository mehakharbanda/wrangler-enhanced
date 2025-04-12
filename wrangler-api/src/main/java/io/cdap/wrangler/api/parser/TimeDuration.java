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

 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 /**
  * Token representing a time duration value like "500ms", "2s", "1.5m", etc.
  */
 public class TimeDuration implements Token {
 
    private static final Pattern TIME_PATTERN = Pattern.compile(
        "(\\d+(?:\\.\\d+)?)(ms|s|m|h|d)?",
        Pattern.CASE_INSENSITIVE
      );      
   private final long millis;
   private final String raw;
 
   public TimeDuration(String value) {
     this.raw = value;
     this.millis = parseMillis(value);
   }
 
   private long parseMillis(String value) {
     Matcher matcher = TIME_PATTERN.matcher(value.trim());
     if (!matcher.matches()) {
       throw new IllegalArgumentException("Invalid time duration: " + value);
     }
 
     double number = Double.parseDouble(matcher.group(1));
     String unit = matcher.group(2) != null ? matcher.group(2).toLowerCase() : "ms";
 
     switch (unit) {
       case "ms":
         return (long) (number);
       case "s":
         return (long) (number * 1000);
       case "m":
         return (long) (number * 60 * 1000);
       case "h":
         return (long) (number * 60 * 60 * 1000);
       case "d":
         return (long) (number * 24 * 60 * 60 * 1000);
       default:
         throw new IllegalArgumentException("Unknown time unit: " + unit);
     }
   }
 
   public long getMillis() {
     return millis;
   }
 
   @Override
   public Object value() {
     return millis;
   }
 
   @Override
   public TokenType type() {
     return TokenType.TIME_DURATION;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(millis);
   }
 
   @Override
   public String toString() {
     return String.format("%d ms", millis);
   }
 }
