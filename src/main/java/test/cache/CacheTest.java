package test.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import javastrava.api.API;
import javastrava.auth.model.Token;
import javastrava.cache.StravaCache;
import javastrava.cache.impl.StravaCacheImpl;
import javastrava.model.StravaActivity;
import javastrava.model.StravaAthlete;
import javastrava.model.reference.StravaResourceState;
import javastrava.service.Strava;
import test.api.APITest;
import test.service.standardtests.data.AthleteDataUtils;
import test.utils.RateLimitedTestRunner;
import test.utils.TestUtils;

/**
 * <p>
 * Test caching behaviour
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class CacheTest extends APITest<StravaCache<?, ?>> {
	private static StravaCache<StravaActivity, Long> activityCache(final boolean populate) throws Exception {
		final Token token = TestUtils.getValidToken();
		final StravaCache<StravaActivity, Long> cache = new StravaCacheImpl<StravaActivity, Long>(StravaActivity.class, token);
		assertEquals(0, cache.size());
		if (!populate) {
			return cache;
		}
		final API api = new API(token);
		final StravaActivity[] activities = api.listAuthenticatedAthleteActivities(null, null, null, null);
		final StravaActivity detailedActivity = api.getActivity(activities[0].getId(), Boolean.FALSE);
		detailedActivity.setResourceState(StravaResourceState.DETAILED);
		cache.put(detailedActivity);

		assertNotEquals(0, cache.size());
		return cache;
	}

	/**
	 * Generates and populates a cache of athletes
	 *
	 * @param populate
	 *            Should the returned cache be populated
	 * @return The cache, populated with athletes (list of friends of the authenticated user) if required
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	private static StravaCache<StravaAthlete, Integer> athleteCache(final boolean populate) throws Exception {
		final Token token = TestUtils.getValidToken();
		final StravaCache<StravaAthlete, Integer> cache = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token);
		assertEquals(0, cache.size());
		if (!populate) {
			return cache;
		}
		final API api = new API(token);
		final StravaAthlete athlete = api.getAuthenticatedAthlete();
		athlete.setResourceState(StravaResourceState.DETAILED);
		cache.put(athlete);
		return cache;
	}

	/**
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testActivityCache() throws Exception {
		RateLimitedTestRunner.run(() -> {
			StravaCache<StravaActivity, Long> cache = activityCache(true);
			assertNotNull(cache);
			assertFalse(cache.size() == 0);
			cache = activityCache(false);
			assertEquals(0, cache.size());
		});
	}

	/**
	 * Test that the athlete cache generator actually does generate a cache, and puts athletes in it
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testAthleteCache() throws Exception {
		RateLimitedTestRunner.run(() -> {
			StravaCache<StravaAthlete, Integer> cache = athleteCache(true);
			assertNotNull(cache);
			assertFalse(cache.size() == 0);
			cache = athleteCache(false);
			assertEquals(0, cache.size());
		});
	}

	/**
	 * <p>
	 * Test that when clearing the cache, there are no remaining accessible entities in there
	 * </p>
	 *
	 * @throws Exception
	 *             For whatever reason
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_clear() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(true);
			assertFalse(cache.size() == 0);
			cache.removeAll();
			assertEquals(0, cache.size());
		});
	}

	/**
	 * <p>
	 * Test that clearing the cache for one class doesn't clear all the other classes
	 * </p>
	 *
	 * @throws Exception
	 *             For whatever unexpected reason
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_clearDoesntClearOtherClasses() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache1 = athleteCache(true);
			final StravaCache<StravaActivity, Long> cache2 = activityCache(true);
			cache1.removeAll();
			assertEquals(0, cache1.size());
			assertFalse(cache2.size() == 0);
		});
	}

	/**
	 * <p>
	 * Test that clearing one token's entire cache doesn't clear data from other tokens' caches
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_clearDoesntClearOtherTokens() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final Token token1 = TestUtils.getValidToken();
			final API api1 = new API(token1);
			final StravaCache<StravaAthlete, Integer> cache1 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token1);
			final StravaAthlete athlete = api1.getAuthenticatedAthlete();
			athlete.setResourceState(StravaResourceState.DETAILED);
			cache1.put(athlete);

			final Token token2 = TestUtils.getValidTokenWithFullAccess();
			final API api2 = new API(token2);
			final StravaCache<StravaAthlete, Integer> cache2 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token2);
			final StravaAthlete athlete2 = api2.getAuthenticatedAthlete();
			athlete2.setResourceState(StravaResourceState.DETAILED);
			cache2.put(athlete2);
			assertFalse(0 == cache2.size());

			cache1.removeAll();
			assertFalse(0 == cache2.size());

		});

	}

	/**
	 * <p>
	 * Test that putting two different object with the same id and class results in the second put overwriting the first
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_doublePutReplacesObject() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			StravaAthlete athlete = api().getAuthenticatedAthlete();
			cache.put(athlete);
			athlete.setFirstname("Bob"); //$NON-NLS-1$
			final Integer id = athlete.getId();
			cache.put(athlete);
			assertEquals(1, cache.size());
			athlete = null;
			athlete = cache.get(id);
			assertNotNull(athlete);
			assertEquals("Bob", athlete.getFirstname()); //$NON-NLS-1$
		});
	}

	/**
	 * <p>
	 * Test that asking for an item NOT in the cache returns null and does not throw an exception
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_getInvalid() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			StravaAthlete athlete = api().getAuthenticatedAthlete();
			cache.put(athlete);
			athlete = cache.get(AthleteDataUtils.ATHLETE_INVALID_ID);
			assertNull(athlete);
		});
	}

	/**
	 * <p>
	 * Test that asking for an object that IS in the cache returns the right object
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_getValid() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			final StravaAthlete athlete = api().getAuthenticatedAthlete();
			cache.put(athlete);
			final StravaAthlete athleteCached = cache.get(AthleteDataUtils.ATHLETE_AUTHENTICATED_ID);
			assertNotNull(athleteCached);
			assertEquals(AthleteDataUtils.ATHLETE_AUTHENTICATED_ID, athleteCached.getId());
		});

	}

	/**
	 * <p>
	 * Test that putting an object, then clearing the cache, then getting it, returns null
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_putClearGet() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			final StravaAthlete athlete = api().getAuthenticatedAthlete();
			cache.put(athlete);
			cache.removeAll();
			final StravaAthlete athleteCached = cache.get(AthleteDataUtils.ATHLETE_AUTHENTICATED_ID);
			assertNull(athleteCached);

		});
	}

	/**
	 * <p>
	 * Test that attempting to overwrite an item in cache with one that is NOT detailed doesn't work
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_putLessDetailedObject() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			final StravaAthlete athleteMeta = new StravaAthlete();
			athleteMeta.setId(new Integer(1));
			athleteMeta.setResourceState(StravaResourceState.META);
			final StravaAthlete athleteSummary = new StravaAthlete();
			athleteSummary.setResourceState(StravaResourceState.SUMMARY);
			athleteSummary.setId(new Integer(1));
			cache.put(athleteSummary);
			assertEquals(0, cache.size());
			cache.put(athleteMeta);
			assertEquals(0, cache.size());

		});
	}

	/**
	 * <p>
	 * Test that putting null is safe and doesn't fall over
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_putNull() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final StravaCache<StravaAthlete, Integer> cache = athleteCache(false);
			cache.put(null);
			assertEquals(0, cache.size());
		});

	}

	/**
	 * <p>
	 * Test that putting an object with one cache instance, then getting it with another cache instance associated with a different token, returns null
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_putWithOneInstanceGetWithAnotherDifferentToken() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final Token token1 = TestUtils.getValidTokenWithFullAccess();
			final Token token2 = TestUtils.getValidToken();
			assertFalse(token1.equals(token2));
			final StravaCache<StravaAthlete, Integer> cache1 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token1);
			final StravaCache<StravaAthlete, Integer> cache2 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token2);
			cache1.put(token1.getAthlete());
			final StravaAthlete athlete = cache2.get(token1.getAthlete().getId());
			assertNull(athlete);
		});

	}

	/**
	 * <p>
	 * Test that putting an object with one cache instance, then getting it with another instance associated with the same token, returns the object successfully
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_putWithOneInstanceGetWithAnotherSameToken() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final Token token = TestUtils.getValidToken();
			final StravaCache<StravaAthlete, Integer> cache1 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token);
			final StravaCache<StravaAthlete, Integer> cache2 = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token);
			cache1.put(token.getAthlete());
			final StravaAthlete athlete = cache2.get(token.getAthlete().getId());
			assertNotNull(athlete);
			assertEquals(token.getAthlete().getId(), athlete.getId());
		});

	}

	/**
	 * <p>
	 * Test that revoking a token results in the cache being empty for that token
	 * </p>
	 *
	 * @throws Exception
	 *             if the test fails in an unexpected way
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCache_revokeTokenEmptiesCache() throws Exception {
		RateLimitedTestRunner.run(() -> {
			final Token token = TestUtils.getValidToken();
			final StravaCache<StravaAthlete, Integer> cache = new StravaCacheImpl<StravaAthlete, Integer>(StravaAthlete.class, token);
			cache.put(token.getAthlete());
			final Strava strava = new Strava(token);
			strava.deauthorise(token);
			assertEquals(0, cache.size());
		});

	}

	/**
	 * @see test.api.APITest#validate(java.lang.Object)
	 */
	@Override
	protected void validate(final StravaCache<?, ?> result) throws Exception {
		return;

	}

}
