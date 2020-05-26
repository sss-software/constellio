package com.constellio.app.modules.restapi.certification.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@JsonRootName("Certification")
public class CertificationDto {

	//private String id;
	private List<SignatureDto> signatures;
	private String documentId;
	private String imageData;
	private String fileAsStr;
	private String userId;
	private String username;

	@JsonIgnore @Getter(onMethod = @__(@JsonIgnore))
	private String eTag;
}
