AUI().ready(
  function(A) {


    //Show site search bar on click
    var siteSearch = A.one('#siteSearch');

    if (siteSearch) {
      var btnSearch = siteSearch.one('button');

      var searchForm = siteSearch.one('form');
      var searchField = searchForm.one('.search-bar-keywords-input');

      //clear search box onload. Does weird things on search page if it has a value
      searchField.set('value', '');

      console.log("tjaglcs chroma 1.2.0")

      btnSearch.on(
        'click',
        function(event) {
          event.preventDefault();

          if (searchField.getStyle('width') == '0px') {
            searchField._node.classList.add('search-bar-keywords-input--expanded');
						btnSearch._node.classList.add('search-bar-search-button--selected');
            searchField.focus();
            return;
          } else if (searchField.get('value') == ''){
              searchField._node.classList.remove('search-bar-keywords-input--expanded');
							btnSearch._node.classList.remove('search-bar-search-button--selected');
              return;
          } else {
            searchForm.submit();
          }


        }
      );
    }

  }
);
