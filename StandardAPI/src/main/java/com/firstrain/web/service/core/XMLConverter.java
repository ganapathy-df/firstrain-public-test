package com.firstrain.web.service.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class XMLConverter {

	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public String getXML(Object obj) throws IOException {
		String xml = null;
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			this.marshaller.marshal(obj, new StreamResult(os));
			if (os != null) {
				xml = os.toString();
			}
		} finally {
			if (os != null) {
				os.close();
			}
		}
		return xml;
	}

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
}
