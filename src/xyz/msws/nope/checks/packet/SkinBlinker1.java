package xyz.msws.nope.checks.packet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import xyz.msws.nope.NOPE;
import xyz.msws.nope.modules.checks.Check;
import xyz.msws.nope.modules.checks.CheckType;
import xyz.msws.nope.modules.checks.Global.Stat;
import xyz.msws.nope.modules.data.CPlayer;
import xyz.msws.nope.protocols.WrapperPlayClientSettings;

/**
 * Listens for SETTINGS packets and checks if they're too often and the player
 * is moving
 * 
 * @author imodm
 *
 */
public class SkinBlinker1 implements Check, Listener {

	@Override
	public CheckType getType() {
		return CheckType.PACKET;
	}

	@SuppressWarnings("unused")
	private NOPE plugin;

	private Map<UUID, Integer> skinValue = new HashMap<>();
	private Map<UUID, Long> skinPacket = new HashMap<>();
	private Map<UUID, Integer> packetAmo = new HashMap<UUID, Integer>();

	@Override
	public void register(NOPE plugin) throws OperationNotSupportedException {
		if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib"))
			throw new OperationNotSupportedException("ProtocolLib is not enabled");
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				WrapperPlayClientSettings wrapped = new WrapperPlayClientSettings(packet);

				int lastSkin = skinValue.getOrDefault(player.getUniqueId(), 0);
				if (lastSkin == wrapped.getDisplayedSkinParts())
					return;
				skinValue.put(player.getUniqueId(), wrapped.getDisplayedSkinParts());
				skinPacket.put(player.getUniqueId(), System.currentTimeMillis());
				packetAmo.put(player.getUniqueId(), packetAmo.getOrDefault(player.getUniqueId(), 0) + 1);
			}

			@Override
			public void onPacketSending(PacketEvent event) {
			}
		};
		manager.addPacketListener(adapter);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (cp.timeSince(Stat.MOVE) > 500 || cp.timeSince(Stat.ON_GROUND) > 500
							|| System.currentTimeMillis() - skinPacket.getOrDefault(player.getUniqueId(), 0L) > 200)
						return;

					int packets = packetAmo.getOrDefault(player.getUniqueId(), 0);
					packetAmo.put(player.getUniqueId(), 0);

					if (packets <= 20)
						return;
					cp.flagHack(SkinBlinker1.this, (packets - 8) * 10, "Packets: &e" + packets + ">&a20");
				}
			}
		}.runTaskTimer(plugin, 0, 20);

	}

	@Override
	public String getCategory() {
		return "SkinBlinker";
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
