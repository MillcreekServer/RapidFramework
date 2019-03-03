package io.github.wysohn.rapidframework.pluginbase.objects.structure;

import io.github.wysohn.rapidframework.pluginbase.PluginLanguage.Language;
import io.github.wysohn.rapidframework.pluginbase.language.DefaultLanguages;
import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.NamedElement;
import io.github.wysohn.rapidframework.pluginbase.objects.SimpleLocation;

import org.bukkit.entity.Player;

import java.util.*;

public abstract class Structure implements NamedElement {
    protected final String name;
    protected final SimpleLocation sloc;
    private final Set<UUID> trusts = new HashSet<>();

    private boolean isPublic;
    private UUID ownerUuid;

    private String title;
    private List<String> desc = new ArrayList<String>();

    /**
     * Always preserve the arguments, SimpleLocation and UUID, in order to allow
     * instantiation through reflection.
     *
     * @param sloc
     * @param ownerUuid
     */
    public Structure(SimpleLocation sloc, UUID ownerUuid) {
	super();
	this.name = sloc.toString();
	this.sloc = sloc;
	this.ownerUuid = ownerUuid;
    }

    @Override
    public String getName() {
	return name;
    }

    public Set<UUID> getTrusts() {
	return trusts;
    }

    public boolean isPublic() {
	return isPublic;
    }

    public void setPublic(boolean isPublic) {
	this.isPublic = isPublic;
    }

    public UUID getOwnerUuid() {
	return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
	this.ownerUuid = ownerUuid;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public List<String> getDesc() {
	return desc;
    }

    public void setDesc(List<String> desc) {
	this.desc = desc;
    }

    public Map<Language, Object> infoToMap(Player player, UUIDToNameParser parser) throws StructureException {
	Map<Language, Object> map = new HashMap<>();

	List<String> trustCopy = new ArrayList();
	trusts.forEach((uuid) -> {
	    String name = parser.parse(uuid);
	    trustCopy.add(name == null ? "[?]" : name);
	});

	map.put(DefaultLanguages.Structure_Trusts, trustCopy);
	map.put(DefaultLanguages.Structure_PublicMode, isPublic);
	map.put(DefaultLanguages.Structure_Title, title == null ? "?" : title);

	return map;
    }

    public void applyInfoMap(Player player, Map<Language, Object> map, NameToUUIDParser parser)
	    throws StructureException {
	List<String> trustCopy = (List<String>) map.get(DefaultLanguages.Structure_Trusts);
	Boolean publicMode = (Boolean) map.get(DefaultLanguages.Structure_PublicMode);
	String title = (String) map.get(DefaultLanguages.Structure_Title);

	if (trustCopy != null) {
	    trusts.clear();

	    for (String name : trustCopy) {
		UUID parsed = parser.parse(ownerUuid, name);
		if (parsed != null)
		    trusts.add(parsed);
	    }
	}

	if (publicMode != null)
	    this.isPublic = publicMode;

	if (title != null)
	    this.title = title;
    }

    public static class StructureException extends RuntimeException {
	private final Language lang;

	public StructureException(Language lang) {
	    super(lang == null ? "null" : lang.toString());
	    this.lang = lang;
	}

	public Language getLang() {
	    return lang;
	}

    }

    public interface NameToUUIDParser {
	UUID parse(UUID ownerGuild, String name);
    }

    public interface UUIDToNameParser {
	String parse(UUID uuid);
    }
}
