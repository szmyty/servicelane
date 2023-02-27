<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("registerWithTitle",(realm.displayName!''))?no_esc}
    <#elseif section = "header">
        ${msg("registerWithTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
        <form id="kc-register-form" class="register form ${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
          <input type="text" readonly value="this is not a login form" style="display: none;">
          <input type="password" readonly value="this is not a login form" style="display: none;">

            <div class="mdc-layout-grid">
                <div class="mdc-layout-grid__inner">
                    <#if !realm.registrationEmailAsUsername>

                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-12">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">person</i>
                            <input id="username" class="mdc-text-field__input ${properties.kcInputClass!}" name="username" type="text" autofocus value="${(register.formData.username!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="username" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("username")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>

                    </#if>

                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">person</i>
                            <input required id="firstName" class="mdc-text-field__input ${properties.kcInputClass!}" name="firstName" type="text" autofocus value="${(register.formData.firstName!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="firstName" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("firstName")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>
                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">person</i>
                            <input required id="lastName" class="mdc-text-field__input ${properties.kcInputClass!}" name="lastName" type="text" value="${(register.formData.lastName!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="lastName" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("lastName")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>

                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">email</i>
                            <input required id="email" class="mdc-text-field__input ${properties.kcInputClass!}" name="email" type="text" value="${(register.formData.email!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="email" class="mdc-floating-label ${properties.kcLabelClass!}">${(msg("email")?no_esc)}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>
                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">phone</i>
                            <input required id="user.attributes.phone" class="mdc-text-field__input ${properties.kcInputClass!}" name="user.attributes.phone" type="text" value="${(register.formData['user.attributes.phone']!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="user.attributes.phone" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("phone")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>

                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-5">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">place</i>
                            <input required id="user.attributes.address" class="mdc-text-field__input ${properties.kcInputClass!}" name="user.attributes.address" type="text" value="${(register.formData['user.attributes.address']!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="user.attributes.address" class="mdc-floating-label ${properties.kcLabelClass!}">${(msg("address")?no_esc)}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>
                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-4">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">location_city</i>
                            <input required id="user.attributes.city" class="mdc-text-field__input ${properties.kcInputClass!}" name="user.attributes.city" type="text" value="${(register.formData['user.attributes.city']!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="user.attributes.city" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("city")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>
                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-3">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">local_post_office</i>
                            <input required id="user.attributes.postal" class="mdc-text-field__input ${properties.kcInputClass!}" name="user.attributes.postal" type="text" value="${(register.formData['user.attributes.postal']!'')}">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="user.attributes.postal" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("postal")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>

                    <#if passwordRequired>

                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">lock</i>
                            <input required id="password" class="mdc-text-field__input ${properties.kcInputClass!}" name="password" type="password">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="password" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("password")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>
                    <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-6">
                        <div class="mdc-text-field mdc-text-field--outlined mdc-text-field--with-leading-icon ${properties.kcLabelClass!}">
                            <i class="material-icons mdc-text-field__icon" tabindex="-1" role="button">lock</i>
                            <input required id="password-confirm" class="mdc-text-field__input ${properties.kcInputClass!}" name="password-confirm" type="password">
                            <div class="${properties.kcLabelWrapperClass!}">
                                <label for="password-confirm" class="mdc-floating-label ${properties.kcLabelClass!}">${msg("passwordConfirm")?no_esc}</label>
                            </div>
                            <div class="mdc-notched-outline">
                                <svg>
                                    <path class="mdc-notched-outline__path"/>
                                </svg>
                            </div>
                            <div class="mdc-notched-outline__idle"></div>
                        </div>
                    </div>

                    </#if>
                </div>
            </div>

            <#if recaptchaRequired??>
            <div class="form-group">
                <div class="${properties.kcInputWrapperClass!}">
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                </div>
            </div>
            </#if>

            <div class="${properties.kcFormGroupClass!} register-button-container">

                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <span>
                            <button class="mdc-button" onclick="window.location.href = ${url.loginUrl}" formnovalidate>
                                <i class="material-icons mdc-button__icon" aria-hidden="true">arrow_back</i>
                                ${msg("backToLogin")?no_esc}
                            </button>
                        </span>
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <button class="mdc-button mdc-button--raised ${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" type="submit">
                        ${msg("doRegister")?no_esc}
                    </button>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
