MDSRC=Recipes.md LanguageReference.md

TOCFILES=$(MDSRC:%.md=%_ToC.html)

all: $(TOCFILES)

%_ToC.html: %.md md2toc.py
	./md2toc.py <"$<" >"$@"


clean:
	$(RM) $(TOCFILES)
