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
package org.perfrepo.web.dao;

import org.perfrepo.model.Tag;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for {@link Tag}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TagDAO extends DAO<Tag, Long> {

   public Tag findByName(String name) {
      List<Tag> tags = getAllByProperty("name", name);
      if (tags.size() > 0) {
         return tags.get(0);
      }
      return null;
   }

   public List<Tag> findByPrefix(String prefix) {
      CriteriaQuery<Tag> criteria = createCriteria();
      Root<Tag> root = criteria.from(Tag.class);
      criteria.select(root);
      CriteriaBuilder cb = criteriaBuilder();
      criteria.where(cb.like(cb.lower(root.<String>get("name")), prefix.toLowerCase() + "%"));
      return query(criteria).getResultList();
   }
}