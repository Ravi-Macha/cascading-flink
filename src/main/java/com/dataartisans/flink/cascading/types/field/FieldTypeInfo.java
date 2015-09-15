/*
 * Copyright 2015 data Artisans GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dataartisans.flink.cascading.types.field;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.AtomicType;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeComparator;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;

import java.util.Comparator;

/**
 * Cascading field type info.
 *
 */
public class FieldTypeInfo extends TypeInformation<Comparable> implements AtomicType {

	private Comparator<Comparable> fieldComparator = null;

	private TypeInformation<Comparable> fieldTypeInfo;

	public FieldTypeInfo() {

	}

	public FieldTypeInfo(Class fieldType) {
		this.fieldTypeInfo = BasicTypeInfo.getInfoFor(fieldType);
	}

	public void setFieldType(Class fieldType) {

		TypeInformation newFieldTypeInfo = BasicTypeInfo.getInfoFor(fieldType);
		if(this.fieldComparator != null) {
			// do not set type info, if we have to use a custom comparator
			return;
		}
		else {
			if(this.fieldTypeInfo != null && newFieldTypeInfo != null) {
				// check if field types are compatible
				if(!this.fieldTypeInfo.equals(newFieldTypeInfo)) {
					throw new RuntimeException("Cannot overwrite a field type by a new field types");
				}
			}
			else if(this.fieldTypeInfo == null) {
				this.fieldTypeInfo = newFieldTypeInfo;
			}
		}
	}

	public void setCustomComparator(Comparator<Comparable> comparator) {
		this.fieldTypeInfo = null;
		this.fieldComparator = comparator;
	}

	@Override
	public boolean isBasicType() {
		return false;
	}

	@Override
	public boolean isTupleType() {
		return false;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public int getTotalFields() {
		return 1;
	}

	@Override
	public Class getTypeClass() {
		if(fieldTypeInfo != null) {
			return this.fieldTypeInfo.getTypeClass();
		}
		else {
			return Comparable.class;
		}
	}

	@Override
	public boolean isKeyType() {
		return true;
	}

	@Override
	public TypeSerializer<Comparable> createSerializer(ExecutionConfig config) {
		if(fieldTypeInfo != null) {
			return this.fieldTypeInfo.createSerializer(config);
		}
		else {
			return new KryoSerializer(Comparable.class, config);
		}
	}

	@Override
	public TypeComparator<Comparable> createComparator(boolean sortOrderAscending, ExecutionConfig config) {

		if(this.fieldTypeInfo != null) {
			TypeComparator<Comparable> fieldComparator = ((AtomicType)fieldTypeInfo).createComparator(sortOrderAscending, config);
			return new WrappingFieldComparator(fieldComparator, sortOrderAscending, Comparable.class);
		}
		else {
			TypeSerializer<Comparable> serializer = this.createSerializer(config);
			if (this.fieldComparator == null) {
				return new FieldComparator(sortOrderAscending, serializer, Comparable.class);
			} else {
				return new CustomFieldComparator(sortOrderAscending, this.fieldComparator, this.createSerializer(config));
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		return  (o instanceof FieldTypeInfo);
	}
}