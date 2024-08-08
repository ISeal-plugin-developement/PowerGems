package dev.iseal.powergems.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GemCreationTest {

    private ServerMock server;
    private PowerGems plugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(PowerGems.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGemCreation() {
        GemManager gemManager = SingletonManager.getInstance().gemManager;
        gemManager.getAllGems().forEach((id, gem) -> {
            assertNotNull(gem);
            assertEquals((long) id, gemManager.lookUpID(gemManager.getGemName(gem)));
            assertEquals(gemManager.getGemName(gem), gemManager.lookUpName(id));
            assert gemManager.isGem(gem);
        });
    }

    @Test
    public void testGemGiving() {
        GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        PlayerMock plr = server.addPlayer();
        assert !gcm.getGiveGemOnFirstLogin() || SingletonManager.getInstance().utils.hasAtLeastXAmountOfGems(plr, 1);
    }
}