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
package com.constellio.app.modules.rm.ui.builders;

import java.util.List;

import com.constellio.app.modules.rm.ui.entities.FolderVO;
import com.constellio.app.ui.entities.MetadataSchemaVO;
import com.constellio.app.ui.entities.MetadataValueVO;
import com.constellio.app.ui.entities.RecordVO.VIEW_MODE;
import com.constellio.app.ui.framework.builders.RecordToVOBuilder;
import com.constellio.model.entities.records.Record;

public class FolderToVOBuilder extends RecordToVOBuilder {

	@Override
	public FolderVO build(Record record, VIEW_MODE viewMode) {
		return (FolderVO) super.build(record, viewMode);
	}

	@Override
	public FolderVO build(Record record, VIEW_MODE viewMode, MetadataSchemaVO schemaVO) {
		return (FolderVO) super.build(record, viewMode, schemaVO);
	}

	@Override
	protected FolderVO newRecordVO(String id, List<MetadataValueVO> metadataValueVOs, VIEW_MODE viewMode) {
		return new FolderVO(id, metadataValueVOs, viewMode);
	}


}
