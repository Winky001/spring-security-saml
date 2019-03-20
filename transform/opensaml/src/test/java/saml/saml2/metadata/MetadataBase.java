/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package saml.saml2.metadata;

import java.io.IOException;
import java.time.Clock;
import java.util.Arrays;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.Saml2Transformer;
import org.springframework.security.saml2.model.key.KeyData;
import org.springframework.security.saml2.model.key.KeyType;
import org.springframework.security.saml2.model.metadata.IdentityProviderMetadata;
import org.springframework.security.saml2.model.metadata.ServiceProviderMetadata;
import org.springframework.security.saml2.model.signature.AlgorithmMethod;
import org.springframework.security.saml2.model.signature.DigestMethod;
import org.springframework.security.saml2.spi.DefaultSaml2Transformer;
import org.springframework.security.saml2.spi.opensaml.OpenSaml2Transformer;
import org.springframework.util.StreamUtils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import saml.helper.SamlTestObjectHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.saml2.spi.ExamplePemKey.IDP_RSA_KEY;
import static org.springframework.security.saml2.spi.ExamplePemKey.SP_RSA_KEY;

public abstract class MetadataBase {

	protected static Saml2Transformer config;
	protected static Clock time;
	protected KeyData spSigning;
	protected KeyData idpSigning;
	protected KeyData spVerifying;
	protected KeyData idpVerifying;
	protected String spBaseUrl;
	protected String idpBaseUrl;
	protected ServiceProviderMetadata serviceProviderMetadata;
	protected IdentityProviderMetadata identityProviderMetadata;
	protected SamlTestObjectHelper helper;

	@BeforeAll
	public static void init() {
		time = Clock.systemUTC();
		config = new OpenSaml2Transformer();
		((DefaultSaml2Transformer) config).afterPropertiesSet();
	}

	protected byte[] getFileBytes(String path) throws IOException {
		ClassPathResource resource = new ClassPathResource(path);
		assertTrue(resource.exists(), path + " must exist.");
		return StreamUtils.copyToByteArray(resource.getInputStream());
	}

	@BeforeEach
	public void setup() {
		idpSigning = IDP_RSA_KEY.getSimpleKey("idp");
		idpVerifying = new KeyData("idp-verify", null, SP_RSA_KEY.getPublic(), null, KeyType.SIGNING);
		spSigning = SP_RSA_KEY.getSimpleKey("sp");
		spVerifying = new KeyData("sp-verify", null, IDP_RSA_KEY.getPublic(), null, KeyType.SIGNING);
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