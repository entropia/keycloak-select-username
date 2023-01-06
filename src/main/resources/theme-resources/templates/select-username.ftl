<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <form id="kc-username-select-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="username" class="${properties.kcLabelClass!}">Which username should be used?</label>
                </div>

                <div class="${properties.kcInputWrapperClass!}">
                    <select id="username" name="username" type="select" class="form-control" tabindex="3" />
                    <#list usernames as username>
                        <option>${username}</option>
                    </#list>
                </div>
            </div>
            <div id="kc-form-buttons" class="${properties.kcFormButtonGroupClass!} ${properties.kcFormGroupClass!}">
                <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"type="submit" value="${msg("doLogIn")}"/>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
