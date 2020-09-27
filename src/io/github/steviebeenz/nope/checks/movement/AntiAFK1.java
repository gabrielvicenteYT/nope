package io.github.steviebeenz.nope.checks.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.steviebeenz.nope.modules.checks.Check;
import io.github.steviebeenz.nope.modules.checks.Global;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.github.steviebeenz.nope.NOPE;
import io.github.steviebeenz.nope.modules.checks.CheckType;
import io.github.steviebeenz.nope.modules.data.CPlayer;

/**
 * Checks if a player moves their yaw without changing their pitch
 * 
 * @author imodm
 *
 */
public class AntiAFK1 implements Check, Listener {

	private NOPE plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	private Map<UUID, List<Double>> timings = new HashMap<>();

	@Override
	public void register(NOPE plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying() || player.isInsideVehicle())
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getYaw() == from.getYaw())
			return;

		if (to.getPitch() != from.getPitch())
			return;

		if (cp.timeSince(Global.Stat.TELEPORT) < 5000)
			return;

		if (to.getPitch() != 0)
			return;

		List<Double> samePitchTimings = timings.getOrDefault(player.getUniqueId(), new ArrayList<>());

		samePitchTimings.add((double) System.currentTimeMillis());

		Iterator<Double> it = samePitchTimings.iterator();
		while (it.hasNext()) {
			double d = it.next();
			if (System.currentTimeMillis() - d > 20000) {
				it.remove();
				continue;
			}
		}

		timings.put(player.getUniqueId(), samePitchTimings);

		if (samePitchTimings.size() < 25)
			return;

		cp.flagHack(this, (samePitchTimings.size() - 25) * 3, "Moved with similar Yaw: &e" + samePitchTimings.size());
	}

	@Override
	public String getCategory() {
		return "AntiAFK";
	}

	@Override
	public String getDebugName() {
		return "AntiAFK#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
