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
package org.apache.beam.runners.spark.stateful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.StateNamespace;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.runners.core.StateTag.StateBinder;
import org.apache.beam.runners.spark.coders.CoderHelpers;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.InstantCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.MapCoder;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.state.CombiningState;
import org.apache.beam.sdk.state.MapState;
import org.apache.beam.sdk.state.MultimapState;
import org.apache.beam.sdk.state.OrderedListState;
import org.apache.beam.sdk.state.ReadableState;
import org.apache.beam.sdk.state.ReadableStates;
import org.apache.beam.sdk.state.SetState;
import org.apache.beam.sdk.state.State;
import org.apache.beam.sdk.state.StateContext;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.state.WatermarkHoldState;
import org.apache.beam.sdk.transforms.Combine.CombineFn;
import org.apache.beam.sdk.transforms.CombineWithContext.CombineFnWithContext;
import org.apache.beam.sdk.transforms.windowing.TimestampCombiner;
import org.apache.beam.sdk.util.CombineFnUtil;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.collect.HashBasedTable;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.collect.Table;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joda.time.Instant;

/** An implementation of {@link StateInternals} for the SparkRunner. */
@SuppressWarnings({
  "nullness" // TODO(https://github.com/apache/beam/issues/20497)
})
class SparkStateInternals<K> implements StateInternals {

  private final K key;
  // Serializable state for internals (namespace to state tag to coded value).
  private final Table<String, String, byte[]> stateTable;

  private SparkStateInternals(K key) {
    this.key = key;
    this.stateTable = HashBasedTable.create();
  }

  private SparkStateInternals(K key, Table<String, String, byte[]> stateTable) {
    this.key = key;
    this.stateTable = stateTable;
  }

  static <K> SparkStateInternals<K> forKey(K key) {
    return new SparkStateInternals<>(key);
  }

  static <K> SparkStateInternals<K> forKeyAndState(
      K key, Table<String, String, byte[]> stateTable) {
    return new SparkStateInternals<>(key, stateTable);
  }

