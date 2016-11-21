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
package org.perfrepo.model.report;

import org.perfrepo.model.Entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@javax.persistence.Entity
@Table(name = "report_property")
@XmlRootElement(name = "reportProperty")
public class ReportProperty implements Entity<ReportProperty>, Comparable<ReportProperty> {

   private static final long serialVersionUID = -2862333826616822888L;

   @Id
   @SequenceGenerator(name = "REPORT_PROPERTY_ID_GENERATOR", sequenceName = "REPORT_PROPERTY_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_PROPERTY_ID_GENERATOR")
   private Long id;

   @Column(name = "name")
   @NotNull
   @Size(max = 2047)
   private String name;

   @Column(name = "value")
   @NotNull
   @Size(max = 8192)
   private String value;

   @ManyToOne(optional = false)
   @JoinColumn(name = "report_id", referencedColumnName = "id")
   private Report report;

   @XmlAttribute(name = "id")
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   @XmlAttribute(name = "name")
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlAttribute(name = "value")
   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @XmlTransient
   public Report getReport() {
      return report;
   }

   public void setReport(Report report) {
      this.report = report;
   }

   @Override
   public int compareTo(ReportProperty o) {
      return getName().compareTo(o.name);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ReportProperty)) return false;

      ReportProperty that = (ReportProperty) o;

      if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
      if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;
      return getReport() != null ? getReport().equals(that.getReport()) : that.getReport() == null;
   }

   @Override
   public int hashCode() {
      int result = getName() != null ? getName().hashCode() : 0;
      result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
      result = 31 * result + (getReport() != null ? getReport().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "ReportProperty{" +
              "value='" + value + '\'' +
              ", name='" + name + '\'' +
              ", id=" + id +
              '}';
   }
}