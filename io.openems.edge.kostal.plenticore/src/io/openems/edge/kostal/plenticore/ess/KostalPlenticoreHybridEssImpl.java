package io.openems.edge.kostal.plenticore.ess;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.ChannelAddress;
import io.openems.common.types.OpenemsType;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.ess.api.HybridEss;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.kostal.plenticore.common.AbstractKostalPlenticore;
import io.openems.edge.kostal.plenticore.common.KostalPlenticore;
import io.openems.edge.timedata.api.Timedata;
import io.openems.edge.timedata.api.TimedataProvider;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Kostal.Plenticore.Hybrid.Ess", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
		EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE //
})
public class KostalPlenticoreHybridEssImpl extends AbstractKostalPlenticore
		implements KostalPlenticoreHybridEss, KostalPlenticore, SymmetricEss, HybridEss, ModbusComponent,
		OpenemsComponent, TimedataProvider, EventHandler, ModbusSlave {

	private static final int MAX_POWER_RAMP = 500; // [W/sec]

	private Config config;

	private final Logger log = LoggerFactory.getLogger(KostalPlenticoreHybridEssImpl.class);

	@Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.OPTIONAL)
	private volatile Timedata timedata = null;

	@Reference
	protected ConfigurationAdmin cm;

	@Reference
	private Power power;

	// @Reference
	// private Sum sum;

	@Reference
	private ComponentManager componentManager;

	@Override
	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws OpenemsNamedException {
		this.config = config;
		if (super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm,
				"Modbus", config.modbus_id())) {
			return;
		}
		this._setCapacity(this.config.capacity());
		this._setGridMode(GridMode.ON_GRID);
		// Set Max-Apparent-Power
		this.timedata.getLatestValue(new ChannelAddress(config.id(), SymmetricEss.ChannelId.MAX_APPARENT_POWER.id()))
				.thenAccept(latestValue -> {
					Integer lastMaxApparentPower = TypeUtils.getAsType(OpenemsType.INTEGER, latestValue);
					if (lastMaxApparentPower != null
							&& lastMaxApparentPower != 10_000 /* throw away value that was previously fixed */) {
						this._setMaxApparentPower(lastMaxApparentPower);
					} else {
						this._setMaxApparentPower(MAX_POWER_RAMP); // start low
					}
				});
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	public KostalPlenticoreHybridEssImpl() throws OpenemsNamedException {
		super(//
				SymmetricEss.ChannelId.ACTIVE_POWER, //
				SymmetricEss.ChannelId.REACTIVE_POWER, //
				HybridEss.ChannelId.DC_DISCHARGE_POWER, //
				SymmetricEss.ChannelId.ACTIVE_CHARGE_ENERGY, //
				SymmetricEss.ChannelId.ACTIVE_DISCHARGE_ENERGY, //
				HybridEss.ChannelId.DC_CHARGE_ENERGY, //
				HybridEss.ChannelId.DC_DISCHARGE_ENERGY, //
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				SymmetricEss.ChannelId.values(), //
				ManagedSymmetricEss.ChannelId.values(), //
				HybridEss.ChannelId.values(), //
				KostalPlenticore.ChannelId.values(), //
				KostalPlenticoreHybridEss.ChannelId.values());
	}

	@Override
	public String debugLog() {
		return "SoC:" + this.getSoc().asString() //
				+ "|L:" + this.getActivePower().asString() //
				+ "|Allowed:"
				+ this.channel(ManagedSymmetricEss.ChannelId.ALLOWED_CHARGE_POWER).value().asStringWithoutUnit() + ";"
				+ this.channel(ManagedSymmetricEss.ChannelId.ALLOWED_DISCHARGE_POWER).value().asString(); //
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}

		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			this.updatePowerAndEnergyChannels();
			break;
		}
	}

	@Override
	public Timedata getTimedata() {
		return this.timedata;
	}

	@Override
	public Integer getSurplusPower() {
		// TODO logic is insufficient
		if (this.getSoc().orElse(0) < 99) {
			return null;
		}
		var productionPower = this.calculatePvProduction();
		if (productionPower == null || productionPower < 100) {
			return null;
		}
		return productionPower;
	}

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(//
				OpenemsComponent.getModbusSlaveNatureTable(accessMode), //
				SymmetricEss.getModbusSlaveNatureTable(accessMode), //
				ManagedSymmetricEss.getModbusSlaveNatureTable(accessMode), //
				HybridEss.getModbusSlaveNatureTable(accessMode), //
				ModbusSlaveNatureTable.of(KostalPlenticoreHybridEss.class, accessMode, 100) //
						.build());
	}

	public int getMaxChargeDischargePower() {
		return this.config.maxBatteryPower();
	}

	@Override
	protected void updatePowerAndEnergyChannels() {
		super.updatePowerAndEnergyChannels();

		var productionPower = this.calculatePvProduction();
		final Channel<Float> pBattery1Channel = this.channel(KostalPlenticore.ChannelId.BATTERY_CHARGE_POWER);
		var dcDischargePower = pBattery1Channel.value().orElse(0f);
		var activePower = Math.round(dcDischargePower - productionPower) * -1; // invert

		// Handle MaxApparentPower
		if (Math.abs(activePower) + MAX_POWER_RAMP > this.getMaxApparentPower().orElse(Integer.MAX_VALUE)) {
			this._setMaxApparentPower(Math.abs(activePower) + MAX_POWER_RAMP);
		}

	}
}
