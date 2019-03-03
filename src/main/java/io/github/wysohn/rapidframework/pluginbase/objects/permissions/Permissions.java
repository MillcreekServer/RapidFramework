package io.github.wysohn.rapidframework.pluginbase.objects.permissions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.NamedElement;

public class Permissions implements NamedElement {
    private final UUID playerUuid;
    private String playerName;

    private Set<String> permissions = new HashSet<>();

    public Permissions(UUID playerUuid) {
	this.playerUuid = playerUuid;
    }

    public UUID getPlayerUuid() {
	return playerUuid;
    }

    public String getPlayerName() {
	return playerName;
    }

    public void setPlayerName(String playerName) {
	this.playerName = playerName;
    }

    public Set<String> getPermissions() {
	return permissions;
    }

    @Override
    public String getName() {
	return playerName;
    }
}
