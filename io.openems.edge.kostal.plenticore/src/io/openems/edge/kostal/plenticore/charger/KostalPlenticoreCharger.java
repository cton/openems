package io.openems.edge.kostal.plenticore.charger;

import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;

public interface KostalPlenticoreCharger extends OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {

		DC_CURRENT(Doc.of(OpenemsType.FLOAT).unit(Unit.AMPERE)), //
		DC_VOLTAGE(Doc.of(OpenemsType.FLOAT).unit(Unit.VOLT)); //;
		
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