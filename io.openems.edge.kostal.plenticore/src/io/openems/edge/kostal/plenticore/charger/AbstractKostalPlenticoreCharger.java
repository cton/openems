package io.openems.edge.kostal.plenticore.charger;

import static io.openems.edge.bridge.modbus.api.ElementToChannelConverter.SCALE_FACTOR_3;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.DummyRegisterElement;
import io.openems.edge.bridge.modbus.api.element.FloatDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.WordOrder;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.ess.dccharger.api.EssDcCharger;
import io.openems.edge.kostal.plenticore.common.KostalPlenticore;
import io.openems.edge.timedata.api.TimedataProvider;
import io.openems.edge.timedata.api.utils.CalculateEnergyFromPower;

public abstract class AbstractKostalPlenticoreCharger extends AbstractOpenemsModbusComponent
		implements EssDcCharger, ModbusComponent, OpenemsComponent, TimedataProvider, EventHandler, ModbusSlave {

	protected abstract KostalPlenticore getEss();

	private final CalculateEnergyFromPower calculateActualEnergy = new CalculateEnergyFromPower(this,
			EssDcCharger.ChannelId.ACTUAL_ENERGY);

	protected AbstractKostalPlenticoreCharger() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				EssDcCharger.ChannelId.values());
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
		final var startAddress = this.getStartAddress();
		return new ModbusProtocol(this, //
				new FC3ReadRegistersTask(startAddress, Priority.HIGH, //
						m(EssDcCharger.ChannelId.CURRENT,
								new FloatDoublewordElement(startAddress).wordOrder(WordOrder.LSWMSW), SCALE_FACTOR_3), //
						m(EssDcCharger.ChannelId.ACTUAL_POWER,
								new FloatDoublewordElement(startAddress + 2).wordOrder(WordOrder.LSWMSW)), //
						new DummyRegisterElement(startAddress + 4, startAddress + 7), //
						m(EssDcCharger.ChannelId.VOLTAGE,
								new FloatDoublewordElement(startAddress + 8).wordOrder(WordOrder.LSWMSW),
								SCALE_FACTOR_3)//
				));
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE:
			this.calculateEnergy();
			break;
		}
	}

	/**
	 * Calculate the Energy values from ActivePower.
	 */
	private void calculateEnergy() {
		var actualPower = this.getActualPower().get();
		if (actualPower == null) {
			// Not available
			this.calculateActualEnergy.update(null);
		} else if (actualPower > 0) {
			this.calculateActualEnergy.update(actualPower);
		} else {
			this.calculateActualEnergy.update(0);
		}
	}

	@Override
	public final String debugLog() {
		return "L:" + this.getActualPower().asString() //
				+ "|U:" + this.getVoltage().asString() //
				+ "|I:" + this.getCurrent().asString();
	}

	protected abstract int getStartAddress();

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(//
				OpenemsComponent.getModbusSlaveNatureTable(accessMode), //
				EssDcCharger.getModbusSlaveNatureTable(accessMode), //
				ModbusSlaveNatureTable.of(AbstractKostalPlenticoreCharger.class, accessMode, 100) //
						.build());
	}
}
