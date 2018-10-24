package com.constellio.app.api.extensions;

import com.constellio.app.api.extensions.taxonomies.UserSearchEvent;
import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.app.modules.rm.wrappers.RMUser;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.ui.entities.FacetVO;
import com.constellio.app.ui.entities.FacetValueVO;
import com.constellio.app.ui.entities.UserVO;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.app.ui.pages.search.AdvancedSearchPresenter;
import com.constellio.app.ui.pages.search.AdvancedSearchView;
import com.constellio.app.ui.pages.search.AdvancedSearchViewImpl;
import com.constellio.app.ui.pages.search.SimpleSearchPresenter;
import com.constellio.app.ui.pages.search.SimpleSearchView;
import com.constellio.app.ui.pages.search.criteria.ConditionBuilder;
import com.constellio.app.ui.pages.search.criteria.CriteriaBuilder;
import com.constellio.app.ui.pages.search.criteria.Criterion;
import com.constellio.data.dao.dto.records.FacetValue;
import com.constellio.model.entities.records.wrappers.SavedSearch;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.FakeSessionContext;
import com.constellio.sdk.tests.MockedNavigation;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.constellio.app.ui.pages.search.criteria.Criterion.BooleanOperator.OR;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Constelio on 2016-10-19.
 */
public class SearchPageExtensionAcceptanceTest extends ConstellioTest {

	@Mock SearchPageExtension searchPageExtension;
	@Mock SearchPageExtension searchPageExtension2;
	@Mock UserVO user;
	@Mock RecordToVOBuilder recordToVOBuilder;
	@Mock SimpleSearchView simpleSearchView;
	@Mock AdvancedSearchViewImpl advancedSearchView;
	MockedNavigation navigator = new MockedNavigation();

	RMSchemasRecordsServices rm;
	AppLayerFactory appLayerFactory;
	RMTestRecords records = new RMTestRecords(zeCollection);
	CriteriaBuilder criteriaBuilder;

