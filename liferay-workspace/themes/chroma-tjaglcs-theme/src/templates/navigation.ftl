<#if has_navigation && is_setup_complete>

<nav class="navbar navbar-expand-lg">

	<button aria-controls="navigationCollapse" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler navbar-toggler-right" data-target="#navigationCollapse" data-toggle="collapse" type="button">
		<span class="navbar-toggler-icon"></span>
	</button>




		<div aria-expanded="false" class="collapse navbar-collapse" id="navigationCollapse">

			<#if show_header_search>

				<div class="search-bar-container justify-content-md-end navbar-form" role="search" id="siteSearch">
					<@liferay.search_bar default_preferences="${headerPortletPreferences}" />
				</div>

			</#if>


			<@liferay.navigation_menu default_preferences="${headerPortletPreferences}" />
		</div>



</nav>


</#if>
