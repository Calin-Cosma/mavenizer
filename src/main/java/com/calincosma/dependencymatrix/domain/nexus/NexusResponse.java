package com.calincosma.dependencymatrix.domain.nexus;

public class NexusResponse {
	
	private ResponseHeader responseHeader;
	private Response response;
	
	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}
	
	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}
	
	public Response getResponse() {
		return response;
	}
	
	public void setResponse(Response response) {
		this.response = response;
	}
}
