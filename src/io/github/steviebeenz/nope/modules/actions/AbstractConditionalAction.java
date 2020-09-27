package io.github.steviebeenz.nope.modules.actions;

import org.bukkit.OfflinePlayer;

import io.github.steviebeenz.nope.NOPE;
import io.github.steviebeenz.nope.modules.checks.Check;

/**
 * Represents a boolean check that shouldn't do anything action wise Examples
 * are vl>50, tps<13, etc.
 * 
 * @author imodm
 *
 */
public abstract class AbstractConditionalAction extends AbstractAction {

	public AbstractConditionalAction(NOPE plugin) {
		super(plugin);
	}

	@Override
	public void execute(OfflinePlayer player, Check check) {
		return;
	}

	public abstract boolean getValue(OfflinePlayer player, Check check);

}
