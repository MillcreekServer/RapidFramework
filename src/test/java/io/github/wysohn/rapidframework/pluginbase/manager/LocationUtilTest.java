package io.github.wysohn.rapidframework.pluginbase.manager;

import static org.junit.Assert.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import io.github.wysohn.rapidframework.utils.locations.LocationUtil;

public class LocationUtilTest {
    private static final int TRIALS = 10000;

    @Rule
    public TestRule benchmark = new BenchmarkRule();

    private Location loc1;
    private Location loc2;
    private Location loc3;

    @Before
    public void init() {
	World world = Mockito.mock(World.class);
	loc1 = new Location(world, 0, 0, 0);
	loc2 = new Location(world, 0, 0, 30);
	loc3 = new Location(world, 0, 55, 0);
    }

    @Test
    public void testWithinDistance() {
	Assert.assertTrue(LocationUtil.withinDistance(loc1, loc2, 50));
	Assert.assertFalse(LocationUtil.withinDistance(loc1, loc3, 50));

	for (int i = 0; i < TRIALS; i++) {
	    loc1.getWorld().getName();
	    LocationUtil.withinDistance(loc1, loc2, 50);
	}
    }

    @Test
    public void testWithinDistanceV2() {
	Assert.assertTrue(LocationUtil.withinDistanceV2(loc1, loc2, 50));
	Assert.assertFalse(LocationUtil.withinDistanceV2(loc1, loc3, 50));

	for (int i = 0; i < TRIALS; i++) {
	    LocationUtil.withinDistanceV2(loc1, loc2, 50);
	}
    }

}
