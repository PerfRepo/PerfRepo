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
package org.perfrepo.web.session;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds ids of test executions selected for comparison.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@SessionScoped
public class TEComparatorSession implements Serializable {

   private static final long serialVersionUID = 1L;

   private Set<Long> execIds = new HashSet<Long>();

   public void add(Long te) {
      execIds.add(te);
   }

   public void remove(Long id) {
      execIds.remove(id);
   }

   public Collection<Long> getExecIds() {
      return execIds;
   }

   public boolean isAnyToCompare() {
      return execIds != null && execIds.size() > 0;
   }

   public void clear() {
      execIds.clear();
   }
}