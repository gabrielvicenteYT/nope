package io.github.steviebeenz.nope.modules.actions.actions;

import org.bukkit.OfflinePlayer;

import io.github.steviebeenz.nope.NOPE;
import io.github.steviebeenz.nope.modules.actions.AbstractConditionalAction;
import io.github.steviebeenz.nope.modules.checks.Check;

/**
 * A useful {@link AbstractConditionalAction} for avoiding spam or kicking the
 * player but not every time they flag a hack. This ONLY prevents the NEXT
 * action from running, this is unique to only this action.
 * 
 * @author imodm
 *
 */
public class DelayAction extends AbstractConditionalAction {

	private long delay, lastActivated;

	public DelayAction(NOPE plugin, long delay) {
		super(plugin);
		this.delay = delay;
		this.lastActivated = 0;
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		super.execute(player, check);
		lastActivated = System.currentTimeMillis();
	}

	@Override
	public boolean getValue(OfflinePlayer player, Check check) {
		return System.currentTimeMillis() - lastActivated > delay;
	}

}
