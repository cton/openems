package io.openems.edge.kostal.plenticore.common.enums;

import io.openems.common.types.OptionsEnum;

public enum ControlMode implements OptionsEnum {
	/**
	 * Uses the internal 'AUTO' mode of the inverter. Allows no remote
	 * control of Set-Points. Requires a Smart Meter at the grid junction
	 * point.
	 */
	INTERNAL(0, "Internal"),
	/**
	 * Uses the internal 'AUTO' mode of the GoodWe inverter but smartly switches to
	 * other modes if required.Requires a GoodWe Smart Meter at the grid junction
	 * point.
	 */
	REMOTE_IO(1,"External I/O"),
	/**
	 * Full control of the GoodWe inverter by OpenEMS. Slower than internal 'AUTO'
	 * mode, but does not require a GoodWe Smart Meter at the grid junction point.
	 */
	REMOTE_MODBUS(2, "External ModBus");
	
	private final int value;
	private final String name;

	private ControlMode(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return this.value;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public OptionsEnum getUndefined() {
		return INTERNAL;
	}
}
