package io.openems.edge.kostal.plenticore.gridmeter;

import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.meter.api.ElectricityMeter;

public interface KostalGridmeter extends ElectricityMeter, OpenemsComponent {

	public static enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		COSINUS_PHI(Doc.of(OpenemsType.FLOAT)), //;
		TOTAL_APPARENT_POWER(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT_AMPERE)); //;
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
