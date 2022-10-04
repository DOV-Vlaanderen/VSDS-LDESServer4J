package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities;

import org.apache.jena.rdf.model.*;

import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class LdesMember {

	private final Model memberModel;
	private final String memberId;

	public LdesMember(String memberId, final Model memberModel) {
		this.memberId = memberId;
		this.memberModel = memberModel;
	}

	public Model getModel() {
		return memberModel;
	}

	public Object getFragmentationObject(String fragmentationProperty) {
		// @formatter:off
        return memberModel
                .listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
                .nextOptional()
                .map(Statement::getObject)
                .map(RDFNode::asLiteral)
                .map(Literal::getValue)
                .orElse(null);
        // @formatter:on
	}

	public List<Object> getFragmentationObjects(String fragmentationProperty) {
		// @formatter:off
		return memberModel
				.listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
				.toList()
				.stream()
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(Literal::getValue)
				.toList();
		// @formatter:on
	}

	public String getLdesMemberId() {
		return memberId;
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return memberModel.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public void removeTreeMember() {
		getCurrentTreeMemberStatement().ifPresent(memberModel::remove);
	}
}
