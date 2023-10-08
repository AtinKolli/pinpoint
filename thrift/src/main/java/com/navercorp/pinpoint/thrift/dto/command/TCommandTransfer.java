/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.navercorp.pinpoint.thrift.dto.command;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCommandTransfer implements org.apache.thrift.TBase<TCommandTransfer, TCommandTransfer._Fields>, java.io.Serializable, Cloneable, Comparable<TCommandTransfer> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TCommandTransfer");

  private static final org.apache.thrift.protocol.TField APPLICATION_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("applicationName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField AGENT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("agentId", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField START_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("startTime", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField PAYLOAD_FIELD_DESC = new org.apache.thrift.protocol.TField("payload", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TCommandTransferStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TCommandTransferTupleSchemeFactory());
  }

  private String applicationName; // required
  private String agentId; // required
  private long startTime; // optional
  private ByteBuffer payload; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    APPLICATION_NAME((short)1, "applicationName"),
    AGENT_ID((short)2, "agentId"),
    START_TIME((short)3, "startTime"),
    PAYLOAD((short)4, "payload");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // APPLICATION_NAME
          return APPLICATION_NAME;
        case 2: // AGENT_ID
          return AGENT_ID;
        case 3: // START_TIME
          return START_TIME;
        case 4: // PAYLOAD
          return PAYLOAD;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __STARTTIME_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.START_TIME};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.APPLICATION_NAME, new org.apache.thrift.meta_data.FieldMetaData("applicationName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AGENT_ID, new org.apache.thrift.meta_data.FieldMetaData("agentId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.START_TIME, new org.apache.thrift.meta_data.FieldMetaData("startTime", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.PAYLOAD, new org.apache.thrift.meta_data.FieldMetaData("payload", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TCommandTransfer.class, metaDataMap);
  }

  public TCommandTransfer() {
  }

  public TCommandTransfer(
    String applicationName,
    String agentId,
    ByteBuffer payload)
  {
    this();
    this.applicationName = applicationName;
    this.agentId = agentId;
    this.payload = payload;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TCommandTransfer(TCommandTransfer other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetApplicationName()) {
      this.applicationName = other.applicationName;
    }
    if (other.isSetAgentId()) {
      this.agentId = other.agentId;
    }
    this.startTime = other.startTime;
    if (other.isSetPayload()) {
      this.payload = org.apache.thrift.TBaseHelper.copyBinary(other.payload);
;
    }
  }

  public TCommandTransfer deepCopy() {
    return new TCommandTransfer(this);
  }

  @Override
  public void clear() {
    this.applicationName = null;
    this.agentId = null;
    setStartTimeIsSet(false);
    this.startTime = 0;
    this.payload = null;
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public void unsetApplicationName() {
    this.applicationName = null;
  }

  /** Returns true if field applicationName is set (has been assigned a value) and false otherwise */
  public boolean isSetApplicationName() {
    return this.applicationName != null;
  }

  public void setApplicationNameIsSet(boolean value) {
    if (!value) {
      this.applicationName = null;
    }
  }

  public String getAgentId() {
    return this.agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public void unsetAgentId() {
    this.agentId = null;
  }

  /** Returns true if field agentId is set (has been assigned a value) and false otherwise */
  public boolean isSetAgentId() {
    return this.agentId != null;
  }

  public void setAgentIdIsSet(boolean value) {
    if (!value) {
      this.agentId = null;
    }
  }

  public long getStartTime() {
    return this.startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
    setStartTimeIsSet(true);
  }

  public void unsetStartTime() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STARTTIME_ISSET_ID);
  }

  /** Returns true if field startTime is set (has been assigned a value) and false otherwise */
  public boolean isSetStartTime() {
    return EncodingUtils.testBit(__isset_bitfield, __STARTTIME_ISSET_ID);
  }

  public void setStartTimeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STARTTIME_ISSET_ID, value);
  }

  public byte[] getPayload() {
    setPayload(org.apache.thrift.TBaseHelper.rightSize(payload));
    return payload == null ? null : payload.array();
  }

  public ByteBuffer bufferForPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
    setPayload(payload == null ? (ByteBuffer)null : ByteBuffer.wrap(payload));
  }

  public void setPayload(ByteBuffer payload) {
    this.payload = payload;
  }

  public void unsetPayload() {
    this.payload = null;
  }

  /** Returns true if field payload is set (has been assigned a value) and false otherwise */
  public boolean isSetPayload() {
    return this.payload != null;
  }

  public void setPayloadIsSet(boolean value) {
    if (!value) {
      this.payload = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case APPLICATION_NAME:
      if (value == null) {
        unsetApplicationName();
      } else {
        setApplicationName((String)value);
      }
      break;

    case AGENT_ID:
      if (value == null) {
        unsetAgentId();
      } else {
        setAgentId((String)value);
      }
      break;

    case START_TIME:
      if (value == null) {
        unsetStartTime();
      } else {
        setStartTime((Long)value);
      }
      break;

    case PAYLOAD:
      if (value == null) {
        unsetPayload();
      } else {
        setPayload((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case APPLICATION_NAME:
      return getApplicationName();

    case AGENT_ID:
      return getAgentId();

    case START_TIME:
      return Long.valueOf(getStartTime());

    case PAYLOAD:
      return getPayload();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case APPLICATION_NAME:
      return isSetApplicationName();
    case AGENT_ID:
      return isSetAgentId();
    case START_TIME:
      return isSetStartTime();
    case PAYLOAD:
      return isSetPayload();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TCommandTransfer)
      return this.equals((TCommandTransfer)that);
    return false;
  }

  public boolean equals(TCommandTransfer that) {
    if (that == null)
      return false;

    boolean this_present_applicationName = true && this.isSetApplicationName();
    boolean that_present_applicationName = true && that.isSetApplicationName();
    if (this_present_applicationName || that_present_applicationName) {
      if (!(this_present_applicationName && that_present_applicationName))
        return false;
      if (!this.applicationName.equals(that.applicationName))
        return false;
    }

    boolean this_present_agentId = true && this.isSetAgentId();
    boolean that_present_agentId = true && that.isSetAgentId();
    if (this_present_agentId || that_present_agentId) {
      if (!(this_present_agentId && that_present_agentId))
        return false;
      if (!this.agentId.equals(that.agentId))
        return false;
    }

    boolean this_present_startTime = true && this.isSetStartTime();
    boolean that_present_startTime = true && that.isSetStartTime();
    if (this_present_startTime || that_present_startTime) {
      if (!(this_present_startTime && that_present_startTime))
        return false;
      if (this.startTime != that.startTime)
        return false;
    }

    boolean this_present_payload = true && this.isSetPayload();
    boolean that_present_payload = true && that.isSetPayload();
    if (this_present_payload || that_present_payload) {
      if (!(this_present_payload && that_present_payload))
        return false;
      if (!this.payload.equals(that.payload))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TCommandTransfer other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetApplicationName()).compareTo(other.isSetApplicationName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetApplicationName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.applicationName, other.applicationName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAgentId()).compareTo(other.isSetAgentId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAgentId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.agentId, other.agentId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStartTime()).compareTo(other.isSetStartTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStartTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.startTime, other.startTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPayload()).compareTo(other.isSetPayload());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPayload()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.payload, other.payload);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TCommandTransfer(");
    boolean first = true;

    sb.append("applicationName:");
    if (this.applicationName == null) {
      sb.append("null");
    } else {
      sb.append(this.applicationName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("agentId:");
    if (this.agentId == null) {
      sb.append("null");
    } else {
      sb.append(this.agentId);
    }
    first = false;
    if (isSetStartTime()) {
      if (!first) sb.append(", ");
      sb.append("startTime:");
      sb.append(this.startTime);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("payload:");
    if (this.payload == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.payload, sb);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TCommandTransferStandardSchemeFactory implements SchemeFactory {
    public TCommandTransferStandardScheme getScheme() {
      return new TCommandTransferStandardScheme();
    }
  }

  private static class TCommandTransferStandardScheme extends StandardScheme<TCommandTransfer> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TCommandTransfer struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // APPLICATION_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.applicationName = iprot.readString();
              struct.setApplicationNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // AGENT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.agentId = iprot.readString();
              struct.setAgentIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // START_TIME
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.startTime = iprot.readI64();
              struct.setStartTimeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // PAYLOAD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.payload = iprot.readBinary();
              struct.setPayloadIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TCommandTransfer struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.applicationName != null) {
        oprot.writeFieldBegin(APPLICATION_NAME_FIELD_DESC);
        oprot.writeString(struct.applicationName);
        oprot.writeFieldEnd();
      }
      if (struct.agentId != null) {
        oprot.writeFieldBegin(AGENT_ID_FIELD_DESC);
        oprot.writeString(struct.agentId);
        oprot.writeFieldEnd();
      }
      if (struct.isSetStartTime()) {
        oprot.writeFieldBegin(START_TIME_FIELD_DESC);
        oprot.writeI64(struct.startTime);
        oprot.writeFieldEnd();
      }
      if (struct.payload != null) {
        oprot.writeFieldBegin(PAYLOAD_FIELD_DESC);
        oprot.writeBinary(struct.payload);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TCommandTransferTupleSchemeFactory implements SchemeFactory {
    public TCommandTransferTupleScheme getScheme() {
      return new TCommandTransferTupleScheme();
    }
  }

  private static class TCommandTransferTupleScheme extends TupleScheme<TCommandTransfer> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TCommandTransfer struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetApplicationName()) {
        optionals.set(0);
      }
      if (struct.isSetAgentId()) {
        optionals.set(1);
      }
      if (struct.isSetStartTime()) {
        optionals.set(2);
      }
      if (struct.isSetPayload()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetApplicationName()) {
        oprot.writeString(struct.applicationName);
      }
      if (struct.isSetAgentId()) {
        oprot.writeString(struct.agentId);
      }
      if (struct.isSetStartTime()) {
        oprot.writeI64(struct.startTime);
      }
      if (struct.isSetPayload()) {
        oprot.writeBinary(struct.payload);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TCommandTransfer struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.applicationName = iprot.readString();
        struct.setApplicationNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.agentId = iprot.readString();
        struct.setAgentIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.startTime = iprot.readI64();
        struct.setStartTimeIsSet(true);
      }
      if (incoming.get(3)) {
        struct.payload = iprot.readBinary();
        struct.setPayloadIsSet(true);
      }
    }
  }

}

