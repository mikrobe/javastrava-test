/**
 *
 */
package test.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javastrava.api.v3.service.impl.StravaServiceImpl;
import javastrava.config.StravaConfig;

/**
 * <p>
 * Tests for Strava rate limiting aspects
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class StravaServiceImplTest {
	/**
	 * Check that the calculation of daily rate limit excession works
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRequestRateDailyPercentageExceeded() {
		StravaServiceImpl.requestRateDaily = StravaConfig.RATE_LIMIT_DAILY + 1;
		assertTrue(100f < StravaServiceImpl.requestRateDailyPercentage());
	}

	/**
	 * Check that the calculation of daily rate limit excession works
	 */
	@SuppressWarnings("static-method")
	@Test
	/**
	 * Check that the calculation of rate limit excession works
	 */
	public void testRequestRateDailyPercentageWarning() {
		StravaServiceImpl.requestRateDaily = StravaConfig.RATE_LIMIT_DAILY;
		assertTrue(100f == StravaServiceImpl.requestRateDailyPercentage());
	}

	/**
	 * Check that the calculation of rate limit excession works
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRequestRatePercentageExceeded() {
		StravaServiceImpl.requestRate = StravaConfig.RATE_LIMIT + 1;
		assertTrue(100f < StravaServiceImpl.requestRatePercentage());
	}

	/**
	 * Check that the calculation of daily rate limit excession works
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testRequestRatePercentageWarning() {
		StravaServiceImpl.requestRate = StravaConfig.RATE_LIMIT;
		assertTrue(100f == StravaServiceImpl.requestRatePercentage());
	}
}
