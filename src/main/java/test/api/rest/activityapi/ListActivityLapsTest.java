package test.api.rest.activityapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import javastrava.api.v3.model.StravaActivity;
import javastrava.api.v3.model.StravaLap;
import javastrava.api.v3.model.reference.StravaResourceState;
import javastrava.api.v3.service.exception.UnauthorizedException;

import org.junit.Test;

import test.api.model.StravaLapTest;
import test.api.rest.APITest;
import test.utils.RateLimitedTestRunner;
import test.utils.TestUtils;

public class ListActivityLapsTest extends APITest {

	/**
	 * <p>
	 * Attempt to list the {@link StravaLap laps} in an {@link StravaActivity} which has laps
	 * </p>
	 *
	 * @throws Exception
	 *
	 * @throws UnauthorizedException
	 *             Thrown when security token is invalid
	 */
	@Test
	public void testListActivityLaps_hasLaps() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = api().listActivityLaps(TestUtils.ACTIVITY_WITH_LAPS);

			assertNotNull("Laps not returned for an activity which should have them", laps);
			assertNotEquals("No laps returned for an activity which should have them", 0, laps.length);
			for (final StravaLap lap : laps) {
				if (lap.getResourceState() != StravaResourceState.META) {
					assertEquals(TestUtils.ACTIVITY_WITH_LAPS, lap.getActivity().getId());
				}
				StravaLapTest.validateLap(lap, lap.getId(), lap.getResourceState());
			}
		});
	}

	/**
	 * <p>
	 * Attempt to list the {@link StravaLap laps} in an {@link StravaActivity} which has NO laps
	 * </p>
	 *
	 * <p>
	 * Should return an empty array of {@link StravaLap}
	 * </p>
	 *
	 * @throws Exception
	 *
	 * @throws UnauthorizedException
	 *             Thrown when security token is invalid
	 */
	@Test
	public void testListActivityLaps_hasNoLaps() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = api().listActivityLaps(TestUtils.ACTIVITY_WITHOUT_LAPS);

			assertNotNull("Laps not returned for an activity which should have them", laps);
			assertNotEquals("No laps returned for an activity which should have them", 0, laps.length);
			for (final StravaLap lap : laps) {
				if (lap.getResourceState() != StravaResourceState.META) {
					assertEquals(TestUtils.ACTIVITY_WITHOUT_LAPS, lap.getActivity().getId());
				}
				StravaLapTest.validateLap(lap, lap.getId(), lap.getResourceState());
			}
		});
	}

	/**
	 * <p>
	 * Attempt to list the {@link StravaLap laps} in a non-existent {@link StravaActivity}
	 * </p>
	 *
	 * <p>
	 * Should return <code>null</code>
	 * </p>
	 *
	 * @throws Exception
	 *
	 * @throws UnauthorizedException
	 *             Thrown when security token is invalid
	 */
	@Test
	public void testListActivityLaps_invalidActivity() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = api().listActivityLaps(TestUtils.ACTIVITY_INVALID);

			assertNull("Laps returned for an invalid activity", laps);
		});
	}

	@Test
	public void testListActivityLaps_privateActivity() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = api().listActivityLaps(TestUtils.ACTIVITY_PRIVATE_OTHER_USER);

			assertNotNull(laps);
			assertEquals(0, laps.length);
		});
	}

	@Test
	public void testListActivityLaps_privateActivityWithoutViewPrivate() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = api().listActivityLaps(TestUtils.ACTIVITY_PRIVATE);
			assertNotNull(laps);
			assertEquals(0, laps.length);
		});
	}

	@Test
	public void testListActivityLaps_privateActivityWithViewPrivate() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaLap[] laps = apiWithViewPrivate().listActivityLaps(TestUtils.ACTIVITY_PRIVATE);
			assertNotNull(laps);
			assertEquals(0, laps.length);
		});
	}

}