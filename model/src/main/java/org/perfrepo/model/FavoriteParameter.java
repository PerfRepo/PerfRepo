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
package org.perfrepo.model;

import org.perfrepo.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents user's favorite parameter for a test.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "favorite_parameter")
@NamedQueries({
    @NamedQuery(name = FavoriteParameter.FIND_BY_TEST_AND_PARAM_NAME, query = "SELECT fp FROM FavoriteParameter fp JOIN fp.test test JOIN fp.user user WHERE test.id = :testId AND user.id = :userId AND fp.parameterName = :paramName")
})
public class FavoriteParameter implements Entity<FavoriteParameter> {

   private static final long serialVersionUID = 2290056642668445219L;

   public static final String FIND_BY_TEST_AND_PARAM_NAME = "findByTestAndParamName";

   @Id
   @SequenceGenerator(name = "FAVORITE_PARAMETER_ID_GENERATOR", sequenceName = "FAVORITE_PARAMETER_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAVORITE_PARAMETER_ID_GENERATOR")
   private Long id;

   @Column(name = "label")
   @NotNull
   @Size(max = 2047)
   private String label;

   @Column(name = "parameter_name")
   @NotNull
   @Size(max = 2047)
   private String parameterName;

   @ManyToOne(optional = false)
   @JoinColumn(name = "user_id", referencedColumnName = "id")
   @NotNull
   private User user;

   @ManyToOne(optional = false)
   @JoinColumn(name = "test_id", referencedColumnName = "id")
   @NotNull
   private Test test;

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getParameterName() {
      return parameterName;
   }

   public void setParameterName(String parameterName) {
      this.parameterName = parameterName;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public Test getTest() {
      return test;
   }

   public void setTest(Test test) {
      this.test = test;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof FavoriteParameter)) return false;

      FavoriteParameter that = (FavoriteParameter) o;

      return getParameterName() != null ? getParameterName().equals(that.getParameterName()) : that.getParameterName() == null;
   }

   @Override
   public int hashCode() {
      return getParameterName() != null ? getParameterName().hashCode() : 0;
   }

   @Override
   public String toString() {
      return "FavoriteParameter{"
          + "id=" + id
          + ", label='" + label + '\''
          + ", parameterName='" + parameterName + '\''
          + '}';
   }
}
