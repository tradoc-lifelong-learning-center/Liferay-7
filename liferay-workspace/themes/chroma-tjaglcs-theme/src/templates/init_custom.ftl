<#--
This file allows you to override and define new FreeMarker variables.
-->
<#assign

	root_css_class += " root"
  css_class += " portal-normal"
  current_url = theme_display.getURLCurrent()
/>


<#assign
	admin_sign_in_text = "Administrator " + languageUtil.get(locale, "sign-in")
	sign_in_url = htmlUtil.escape(theme_display.getURLSignIn())
/>


<#assign
	show_header_search = true
/>
