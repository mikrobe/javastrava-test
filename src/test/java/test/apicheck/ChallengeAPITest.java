package test.apicheck;

import org.junit.Test;

import javastrava.api.API;
import javastrava.api.ChallengeAPI;
import javastrava.config.JavastravaApplicationConfig;
import javastrava.model.StravaChallenge;
import retrofit.client.Response;
import test.service.standardtests.data.ChallengeDataUtils;
import test.utils.TestUtils;

/**
 * <p>
 * Check that the API returns no more data than that which is configured in the model
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class ChallengeAPITest {
	private static ChallengeAPI api() {
		return API.instance(ChallengeAPI.class, TestUtils.getValidToken());
	}

	/**
	 * <p>
	 * Test the {@link API#getChallenge(Integer)} endpoint
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails for an unexpected reason
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testAPI_getChallenge() throws Exception {
		// Can only run the test if the challenges endpoint is enabled
		if (JavastravaApplicationConfig.STRAVA_ALLOWS_CHALLENGES_ENDPOINT) {
			final Response response = api().getChallengeRaw(ChallengeDataUtils.CHALLENGE_VALID_ID);
			ResponseValidator.validate(response, StravaChallenge.class, "getChallenge"); //$NON-NLS-1$
		}
	}

	/**
	 * <p>
	 * Test the {@link API#listJoinedChallenges()} endpoint
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails for an unexpected reason
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testAPI_listJoinedChallenges() throws Exception {
		// Can only run the test if the challenges endpoint is enabled
		if (JavastravaApplicationConfig.STRAVA_ALLOWS_CHALLENGES_ENDPOINT) {
			final Response response = api().listJoinedChallengesRaw();
			ResponseValidator.validate(response, StravaChallenge.class, "listJoinedChallenges"); //$NON-NLS-1$
		}
	}
}
