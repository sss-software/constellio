package com.constellio.app.services.menu;

import lombok.Getter;

@Getter
public class MenuItemActionState {

	private MenuItemActionStateStatus status;
	private String reason;

	public MenuItemActionState(MenuItemActionStateStatus actionStateStatus) {
		this(actionStateStatus, null);
	}

	public MenuItemActionState(MenuItemActionStateStatus status, String reason) {
		this.status = status;
		this.reason = reason;
	}

	public enum MenuItemActionStateStatus {
		VISIBLE, HIDDEN, DISABLED;
	}
}
