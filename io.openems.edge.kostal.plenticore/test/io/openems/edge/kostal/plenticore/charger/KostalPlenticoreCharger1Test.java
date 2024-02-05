package io.openems.edge.kostal.plenticore.charger;

import org.junit.Test;

import io.openems.edge.bridge.modbus.test.DummyModbusBridge;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;


public class KostalPlenticoreCharger1Test {
	private static final String MODBUS_ID = "modbus0";
	private static final String ESS_ID = "ess0";
	private static final String CHARGER_ID = "charger0";

	@Test
	public void test() throws Exception {
		new ComponentTest(new KostalPlenticoreCharger1()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge(MODBUS_ID)) //
				.activate(MyConfig.create() //
						.setId(CHARGER_ID) //
						.setEssId(ESS_ID) //
						.setModbusId(MODBUS_ID) //
						.build());
	}
}
