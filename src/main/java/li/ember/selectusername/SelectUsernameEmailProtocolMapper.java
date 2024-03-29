package li.ember.selectusername;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;

public class SelectUsernameEmailProtocolMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String MAPPER_MAIL_DOMAIN = "mapper.mail_domain";
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();
    static {
        ProviderConfigProperty mailDomainProperty;
        mailDomainProperty = new ProviderConfigProperty();
        mailDomainProperty.setName(MAPPER_MAIL_DOMAIN);
        mailDomainProperty.setLabel("Mail domain");
        mailDomainProperty.setType(ProviderConfigProperty.STRING_TYPE);
        mailDomainProperty.setHelpText("Domain which is used with the selected username to fake an email address");
        mailDomainProperty.setDefaultValue("localhost");
        configProperties.add(mailDomainProperty);
    }

    public static final String PROVIDER_ID = "select-username-email-oidc-mapper";

    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, SelectUsernameProtocolMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public String getDisplayType() {
        return "Select Username Mapper (Email)";
    }

    @Override
    public String getHelpText() {
        return "Sets an email to a faked email address consisting of the faked username and a configurable domain";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession, KeycloakSession keycloakSession,
                            ClientSessionContext clientSessionCtx) {
        String username;
        if (userSession.getNote("selected_username").isEmpty()) {
            username = userSession.getUser().getUsername();
        } else {
            username = userSession.getNote("selected_username");
        }
        String email = username + "@" + mappingModel.getConfig().get(MAPPER_MAIL_DOMAIN);
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, email);
    }
}
