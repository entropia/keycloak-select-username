package li.ember.selectusername;

import org.keycloak.Config;
import org.keycloak.authentication.*;
import org.keycloak.models.*;
import org.keycloak.provider.ProviderConfigProperty;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectUsernameAuthenticator implements AuthenticatorFactory, Authenticator {
    public static final String USERNAME_ATTRIBUTE = "username-attribute";
    public static final String PROVIDER_ID = "select-username";

    public static final SelectUsernameAuthenticator ROLE_AUTHENTICATOR = new SelectUsernameAuthenticator();

    @Override
    public String getDisplayType() {
        return "Select Username";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    public List<String> getValidUsernames(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        return context.getUser().getAttributeStream(config.getConfig().get(USERNAME_ATTRIBUTE)).collect(Collectors.toList());
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();

        List<String> usernames = getValidUsernames(context);

        if (usernames.size() == 0) {
            context.failure(AuthenticationFlowError.ACCESS_DENIED);
            return;
        }

	if (usernames.size() == 1) {
	    context.getAuthenticationSession().setUserSessionNote("selected_username", usernames.get(0));
	    context.success();
	    return;
	}

        Response challenge = context.form()
                .setAttribute("usernames", usernames)
                .createForm("select-username.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        boolean validated = validateAnswer(context);
        if (!validated) {
            AuthenticatorConfigModel config = context.getAuthenticatorConfig();

            Response challenge = context.form()
                    .setAttribute("usernames", getValidUsernames(context))
                    .setError("Please select a valid username")
                    .createForm("select-username.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst("username");

        context.getAuthenticationSession().setUserSessionNote("selected_username", username);
        context.success();
    }

    protected boolean validateAnswer(AuthenticationFlowContext context) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst("username");

        return getValidUsernames(context).contains(username);
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "This Authenticator only passes if a user is member of a specific configurable client-role";
    }

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(USERNAME_ATTRIBUTE);
        property.setLabel("Username Attribute");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("User Attribute which contains usernames to be selected");
        CONFIG_PROPERTIES.add(property);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return ROLE_AUTHENTICATOR;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }
}
