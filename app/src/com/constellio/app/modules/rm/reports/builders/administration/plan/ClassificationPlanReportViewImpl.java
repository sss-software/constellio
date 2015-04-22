/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.app.modules.rm.reports.builders.administration.plan;

import static com.constellio.app.ui.i18n.i18n.$;

import com.constellio.app.modules.rm.reports.model.administration.plan.ClassificationPlanReportPresenter;
import com.constellio.app.reports.builders.administration.plan.ReportBuilder;
import com.constellio.app.reports.builders.administration.plan.ReportBuilderFactory;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.app.ui.pages.base.SessionContext;
import com.constellio.model.services.factories.ModelLayerFactory;

public class ClassificationPlanReportViewImpl implements ReportBuilderFactory {

	boolean detail;

	public ClassificationPlanReportViewImpl(boolean detail) {
		this.detail = detail;
	}

	@Override
	public ReportBuilder getReportBuilder(ModelLayerFactory modelLayerFactory) {
		String collection = getSessionContext().getCurrentCollection();
		ClassificationPlanReportPresenter presenter = new ClassificationPlanReportPresenter(collection, modelLayerFactory,
				detail);
		return new ClassificationPlanReportBuilder(presenter.build(), presenter.getFoldersLocator());
	}

	@Override
	public String getFilename() {
		if (detail) {
			return $("Report.DetailedClassificationPlan") + ".pdf";
		} else {
			return $("Report.ClassificationPlan") + ".pdf";
		}
	}

	SessionContext getSessionContext() {
		return ConstellioUI.getCurrentSessionContext();
	}
}
