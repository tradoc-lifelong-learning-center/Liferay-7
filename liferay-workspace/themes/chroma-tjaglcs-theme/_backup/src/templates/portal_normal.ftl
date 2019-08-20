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


	<header id="banner" role="banner">
			<div id="heading">

				<#-- TODO: Should I bother adding if to search so it can be hidden based on prefs? (#if ($show_search_field && !$default_color_scheme))-->
				<div id="sitesearch">
					<#-- TODO: hide "search bar" title
					     I don't think that's available through prefs (at least it isn't through the GUI,
					     so I may just need to do that in the CSS)
					-->
					<@liferay.search_bar/>
				</div>


				<h1 class="site-title">
					<a class="${logo_css_class}" href="${site_default_url}" title="<@liferay.language_format arguments="${site_name}" key="go-to-x" />">
						<img alt="${logo_description}" height="${site_logo_height}" src="${site_logo}" width="${site_logo_width}" />
					</a>

					<#if show_site_name>
						<span class="site-name" title="<@liferay.language_format arguments="${site_name}" key="go-to-x" />">
							${site_name}
						</span>
					</#if>
				</h1>
			</div>






			<#-- TODO: previous template has condition #if ($has_navigation || $is_signed_in). Is this ok as is? -->

			<#if has_navigation && is_setup_complete>
				<div class="nav-wrapper">
					<#-- <#include "${full_templates_path}/navigation.ftl" /> -->
					<@liferay.navigation_menu />

					<#if !is_signed_in>
						<a data-redirect="${is_login_redirect_required?string}" href="${sign_in_url}" id="sign-in" rel="nofollow">${sign_in_text}</a>
					</#if>

					<#if is_signed_in>
						<@liferay.user_personal_bar />
					</#if>
				</div>
			</#if>
		</header>


	<#-- TODO: previous template has a div wrap here, then subbanner. Do we need either? -->


	<section id="content">

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


	<footer id="footer" role="contentinfo">

		<#-- TODO: need to add contact info here. Can I make this configurable to grab a web content article?
				 Don't want to have to update theme for minor edits
	   -->

		<p class="powered-by">
			<@liferay.language key="powered-by" /> <a href="http://www.liferay.com" rel="external">Liferay</a>
		</p>
	</footer>


<@liferay_util["include"] page=body_bottom_include />

<@liferay_util["include"] page=bottom_include />

</body>

</html>
