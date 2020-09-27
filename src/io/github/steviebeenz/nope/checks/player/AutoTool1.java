package io.github.steviebeenz.nope.checks.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.steviebeenz.nope.modules.checks.Check;
import io.github.steviebeenz.nope.modules.checks.CheckType;
import io.github.steviebeenz.nope.modules.data.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import io.github.steviebeenz.nope.NOPE;

/**
 * Wurst-Specific based on tested timings
 * 
 * @author imodm
 *
 */
public class AutoTool1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PLAYER;
	}

	private Map<UUID, Long> clicks = new HashMap<>();
	private NOPE plugin;

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		clicks.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (clicks.getOrDefault(player.getUniqueId(), 0L) != 98)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "AutoTool";
	}

	@Override
	public String getDebugName() {
		return getCategory() + "#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}

}
