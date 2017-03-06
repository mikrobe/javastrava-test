package test.api.rest.upload;

import javastrava.api.v3.model.StravaUploadResponse;
import test.api.model.StravaUploadResponseTest;
import test.api.rest.APIGetTest;
import test.api.rest.callback.TestGetCallback;
import test.service.standardtests.data.ActivityDataUtils;

public class CheckUploadStatusTest extends APIGetTest<StravaUploadResponse, Long> {
	/**
	 * @see test.api.rest.APIGetTest#invalidId()
	 */
	@Override
	protected Long invalidId() {
		return new Long(0L);
	}

	/**
	 * @see test.api.rest.APIGetTest#privateId()
	 */
	@Override
	protected Long privateId() {
		return null;
	}

	/**
	 * @see test.api.rest.APIGetTest#privateIdBelongsToOtherUser()
	 */
	@Override
	protected Long privateIdBelongsToOtherUser() {
		return null;
	}

	/**
	 * @see test.api.rest.APIGetTest#validId()
	 */
	@Override
	protected Long validId() {
		return api().getActivity(ActivityDataUtils.ACTIVITY_FOR_AUTHENTICATED_USER, null).getUploadId();
	}

	/**
	 * @see test.api.rest.APIGetTest#validIdBelongsToOtherUser()
	 */
	@Override
	protected Long validIdBelongsToOtherUser() {
		return api().getActivity(ActivityDataUtils.ACTIVITY_FOR_UNAUTHENTICATED_USER, null).getUploadId();
	}

	/**
	 * @see test.api.rest.APITest#validate(java.lang.Object)
	 */
	@Override
	protected void validate(final StravaUploadResponse result) throws Exception {
		StravaUploadResponseTest.validate(result);

	}

	@Override
	protected TestGetCallback<StravaUploadResponse, Long> getter() {
		return ((api, id) -> api.checkUploadStatus(id));
	}

}
