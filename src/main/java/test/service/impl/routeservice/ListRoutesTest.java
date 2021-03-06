package test.service.impl.routeservice;

import javastrava.model.StravaRoute;
import javastrava.service.Strava;
import test.service.standardtests.ListMethodTest;
import test.service.standardtests.callbacks.ListCallback;
import test.service.standardtests.data.RouteDataUtils;

/**
 * <p>
 * Tests for {@link Strava#listAthleteRoutes(Integer)}
 * </p>
 *
 * @author Dan Shannon
 *
 */
public class ListRoutesTest extends ListMethodTest<StravaRoute, Integer> {

	@Override
	protected Class<StravaRoute> classUnderTest() {
		return StravaRoute.class;

	}

	@Override
	protected Integer idInvalid() {
		return RouteDataUtils.ROUTE_INVALID_ID;
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
		return RouteDataUtils.ROUTE_VALID_ID;
	}

	@Override
	protected Integer idValidWithoutEntries() {
		return null;
	}

	@Override
	protected ListCallback<StravaRoute, Integer> lister() {
		return ((strava, id) -> strava.listAthleteRoutes(id));
	}

	@Override
	protected void validate(StravaRoute result) {
		RouteDataUtils.validateRoute(result);
	}

}
