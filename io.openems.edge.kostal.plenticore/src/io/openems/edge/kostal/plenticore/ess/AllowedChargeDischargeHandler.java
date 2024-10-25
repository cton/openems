package io.openems.edge.kostal.plenticore.ess;

import io.openems.edge.battery.api.Battery;
import io.openems.edge.batteryinverter.api.SymmetricBatteryInverter;
import io.openems.edge.common.channel.FloatReadChannel;
import io.openems.edge.common.component.ClockProvider;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.ess.generic.common.AbstractAllowedChargeDischargeHandler;
import io.openems.edge.kostal.plenticore.common.KostalPlenticore;

public class AllowedChargeDischargeHandler extends AbstractAllowedChargeDischargeHandler<KostalPlenticoreHybridEssImpl> {

		public AllowedChargeDischargeHandler(KostalPlenticoreHybridEssImpl parent) {
			super(parent);
		}

		@Override
		public void accept(ClockProvider clockProvider, Battery battery, SymmetricBatteryInverter inverter) {
			this.accept(clockProvider);
		}

		/**
		 * Calculates AllowedChargePower and AllowedDischargePower and sets the
		 * Channels.
		 *
		 * @param clockProvider a {@link ClockProvider}
		 */
		public void accept(ClockProvider clockProvider) {
			FloatReadChannel bmsChargePmaxChannel = parent.channel(KostalPlenticore.ChannelId.MAXIMUM_CHARGE_POWER_LIMIT);
			
			FloatReadChannel bmsDischargePmaxChannel = parent.channel(KostalPlenticore.ChannelId.MAXIMUM_DISCHARGE_POWER_LIMIT);
			
			FloatReadChannel wbmsVoltageChannel = parent.channel(KostalPlenticore.ChannelId.BATTERY_VOLTAGE);
			var wbmsVoltage = Integer.valueOf(Math.round(wbmsVoltageChannel.value().orElse(0f)));
			var bmsChargeImax = Integer.valueOf(Math.round(bmsChargePmaxChannel.value().orElse(0f)/wbmsVoltage));
			var bmsDischargeImax = Integer.valueOf(Math.round(bmsDischargePmaxChannel.value().orElse(0f)/wbmsVoltage));
			this.calculateAllowedChargeDischargePower(clockProvider, true, bmsChargeImax, bmsDischargeImax, wbmsVoltage);

			// Battery limits
			var batteryAllowedChargePower = Math.round(this.lastBatteryAllowedChargePower);
			var batteryAllowedDischargePower = Math.round(this.lastBatteryAllowedDischargePower);

			// PV-Production
			var pvProduction = Math.max(//
					TypeUtils.orElse(//
							TypeUtils.subtract(this.parent.getActivePower().get(), this.parent.getDcDischargePower().get()), //
							0),
					0);

			// Apply AllowedChargePower and AllowedDischargePower
			this.parent._setAllowedChargePower(batteryAllowedChargePower * -1 /* invert charge power */);
			this.parent._setAllowedDischargePower(batteryAllowedDischargePower + pvProduction);
		}
}
