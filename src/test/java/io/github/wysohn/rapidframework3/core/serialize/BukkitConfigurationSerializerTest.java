package io.github.wysohn.rapidframework3.core.serialize;

import com.google.common.collect.Multimap;
import copy.com.google.gson.Gson;
import copy.com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BukkitConfigurationSerializerTest {

    Gson gson;
    private Server server;

    @Before
    public void init() throws Exception {
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new BukkitConfigurationSerializer())
                .create();

        server = mock(Server.class, RETURNS_DEEP_STUBS);
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, server);

        when(server.getUnsafe().getDataVersion()).thenReturn(1231);
        when(server.getUnsafe().getMaterial(any(), anyInt())).then(invocation -> {
            String material = (String) invocation.getArguments()[0];
            return Material.valueOf(material);
        });
        when(server.getItemFactory().getItemMeta(any())).thenReturn(new TestMeta());
    }

    @Test
    public void testDeserialize() {
        String serialized = "{\"\\u003d\\u003d\":\"org.bukkit.inventory.ItemStack\"," +
                "\"v\":1231," +
                "\"type\":\"DIAMOND\"," +
                "\"amount\":33," +
                "\"meta\":{\"\\u003d\\u003d\":\"io.github.wysohn.rapidframework3.core.serialize.BukkitConfigurationSerializerTest$TestMeta\"," +
                "\"Meta1\":123," +
                "\"Meta2\":\"abc\"," +
                "\"Meta3\":true}}";

        when(server.getItemFactory().equals(any(), any())).thenReturn(true);

        ItemStack itemStack = gson.fromJson(serialized, ItemStack.class);
        assertEquals(new ItemStack(Material.DIAMOND, 33), itemStack);
    }

    @Test
    public void testSerialize() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND, 33);

        when(server.getItemFactory().equals(any(), any())).thenReturn(false);

        String serialized = gson.toJson(itemStack, ItemStack.class);
        assertEquals("{\"\\u003d\\u003d\":\"org.bukkit.inventory.ItemStack\"," +
                "\"v\":1231," +
                "\"type\":\"DIAMOND\"," +
                "\"amount\":33," +
                "\"meta\":{\"\\u003d\\u003d\":\"io.github.wysohn.rapidframework3.core.serialize.BukkitConfigurationSerializerTest$TestMeta\"," +
                "\"Meta1\":123," +
                "\"Meta2\":\"abc\"," +
                "\"Meta3\":true}}", serialized);
    }

    static {
        ConfigurationSerialization.registerClass(TestMeta.class);
    }

    public static class TestMeta implements ItemMeta, Damageable {
        int Meta1 = 123;
        String Meta2 = "abc";

        @Override
        public boolean hasDamage() {
            return false;
        }

        @Override
        public int getDamage() {
            return 0;
        }

        @Override
        public void setDamage(int damage) {

        }

        boolean Meta3 = true;

        @NotNull
        @Override
        public Map<String, Object> serialize() {
            return new HashMap<String, Object>() {{
                put("Meta1", Meta1);
                put("Meta2", Meta2);
                put("Meta3", Meta3);
            }};
        }

        public static TestMeta deserialize(Map<String, Object> map) {
            TestMeta meta = new TestMeta();
            meta.Meta1 = (int) map.get("Meta1");
            meta.Meta2 = (String) map.get("Meta2");
            meta.Meta3 = (boolean) map.get("Meta3");
            return meta;
        }

        @Override
        public boolean hasDisplayName() {
            return false;
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public void setDisplayName(@Nullable String name) {

        }

        @Override
        public boolean hasLocalizedName() {
            return false;
        }

        @NotNull
        @Override
        public String getLocalizedName() {
            return null;
        }

        @Override
        public void setLocalizedName(@Nullable String name) {

        }

        @Override
        public boolean hasLore() {
            return false;
        }

        @Nullable
        @Override
        public List<String> getLore() {
            return null;
        }

        @Override
        public void setLore(@Nullable List<String> lore) {

        }

        @Override
        public boolean hasCustomModelData() {
            return false;
        }

        @Override
        public int getCustomModelData() {
            return 0;
        }

        @Override
        public void setCustomModelData(@Nullable Integer data) {

        }

        @Override
        public boolean hasEnchants() {
            return false;
        }

        @Override
        public boolean hasEnchant(@NotNull Enchantment ench) {
            return false;
        }

        @Override
        public int getEnchantLevel(@NotNull Enchantment ench) {
            return 0;
        }

        @NotNull
        @Override
        public Map<Enchantment, Integer> getEnchants() {
            return null;
        }

        @Override
        public boolean addEnchant(@NotNull Enchantment ench, int level, boolean ignoreLevelRestriction) {
            return false;
        }

        @Override
        public boolean removeEnchant(@NotNull Enchantment ench) {
            return false;
        }

        @Override
        public boolean hasConflictingEnchant(@NotNull Enchantment ench) {
            return false;
        }

        @Override
        public void addItemFlags(@NotNull ItemFlag... itemFlags) {

        }

        @Override
        public void removeItemFlags(@NotNull ItemFlag... itemFlags) {

        }

        @NotNull
        @Override
        public Set<ItemFlag> getItemFlags() {
            return null;
        }

        @Override
        public boolean hasItemFlag(@NotNull ItemFlag flag) {
            return false;
        }

        @Override
        public boolean isUnbreakable() {
            return false;
        }

        @Override
        public void setUnbreakable(boolean unbreakable) {

        }

        @Override
        public boolean hasAttributeModifiers() {
            return false;
        }

        @Nullable
        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
            return null;
        }

        @NotNull
        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot) {
            return null;
        }

        @Nullable
        @Override
        public Collection<AttributeModifier> getAttributeModifiers(@NotNull Attribute attribute) {
            return null;
        }

        @Override
        public boolean addAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
            return false;
        }

        @Override
        public void setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> attributeModifiers) {

        }

        @Override
        public boolean removeAttributeModifier(@NotNull Attribute attribute) {
            return false;
        }

        @Override
        public boolean removeAttributeModifier(@NotNull EquipmentSlot slot) {
            return false;
        }

        @Override
        public boolean removeAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
            return false;
        }

        @NotNull
        @Override
        public CustomItemTagContainer getCustomTagContainer() {
            return null;
        }

        @Override
        public void setVersion(int version) {

        }

        @NotNull
        @Override
        public TestMeta clone() {
            return null;
        }

        @NotNull
        @Override
        public PersistentDataContainer getPersistentDataContainer() {
            return null;
        }

        @Override
        public String toString() {
            return "TestMeta{" +
                    "Meta1=" + Meta1 +
                    ", Meta2='" + Meta2 + '\'' +
                    ", Meta3=" + Meta3 +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestMeta meta = (TestMeta) o;
            return Meta1 == meta.Meta1 &&
                    Meta3 == meta.Meta3 &&
                    Meta2.equals(meta.Meta2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Meta1, Meta2, Meta3);
        }
    }
}