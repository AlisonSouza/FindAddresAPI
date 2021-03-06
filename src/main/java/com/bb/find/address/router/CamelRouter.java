package com.bb.find.address.router;

import static org.apache.camel.model.rest.RestParamType.query;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.bb.find.address.bean.ParserBean;
import com.google.code.geocoder.model.GeocodeResponse;

@Component
public class CamelRouter extends RouteBuilder {

	@Override
    public void configure() throws Exception {
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .apiContextPath("/api-doc")
            .apiProperty("api.title", "find address API").apiProperty("api.version", "1.0.0")
            .apiProperty("cors", "true");

        rest("/find").description("find address REST service")
            .consumes("application/xml")
            .produces("application/json")
            .get().description("Geocoder address lookup").outType(GeocodeResponse.class)
            .param().name("address").type(query).description("The address to find").dataType("string").endParam()
            .responseMessage().code(200).message("Find Location successful").endResponseMessage()
            .to("direct:parser");
        
        from("direct:parser")
        .toD("geocoder:address:${header.address}").bean(ParserBean.class, "parserToNewTemplate")
	    .end();
        
    }
}
