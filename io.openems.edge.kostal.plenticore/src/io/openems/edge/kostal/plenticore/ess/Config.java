package io.openems.edge.kostal.plenticore.ess;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "KOSTAL Plenticore Hybrid ESS", //
		description = "The energy storage system implementation of a KOSTAL Plenticore")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "ess0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;
	
	@AttributeDefinition(name = "Read-Only mode", description = "Enables Read-Only mode")
	boolean readOnlyMode() default false;

	@AttributeDefinition(name = "Modbus-ID", description = "ID of Modbus bridge.")
	String modbus_id() default "modbus0";
	
	@AttributeDefinition(name = "Modbus Unit-ID", description = "The Unit-ID of the Modbus device.")
	int modbusUnitId() default 71;
	
	@AttributeDefinition(name = "Capacity", description = "Capacity of the battery in [Wh]")
	int capacity() default 7_300;

	@AttributeDefinition(name = "Maximum power", description = "Maximum charge/discharge power in [W]")
	int maxBatteryPower() default 4_000;
	
	@AttributeDefinition(name = "Modbus target filter", description = "This is auto-generated by 'Modbus-ID'.")
	String Modbus_target() default "(enabled=true)";

	String webconsole_configurationFactory_nameHint() default "KOSTAL Plenticore Hybrid ESS [{id}]";
}
