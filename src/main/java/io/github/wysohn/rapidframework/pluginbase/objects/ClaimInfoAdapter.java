package io.github.wysohn.rapidframework.pluginbase.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ClaimInfoAdapter implements ClaimInfo {
    private transient Area area;

    private final String name;

    private UUID owner;
    private boolean isPublic;
    private Set<UUID> trusts = new HashSet<>();

    public ClaimInfoAdapter(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#setArea(org.generallib. pluginbase.constants.Area)
     */
    @Override
    public void setArea(Area area) {
	this.area = area;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#getArea()
     */
    @Override
    public Area getArea() {
	return area;
    }

    @Override
    public void setPublic(boolean bool) {
	isPublic = bool;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#isPublic()
     */
    @Override
    public boolean isPublic() {
	return isPublic;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#getOwner()
     */
    @Override
    public UUID getOwner() {
	return owner;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#setOwner(java.util.UUID)
     */
    @Override
    public void setOwner(UUID uuid) {
	this.owner = uuid;
    }

    /*
     * (non-Javadoc)
     *
     * @see ClaimInfo#getTrusts()
     */
    @Override
    public Set<UUID> getTrusts() {
	return trusts;
    }
}
