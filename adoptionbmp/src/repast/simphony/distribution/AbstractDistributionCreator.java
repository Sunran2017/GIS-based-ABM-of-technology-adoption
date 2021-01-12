/**
 * 
 */
package repast.simphony.distribution;

import java.util.Map;

/**
 * Provides convenience methods for retrieving parameters from a Map, converting
 * to doubles and handling errors.
 * 
 * @author Nick Collier
 */
public abstract class AbstractDistributionCreator implements RSDistributionCreator {
	
	protected String strValue(String name, Map<String, String> attribs) {
		String val = attribs.get(name);
		if (val == null) {
			throw new DistributionCreationException(getDistributionName() + " distribution is missing a required '" + name + "' attribute.");
		}
		return val;
	}

	protected double doubleValue(String name, Map<String, String> attribs) {
		try {
			String val = strValue(name, attribs);
			return Double.parseDouble(val);
		} catch (NumberFormatException ex) {
			throw new DistributionCreationException("Invalid value for attribute '" + name + "' in "
					+ getDistributionName() + " distribution.");
		}
	}

	protected int intValue(String name, Map<String, String> attribs) {
		try {
			String val = strValue(name, attribs);
			return Integer.parseInt(val);
		} catch (NumberFormatException ex) {
			throw new DistributionCreationException("Invalid value for attribute '" + name + "' in "
					+ getDistributionName() + " distribution.");
		}
	}
}
