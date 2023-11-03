package io.openems.edge.kostal.plenticore.common;


import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Level;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.kostal.plenticore.charger.AbstractKostalPlenticoreCharger;
import io.openems.edge.kostal.plenticore.charger.KostalPlenticoreCharger1;
import io.openems.edge.kostal.plenticore.common.enums.ControlMode;

public interface KostalPlenticore {

	/**
	 * Registers a GoodWe Charger.
	 *
	 * @param charger  {@link KostalPlenticoreCharger1}
	 */
	public void addCharger(AbstractKostalPlenticoreCharger charger);

	/**
	 * Unregisters a GoodWe Charger.
	 *
	 * @param charger  {@link KostalPlenticoreCharger1} 
	 */
	public void removeCharger(AbstractKostalPlenticoreCharger charger);

	
	/**
	 * Gets the PV production from chargers ACTUAL_POWER. Returns null if the PV
	 * production is not available.
	 *
	 * @return production power
	 */
	public Integer calculatePvProduction();

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		UNABLE_TO_READ_DATA(Doc.of(Level.FAULT)), //

		/*
		 * Core
		 */
		INVERTER_NAME(Doc.of(OpenemsType.STRING)), //
		ARTICLE_NUMBER(Doc.of(OpenemsType.STRING)), //
		INVERTER_SERIAL_NUMBER(Doc.of(OpenemsType.STRING)), //
		FIRMWARE_VERSION(Doc.of(OpenemsType.STRING)), //
		HARDWARE_VERSION(Doc.of(OpenemsType.STRING)), //
		KOMBOARD_VERSION(Doc.of(OpenemsType.STRING)), //
		PARAMETER_VERSION(Doc.of(OpenemsType.STRING)), //
		COUNTRY_NAME(Doc.of(OpenemsType.STRING)), //
		INVERTER_OPERATING_STATUS(Doc.of(OpenemsType.STRING)), //
		INVERTER_TYPE_NAME(Doc.of(OpenemsType.STRING)), //
		NUMBER_OF_BIDIRECTIONAL_CONVERTER(Doc.of(OpenemsType.INTEGER)), //
		NUMBER_OF_STRING(Doc.of(OpenemsType.INTEGER)), //
		NUMBER_OF_PHASES(Doc.of(OpenemsType.INTEGER)), //
		POWER_ID(Doc.of(OpenemsType.INTEGER)), //
		PRESENT_ERROR_EVENT_CODE_1(Doc.of(OpenemsType.INTEGER)), //
		PRESENT_ERROR_EVENT_CODE_2(Doc.of(OpenemsType.INTEGER)), //
		FEED_IN_TIME(Doc.of(OpenemsType.INTEGER)), //
		INVERTER_STATUS(Doc.of(OpenemsType.INTEGER)), //
		ADDRESS_MODBUS_RTU(Doc.of(OpenemsType.INTEGER)), //
		BAUDRATE_INDEX_MODBUS_RTU(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP1(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP2(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP3(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP4(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_SUBNET_MASK_1(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_SUBNET_MASK_2(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_SUBNET_MASK_3(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_SUBNET_MASK_4(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_GATEWAY_1(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_GATEWAY_2(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_GATEWAY_3(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_GATEWAY_4(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_FIRST_1(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_FIRST_2(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_FIRST_3(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_FIRST_4(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_SECOND_1(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_SECOND_2(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_SECOND_3(Doc.of(OpenemsType.INTEGER)), //
		SETTING_MANUAL_IP_DNS_SECOND_4(Doc.of(OpenemsType.INTEGER)), //
		WORKTIME(Doc.of(OpenemsType.FLOAT).unit(Unit.SECONDS)), //

		FEED_IN_STATUS(Doc.of(OpenemsType.BOOLEAN)), //
		SETTING_AUTO_IP(Doc.of(OpenemsType.BOOLEAN)), //
		SETTING_MANUAL_EXTERNAL_ROUTER(Doc.of(OpenemsType.BOOLEAN)), //
		PRELOAD_MODBUS_RTU(Doc.of(OpenemsType.BOOLEAN)), //
		TERMINATION_MODBUS_RTU(Doc.of(OpenemsType.BOOLEAN)), //

		/*
		 * ESS
		 */
		//BATTERY_SOC(Doc.of(OpenemsType.INTEGER).unit(Unit.PERCENT)), //
		BATTERY_CYCLES(Doc.of(OpenemsType.INTEGER)), //
		BATTERY_CHARGE_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		BATTERY_CHARGE_CURRENT(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		//DC_CHARGE_ENERGY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		BATTERY_DISCHARGE_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		//DC_DISCHARGE_ENERGY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		BATTERY_CURRENT_DIRECTION(Doc.of(BatteryCurrentDirection.values())), //
		BATTERY_CURRENT(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		BATTERY_VOLTAGE(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		BATTERY_TEMPERATURE(Doc.of(OpenemsType.FLOAT).unit(Unit.DEGREE_CELSIUS)), //
		BATTERY_WORK_CAPACITY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		MAXIMUM_CHARGE_POWER_LIMIT(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		MAXIMUM_DISCHARGE_POWER_LIMIT(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		PSSB_FUSE_STATE(Doc.of(OpenemsType.FLOAT)), //

		/*
		 * PV Charger
		 */
		OVERALL_DC_CURRENT(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		OVERALL_DC_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		DC_CURRENT_STRING_1(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		DC_VOLTAGE_STRING_1(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		DC_POWER_STRING_1(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		DC_CURRENT_STRING_2(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		DC_VOLTAGE_STRING_2(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		DC_POWER_STRING_2(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		DC_CURRENT_STRING_3(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		DC_VOLTAGE_STRING_3(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		DC_POWER_STRING_3(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //

		/*
		 * Grid
		 */
		GRID_AC_P_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //

		ACTUAL_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		TOTAL_AC_ACTIVE_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		TOTAL_AC_REACTIVE_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT_AMPERE_REACTIVE)), //
		TOTAL_AC_APPARENT_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT_AMPERE)), //
		AC_VOLTAGE_L1(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		AC_VOLTAGE_L2(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		AC_VOLTAGE_L3(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		AC_CURRENT_L1(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		AC_CURRENT_L2(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		AC_CURRENT_L3(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		AC_POWER_L1(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		AC_POWER_L2(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		AC_POWER_L3(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		POWER_LIMITATION_OF_EVU(Doc.of(OpenemsType.FLOAT).unit(Unit.PERCENT)), //
		GRID_FREQUENCY(Doc.of(OpenemsType.FLOAT).unit(Unit.HERTZ)), //
		COSINUS_PHI(Doc.of(OpenemsType.FLOAT)), //
		HOME_CONSUMPTION_PV(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		HOME_CONSUMPTION_BAT(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		HOME_CONSUMPTION_GRID(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		HOME_CURRENT_FROM_EXT_SENSOR_L1(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		HOME_POWER_L1(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		TOTAL_HOME_CONSUMPTION_BATTERY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		HOME_CURRENT_FROM_EXT_SENSOR_L2(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		HOME_POWER_L2(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		TOTAL_HOME_CONSUMPTION_GRID(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		HOME_CURRENT_FROM_EXT_SENSOR_L3(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		HOME_POWER_L3(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		TOTAL_HOME_CONSUMPTION_PV(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		HOME_TOTAL_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		HOME_SELF_CONSUMPTION_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT)), //
		ISOLATION_RESISTOR(Doc.of(OpenemsType.FLOAT).unit(Unit.KILOOHM)), //
		MAX_RESIDUAL_CURRENT(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		ANALOG_INPUT_CH_1(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		ANALOG_INPUT_CH_2(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		ANALOG_INPUT_CH_3(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		ANALOG_INPUT_CH_4(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)), //
		YIELD_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.KILOWATT_HOURS)), //
		YIELD_DAY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		HOME_CONSUMPTION_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.KILOWATT_HOURS)), //
		HOME_CONSUMPTION_DAY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		SELF_CONSUMPTION_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.KILOWATT_HOURS)), //
		SELF_CONSUMPTION_DAY(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT_HOURS)), //
		SELF_CONSUMPTION_RATE_TOTAL(Doc.of(OpenemsType.FLOAT).unit(Unit.PERCENT)), //
		SELF_CONSUMPTION_RATE_DAY(Doc.of(OpenemsType.FLOAT).unit(Unit.PERCENT)), //
		DEGREE_OF_SELF_SUFFICIENCY_DAY(Doc.of(OpenemsType.FLOAT)), //
		DEGREE_OF_SELF_SUFFICIENCY_TOTAL(Doc.of(OpenemsType.FLOAT)), //
		

		INVERTER_MAX_POWER(Doc.of(OpenemsType.INTEGER).unit(Unit.WATT)),//
		INVERTER_GENERATION_POWER(Doc.of(OpenemsType.INTEGER).unit(Unit.WATT)),//
		ACTIVE_POWER_SETPOINT(Doc.of(OpenemsType.INTEGER).unit(Unit.PERCENT)),//
		LOW_PRIORITY_ACTIVE_POWER_SETPOINT(Doc.of(OpenemsType.INTEGER).unit(Unit.WATT).accessMode(AccessMode.WRITE_ONLY)),//
		BATTERY_CHARGE_POWER_SETPOINT(Doc.of(OpenemsType.FLOAT).unit(Unit.WATT).accessMode(AccessMode.READ_WRITE)),//
		REACTIVE_POWER_SETPOINT(Doc.of(OpenemsType.INTEGER).unit(Unit.PERCENT)),//
		BATTERY_MANAGEMENT_MODE(Doc.of(ControlMode.values())//
				);
		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}
}
