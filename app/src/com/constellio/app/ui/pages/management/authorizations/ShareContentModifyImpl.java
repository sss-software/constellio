package com.constellio.app.ui.pages.management.authorizations;

import com.constellio.app.ui.entities.AuthorizationVO;
import com.constellio.app.ui.framework.components.BaseForm;
import com.constellio.model.frameworks.validation.ValidationException;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;

public class ShareContentModifyImpl extends ShareContentViewImpl {

	@Override
	protected Component buildMainComponent(ViewChangeEvent event) {
		buildUsersAndGroupsField();
		buildAccessField();
		buildRolesField();
		buildDateFields();
		AuthorizationVO shareVO = presenter.getAuthorization(record.getRecord());
		if (shareVO != null) {
			return new BaseForm<AuthorizationVO>(
					shareVO, this, users, groups, accessRoles, userRoles, startDate, endDate) {
				@Override
				protected void saveButtonClick(AuthorizationVO authorization)
						throws ValidationException {
					presenter.authorizationModifyRequested(authorization);
				}

				@Override
				protected void cancelButtonClick(AuthorizationVO authorization) {
					returnFromPage();
				}
			};
		} else {
			return new BaseForm<AuthorizationVO>(
					AuthorizationVO.forContent(record.getId()), this, users, groups, accessRoles, userRoles, startDate, endDate) {
				@Override
				protected void saveButtonClick(AuthorizationVO authorization)
						throws ValidationException {
					presenter.authorizationCreationRequested(authorization);
				}

				@Override
				protected void cancelButtonClick(AuthorizationVO authorization) {
					returnFromPage();
				}
			};
		}
	}
}
