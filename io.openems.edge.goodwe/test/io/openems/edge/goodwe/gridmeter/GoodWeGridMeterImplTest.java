package io.openems.edge.goodwe.gridmeter;

import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeter.ChannelId.EXTERNAL_METER_RATIO;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeter.ChannelId.METER_CON_CORRECTLY_L1;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeter.ChannelId.METER_CON_INCORRECTLY_L1;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeter.ChannelId.METER_CON_REVERSE_L1;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeterCategory.COMMERCIAL_METER;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeterCategory.SMART_METER;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeterImpl.calculateRatio;
import static io.openems.edge.goodwe.gridmeter.GoodWeGridMeterImpl.getPhaseConnectionValue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.openems.edge.bridge.modbus.test.DummyModbusBridge;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.ess.power.api.Phase;

public class GoodWeGridMeterImplTest {

	@Test
	public void test() throws Exception {
		final var sut = new GoodWeGridMeterImpl();

		new ComponentTest(sut) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge("modbus0")) //
				.activate(MyConfig.create() //
						.setId("meter0") //
						.setModbusId("modbus0") //
						.setGoodWeMeterCategory(SMART_METER) //
						.setExternalMeterRatioValueA(0) //
						.setExternalMeterRatioValueB(0) //
						.build()) //
				.next(new TestCase() //
						.onBeforeProcessImage(() -> sut.convertMeterConnectStatus(null))
						.output(METER_CON_CORRECTLY_L1, false) //
						.output(METER_CON_INCORRECTLY_L1, false) //
						.output(METER_CON_REVERSE_L1, false)) //
				.next(new TestCase() //
						.onBeforeProcessImage(() -> sut.convertMeterConnectStatus(1))
						.output(METER_CON_CORRECTLY_L1, true) //
						.output(METER_CON_INCORRECTLY_L1, false) //
						.output(METER_CON_REVERSE_L1, false)) //
				.next(new TestCase() //
						.onBeforeProcessImage(() -> sut.convertMeterConnectStatus(2))
						.output(METER_CON_CORRECTLY_L1, false) //
						.output(METER_CON_INCORRECTLY_L1, false) //
						.output(METER_CON_REVERSE_L1, true)) //
				.next(new TestCase() //
						.onBeforeProcessImage(() -> sut.convertMeterConnectStatus(4))
						.output(METER_CON_CORRECTLY_L1, false) //
						.output(METER_CON_INCORRECTLY_L1, true) //
						.output(METER_CON_REVERSE_L1, false));
	}

	@Test
	public void testMeterConnectStateConverter() throws Exception {

		var l1Result = getPhaseConnectionValue(Phase.L1, 0x0124);
		var l2Result = getPhaseConnectionValue(Phase.L2, 0x0124);
		var l3Result = getPhaseConnectionValue(Phase.L3, 0x0124);

		assertEquals(4, (int) l1Result);
		assertEquals(2, (int) l2Result);
		assertEquals(1, (int) l3Result);

		l1Result = getPhaseConnectionValue(Phase.L1, 0x0524);
		l2Result = getPhaseConnectionValue(Phase.L2, 0x0462);
		l3Result = getPhaseConnectionValue(Phase.L3, 0x1647);

		assertEquals(4, (int) l1Result);
		assertEquals(6, (int) l2Result);
		assertEquals(6, (int) l3Result);

		var l1NoResult = getPhaseConnectionValue(Phase.L1, 0x000);
		var l2NoResult = getPhaseConnectionValue(Phase.L2, 0x000);
		var l3NoResult = getPhaseConnectionValue(Phase.L3, 0x000);

		assertEquals(0, (int) l1NoResult);
		assertEquals(0, (int) l2NoResult);
		assertEquals(0, (int) l3NoResult);

		var noResult = getPhaseConnectionValue(Phase.L3, 0x000);

		assert noResult == 0x000;
	}

	@Test
	public void testExternalMeterRatio() throws Exception {
		new ComponentTest(new GoodWeGridMeterImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge("modbus0")) //
				.activate(MyConfig.create() //
						.setId("meter0") //
						.setModbusId("modbus0") //
						.setGoodWeMeterCategory(COMMERCIAL_METER) //
						.setExternalMeterRatioValueA(3000) //
						.setExternalMeterRatioValueB(5) //
						.build()) //
				.next(new TestCase() //
						.output(EXTERNAL_METER_RATIO, 600));

		new ComponentTest(new GoodWeGridMeterImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge("modbus0")) //
				.activate(MyConfig.create() //
						.setId("meter0") //
						.setModbusId("modbus0") //
						.setGoodWeMeterCategory(COMMERCIAL_METER) //
						.setExternalMeterRatioValueA(500) //
						.setExternalMeterRatioValueB(5) //
						.build()) //
				.next(new TestCase() //
						.output(EXTERNAL_METER_RATIO, 100));

		new ComponentTest(new GoodWeGridMeterImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("setModbus", new DummyModbusBridge("modbus0")) //
				.activate(MyConfig.create() //
						.setId("meter0") //
						.setModbusId("modbus0") //
						.setGoodWeMeterCategory(SMART_METER) //
						.setExternalMeterRatioValueA(3000) //
						.setExternalMeterRatioValueB(5) //
						.build()) //
				.next(new TestCase() //
						.output(EXTERNAL_METER_RATIO, null));
	}

	@Test
	public void testCalculateRatio() {
		assertEquals(600, calculateRatio(3000, 5).intValue());
		assertEquals(100, calculateRatio(500, 5).intValue());
		assertEquals(null, calculateRatio(-5, 5));
		assertEquals(null, calculateRatio(3000, 0));
		assertEquals(null, calculateRatio(500, -5));
	}

}
