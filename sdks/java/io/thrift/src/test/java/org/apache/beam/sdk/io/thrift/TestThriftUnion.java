/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(
    value = "Autogenerated by Thrift Compiler (0.13.0)",
    date = "2020-12-10")
public class TestThriftUnion
    extends org.apache.thrift.TUnion<TestThriftUnion, TestThriftUnion._Fields> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC =
      new org.apache.thrift.protocol.TStruct("TestThriftUnion");
  private static final org.apache.thrift.protocol.TField SNAKE_CASE_NESTED_STRUCT_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "snake_case_nested_struct", org.apache.thrift.protocol.TType.STRUCT, (short) 1);
  private static final org.apache.thrift.protocol.TField CAMEL_CASE_ENUM_FIELD_DESC =
      new org.apache.thrift.protocol.TField(
          "camelCaseEnum", org.apache.thrift.protocol.TType.I32, (short) 2);

  /**
   * The set of fields this struct contains, along with convenience methods for finding and
   * manipulating them.
   */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SNAKE_CASE_NESTED_STRUCT((short) 1, "snake_case_nested_struct"),
    /** @see TestThriftEnum */
    CAMEL_CASE_ENUM((short) 2, "camelCaseEnum");

    private static final java.util.Map<java.lang.String, _Fields> byName =
        new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /** Find the _Fields constant that matches fieldId, or null if its not found. */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch (fieldId) {
        case 1: // SNAKE_CASE_NESTED_STRUCT
          return SNAKE_CASE_NESTED_STRUCT;
        case 2: // CAMEL_CASE_ENUM
          return CAMEL_CASE_ENUM;
        default:
          return null;
      }
    }

    /** Find the _Fields constant that matches fieldId, throwing an exception if it is not found. */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null)
        throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /** Find the _Fields constant that matches name, or null if its not found. */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap =
        new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(
        _Fields.SNAKE_CASE_NESTED_STRUCT,
        new org.apache.thrift.meta_data.FieldMetaData(
            "snake_case_nested_struct",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.StructMetaData(
                org.apache.thrift.protocol.TType.STRUCT, TestThriftInnerStruct.class)));
    tmpMap.put(
        _Fields.CAMEL_CASE_ENUM,
        new org.apache.thrift.meta_data.FieldMetaData(
            "camelCaseEnum",
            org.apache.thrift.TFieldRequirementType.OPTIONAL,
            new org.apache.thrift.meta_data.EnumMetaData(
                org.apache.thrift.protocol.TType.ENUM, TestThriftEnum.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
        TestThriftUnion.class, metaDataMap);
  }

  public TestThriftUnion() {
    super();
  }

  public TestThriftUnion(_Fields setField, java.lang.Object value) {
    super(setField, value);
  }

  public TestThriftUnion(TestThriftUnion other) {
    super(other);
  }

  public TestThriftUnion deepCopy() {
    return new TestThriftUnion(this);
  }

  public static TestThriftUnion snake_case_nested_struct(TestThriftInnerStruct value) {
    TestThriftUnion x = new TestThriftUnion();
    x.setSnake_case_nested_struct(value);
    return x;
  }

  public static TestThriftUnion camelCaseEnum(TestThriftEnum value) {
    TestThriftUnion x = new TestThriftUnion();
    x.setCamelCaseEnum(value);
    return x;
  }

  @Override
  protected void checkType(_Fields setField, java.lang.Object value)
      throws java.lang.ClassCastException {
    switch (setField) {
      case SNAKE_CASE_NESTED_STRUCT:
        if (value instanceof TestThriftInnerStruct) {
          break;
        }
        throw new java.lang.ClassCastException(
            "Was expecting value of type TestThriftInnerStruct for field 'snake_case_nested_struct', but got "
                + value.getClass().getSimpleName());
      case CAMEL_CASE_ENUM:
        if (value instanceof TestThriftEnum) {
          break;
        }
        throw new java.lang.ClassCastException(
            "Was expecting value of type TestThriftEnum for field 'camelCaseEnum', but got "
                + value.getClass().getSimpleName());
      default:
        throw new java.lang.IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected java.lang.Object standardSchemeReadValue(
      org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TField field)
      throws org.apache.thrift.TException {
    _Fields setField = _Fields.findByThriftId(field.id);
    if (setField != null) {
      switch (setField) {
        case SNAKE_CASE_NESTED_STRUCT:
          if (field.type == SNAKE_CASE_NESTED_STRUCT_FIELD_DESC.type) {
            TestThriftInnerStruct snake_case_nested_struct;
            snake_case_nested_struct = new TestThriftInnerStruct();
            snake_case_nested_struct.read(iprot);
            return snake_case_nested_struct;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        case CAMEL_CASE_ENUM:
          if (field.type == CAMEL_CASE_ENUM_FIELD_DESC.type) {
            TestThriftEnum camelCaseEnum;
            camelCaseEnum =
                org.apache.beam.sdk.io.thrift.TestThriftEnum.findByValue(iprot.readI32());
            return camelCaseEnum;
          } else {
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
            return null;
          }
        default:
          throw new java.lang.IllegalStateException(
              "setField wasn't null, but didn't match any of the case statements!");
      }
    } else {
      org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
      return null;
    }
  }

  @Override
  protected void standardSchemeWriteValue(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    switch (setField_) {
      case SNAKE_CASE_NESTED_STRUCT:
        TestThriftInnerStruct snake_case_nested_struct = (TestThriftInnerStruct) value_;
        snake_case_nested_struct.write(oprot);
        return;
      case CAMEL_CASE_ENUM:
        TestThriftEnum camelCaseEnum = (TestThriftEnum) value_;
        oprot.writeI32(camelCaseEnum.getValue());
        return;
      default:
        throw new java.lang.IllegalStateException(
            "Cannot write union with unknown field " + setField_);
    }
  }

  @Override
  protected java.lang.Object tupleSchemeReadValue(
      org.apache.thrift.protocol.TProtocol iprot, short fieldID)
      throws org.apache.thrift.TException {
    _Fields setField = _Fields.findByThriftId(fieldID);
    if (setField != null) {
      switch (setField) {
        case SNAKE_CASE_NESTED_STRUCT:
          TestThriftInnerStruct snake_case_nested_struct;
          snake_case_nested_struct = new TestThriftInnerStruct();
          snake_case_nested_struct.read(iprot);
          return snake_case_nested_struct;
        case CAMEL_CASE_ENUM:
          TestThriftEnum camelCaseEnum;
          camelCaseEnum = org.apache.beam.sdk.io.thrift.TestThriftEnum.findByValue(iprot.readI32());
          return camelCaseEnum;
        default:
          throw new java.lang.IllegalStateException(
              "setField wasn't null, but didn't match any of the case statements!");
      }
    } else {
      throw new org.apache.thrift.protocol.TProtocolException(
          "Couldn't find a field with field id " + fieldID);
    }
  }

  @Override
  protected void tupleSchemeWriteValue(org.apache.thrift.protocol.TProtocol oprot)
      throws org.apache.thrift.TException {
    switch (setField_) {
      case SNAKE_CASE_NESTED_STRUCT:
        TestThriftInnerStruct snake_case_nested_struct = (TestThriftInnerStruct) value_;
        snake_case_nested_struct.write(oprot);
        return;
      case CAMEL_CASE_ENUM:
        TestThriftEnum camelCaseEnum = (TestThriftEnum) value_;
        oprot.writeI32(camelCaseEnum.getValue());
        return;
      default:
        throw new java.lang.IllegalStateException(
            "Cannot write union with unknown field " + setField_);
    }
  }

  @Override
  protected org.apache.thrift.protocol.TField getFieldDesc(_Fields setField) {
    switch (setField) {
      case SNAKE_CASE_NESTED_STRUCT:
        return SNAKE_CASE_NESTED_STRUCT_FIELD_DESC;
      case CAMEL_CASE_ENUM:
        return CAMEL_CASE_ENUM_FIELD_DESC;
      default:
        throw new java.lang.IllegalArgumentException("Unknown field id " + setField);
    }
  }

  @Override
  protected org.apache.thrift.protocol.TStruct getStructDesc() {
    return STRUCT_DESC;
  }

  @Override
  protected _Fields enumForId(short id) {
    return _Fields.findByThriftIdOrThrow(id);
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public TestThriftInnerStruct getSnake_case_nested_struct() {
    if (getSetField() == _Fields.SNAKE_CASE_NESTED_STRUCT) {
      return (TestThriftInnerStruct) getFieldValue();
    } else {
      throw new java.lang.RuntimeException(
          "Cannot get field 'snake_case_nested_struct' because union is currently set to "
              + getFieldDesc(getSetField()).name);
    }
  }

  public void setSnake_case_nested_struct(TestThriftInnerStruct value) {
    if (value == null) throw new java.lang.NullPointerException();
    setField_ = _Fields.SNAKE_CASE_NESTED_STRUCT;
    value_ = value;
  }

  /** @see TestThriftEnum */
  public TestThriftEnum getCamelCaseEnum() {
    if (getSetField() == _Fields.CAMEL_CASE_ENUM) {
      return (TestThriftEnum) getFieldValue();
    } else {
      throw new java.lang.RuntimeException(
          "Cannot get field 'camelCaseEnum' because union is currently set to "
              + getFieldDesc(getSetField()).name);
    }
  }

  /** @see TestThriftEnum */
  public void setCamelCaseEnum(TestThriftEnum value) {
    if (value == null) throw new java.lang.NullPointerException();
    setField_ = _Fields.CAMEL_CASE_ENUM;
    value_ = value;
  }

  public boolean isSetSnake_case_nested_struct() {
    return setField_ == _Fields.SNAKE_CASE_NESTED_STRUCT;
  }

  public boolean isSetCamelCaseEnum() {
    return setField_ == _Fields.CAMEL_CASE_ENUM;
  }

  public boolean equals(java.lang.Object other) {
    if (other instanceof TestThriftUnion) {
      return equals((TestThriftUnion) other);
    } else {
      return false;
    }
  }

  public boolean equals(TestThriftUnion other) {
    return other != null
        && getSetField() == other.getSetField()
        && getFieldValue().equals(other.getFieldValue());
  }

  @Override
  public int compareTo(TestThriftUnion other) {
    int lastComparison =
        org.apache.thrift.TBaseHelper.compareTo(getSetField(), other.getSetField());
    if (lastComparison == 0) {
      return org.apache.thrift.TBaseHelper.compareTo(getFieldValue(), other.getFieldValue());
    }
    return lastComparison;
  }

  @Override
  public int hashCode() {
    java.util.List<java.lang.Object> list = new java.util.ArrayList<java.lang.Object>();
    list.add(this.getClass().getName());
    org.apache.thrift.TFieldIdEnum setField = getSetField();
    if (setField != null) {
      list.add(setField.getThriftFieldId());
      java.lang.Object value = getFieldValue();
      if (value instanceof org.apache.thrift.TEnum) {
        list.add(((org.apache.thrift.TEnum) getFieldValue()).getValue());
      } else {
        list.add(value);
      }
    }
    return list.hashCode();
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(
          new org.apache.thrift.protocol.TCompactProtocol(
              new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(
          new org.apache.thrift.protocol.TCompactProtocol(
              new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }
}
