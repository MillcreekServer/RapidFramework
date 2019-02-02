package io.github.wysohn.rapidframework.pluginbase;

import java.util.List;
import java.io.File;
import java.util.ArrayList;

public class PluginConfig extends ConfigBase{
	public int Command_Help_SentencePerPage = 6;

    public int Languages_Double_DecimalPoints = 4;

    public boolean Plugin_Enabled = true;
    public boolean Plugin_Debugging = false;
    public String Plugin_Language_Default = "en";
    public List<String> Plugin_Language_List = new ArrayList<String>() {
        {
            add("en");
            add("ko");
        }
    };
    public String Plugin_Prefix = "&6[&5?&6]";

    public String Plugin_ServerName = "yourServer";

    public boolean MySql_Enabled = false;
    public String MySql_DBAddress = "localhost:3306";
    public String MySql_DBName = "somedb";
    public String MySql_DBUser = "root";
    public String MySql_DBPassword = "1234";
    
	@Override
	protected File initConfigFile(PluginBase base) {
		return new File(base.getDataFolder(), "config.yml");
	}
}