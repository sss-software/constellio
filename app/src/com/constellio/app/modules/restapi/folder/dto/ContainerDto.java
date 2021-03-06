package com.constellio.app.modules.restapi.folder.dto;

import com.constellio.app.modules.restapi.resource.dto.BaseReferenceDto;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonRootName("Container")
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ContainerDto extends BaseReferenceDto {
	@Builder
	public ContainerDto(String id, String title) {
		super(id, title);
	}
}
