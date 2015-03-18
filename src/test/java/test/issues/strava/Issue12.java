package test.issues.strava;

import static org.junit.Assert.assertTrue;
import javastrava.api.v3.model.StravaActivity;
import javastrava.api.v3.model.StravaSegmentEffort;
import javastrava.api.v3.rest.ActivityAPI;
import javastrava.api.v3.rest.API;
import javastrava.api.v3.service.exception.NotFoundException;

import org.junit.Test;

import test.utils.TestUtils;

/**
 * <p>
 * These tests will PASS if issue <a href="https://github.com/danshannon/javastravav3api/issues/12">javastrava-api #12</a> remains
 * </p>
 * 
 * @author Dan Shannon
 * @see <a href="https://github.com/danshannon/javastravav3api/issues/12">https://github.com/danshannon/javastravav3api/issues/12</a>
 *
 */
public class Issue12 {
	@Test
	public void testIssue() throws NotFoundException {
		ActivityAPI retrofit = API.instance(ActivityAPI.class, TestUtils.getValidToken());
		StravaActivity activity = retrofit.getActivity(245713183, null);
		boolean issue = false;
		for (StravaSegmentEffort effort : activity.getSegmentEfforts()) {
			if (effort.getAthlete().getResourceState() == null) {
				issue = true;
			}
		}
		assertTrue(issue);
	}
}
