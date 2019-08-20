<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${the_title} - ${company_name}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<@liferay_util["include"] page=top_head_include />
</head>

<body class="${css_class} color-scheme-default">

<#-- TODO: I'm assuming these are the same as vm declarations, but check -->

<@liferay_ui["quick-access"] contentId="#main-content" />

<@liferay_util["include"] page=body_top_include />

<@liferay.control_menu />

<#-- -->

<#assign headerPortletPreferences = freeMarkerPortletPreferences.getPreferences({"portletSetupPortletDecoratorId": "barebone", "destination": "/search"}) />

<div id="wrapper">

		<header id="banner">

			<div class="banner__background--fixed"></div>

			<div class="banner__inner-wrapper">
				<div class="container">
					<div class="row">
						<div class="col-sm banner__logo-column">
							<a class="${logo_css_class}" href="${site_default_url}" title="Go to TJAGLCS">
								<img alt="${logo_description}" src="${site_logo}" />

								<#if show_site_name>
									<h1 class="font-weight-bold h2 mb-0">${site_name}</h1>
								</#if>
							</a>
						</div>


						<#--<div class="col-sm">
							<#if show_header_search>
								<div class="justify-content-md-end mr-4 navbar-form" role="search">
									<@liferay.search_bar default_preferences="${headerPortletPreferences}" />
								</div>
							</#if>
						</div>-->

						 <div class="col-sm-auto banner__nav-column">
							<#include "${full_templates_path}/navigation.ftl" />
						</div>

						<!--Can this be added to the control menu instead?
								Then we could have admin sign in in the footer and account controls in the menu
								Should make nav bar simpler
								https://portal.liferay.dev/docs/7-1/tutorials/-/knowledge_base/t/creating-control-menu-entries
					 -->
						<#--<div class="col-sm">
							<@liferay.user_personal_bar />
						</div>-->

					</div>

				</div>
			</div>
		</header>



	<#-- TODO: previous template has a div wrap here, then subbanner. Do we need either? -->


	<section id="content" class="container">

		<#-- TODO: add breadcrumbs -->

		<h1 class="hide-accessible">${the_title}</h1>

		<#if selectable>
			<@liferay_util["include"] page=content_include />
		<#else>
			${portletDisplay.recycle()}

			${portletDisplay.setTitle(the_title)}

			<@liferay_theme["wrap-portlet"] page="portlet.ftl">
				<@liferay_util["include"] page=content_include />
			</@>
		</#if>
	</section>


	<footer id="footer" role="contentinfo" class="footer">
		 <div class="container">
		 		<div class="row">
				 <div class="col-sm footer-address-container">
					 <p class="mb-3">The Judge Advocate General's Legal Center & School</p>
					 <p class="mb-1">600 Massie Rd</p>
					 <p class="mb-3">Charlottesville, VA 22903</p>
					 <p class="mb-1">Main Reception Desk: <a href="tel:1-434-971-3300">(434) 971-3300</a></p>
					 <p><a href="/lodging">Lodging:</a> <a href="tel:1-434-972-6450">(434) 972-6450</a></p>
				 </div>
				 <div class="col-sm-auto footer-contact-container">
					 <p><a href="/documents/27431/135130/Directory+2016-2017/24975c80-c9e5-4797-8659-bef0abd022dc">Directory</a></p>
					 <p><a href="/support">Contact Us</a></p>

					 <div class="sign-in-link">
						 <#if !is_signed_in>

							 <a data-redirect="${is_login_redirect_required?string}" href="${sign_in_url}" id="sign-in" rel="nofollow">${admin_sign_in_text}</a>
						</#if>

						<#if is_signed_in>
							<@liferay.user_personal_bar />
						</#if>
					</div>
				 </div>
			 </div>
		 </div>
	</footer>
</div>

<@liferay_util["include"] page=body_bottom_include />

<@liferay_util["include"] page=bottom_include />

</body>

</html>
