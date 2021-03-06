package test.service.impl.activityservice;

import static org.junit.Assume.assumeFalse;

import javastrava.model.StravaLap;
import test.issues.strava.Issue105;
import test.service.standardtests.ListMethodTest;
import test.service.standardtests.callbacks.ListCallback;
import test.service.standardtests.data.ActivityDataUtils;

/**
 * <p>
 * Specific tests for list activity laps methods
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class ListActivityLapsTest extends ListMethodTest<StravaLap, Long> {

	@Override
	protected Class<StravaLap> classUnderTest() {
		return StravaLap.class;
	}

	@Override
	protected Long idInvalid() {
		return ActivityDataUtils.ACTIVITY_INVALID;
	}

	@Override
	protected Long idPrivate() {
		return ActivityDataUtils.ACTIVITY_PRIVATE;
	}

	@Override
	public Long idPrivateBelongsToOtherUser() {
		return ActivityDataUtils.ACTIVITY_PRIVATE_OTHER_USER;
	}

	@Override
	public Long idValidWithEntries() {
		return ActivityDataUtils.ACTIVITY_WITH_LAPS;
	}

	@Override
	public Long idValidWithoutEntries() {
		return ActivityDataUtils.ACTIVITY_WITHOUT_LAPS;
	}

	@Override
	protected ListCallback<StravaLap, Long> lister() {
		return ((strava, id) -> strava.listActivityLaps(id));
	}

	@Override
	public void testValidParentWithNoEntries() throws Exception {
		assumeFalse(Issue105.issue());
		super.testValidParentWithNoEntries();
	}

	@Override
	protected void validate(final StravaLap object) {
		ActivityDataUtils.validateLap(object);
	}

}
