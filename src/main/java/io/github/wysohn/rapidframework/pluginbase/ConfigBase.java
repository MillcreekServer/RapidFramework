/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.pluginbase;

import io.github.wysohn.rapidframework.utils.serializations.Utf8YamlConfiguration;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Let automatically make public fields to be saved and loaded.
 * </p>
 *
 * Child class only need to declare <b>public field with '_'</b> as _ will be
 * used to indicate the path. Fields with other than public modifier will be
 * ignored.
 *
 * <p>
 * For example) test_test field is equivalent to test.test in config
 * </p>
 *
 * @author wysohn
 *
 */
abstract class ConfigBase implements PluginProcedure {
    /*
     * public static void main(String[] ar){
     * System.out.println(convertToConfigName("test_test_test"));
     * System.out.println(converToFieldName("test.test.test")); }
     */

	private File file;
	
    protected PluginBase base;
    private FileConfiguration config;

    /**
     * Return the config file location. By default, it is
     * config.yml in the top of the "this plugin's" folder
     * (e.g. ExamplePlugin/config.yml)
     * Override this method to change the config file location and name.
     * @param base
     * @return the config File.
     */
	protected File initConfigFile(final PluginBase base) {
		return new File(base.getDataFolder(), "config.yml");
	}
    
	@Override
    public void onEnable(final PluginBase base) throws Exception {
		this.file = initConfigFile(base);
		this.base = base;
		this.config = new Utf8YamlConfiguration();
 
        if (!file.getParentFile().exists())
        	file.getParentFile().mkdirs();

        Field[] fields = this.getClass().getFields();
        if(fields.length < 1)
        	return;
        
        if (!file.exists())
            file.createNewFile();

        config.load(file);
        
        validateAndLoad(fields);
    }

    @Override
    public void onDisable(PluginBase base) throws Exception {

    }

    @Override
    public void onReload(PluginBase base) throws Exception {
        reload();
    }

    private static String convertToConfigName(String fieldName) {
        return fieldName.replaceAll("_", ".");
    }

    private static String converToFieldName(String configKey) {
        return configKey.replaceAll("\\.", "_");
    }

    /**
     * check all the config and add necessary/remove unnecessary configs.
     */
    protected void validateAndLoad(Field[] fields) {
        base.getLogger().info("Validating config ["+this.file.getName()+"]");
        
        int addedNew = 0;
        // fill empty config
        for (Field field : fields) {
        	field.setAccessible(true);
            try {
                String configName = convertToConfigName(field.getName());
                Object obj = field.get(this);

                if (!config.contains(configName) && obj != null) {
                    config.set(configName, obj);
                    addedNew++;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                base.getLogger().severe(e.getMessage());
            }
        }

        if(addedNew != 0)
        	base.getLogger().info("Added [" + addedNew + "] new configs with default value.");

        int loaded = 0;
        // delete non existing config or set value with existing config
        Configuration root = config.getRoot();
        Set<String> keys = root.getKeys(true);
        for (String key : keys) {
            try {
                if (config.isConfigurationSection(key))
                    continue;

                if (key.contains("_COMMENT_"))
                    continue;

                String fieldName = converToFieldName(key);

                Field field = this.getClass().getField(fieldName);
                field.setAccessible(true);

                field.set(this, config.get(key));
                loaded++;
            } catch (NoSuchFieldException e) {
            	// do nothing
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                base.getLogger().severe(e.getMessage());
            }
        }

        try {
            if(addedNew != 0)
                save();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        if(loaded != 0)
        	base.getLogger().info("Loaded [" + loaded + "] configs.");

        base.getLogger().info("Validation and Loading complete!");
    }

    /**
     * save current values into the config file
     * 
     * @throws IOException
     */
    public void save() throws IOException {
        base.getLogger().info("Saving to [" + file.getName() + "]...");

        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
        	field.setAccessible(true);
            try {
                Object obj = field.get(this);
                if (obj != null) {
                    config.set(convertToConfigName(field.getName()), obj);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream stream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(stream, Charset.forName("UTF-8"));

        String output = config.saveToString();
        String[] split = output.split("\n");
        for (String s : split) {
            if (s.contains("_COMMENT_")) {
                writer.write("#" + s.replaceAll("'", "").substring(s.indexOf(':') + 1) + "\n");
            } else {
                writer.write(s + "\n");
            }
        }

        writer.close();
        stream.close();

        base.getLogger().info("Complete!");
    }

    /**
     * Override all current values using values in config file
     * 
     * @throws IOException
     * @throws InvalidConfigurationException
     */
    public void reload() throws IOException, InvalidConfigurationException {
        Field[] fields = this.getClass().getFields();
        if(fields.length < 1)
        	return;
        
        base.getLogger().info("Loading [" + file.getName() + "]...");
        config.load(file);
        base.getLogger().info("Complete!");
        
        validateAndLoad(fields);
    }

    public ConfigurationSection getSection(String key){
        return config.getConfigurationSection(convertToConfigName(key));
    }
}
