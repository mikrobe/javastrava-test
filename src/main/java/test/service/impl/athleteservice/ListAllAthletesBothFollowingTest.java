package test.service.impl.athleteservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import javastrava.model.StravaAthlete;
import javastrava.model.reference.StravaFollowerState;
import test.service.standardtests.ListMethodTest;
import test.service.standardtests.callbacks.ListCallback;
import test.service.standardtests.data.AthleteDataUtils;
import test.utils.RateLimitedTestRunner;
import test.utils.TestUtils;

/**
 * <p>
 * Specific tests for list all athletes both following methods
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class ListAllAthletesBothFollowingTest extends ListMethodTest<StravaAthlete, Integer> {
	@Override
	protected Class<StravaAthlete> classUnderTest() {
		return StravaAthlete.class;
	}

	@Override
	protected Integer idInvalid() {
		return AthleteDataUtils.ATHLETE_INVALID_ID;
	}

	@Override
	protected Integer idPrivate() {
		return null;
	}

	@Override
	protected Integer idPrivateBelongsToOtherUser() {
		return AthleteDataUtils.ATHLETE_PRIVATE_ID;
	}

	@Override
	protected Integer idValidWithEntries() {
		return AthleteDataUtils.ATHLETE_AUTHENTICATED_ID;
	}

	@Override
	protected Integer idValidWithoutEntries() {
		return AthleteDataUtils.ATHLETE_WITHOUT_FRIENDS;
	}

	@Override
	protected ListCallback<StravaAthlete, Integer> lister() {
		return ((strava, id) -> strava.listAllAthletesBothFollowing(id));
	}

	/**
	 * <p>
	 * Test it works even if you specify yourself
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testListAllAthletesBothFollowing_sameAthlete() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final List<StravaAthlete> athletes = TestUtils.strava().listAllAthletesBothFollowing(AthleteDataUtils.ATHLETE_AUTHENTICATED_ID);
			assertNotNull(athletes);
			int friendCount = 0;

			// Will have returned all the athletes that the authenticated user is following
			final List<StravaAthlete> friends = TestUtils.strava().listAllAuthenticatedAthleteFriends();
			for (final StravaAthlete athlete : friends) {
				if (athlete.getFriend() == StravaFollowerState.ACCEPTED) {
					friendCount++;
				}
			}
			assertEquals(friendCount, athletes.size());
		});
	}

	@Override
	protected void validate(StravaAthlete object) {
		AthleteDataUtils.validateAthlete(object);
	}

}
