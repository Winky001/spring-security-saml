/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.security.saml.spi;

import javax.xml.datatype.Duration;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.saml.SamlTransformer;
import org.springframework.security.saml.key.SimpleKey;
import org.springframework.security.saml.saml2.Saml2Object;

public class DefaultSamlTransformer implements SamlTransformer, InitializingBean {

    private SpringSecuritySaml implementation;
    private Defaults defaults;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        implementation = SpringSecuritySaml.getInstance().init();
        defaults = new Defaults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long durationToMillis(Duration duration) {
        return implementation.toMillis(duration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration millisToDuration(long millis) {
        return implementation.toDuration(millis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toXml(Saml2Object saml2Object) {
        return implementation.toXml(saml2Object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Saml2Object resolve(byte[] xml, List<SimpleKey> trustedKeys) {
        return implementation.resolve(xml, trustedKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String samlEncode(String s) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String samlDecode(String s) {
        return null;
    }

    @Override
    public Defaults getDefaults() {
        return defaults;
    }
}