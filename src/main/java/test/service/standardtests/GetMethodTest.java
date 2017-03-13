package test.service.standardtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import javastrava.api.v3.model.StravaEntity;
import javastrava.api.v3.model.reference.StravaResourceState;
import test.service.standardtests.spec.GetTests;
import test.utils.RateLimitedTestRunner;
import test.utils.TestUtils;

/**
 * @author Dan Shannon
 *
 * @param <T>
 *            The type of Strava object under test
 * @param <U>
 *            The object type's identifier class
 */
public abstract class GetMethodTest<T extends StravaEntity<U>, U> extends MethodTest<T, U> implements GetTests<T, U> {

	protected abstract U getIdInvalid();

	protected abstract U getIdPrivate();

	protected abstract U getIdPrivateBelongsToOtherUser();

	protected abstract U getIdValid();

	@Override
	@Test
	public void testGetInvalidId() throws Exception {
		RateLimitedTestRunner.run(() -> {
			// Don't run if the id to test against is null
			if (getIdInvalid() == null) {
				return;
			}

			final U id = getIdInvalid();

			// If there's Nosaj Thing, then quit
			if (id == null) {
				return;
			}

			// Get the data - it should be null
			final T object = getter().get(TestUtils.strava(), id);

			assertNull("Retrieved object that has an invalid id!", object); //$NON-NLS-1$
		});
	}

	@Override
	public void testGetNullId() throws Exception {
		final T object = getter().get(TestUtils.strava(), null);
		assertNull(object);
	}

	@Override
	@Test
	public void testGetValidId() throws Exception {
		// Don't run if the id to test against is null
		if (getIdValid() == null) {
			return;
		}

		RateLimitedTestRunner.run(() -> {

			final U id = getIdValid();

			// If there's Nosaj Thing, then quit
			if (id == null) {
				return;
			}

			// Get the data - it should work; if it doesn't there'll be an Exception thrown
			final T object = getter().get(TestUtils.strava(), id);
			assertNotNull(object);
			validate(object);
		});
	}

	@Override
	@Test
	public void testInvalidId() throws Exception {
		// Catered for by testGetInvalidId()
		return;

	}

	@Override
	@Test
	public void testPrivateBelongsToOtherUser() throws Exception {
		// Don't run if the id to test against is null
		if (getIdPrivateBelongsToOtherUser() == null) {
			return;
		}

		RateLimitedTestRunner.run(() -> {
			final U id = getIdPrivateBelongsToOtherUser();

			// If there's Nosaj Thing, then quit
			if (id == null) {
				return;
			}

			// Get the data
			final T object = getter().get(TestUtils.strava(), id);
			assertEquals(StravaResourceState.PRIVATE, object.getResourceState());
		});

	}

	@Override
	@Test
	public void testPrivateWithNoViewPrivateScope() throws Exception {
		// Don't run if the id to test against is null
		if (getIdPrivate() == null) {
			return;
		}

		RateLimitedTestRunner.run(() -> {

			final U id = getIdPrivate();

			// If there's Nosaj Thing, then quit
			if (id == null) {
				return;
			}

			// Get the data - it should return an object with resource state PRIVATE
			final T object = getter().get(TestUtils.strava(), id);
			assertEquals(StravaResourceState.PRIVATE, object.getResourceState());
		});
	}

	@Override
	@Test
	public void testPrivateWithViewPrivateScope() throws Exception {
		// Don't run if the id to test against is null
		if (getIdPrivate() == null) {
			return;
		}

		RateLimitedTestRunner.run(() -> {

			final U id = getIdPrivate();

			// If there's Nosaj Thing, then quit
			if (id == null) {
				return;
			}

			// Get the data - it should work; if it doesn't there'll be an UnauthorisedException thrown
			final T object = getter().get(TestUtils.stravaWithViewPrivate(), id);
			assertNotNull(object);
			validate(object);
		});
	}

}
