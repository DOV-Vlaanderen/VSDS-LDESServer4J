package be.vlaanderen.informatievlaanderen.ldes.server.ingest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.MemberIngestValidator;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberIngesterImpl implements MemberIngester {

    public static final String LDES_SERVER_INGESTED_MEMBERS_COUNT = "ldes_server_ingested_members_count";
    public static final String MEMBER_WITH_ID_INGESTED = "Member with id {} ingested.";
    public static final String DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS = "Duplicate member ingested. Member with id {} already exists";
    private final MemberIngestValidator validator;
	private final MemberRepository memberRepository;
	private final ApplicationEventPublisher eventPublisher;

	private static final Logger log = LoggerFactory.getLogger(MemberIngesterImpl.class);

	public MemberIngesterImpl(MemberIngestValidator validator, MemberRepository memberRepository,
			ApplicationEventPublisher eventPublisher) {
		this.validator = validator;
		this.memberRepository = memberRepository;
		this.eventPublisher = eventPublisher;
	}

    @Override
    public void ingest(Member member) {
        validator.validate(member);
        final String memberId = member.getId().replaceAll("[\n\r\t]", "_");
        ingestNewMember(member, memberId);
    }

    private void ingestNewMember(Member member, String memberId) {
        Optional<Member> savedMember = insert(member);
        if (savedMember.isPresent()) {
            Member sMember = savedMember.get();
            Metrics.counter(LDES_SERVER_INGESTED_MEMBERS_COUNT).increment();
            final var memberIngestedEvent = new MemberIngestedEvent(sMember.getModel(), sMember.getId(),
                    sMember.getCollectionName(), sMember.getSequenceNr());
            eventPublisher.publishEvent(memberIngestedEvent);
            log.debug(MEMBER_WITH_ID_INGESTED, memberId);
        } else {
            log.warn(DUPLICATE_MEMBER_INGESTED_MEMBER_WITH_ID_ALREADY_EXISTS, memberId);
        }
    }

    private Optional<Member> insert(Member member) {
        member.removeTreeMember();
        return memberRepository.insertMember(member);
    }

}