	@Before
	public void setup() {
		prepareSystem(
				withZeCollection().withConstellioRMModule().withConstellioESModule().withAllTestUsers()
						.withRMTest(records).withFoldersAndContainersOfEveryStatus().withDocumentsDecommissioningList()
		);
		appLayerFactory = getAppLayerFactory();
		rm = new RMSchemasRecordsServices(zeCollection, appLayerFactory);

		when(simpleSearchView.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(simpleSearchView.getSessionContext()).thenReturn(FakeSessionContext.gandalfInCollection(zeCollection));
		when(simpleSearchView.navigate()).thenReturn(navigator);
		when(simpleSearchView.getCollection()).thenReturn(zeCollection);
		when(advancedSearchView.getConstellioFactories()).thenReturn(getConstellioFactories());
		when(advancedSearchView.getSessionContext()).thenReturn(FakeSessionContext.gandalfInCollection(zeCollection));
		when(advancedSearchView.navigate()).thenReturn(navigator);
		when(advancedSearchView.getCollection()).thenReturn(zeCollection);

		criteriaBuilder = new CriteriaBuilder(rm.folderSchemaType(), advancedSearchView.getSessionContext());
		criteriaBuilder.addCriterion(Folder.TITLE).isEqualTo("Abeille").booleanOperator(OR);
		criteriaBuilder.addCriterion(Folder.TITLE).isEqualTo("Cheval");

		when(advancedSearchView.getSearchCriteria()).thenReturn(criteriaBuilder.build());
	}

	@Test
	public void givenASimpleSearchThenWriteValidParam() {

		appLayerFactory.getExtensions().forCollection(zeCollection).searchPageExtensions.add(searchPageExtension);
		appLayerFactory.getExtensions().forCollection(zeCollection).searchPageExtensions.add(searchPageExtension2);

		final LogicalSearchQuery query = new LogicalSearchQuery(from(rm.folder.schema()).where(rm.folder.title())
				.isEqualTo("Abeille"));
		SimpleSearchPresenter simpleSearchPresenter = new SimpleSearchPresenter(simpleSearchView) {
			@Override
			protected LogicalSearchQuery getSearchQuery() {
				return query;
			}
		};
		simpleSearchPresenter.getSearchResults(false).listSearchResultVOs(0, 10);

		ArgumentCaptor<UserSearchEvent> paramCaptor = ArgumentCaptor.forClass(UserSearchEvent.class);
		verify(searchPageExtension).notifyNewUserSearch(paramCaptor.capture());
		UserSearchEvent capturedParam = paramCaptor.getValue();

		verify(searchPageExtension, times(1)).notifyNewUserSearch(any(UserSearchEvent.class));
		verify(searchPageExtension2, times(1)).notifyNewUserSearch(any(UserSearchEvent.class));
		assertThat(capturedParam.getUsername()).isEqualTo(gandalf);
		assertThat(rm.getUser(capturedParam.getUserID()).toString()).isEqualTo(gandalf);
		assertThat(capturedParam.getCollection()).isEqualTo(zeCollection);
		assertThat(capturedParam.getNumFound()).isEqualTo(1);
		assertThat(capturedParam.getSolrQuery()).isEqualTo("( title_t_fr:\"Abeille\" )");
	}

	@Test
	public void givenAnAdvancedSearchThenWriteValidParam()
			throws Exception {

		appLayerFactory.getExtensions().forCollection(zeCollection).searchPageExtensions.add(searchPageExtension);
		appLayerFactory.getExtensions().forCollection(zeCollection).searchPageExtensions.add(searchPageExtension2);

		final LogicalSearchQuery query = new LogicalSearchQuery()
				.setCondition(new ConditionBuilder(rm.folderSchemaType(), "fr").build(criteriaBuilder.build()));
		AdvancedSearchPresenter advancedSearchPresenter = new AdvancedSearchPresenter(advancedSearchView) {
			@Override
			protected LogicalSearchQuery getSearchQuery() {
				return query;
			}
		};
		advancedSearchPresenter.getSearchResults(false).listSearchResultVOs(0, 10);

		ArgumentCaptor<UserSearchEvent> paramCaptor = ArgumentCaptor.forClass(UserSearchEvent.class);
		verify(searchPageExtension).notifyNewUserSearch(paramCaptor.capture());
		UserSearchEvent capturedParam = paramCaptor.getValue();
		List<Criterion> criterionList = capturedParam.getCriterionList();

		verify(searchPageExtension, times(1)).notifyNewUserSearch(any(UserSearchEvent.class));
		verify(searchPageExtension2, times(1)).notifyNewUserSearch(any(UserSearchEvent.class));
		assertThat(capturedParam.getUsername()).isEqualTo(gandalf);
		assertThat(rm.getUser(capturedParam.getUserID()).toString()).isEqualTo(gandalf);
		assertThat(capturedParam.getCollection()).isEqualTo(zeCollection);
		assertThat(capturedParam.getNumFound()).isEqualTo(2);
		assertThat(capturedParam.getSolrQuery()).isEqualTo("( ( title_t_fr:\"Abeille\" ) OR ( title_t_fr:\"Cheval\" ) )");
		assertThat(criterionList.size()).isEqualTo(2);
		assertThat(criterionList.get(0).getSchemaType().toString()).isEqualTo("folder");
		assertThat(criterionList.get(0).getMetadataCode()).isEqualTo("folder_default_title");
		assertThat(criterionList.get(0).getValue()).isEqualTo("Abeille");
	}

	@Test
	public void givenUserInAdvancedSearchViewThenOnlySeeNonActivesIfConfigIsDeactivated()
			throws Exception {
		when(advancedSearchView.getSchemaType()).thenReturn(Folder.SCHEMA_TYPE);
		when(advancedSearchView.getSearchExpression()).thenReturn("*");
		when(advancedSearchView.getSearchCriteria()).thenReturn(new ArrayList<Criterion>());
		final User gandalf = getModelLayerFactory().newUserServices().getUserInCollection(this.gandalf, zeCollection);
		AdvancedSearchPresenter advancedSearchPresenter = buildDefaultAdvancedSearchPresenter(gandalf);

		List<FacetVO> facets = advancedSearchPresenter.getFacets(null);
		assertThat(facets).has(allArchivisticStatuses);

		gandalf.set(RMUser.HIDE_NOT_ACTIVE, true);
		advancedSearchPresenter = buildDefaultAdvancedSearchPresenter(gandalf);
		facets = advancedSearchPresenter.getFacets(null);
		assertThat(facets).has(onlyActiveFolders);
	}

	AdvancedSearchPresenter buildDefaultAdvancedSearchPresenter(final User user) {
		AdvancedSearchPresenter advancedSearchPresenter = new AdvancedSearchPresenter(advancedSearchView) {
			@Override
			protected void updateUIContext(SavedSearch savedSearch) {}

			@Override
			protected User getCurrentUser() {
				return user;
			}
		};
		advancedSearchPresenter.forRequestParameters(null);
		return advancedSearchPresenter;
	}

	Condition<List<FacetVO>> onlyActiveFolders = new Condition<List<FacetVO>>() {
		@Override
		public boolean matches(List<FacetVO> facets) {
			for(FacetVO facetVO: facets) {
				if(rm.getFacet(facetVO.getId()).getFieldDataStoreCode().equals(rm.folder.archivisticStatus().getDataStoreCode())) {
					List<FacetValueVO> facetValues = facetVO.getValues();
					return facetValues.size() == 1 && facetValues.get(0).getValue().equals("a");
				}
			}
			return false;
		}
	};

	Condition<List<FacetVO>> allArchivisticStatuses = new Condition<List<FacetVO>>() {
		@Override
		public boolean matches(List<FacetVO> facets) {
			for(FacetVO facetVO: facets) {
				if(rm.getFacet(facetVO.getId()).getFieldDataStoreCode().equals(rm.folder.archivisticStatus().getDataStoreCode())) {
					List<FacetValueVO> facetValues = facetVO.getValues();
					return facetValues.size() == 4;
				}
			}
			return false;
		}
	};
}
