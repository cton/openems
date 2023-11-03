package io.openems.edge.kostal.plenticore.ess;

import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.kostal.plenticore.common.KostalPlenticore;

public interface KostalPlenticoreHybridEss extends KostalPlenticore, SymmetricEss, OpenemsComponent {

	public static enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		;
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