  public Table<String, String, byte[]> getState() {
    return stateTable;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public <T extends State> T state(
      StateNamespace namespace, StateTag<T> address, StateContext<?> c) {
    return address.bind(new SparkStateBinder(namespace, c));
  }

  private class SparkStateBinder implements StateBinder {
    private final StateNamespace namespace;
    private final StateContext<?> c;

    private SparkStateBinder(StateNamespace namespace, StateContext<?> c) {
      this.namespace = namespace;
      this.c = c;
    }

    @Override
    public <T> ValueState<T> bindValue(StateTag<ValueState<T>> address, Coder<T> coder) {
      return new SparkValueState<>(namespace, address, coder);
    }

    @Override
    public <T> BagState<T> bindBag(StateTag<BagState<T>> address, Coder<T> elemCoder) {
      return new SparkBagState<>(namespace, address, elemCoder);
    }

    @Override
    public <T> SetState<T> bindSet(StateTag<SetState<T>> spec, Coder<T> elemCoder) {
      throw new UnsupportedOperationException(
          String.format("%s is not supported", SetState.class.getSimpleName()));
    }

    @Override
    public <KeyT, ValueT> MapState<KeyT, ValueT> bindMap(
        StateTag<MapState<KeyT, ValueT>> address,
        Coder<KeyT> mapKeyCoder,
        Coder<ValueT> mapValueCoder) {
      return new SparkMapState<>(namespace, address, MapCoder.of(mapKeyCoder, mapValueCoder));
    }

    @Override
    public <KeyT, ValueT> MultimapState<KeyT, ValueT> bindMultimap(
        StateTag<MultimapState<KeyT, ValueT>> spec,
        Coder<KeyT> keyCoder,
        Coder<ValueT> valueCoder) {
      throw new UnsupportedOperationException(
          String.format("%s is not supported", MultimapState.class.getSimpleName()));
    }

    @Override
    public <T> OrderedListState<T> bindOrderedList(
        StateTag<OrderedListState<T>> spec, Coder<T> elemCoder) {
      throw new UnsupportedOperationException(
          String.format("%s is not supported", OrderedListState.class.getSimpleName()));
    }

    @Override
    public <InputT, AccumT, OutputT> CombiningState<InputT, AccumT, OutputT> bindCombiningValue(
        StateTag<CombiningState<InputT, AccumT, OutputT>> address,
        Coder<AccumT> accumCoder,
        CombineFn<InputT, AccumT, OutputT> combineFn) {
      return new SparkCombiningState<>(namespace, address, accumCoder, combineFn);
    }

    @Override
    public <InputT, AccumT, OutputT>
        CombiningState<InputT, AccumT, OutputT> bindCombiningValueWithContext(
            StateTag<CombiningState<InputT, AccumT, OutputT>> address,
            Coder<AccumT> accumCoder,
            CombineFnWithContext<InputT, AccumT, OutputT> combineFn) {
      return new SparkCombiningState<>(
          namespace, address, accumCoder, CombineFnUtil.bindContext(combineFn, c));
    }

    @Override
    public WatermarkHoldState bindWatermark(
        StateTag<WatermarkHoldState> address, TimestampCombiner timestampCombiner) {
      return new SparkWatermarkHoldState(namespace, address, timestampCombiner);
    }
  }

  private class AbstractState<T> {
    final StateNamespace namespace;
    final StateTag<? extends State> address;
    final Coder<T> coder;

    private AbstractState(
        StateNamespace namespace, StateTag<? extends State> address, Coder<T> coder) {
      this.namespace = namespace;
      this.address = address;
      this.coder = coder;
    }

    T readValue() {
      byte[] buf = stateTable.get(namespace.stringKey(), address.getId());
      if (buf != null) {
        return CoderHelpers.fromByteArray(buf, coder);
      }
      return null;
    }

    void writeValue(T input) {
      stateTable.put(
          namespace.stringKey(), address.getId(), CoderHelpers.toByteArray(input, coder));
    }

    public void clear() {
      stateTable.remove(namespace.stringKey(), address.getId());
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      @SuppressWarnings("unchecked")
      AbstractState<?> that = (AbstractState<?>) o;
      return namespace.equals(that.namespace) && address.equals(that.address);
    }

    @Override
    public int hashCode() {
      int result = namespace.hashCode();
      result = 31 * result + address.hashCode();
      return result;
    }
  }

  private class SparkValueState<T> extends AbstractState<T> implements ValueState<T> {

    private SparkValueState(
        StateNamespace namespace, StateTag<ValueState<T>> address, Coder<T> coder) {
      super(namespace, address, coder);
    }

    @Override
    public SparkValueState<T> readLater() {
      return this;
    }

    @Override
    public T read() {
      return readValue();
    }

    @Override
    public void write(T input) {
      writeValue(input);
    }
  }

  private class SparkWatermarkHoldState extends AbstractState<Instant>
      implements WatermarkHoldState {

    private final TimestampCombiner timestampCombiner;

    SparkWatermarkHoldState(
        StateNamespace namespace,
        StateTag<WatermarkHoldState> address,
        TimestampCombiner timestampCombiner) {
      super(namespace, address, InstantCoder.of());
      this.timestampCombiner = timestampCombiner;
    }

    @Override
    public SparkWatermarkHoldState readLater() {
      return this;
    }

    @Override
    public Instant read() {
      return readValue();
    }

    @Override
    public void add(Instant outputTime) {
      Instant combined = read();
      combined =
          (combined == null) ? outputTime : getTimestampCombiner().combine(combined, outputTime);
      writeValue(combined);
    }

    @Override
    public ReadableState<Boolean> isEmpty() {
      return new ReadableState<Boolean>() {
        @Override
        public ReadableState<Boolean> readLater() {
          return this;
        }

        @Override
        public Boolean read() {
          return stateTable.get(namespace.stringKey(), address.getId()) == null;
        }
      };
    }

    @Override
    public TimestampCombiner getTimestampCombiner() {
      return timestampCombiner;
    }
  }

  @SuppressWarnings("TypeParameterShadowing")
  private class SparkCombiningState<K, InputT, AccumT, OutputT> extends AbstractState<AccumT>
      implements CombiningState<InputT, AccumT, OutputT> {

    private final CombineFn<InputT, AccumT, OutputT> combineFn;

    private SparkCombiningState(
        StateNamespace namespace,
        StateTag<CombiningState<InputT, AccumT, OutputT>> address,
        Coder<AccumT> coder,
        CombineFn<InputT, AccumT, OutputT> combineFn) {
      super(namespace, address, coder);
      this.combineFn = combineFn;
    }

    @Override
    public SparkCombiningState<K, InputT, AccumT, OutputT> readLater() {
      return this;
    }

    @Override
    public OutputT read() {
      return combineFn.extractOutput(getAccum());
    }

    @Override
    public void add(InputT input) {
      AccumT accum = combineFn.addInput(getAccum(), input);
      writeValue(accum);
    }

    @Override
    public AccumT getAccum() {
      AccumT accum = readValue();
      if (accum == null) {
        accum = combineFn.createAccumulator();
      }
      return accum;
    }

    @Override
    public ReadableState<Boolean> isEmpty() {
      return new ReadableState<Boolean>() {
        @Override
        public ReadableState<Boolean> readLater() {
          return this;
        }

        @Override
        public Boolean read() {
          return stateTable.get(namespace.stringKey(), address.getId()) == null;
        }
      };
    }

    @Override
    public void addAccum(AccumT accum) {
      accum = combineFn.mergeAccumulators(Arrays.asList(getAccum(), accum));
      writeValue(accum);
    }

    @Override
    public AccumT mergeAccumulators(Iterable<AccumT> accumulators) {
      return combineFn.mergeAccumulators(accumulators);
    }
  }

  private final class SparkMapState<MapKeyT, MapValueT>
      extends AbstractState<Map<MapKeyT, MapValueT>> implements MapState<MapKeyT, MapValueT> {

    private SparkMapState(
        StateNamespace namespace,
        StateTag<? extends State> address,
        Coder<Map<MapKeyT, MapValueT>> coder) {
      super(namespace, address, coder);
    }

    @Override
    public ReadableState<MapValueT> get(MapKeyT key) {
      return getOrDefault(key, null);
    }

    @Override
    public ReadableState<MapValueT> getOrDefault(MapKeyT key, @Nullable MapValueT defaultValue) {
      return new ReadableState<MapValueT>() {
        @Override
        public MapValueT read() {
          Map<MapKeyT, MapValueT> sparkMapState = readValue();
          if (sparkMapState == null) {
            return defaultValue;
          }
          return sparkMapState.getOrDefault(key, defaultValue);
        }

        @Override
        public ReadableState<MapValueT> readLater() {
          return this;
        }
      };
    }

    @Override
    public void put(MapKeyT key, MapValueT value) {
      Map<MapKeyT, MapValueT> sparkMapState = readValue();
      if (sparkMapState == null) {
        sparkMapState = new HashMap<>();
      }
      sparkMapState.put(key, value);
      writeValue(sparkMapState);
    }

    @Override
    public ReadableState<MapValueT> computeIfAbsent(
        MapKeyT key, Function<? super MapKeyT, ? extends MapValueT> mappingFunction) {
      Map<MapKeyT, MapValueT> sparkMapState = readValue();
      MapValueT current = sparkMapState.get(key);
      if (current == null) {
        put(key, mappingFunction.apply(key));
      }
      return ReadableStates.immediate(current);
    }

    @Override
    public void remove(MapKeyT key) {
      Map<MapKeyT, MapValueT> sparkMapState = readValue();
      sparkMapState.remove(key);
      writeValue(sparkMapState);
    }

    @Override
    public ReadableState<Iterable<MapKeyT>> keys() {
      return new ReadableState<Iterable<MapKeyT>>() {
        @Override
        public Iterable<MapKeyT> read() {
          Map<MapKeyT, MapValueT> sparkMapState = readValue();
          if (sparkMapState == null) {
            return Collections.emptyList();
          }
          return sparkMapState.keySet();
        }

        @Override
        public ReadableState<Iterable<MapKeyT>> readLater() {
          return this;
        }
      };
    }

    @Override
    public ReadableState<Iterable<MapValueT>> values() {
      return new ReadableState<Iterable<MapValueT>>() {
        @Override
        public Iterable<MapValueT> read() {
          Map<MapKeyT, MapValueT> sparkMapState = readValue();
          if (sparkMapState == null) {
            return Collections.emptyList();
          }
          Iterable<MapValueT> result = readValue().values();
          return result != null ? ImmutableList.copyOf(result) : Collections.emptyList();
        }

        @Override
        public ReadableState<Iterable<MapValueT>> readLater() {
          return this;
        }
      };
    }

    @Override
    public ReadableState<Iterable<Map.Entry<MapKeyT, MapValueT>>> entries() {
      return new ReadableState<Iterable<Map.Entry<MapKeyT, MapValueT>>>() {
        @Override
        public Iterable<Map.Entry<MapKeyT, MapValueT>> read() {
          Map<MapKeyT, MapValueT> sparkMapState = readValue();
          if (sparkMapState == null) {
            return Collections.emptyList();
          }
          return sparkMapState.entrySet();
        }

        @Override
        public ReadableState<Iterable<Map.Entry<MapKeyT, MapValueT>>> readLater() {
          return this;
        }
      };
    }

    @Override
    public ReadableState<Boolean> isEmpty() {
      return new ReadableState<Boolean>() {
        @Override
        public Boolean read() {
          return stateTable.get(namespace.stringKey(), address.getId()) == null;
        }

        @Override
        public ReadableState<Boolean> readLater() {
          return this;
        }
      };
    }
  }

  private final class SparkBagState<T> extends AbstractState<List<T>> implements BagState<T> {
    private SparkBagState(StateNamespace namespace, StateTag<BagState<T>> address, Coder<T> coder) {
      super(namespace, address, ListCoder.of(coder));
    }

    @Override
    public SparkBagState<T> readLater() {
      return this;
    }

    @Override
    public List<T> read() {
      List<T> value = super.readValue();
      if (value == null) {
        value = new ArrayList<>();
      }
      return value;
    }

    @Override
    public void add(T input) {
      List<T> value = read();
      value.add(input);
      writeValue(value);
    }

    @Override
    public ReadableState<Boolean> isEmpty() {
      return new ReadableState<Boolean>() {
        @Override
        public ReadableState<Boolean> readLater() {
          return this;
        }

        @Override
        public Boolean read() {
          return stateTable.get(namespace.stringKey(), address.getId()) == null;
        }
      };
    }
  }
}
