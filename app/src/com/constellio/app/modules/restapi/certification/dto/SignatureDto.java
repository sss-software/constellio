package com.constellio.app.modules.restapi.certification.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;

@Data
@Builder
@JsonRootName("Signature")
public class SignatureDto {

	private int page;
	@Valid
	private RectangleDto position;

}
