/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.springframework.security.saml.saml2.metadata;

import java.io.IOException;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml.SamlTransformer;
import org.springframework.security.saml.helper.SamlTestObjectHelper;
import org.springframework.security.saml.key.KeyType;
import org.springframework.security.saml.key.SimpleKey;
import org.springframework.security.saml.saml2.authentication.Assertion;
import org.springframework.security.saml.saml2.authentication.AudienceRestriction;
import org.springframework.security.saml.saml2.authentication.AuthenticationRequest;
import org.springframework.security.saml.saml2.authentication.AuthenticationStatement;
import org.springframework.security.saml.saml2.authentication.Conditions;
import org.springframework.security.saml.saml2.authentication.Issuer;
import org.springframework.security.saml.saml2.authentication.NameIdPolicy;
import org.springframework.security.saml.saml2.authentication.NameIdPrincipal;
import org.springframework.security.saml.saml2.authentication.Subject;
import org.springframework.security.saml.saml2.authentication.SubjectConfirmation;
import org.springframework.security.saml.saml2.authentication.SubjectConfirmationData;
import org.springframework.security.saml.saml2.authentication.SubjectConfirmationMethod;
import org.springframework.security.saml.saml2.signature.AlgorithmMethod;
import org.springframework.security.saml.saml2.signature.DigestMethod;
import org.springframework.security.saml.spi.DefaultSamlTransformer;
import org.springframework.security.saml.spi.opensaml.OpenSamlImplementation;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.saml.saml2.metadata.Binding.REDIRECT;
import static org.springframework.security.saml.spi.ExamplePemKey.IDP_RSA_KEY;
import static org.springframework.security.saml.spi.ExamplePemKey.SP_RSA_KEY;

public abstract class MetadataBase {

	public long NOT_BEFORE = 60000;
	public long NOT_AFTER = 120000;
	public long SESSION_NOT_AFTER = 30 * 60 * 1000;


	protected static SamlTransformer config;
	protected static Clock time;
	protected SimpleKey spSigning;
	protected SimpleKey idpSigning;
	protected SimpleKey spVerifying;
	protected SimpleKey idpVerifying;
	protected String spBaseUrl;
	protected String idpBaseUrl;
	protected ServiceProviderMetadata serviceProviderMetadata;
	protected IdentityProviderMetadata identityProviderMetadata;
	protected SamlTestObjectHelper helper;

	@BeforeAll
	public static void init() throws Exception {
		time = Clock.systemUTC();
		config = new DefaultSamlTransformer(new OpenSamlImplementation(time).init());
		((DefaultSamlTransformer) config).afterPropertiesSet();
	}

	protected byte[] getFileBytes(String path) throws IOException {
		ClassPathResource resource = new ClassPathResource(path);
		assertTrue(resource.exists(), path + " must exist.");
		return StreamUtils.copyToByteArray(resource.getInputStream());
	}

	@BeforeEach
	public void setup() {
		idpSigning = IDP_RSA_KEY.getSimpleKey("idp");
		idpVerifying = new SimpleKey("idp-verify", null, SP_RSA_KEY.getPublic(), null, KeyType.SIGNING);
		spSigning = SP_RSA_KEY.getSimpleKey("sp");
		spVerifying = new SimpleKey("sp-verify", null, IDP_RSA_KEY.getPublic(), null, KeyType.SIGNING);
		spBaseUrl = "http://sp.localhost:8080/uaa";
		idpBaseUrl = "http://idp.localhost:8080/uaa";
		helper = new SamlTestObjectHelper(time);

		serviceProviderMetadata = helper.serviceProviderMetadata(
			spBaseUrl,
			spSigning,
			Arrays.asList(spSigning),
			"saml/sp/",
			"sp-alias",
			AlgorithmMethod.RSA_SHA1,
			DigestMethod.SHA1
		);
		identityProviderMetadata = helper.identityProviderMetadata(
			idpBaseUrl,
			idpSigning,
			Arrays.asList(idpSigning),
			"saml/idp/",
			"idp-alias",
			AlgorithmMethod.RSA_SHA1,
			DigestMethod.SHA1
		);
	}


}
