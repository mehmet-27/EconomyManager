package dev.mehmet27.economymanager.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class WithdrawEvent extends Event implements Cancellable {

	private final Player player;
	private final BigDecimal amount;
	private static final HandlerList handlerList = new HandlerList();
	private boolean isCancel;

	public WithdrawEvent(Player player, BigDecimal amount) {
		this.player = player;
		this.amount = amount;
	}

	public Player getPlayer() {
		return player;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public boolean isCancelled() {
		return isCancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		isCancel = cancel;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
