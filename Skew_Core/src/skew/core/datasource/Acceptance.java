package skew.core.datasource;

/**
 * Indicators of how likely a file will be accepted by a datasource. Accept indicates a certainty or a strong
 * possibility that the data will be accepted by a datasource. Reject indicates that the data will not be accepted
 * by the datasource. Maybe is useful for datasources which use common file extensions (eg. dat) and for which
 * scanning the file to determine if it is acceptable would take too long. 
 * @author Nathaniel Sherry
 *
 */

public enum Acceptance
{
	REJECT, MAYBE, ACCEPT
}
