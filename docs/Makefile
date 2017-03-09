MDSRC=Recipes.md LanguageReference.md LibraryReference.md

TOCFILES=$(MDSRC:%.md=%_ToC.html)

.PHONY: alltoc
alltoc: $(TOCFILES)

%_ToC.html: %.md md2toc.py
	./md2toc.py <"$<" >"$@"

.PHONY: server
# See: https://help.github.com/articles/setting-up-your-github-pages-site-locally-with-jekyll/
_site server: alltoc
	bundle exec jekyll serve

clean:
	$(RM) $(TOCFILES)
	$(RM) Gemfile.lock
	$(RM) -r _site
	$(RM) -r .sass-cache
