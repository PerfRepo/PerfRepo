/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.enums;

/**
 * AccessLevel is a attribute of permission. It decides to who the permission is applied.
 * <p>
 * If it's USER, the permission is applied to the user specified in @link{Permission.userId}.
 * If it's GROUP, the permission is applied to the group specified in @link(Permission.groupId}.
 * If it's PUBLIC, the permission is applied to everybody, therefore neither @link{Permission.userId}
 * nor @link{Permission.groupId} is set.
 */
public enum AccessLevel {

   /**
    * User Level
    */
   USER,

   /**
    * Group Level
    */
   GROUP,

   /**
    * Public Level
    */
   PUBLIC

}
