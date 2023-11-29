package me.boomber.spice.trait;

import lombok.SneakyThrows;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class Serialization {

    public interface FieldDeserializer<T> {
        void deserialize(CompoundTag tag, T object) throws IllegalAccessException;
    }

    @SneakyThrows
    public static <T> TraitDeserializer<T> getDeserializer(Class<T> type) {
        var fields = Arrays.stream(type.getDeclaredFields()).map(Serialization::getFieldDeserializer).toList();
        var constructor = type.getConstructor();

        return new TraitDeserializer<T>() {
            @SneakyThrows
            @Override
            public T deserialize(CompoundTag nbt) {
                var object = constructor.newInstance();

                for (var field : fields) {
                    field.deserialize(nbt, object);
                }

                return object;
            }
        };
    }

    @SneakyThrows
    private static <T> FieldDeserializer<T> getFieldDeserializer(Field field) {
        var type = field.getType();
        var name = getFieldName(field);

        return (nbt, object) -> {
            var data = deserialize(nbt, name, type);
            field.set(object, data);
        };
    }

    private static String getFieldName(Field field) {
        var fieldName = field.getAnnotation(FieldName.class);

        return Optional.ofNullable(fieldName).map(FieldName::value)
                .orElse(field.getName());
    }

    @SneakyThrows
    private static <T> Object deserialize(CompoundTag nbt, String key, Class<T> type) {
        if (Integer.class.isAssignableFrom(type)) {
            return nbt.getInt(key);
        } else if (Long.class.isAssignableFrom(type)) {
            return nbt.getLong(key);
        } else if (Float.class.isAssignableFrom(type)) {
            return nbt.getFloat(key);
        } else if (Double.class.isAssignableFrom(type)) {
            return nbt.getDouble(key);
        } else if (Boolean.class.isAssignableFrom(type)) {
            return nbt.getBoolean(key);
        } else if (String.class.isAssignableFrom(type)) {
            return nbt.getString(key);
        } else if (CompoundTag.class.isAssignableFrom(type)) {
            return nbt.getCompound(key);
        } else if (ListTag.class.isAssignableFrom(type)) {
            return nbt.getList(key, Tag.TAG_COMPOUND);
        } else {
            return getDeserializer(type).deserialize(nbt.getCompound(key));
        }
    }
}
