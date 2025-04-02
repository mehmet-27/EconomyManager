package dev.mehmet27.economymanager;

import java.math.BigDecimal;
import java.util.UUID;

public class EconomyPlayer {

	private final UUID uuid;
	private BigDecimal balance;

	public EconomyPlayer(UUID uuid, BigDecimal balance) {
		this.uuid = uuid;
		this.balance = balance;
	}

	public UUID getUuid() {
		return uuid;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void addBalance(BigDecimal amount) {
		this.balance = balance.add(amount);
	}

	public void removeBalance(BigDecimal amount) {
		this.balance = balance.subtract(amount);
	}
}
