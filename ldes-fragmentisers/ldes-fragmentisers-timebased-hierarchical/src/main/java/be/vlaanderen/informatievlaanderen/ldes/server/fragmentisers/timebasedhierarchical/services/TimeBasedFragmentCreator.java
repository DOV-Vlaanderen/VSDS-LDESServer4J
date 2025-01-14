package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.model.FragmentationTimestamp;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationService.LDES_SERVER_CREATE_FRAGMENTS_COUNT;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.FRAGMENTATION_STRATEGY;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.metrics.MetricsConstants.VIEW;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.timebasedhierarchical.HierarchicalTimeBasedFragmentationStrategy.TIMEBASED_FRAGMENTATION_HIERARCHICAL;

public class TimeBasedFragmentCreator {

	private final FragmentRepository fragmentRepository;
	private final TimeBasedRelationsAttributer relationsAttributer;
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedFragmentCreator.class);

	public TimeBasedFragmentCreator(FragmentRepository fragmentRepository,
			TimeBasedRelationsAttributer relationsAttributer) {
		this.fragmentRepository = fragmentRepository;
		this.relationsAttributer = relationsAttributer;
	}

	public Fragment getOrCreateFragment(Fragment parentFragment, FragmentationTimestamp fragmentationTimestamp,
			Granularity granularity) {
		return getOrCreateFragment(parentFragment, fragmentationTimestamp.getTimeValueForGranularity(granularity), granularity);
	}

	public Fragment getOrCreateFragment(Fragment parentFragment, String timeValue,
										Granularity granularity) {
		Fragment child = parentFragment
				.createChild(new FragmentPair(granularity.getValue(),
						timeValue));
		return fragmentRepository
				.retrieveFragment(child.getFragmentId())
				.orElseGet(() -> {
					fragmentRepository.saveFragment(child);

					if (isDefaultBucket(child)) {
						relationsAttributer.addDefaultRelation(parentFragment, child);
					} else {
						relationsAttributer
								.addInBetweenRelation(parentFragment, child);
					}
					String viewName = parentFragment.getViewName().asString();
					Metrics.counter(LDES_SERVER_CREATE_FRAGMENTS_COUNT, VIEW, viewName, FRAGMENTATION_STRATEGY, TIMEBASED_FRAGMENTATION_HIERARCHICAL).increment();
					LOGGER.debug("Timebased fragment created with id: {}", child.getFragmentId());
					return child;
				});
	}

	private boolean isDefaultBucket(Fragment fragment) {
		return fragment.getValueOfKey(Granularity.YEAR.getValue()).orElse("").equals(DEFAULT_BUCKET_STRING);
	}
}
