package io.openems.edge.kostal.plenticore.common;

import java.util.ArrayList;
import java.util.List;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.DummyRegisterElement;
import io.openems.edge.bridge.modbus.api.element.FloatDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.element.StringWordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedWordElement;
import io.openems.edge.bridge.modbus.api.element.WordOrder;
import io.openems.edge.bridge.modbus.api.task.FC16WriteRegistersTask;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.kostal.plenticore.charger.AbstractKostalPlenticoreCharger;
import io.openems.edge.timedata.api.TimedataProvider;
import io.openems.edge.timedata.api.utils.CalculateEnergyFromPower;

public abstract class AbstractKostalPlenticore extends AbstractOpenemsModbusComponent
		implements KostalPlenticore, OpenemsComponent, TimedataProvider {

	private final io.openems.edge.common.channel.ChannelId activePowerChannelId;
	private final io.openems.edge.common.channel.ChannelId reactivePowerChannelId;
	private final io.openems.edge.common.channel.ChannelId dcDischargePowerChannelId;
	private final CalculateEnergyFromPower calculateAcChargeEnergy;
	private final CalculateEnergyFromPower calculateAcDischargeEnergy;
	private final CalculateEnergyFromPower calculateDcChargeEnergy;
	private final CalculateEnergyFromPower calculateDcDischargeEnergy;

	protected List<AbstractKostalPlenticoreCharger> chargers;

	protected AbstractKostalPlenticore(//
			io.openems.edge.common.channel.ChannelId activePowerChannelId, //
			io.openems.edge.common.channel.ChannelId reactivePowerChannelId, //
			io.openems.edge.common.channel.ChannelId dcDischargePowerChannelId, //
			io.openems.edge.common.channel.ChannelId activeChargeEnergyChannelId, //
			io.openems.edge.common.channel.ChannelId activeDischargeEnergyChannelId, //
			io.openems.edge.common.channel.ChannelId dcChargeEnergyChannelId, //
			io.openems.edge.common.channel.ChannelId dcDischargeEnergyChannelId, //
			io.openems.edge.common.channel.ChannelId[] firstInitialChannelIds, //
			io.openems.edge.common.channel.ChannelId[]... furtherInitialChannelIds) throws OpenemsNamedException {
		super(firstInitialChannelIds, furtherInitialChannelIds);
		this.activePowerChannelId = activePowerChannelId;
		this.reactivePowerChannelId = reactivePowerChannelId;
		this.dcDischargePowerChannelId = dcDischargePowerChannelId;
		this.calculateAcChargeEnergy = new CalculateEnergyFromPower(this, activeChargeEnergyChannelId);
		this.calculateAcDischargeEnergy = new CalculateEnergyFromPower(this, activeDischargeEnergyChannelId);
		this.calculateDcChargeEnergy = new CalculateEnergyFromPower(this, dcChargeEnergyChannelId);
		this.calculateDcDischargeEnergy = new CalculateEnergyFromPower(this, dcDischargeEnergyChannelId);
	}

	protected ModbusProtocol defineModbusProtocol() {
		var protocol = new ModbusProtocol(this, //
				new FC3ReadRegistersTask(6, Priority.LOW, //
						m(KostalPlenticore.ChannelId.ARTICLE_NUMBER, new StringWordElement(6, 8)), //
						m(KostalPlenticore.ChannelId.INVERTER_SERIAL_NUMBER, new StringWordElement(14, 8)), //
						new DummyRegisterElement(22, 29), //
						m(KostalPlenticore.ChannelId.NUMBER_OF_BIDIRECTIONAL_CONVERTER, new UnsignedWordElement(30)), //
						new DummyRegisterElement(31), //
						m(KostalPlenticore.ChannelId.NUMBER_OF_PHASES, new UnsignedWordElement(32)), //
						new DummyRegisterElement(33), //
						m(KostalPlenticore.ChannelId.NUMBER_OF_STRING, new UnsignedWordElement(34)) //
				),
				new FC3ReadRegistersTask(100, Priority.HIGH,
						m(KostalPlenticore.ChannelId.OVERALL_DC_POWER,
								new FloatDoublewordElement(100).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(102, 105), //
						// m(KostalPlenticoreCore.ChannelId.HOME_CONSUMPTION_PV,
						// new UnsignedDoublewordElement(104).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.HOME_CONSUMPTION_BAT,
								new FloatDoublewordElement(106).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.HOME_CONSUMPTION_GRID,
								new FloatDoublewordElement(108).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.TOTAL_HOME_CONSUMPTION_BATTERY,
								new FloatDoublewordElement(110).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.TOTAL_HOME_CONSUMPTION_GRID,
								new FloatDoublewordElement(112).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.TOTAL_HOME_CONSUMPTION_PV,
								new FloatDoublewordElement(114).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.HOME_CONSUMPTION_PV,
								new FloatDoublewordElement(116).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.HOME_CONSUMPTION_TOTAL,
								new FloatDoublewordElement(118).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.ISOLATION_RESISTOR,
								new FloatDoublewordElement(120).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.POWER_LIMITATION_OF_EVU,
								new FloatDoublewordElement(122).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.SELF_CONSUMPTION_RATE_TOTAL,
								new FloatDoublewordElement(124).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(126, 143), //
						m(KostalPlenticore.ChannelId.WORKTIME,
								new FloatDoublewordElement(144).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(146, 149), //
						m(KostalPlenticore.ChannelId.COSINUS_PHI,
								new FloatDoublewordElement(150).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.GRID_FREQUENCY,
								new FloatDoublewordElement(152).wordOrder(WordOrder.LSWMSW)), //

						m(KostalPlenticore.ChannelId.AC_CURRENT_L1,
								new FloatDoublewordElement(154).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_POWER_L1,
								new FloatDoublewordElement(156).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_VOLTAGE_L1,
								new FloatDoublewordElement(158).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_CURRENT_L2,
								new FloatDoublewordElement(160).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_POWER_L2,
								new FloatDoublewordElement(162).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_VOLTAGE_L2,
								new FloatDoublewordElement(164).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_CURRENT_L3,
								new FloatDoublewordElement(166).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_POWER_L3,
								new FloatDoublewordElement(168).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.AC_VOLTAGE_L3,
								new FloatDoublewordElement(170).wordOrder(WordOrder.LSWMSW)), //

						m(KostalPlenticore.ChannelId.TOTAL_AC_ACTIVE_POWER,
								new FloatDoublewordElement(172).wordOrder(WordOrder.LSWMSW)), //
						m(this.reactivePowerChannelId,
								new FloatDoublewordElement(174).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(176, 177), //
						m(KostalPlenticore.ChannelId.TOTAL_AC_APPARENT_POWER,
								new FloatDoublewordElement(178).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(180, 189), //
						m(KostalPlenticore.ChannelId.BATTERY_CHARGE_CURRENT,
								new FloatDoublewordElement(190).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(192, 193), //
						m(KostalPlenticore.ChannelId.BATTERY_CYCLES,
								new FloatDoublewordElement(194).wordOrder(WordOrder.LSWMSW)) //
				),
				new FC3ReadRegistersTask(200, Priority.HIGH,
						m(KostalPlenticore.ChannelId.BATTERY_CURRENT,
								new FloatDoublewordElement(200).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.PSSB_FUSE_STATE,
								new FloatDoublewordElement(202).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(204, 209),
						m(SymmetricEss.ChannelId.SOC, new FloatDoublewordElement(210).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(212, 213),
						m(KostalPlenticore.ChannelId.BATTERY_TEMPERATURE,
								new FloatDoublewordElement(214).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.BATTERY_VOLTAGE,
								new FloatDoublewordElement(216).wordOrder(WordOrder.LSWMSW))),
				new FC3ReadRegistersTask(529, Priority.HIGH, //
						m(SymmetricEss.ChannelId.CAPACITY, new UnsignedDoublewordElement(529)), //
						m(KostalPlenticore.ChannelId.INVERTER_MAX_POWER, new UnsignedWordElement(531)), //
						new DummyRegisterElement(532), //
						m(KostalPlenticore.ChannelId.ACTIVE_POWER_SETPOINT, new UnsignedWordElement(533)), //
						new DummyRegisterElement(534, 574), //
						m(KostalPlenticore.ChannelId.INVERTER_GENERATION_POWER, new SignedWordElement(575)), //
						new DummyRegisterElement(576, 581), //
						m(KostalPlenticore.ChannelId.BATTERY_CHARGE_POWER, new SignedWordElement(582)), //
						m(KostalPlenticore.ChannelId.REACTIVE_POWER_SETPOINT, new SignedWordElement(583)) //
				), //
				new FC16WriteRegistersTask(1034, //
						m(KostalPlenticore.ChannelId.BATTERY_CHARGE_POWER_SETPOINT,
								new FloatDoublewordElement(1034).wordOrder(WordOrder.LSWMSW))), //
				new FC3ReadRegistersTask(1076, Priority.HIGH, //
						m(KostalPlenticore.ChannelId.MAXIMUM_CHARGE_POWER_LIMIT,
								new FloatDoublewordElement(1076).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.MAXIMUM_DISCHARGE_POWER_LIMIT,
								new FloatDoublewordElement(1078).wordOrder(WordOrder.LSWMSW)), //
						m(KostalPlenticore.ChannelId.BATTERY_MANAGEMENT_MODE, new UnsignedWordElement(1080))
				)); //
		return protocol;
	}

	@Override
	public final void addCharger(AbstractKostalPlenticoreCharger charger) {
		if (this.chargers == null) {
			this.chargers = new ArrayList<>();
		}
		this.chargers.add(charger);
	}

	@Override
	public final void removeCharger(AbstractKostalPlenticoreCharger charger) {
		if (this.chargers != null && this.chargers.contains(charger)) {
			this.chargers.remove(charger);
		}
	}

	/**
	 * Gets the PV production from chargers ACTUAL_POWER. Returns null if the PV
	 * production is not available.
	 *
	 * @return production power
	 */
	public final Integer calculatePvProduction() {
		Integer productionPower = 0;
		for (AbstractKostalPlenticoreCharger charger : this.chargers) {
			productionPower = TypeUtils.sum(productionPower, charger.getActualPower().get());
		}
		return productionPower;
	}

	protected void updatePowerAndEnergyChannels() {
		if (this.chargers != null && this.chargers.size() > 0) {
			var productionPower = this.calculatePvProduction();
			final Channel<Float> pBattery1Channel = this.channel(KostalPlenticore.ChannelId.BATTERY_CHARGE_POWER);
			var dcDischargePower = pBattery1Channel.value().orElse(0f);
			var acActivePower = TypeUtils.sum(productionPower, Math.round(dcDischargePower));
			

			
			
			/*
			 * Update AC Active Power
			 */
			IntegerReadChannel activePowerChannel = this.channel(this.activePowerChannelId);
			activePowerChannel.setNextValue(acActivePower);

			/*
			 * Calculate AC Energy
			 */
			if (acActivePower == null) {
				// Not available
				this.calculateAcChargeEnergy.update(null);
				this.calculateAcDischargeEnergy.update(null);
			} else if (acActivePower > 0) {
				// Discharge
				this.calculateAcChargeEnergy.update(0);
				this.calculateAcDischargeEnergy.update(acActivePower);
			} else {
				// Charge
				this.calculateAcChargeEnergy.update(acActivePower * -1);
				this.calculateAcDischargeEnergy.update(0);
			}

			/*
			 * Update DC Discharge Power
			 */
			IntegerReadChannel dcDischargePowerChannel = this.channel(this.dcDischargePowerChannelId);
			dcDischargePowerChannel.setNextValue(dcDischargePower);

			/*
			 * Calculate DC Energy
			 */
			if (dcDischargePower == null) {
				// Not available
				this.calculateDcChargeEnergy.update(null);
				this.calculateDcDischargeEnergy.update(null);
			} else if (dcDischargePower > 0) {
				// Discharge
				this.calculateDcChargeEnergy.update(0);
				this.calculateDcDischargeEnergy.update(Math.round(dcDischargePower));
			} else {
				// Charge
				this.calculateDcChargeEnergy.update(Math.round(dcDischargePower * -1));
				this.calculateDcDischargeEnergy.update(0);
			}
			
		}
	}
}
