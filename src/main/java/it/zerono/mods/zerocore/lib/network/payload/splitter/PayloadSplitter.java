package it.zerono.mods.zerocore.lib.network.payload.splitter;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.zerocore.internal.mixin.ServerboundCustomPayloadPacketAccessor;
import it.zerono.mods.zerocore.internal.mixin.VarIntAccessor;
import it.zerono.mods.zerocore.lib.network.payload.AbstractBytesPayload;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PayloadSplitter {

    public interface Splitter<Header, Data> {

        List<List<byte[]>> split(Header header, Stream<@Nullable Data> data);

        default List<List<byte[]>> split(Header header, Collection<@Nullable Data> data) {
            return this.split(header, data.stream());
        }

        default <Payload extends AbstractBytesPayload>
        List<Payload> splitToPayloads(Header header, Stream<@Nullable Data> data,
                                      Function<List<byte[]>, Payload> factory) {
            return bytesToPayloads(factory, this.split(header, data));
        }

        default <Payload extends AbstractBytesPayload>
        List<Payload> splitToPayloads(Header header, Collection<@Nullable Data> data,
                                      Function<List<byte[]>, Payload> factory) {
            return this.splitToPayloads(header, data.stream(), factory);
        }

        Pair<@Nullable Header, List<@Nullable Data>> revert(List<byte[]> bytes);

        default Pair<@Nullable Header, List<@Nullable Data>> revertFromPayload(AbstractBytesPayload payload) {
            return this.revert(payload.getBytes());
        }
    }

    public interface HeadlessSplitter<Data> {

        List<List<byte[]>> split(Stream<@Nullable Data> data);

        default List<List<byte[]>> split(Collection<@Nullable Data> data) {
            return this.split(data.stream());
        }

        default <Payload extends AbstractBytesPayload>
        List<Payload> splitToPayloads(Stream<@Nullable Data> data, Function<List<byte[]>, Payload> factory) {
            return bytesToPayloads(factory, this.split(data));
        }

        default <Payload extends AbstractBytesPayload>
        List<Payload> splitToPayloads(Collection<@Nullable Data> data, Function<List<byte[]>, Payload> factory) {
            return this.splitToPayloads(data.stream(), factory);
        }

        List<@Nullable Data> revert(List<byte[]> bytes);

        default List<@Nullable Data> revertFromPayload(AbstractBytesPayload payload) {
            return this.revert(payload.getBytes());
        }
    }

    public static <Header, Data> Splitter<Header, Data> splitter(RegistryAccess registryAccess,
                                                                 StreamCodec<? super RegistryFriendlyByteBuf, Header> headerCodec,
                                                                 StreamCodec<? super RegistryFriendlyByteBuf, Data> dataCodec) {
        return new SplitterImp<>(registryAccess, headerCodec, dataCodec);
    }

    public static <Data> HeadlessSplitter<Data> headless(RegistryAccess registryAccess,
                                                         StreamCodec<? super RegistryFriendlyByteBuf, Data> dataCodec) {
        return new HeadlessSplitterImp<>(registryAccess, dataCodec);
    }

    //region internals
    //region AbstractSplitter

    private static class AbstractSplitter<Data> {

        protected AbstractSplitter(RegistryAccess registryAccess,
                                   StreamCodec<? super RegistryFriendlyByteBuf, Data> dataCodec) {

            Preconditions.checkNotNull(registryAccess, "Registry access must not be null");
            Preconditions.checkNotNull(dataCodec, "Data codec must not be null");

            this._registryAccess = registryAccess;
            this._dataCodec = dataCodec;
        }

        protected StreamCodec<? super RegistryFriendlyByteBuf, Data> getDataCodec() {
            return this._dataCodec;
        }

        protected RegistryFriendlyByteBuf buffer() {
            return new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()),
                    this._registryAccess, ConnectionType.NEOFORGE);
        }

        protected RegistryFriendlyByteBuf buffer(byte[] data) {
            return new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.wrappedBuffer(data)),
                    this._registryAccess, ConnectionType.NEOFORGE);
        }

        protected static <T> byte[] encodeToArray(RegistryFriendlyByteBuf buffer,
                                                  StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
                                                  @Nullable T value) {

            buffer.clear();

            if (null != value) {

                buffer.writeBoolean(true);
                codec.encode(buffer, value);

            } else {

                buffer.writeBoolean(false);
            }

            final byte[] encoded = buffer.array();

            buffer.clear();

            if (encoded.length > MAX_DATA_SIZE_IN_BYTES) {
                throw new IllegalArgumentException("The value to encode was too big (%d bytes, over maximum %d)".formatted(
                        encoded.length, MAX_DATA_SIZE_IN_BYTES));
            }

            return encoded;
        }

        protected <T> @Nullable T decodeFromArray(byte[] array, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {

            final var buffer = this.buffer(array);

            if (!buffer.readBoolean()) {
                return null;
            }

            return codec.decode(buffer);
        }

        protected List<byte[]> encodeData(RegistryFriendlyByteBuf buffer, Stream<@Nullable Data> data) {
            return data.map(datum -> encodeToArray(buffer, this._dataCodec, datum)).toList();
        }

        protected List<@Nullable Data> convertData(List<byte[]> bytes) {
            return this.convertData(0, bytes);
        }

        protected List<@Nullable Data> convertData(int offset, List<byte[]> bytes) {

            Preconditions.checkArgument(offset < bytes.size());

            final List<@Nullable Data> decoded = new ObjectArrayList<>(bytes.size() - offset);

            for (int idx = offset; idx < bytes.size(); ++idx) {
                decoded.add(this.decodeFromArray(bytes.get(idx), this.getDataCodec()));
            }

            return decoded;
        }

        protected static List<List<byte[]>> split(byte @Nullable [] header, List<byte[]> bytes) {

            final List<List<byte[]>> split = new ObjectArrayList<>(32);
            // header size + a VarInt since the element count of the list will be sent before the actual list of data
            final int headerSize;
            final Supplier<List<byte[]>> listFactory;

            if (header != null) {

                headerSize = header.length + VarIntAccessor.getMaxByteSize();
                listFactory = () -> {

                    final List<byte[]> list = new ObjectArrayList<>(32);

                    list.add(header);
                    return list;
                };

            } else {

                headerSize = VarIntAccessor.getMaxByteSize();
                listFactory = () -> new ObjectArrayList<>(32);
            }

            List<byte[]> currentList = listFactory.get();
            int currentSize = headerSize;

            for (final var array : bytes) {

                if (currentSize + array.length > MAX_DATA_SIZE_IN_BYTES) {

                    split.add(currentList);
                    currentList = listFactory.get();
                    currentSize = headerSize;
                }

                currentList.add(array);
                currentSize += array.length;
            }

            return split;
        }

        //region internals

        private static final int MAX_DATA_SIZE_IN_BYTES = ServerboundCustomPayloadPacketAccessor.getMaxPayloadSize();

        private final RegistryAccess _registryAccess;
        private final StreamCodec<? super RegistryFriendlyByteBuf, Data> _dataCodec;

        //endregion
    }

    //endregion
    //region SplitterImp

    public static class SplitterImp<Header, Data>
            extends AbstractSplitter<Data>
            implements Splitter<Header, Data> {

        protected SplitterImp(RegistryAccess registryAccess, StreamCodec<? super RegistryFriendlyByteBuf, Header> headerCodec,
                              StreamCodec<? super RegistryFriendlyByteBuf, Data> dataCodec) {

            super(registryAccess, dataCodec);

            Preconditions.checkNotNull(headerCodec, "Header codec must not be null");

            this._headerCodec = headerCodec;
        }

        //region Splitter<Header, Data>

        @Override
        public List<List<byte[]>> split(Header header, Stream<@Nullable Data> data) {

            final var buffer = this.buffer();

            return split(encodeToArray(buffer, this._headerCodec, header), this.encodeData(buffer, data));
        }

        @Override
        public Pair<Header, List<@Nullable Data>> revert(List<byte[]> bytes) {

            if (bytes.isEmpty()) {
                return Pair.of(null, List.of());
            }

            final var header = this.decodeFromArray(bytes.getFirst(), this._headerCodec);
            final var data = this.convertData(1, bytes);

            return Pair.of(header, data);
        }

        //endregion
        //region internals

        private final StreamCodec<? super RegistryFriendlyByteBuf, Header> _headerCodec;

        //endregion
    }

    //endregion
    //region HeadlessSplitterImp

    public static class HeadlessSplitterImp<Data>
            extends AbstractSplitter<Data>
            implements HeadlessSplitter<Data> {

        protected HeadlessSplitterImp(RegistryAccess registryAccess,
                                      StreamCodec<? super RegistryFriendlyByteBuf, Data> dataCodec) {
            super(registryAccess, dataCodec);
        }

        //region HeadlessSplitter<Data>

        @Override
        public List<List<byte[]>> split(Stream<@Nullable Data> data) {
            return split(null, this.encodeData(this.buffer(), data));
        }

        @Override
        public List<@Nullable Data> revert(List<byte[]> bytes) {
            return this.convertData(bytes);
        }

        //endregion
    }

    //endregion

    private static <Payload extends AbstractBytesPayload>
    List<Payload> bytesToPayloads(Function<List<byte[]>, Payload> factory, List<List<byte[]>> bytes) {
        return bytes.stream()
                .map(factory)
                .toList();
    }

    //endregion
}
