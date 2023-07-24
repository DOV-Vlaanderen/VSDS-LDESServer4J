package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebased.config;

public class TimeBasedConfig {

	public static final String DEFAULT_FRAGMENTER_SUBJECT_FILTER = ".*";

	private String fragmenterSubjectFilter = DEFAULT_FRAGMENTER_SUBJECT_FILTER;
	private String fragmentationPath;
	private String maxGranularity;

	public String getFragmentationPath() {
		return fragmentationPath;
	}

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public void setFragmenterSubjectFilter(String fragmenterSubjectFilter) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
	}

	public void setFragmenterPath(String fragmentationPath) {
		this.fragmentationPath = fragmentationPath;
	}

	public void setMaxGranularity(String maxGranularity) {
		this.maxGranularity = maxGranularity;
	}

	public String getMaxGranularity() {
		return maxGranularity;
	}
}
