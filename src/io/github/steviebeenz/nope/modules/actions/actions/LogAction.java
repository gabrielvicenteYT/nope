package io.github.steviebeenz.nope.modules.actions.actions;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.github.steviebeenz.nope.NOPE;
import io.github.steviebeenz.nope.modules.actions.AbstractAction;
import io.github.steviebeenz.nope.modules.actions.Webhook;
import io.github.steviebeenz.nope.modules.checks.Check;
import io.github.steviebeenz.nope.modules.data.CPlayer;
import io.github.steviebeenz.nope.utils.MSG;

/**
 * Logs the specified message to either CONSOLE, FILE, or INGAME. {@link Type}
 * 
 * @author imodm
 *
 */
public class LogAction extends AbstractAction {

	private Type type;
	private String message;
	private Webhook hook;

	public LogAction(NOPE plugin, Type type, String message) {
		super(plugin);
		this.type = type;
		this.message = message;
	}

	public LogAction(NOPE plugin, Webhook hook, String message) {
		super(plugin);
		this.type = Type.WEBHOOK;
		this.hook = hook;
		this.message = message;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		String msg = MSG.color(MSG.replaceCheckPlaceholder(message, plugin.getCPlayer(player), check));
		switch (type) {
			case CONSOLE:
				MSG.log(msg);
				break;
			case FILE:
				plugin.getCPlayer(player).addLogMessage(msg);
				break;
			case INGAME:
				for (Player p : Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("nope.message.normal"))
						.collect(Collectors.toList())) {
					CPlayer cp = plugin.getCPlayer(p);
					if (!cp.getOption("notifications").asBoolean())
						continue;
					MSG.tell(p, msg);
				}
				MSG.sendPluginMessage(Bukkit.getOnlinePlayers().parallelStream().findFirst().orElse(null),
						"perm:nope.message.normal " + msg);
				break;
			case WEBHOOK:
				if (hook == null)
					throw new NullPointerException("Invalid log action, no hook specified");
				hook.sendMessage(msg, plugin.getCPlayer(player), check);
				break;
		}
	}

	public enum Type {
		CONSOLE, FILE, INGAME, WEBHOOK;
	}

}
