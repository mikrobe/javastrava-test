package test.service.impl.clubservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import javastrava.api.v3.model.StravaActivity;
import test.api.model.StravaActivityTest;
import test.service.standardtests.ListMethodTest;
import test.service.standardtests.callbacks.ListCallback;
import test.utils.RateLimitedTestRunner;
import test.utils.TestUtils;

/**
 * <p>
 * Specific tests for listAllRecentClubActivities methods
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class ListAllRecentClubActivitiesTest extends ListMethodTest<StravaActivity, Integer> {
	/**
	 * <p>
	 * Check that no activity flagged as private is returned
	 * </p>
	 *
	 * @throws Exception
	 *             if test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testListAllRecentClubActivities_checkPrivacy() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final List<StravaActivity> activities = TestUtils.strava().listAllRecentClubActivities(TestUtils.CLUB_PUBLIC_MEMBER_ID);
			for (final StravaActivity activity : activities) {
				if (activity.getPrivateActivity().equals(Boolean.TRUE)) {
					fail("List recent club activities returned an activity flagged as private!"); //$NON-NLS-1$
				}
			}
		});
	}

	/**
	 * <p>
	 * Check can list activities for a private club which the authenticated user is a member
	 * </p>
	 *
	 * @throws Exception
	 *             if test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testListAllRecentClubActivities_privateClubMember() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final List<StravaActivity> activities = TestUtils.strava()
					.listAllRecentClubActivities(TestUtils.CLUB_PRIVATE_MEMBER_ID);
			assertNotNull(activities);
			for (final StravaActivity activity : activities) {
				StravaActivityTest.validateActivity(activity);
			}
		});
	}

	/**
	 * <p>
	 * Check CANNOT list activities for a private club which the authenticated user is NOT a member
	 * </p>
	 *
	 * @throws Exception
	 *             if test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testListAllRecentClubActivities_privateClubNonMember() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final List<StravaActivity> activities = TestUtils.strava()
					.listAllRecentClubActivities(TestUtils.CLUB_PRIVATE_NON_MEMBER_ID);
			assertNotNull(activities);
			assertEquals(0, activities.size());
		});
	}

	/**
	 * <p>
	 * Check CAN list activities for a public club which the authenticated user is NOT a member
	 * </p>
	 *
	 * @throws Exception
	 *             if test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testListAllRecentClubActivities_publicClubNonMember() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final List<StravaActivity> activities = TestUtils.strava()
					.listAllRecentClubActivities(TestUtils.CLUB_PUBLIC_NON_MEMBER_ID);
			assertNotNull(activities);
			for (final StravaActivity activity : activities) {
				StravaActivityTest.validateActivity(activity);
			}
		});
	}

	@Override
	protected ListCallback<StravaActivity, Integer> lister() {
		return ((strava, id) -> strava.listAllRecentClubActivities(id));
	}

	@Override
	protected Integer idPrivate() {
		return null;
	}

	@Override
	protected Integer idPrivateBelongsToOtherUser() {
		return null;
	}

	@Override
	protected Integer idValidWithEntries() {
		return TestUtils.CLUB_VALID_ID;
	}

	@Override
	protected Integer idValidWithoutEntries() {
		return null;
	}

	@Override
	protected Integer idInvalid() {
		return TestUtils.CLUB_VALID_ID;
	}

	@Override
	protected void validate(StravaActivity object) {
		StravaActivityTest.validateActivity(object);
	}

}